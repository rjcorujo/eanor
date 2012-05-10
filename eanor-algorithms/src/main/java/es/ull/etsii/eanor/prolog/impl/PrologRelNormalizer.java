package es.ull.etsii.eanor.prolog.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import es.ull.etsii.eanor.prolog.AnomaliesWrapper;
import es.ull.etsii.eanor.prolog.Anomaly2NF;
import es.ull.etsii.eanor.prolog.Anomaly3NF;
import es.ull.etsii.eanor.prolog.AnomalyBCNF;
import es.ull.etsii.eanor.prolog.Attribute;
import es.ull.etsii.eanor.prolog.Dependency;
import es.ull.etsii.eanor.prolog.Relation;
import es.ull.etsii.eanor.prolog.RelationSet;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;
import es.ull.etsii.eanor.prolog.interfaces.KeySet;
import es.ull.etsii.eanor.prolog.interfaces.RelationNormalizer;
import es.ull.etsii.eanor.prolog.utils.PrologParser;
import static es.ull.etsii.eanor.prolog.utils.PrologParser.*;

import jpl.*;

public class PrologRelNormalizer implements RelationNormalizer {
	private Relation relation;
	private Logger log = Logger.getLogger(PrologRelNormalizer.class.getName());
	private static boolean loaded = false;
	
	public static boolean loadPLFile(String filename) {
		String osName = System.getProperty("os.name").toLowerCase();
		//TODO: Contemplar el caso de Unix
		if (osName.startsWith("windows")) {
			if (filename.startsWith("/"))
				filename = filename.substring(1);
			filename = filename.replaceAll("%20", " ");
		}
			
		Query q = new Query("consult('"+filename+"')");
		q.hasSolution();
		try {
			q = new Query("loadingTest(a,a)");
			q.hasSolution();
			loaded = true;
		}
		catch (PrologException e) {
			loaded = false;
		}
		return loaded;
	}
	
	public static boolean isLoaded() {
		return loaded;
	}
	
	public PrologRelNormalizer(Relation rel) {
		relation = rel;
	}
	
	public RelationSet applySynthesis() {
		Variable varrels = new Variable("Newrels");

		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());
		Query query = new Query(new Compound("sintesis",new Term[]{contexto,dependencias,varrels}));
		Term[] rels_list = Util.listToTermArray(((Term)query.oneSolution().get(varrels.name())));
		
		RelationSet relSet = new RelationSet();
		for (int i = 0; i < rels_list.length; i++) {
			AttributeSet context = PrologParser.parseAttributeSet(Util.listToTermArray(rels_list[i].arg(SYNTHESIS_ATTRIBUTES_ARG)));
			DependencySet depSet = PrologParser.parseDependencySet( Util.listToTermArray(rels_list[i].arg(SYNTHESIS_DEPENDENCIES_ARG)));
			relSet.add(new Relation(context,depSet));
		}
		
		return relSet;
	}

	public AttributeSet getClosure(AttributeSet attrs) {
		log.info("Calculo de claves");
		Variable cierre = new Variable("Cierre");
		
		/*Obtenemos la lista de atributos en cuetsion de termino*/
		Term listAttrsTerm = PrologParser.getAttrSetAsTerm(attrs);
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());
		
		/*Realizanmos consulta*/
		Query query = new Query(new Compound("cierre",new Term[]{listAttrsTerm,dependencias,cierre}));
		if (query.hasSolution()) 
			log.info("Tiene solucion "+query.oneSolution());
		
		Term[] cierre_term = Util.listToTermArray((Term)(query.oneSolution().get(cierre.name)));
		AttributeSet attrSet = PrologParser.parseAttributeSet(cierre_term);
		return attrSet;
	}

	public KeySet getKeys() {
		log.info("Calculo de claves");
		Variable claves = new Variable("Claves"); 
		
		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());
		
		/*Realizanmos consulta*/
		Query query = new Query(new Compound("claves_rel",new Term[]{contexto,dependencias,claves}));
		if (query.hasSolution()) 
			log.info("Tiene solucion "+query.oneSolution());
		Term[] claves_term = Util.listToTermArray((Term)(query.oneSolution().get(claves.name)));
		
		KeySet kset = new KeyListSet();
		for (int i = 0 ; i < claves_term.length; i++) {
			Term[] atribs_list = Util.listToTermArray(claves_term[i]);
			AttributeSet attrset = PrologParser.parseAttributeSet(atribs_list);
			kset.add(attrset);
		}
		log.info("Claves: "+kset);
		return kset;
	}

	public DependencySet getMinimalCover() {
		Variable recmin = new Variable("Recubmin");
		
		/*Obtener la lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());
		
		Query query = new Query(new Compound("recub_minimo",new Term[]{dependencias,recmin}));
		
		Term[] list_deps = Util.listToTermArray((Term)(query.oneSolution().get(recmin.name)));
		
		DependencySet depset = new DepListSet();

		for (int i = 0 ; i < list_deps.length; i++) {
			Term[] antecs = Util.listToTermArray(list_deps[i].arg(1));
			Term[] cons = Util.listToTermArray(list_deps[i].arg(2));
			
			AttributeSet attrset_ant = (AttributeSet)new AttrListSet();
			for (int j = 0; j < antecs.length; j++) 
				attrset_ant.add(new Attribute(antecs[j].toString()));
			
			AttributeSet attrset_cons = (AttributeSet)new AttrListSet();
			for (int j = 0; j < cons.length; j++)
				attrset_cons.add(new Attribute(cons[j].toString()));
			
			depset.add(new Dependency(attrset_ant,attrset_cons));
		}
		return depset;
	}

	public AnomaliesWrapper test2NF() {
		Variable var = new Variable("Anomalias");
		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());

		Query query = new Query(new Compound("test_2fn_detail",new Term[]{contexto,dependencias,var}));
		
		Term[] anomalias = Util.listToTermArray(((Term)query.oneSolution().get(var.name())));
		
		Set<Anomaly2NF> anomList = PrologParser.parseAnomalies2NF(anomalias);
		return new AnomaliesWrapper(anomList);
	}

	public AnomaliesWrapper test3NF() {
		Variable var2fn = new Variable("Anomalias2FN");
		Variable var3fn = new Variable("Anomalias3FN");
		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());

		Query query = new Query(new Compound("test_3fn_detail",new Term[]{contexto,dependencias,var2fn,var3fn}));
		
		Term[] anomalias2nf = Util.listToTermArray(((Term)query.oneSolution().get(var2fn.name())));
		
		Set<Anomaly2NF> anom2nf =  PrologParser.parseAnomalies2NF(anomalias2nf);

		Term[] anomalias3nf = Util.listToTermArray(((Term)query.oneSolution().get(var3fn.name())));
		
		Set<Anomaly3NF> anom3nf =  PrologParser.parseAnomalies3NF(anomalias3nf);
		
		return new AnomaliesWrapper(anom2nf, anom3nf);
	}

	public AnomaliesWrapper testBCNF() {
		Variable var2fn = new Variable("Anomalias2FN");
		Variable var3fn = new Variable("Anomalias3FN");
		Variable varfnbc = new Variable("AnomaliasFNBC");
		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());

		Query query = new Query(new Compound("test_fnbc_detail",new Term[]{contexto,dependencias,var2fn,var3fn,varfnbc}));
		
		Term[] anomalias2nf = Util.listToTermArray(((Term)query.oneSolution().get(var2fn.name())));
		Set<Anomaly2NF> anomalies2NF = PrologParser.parseAnomalies2NF(anomalias2nf);
				
		Term[] anomalias3nf = Util.listToTermArray(((Term)query.oneSolution().get(var3fn.name())));
		Set<Anomaly3NF> anomalies3NF = PrologParser.parseAnomalies3NF(anomalias3nf);
		
		Term[] anomaliasbcnf = Util.listToTermArray(((Term)query.oneSolution().get(varfnbc.name())));
		Set<AnomalyBCNF> anomaliesBCNF = PrologParser.parseAnomaliesBCNF(anomaliasbcnf);
		
		return new AnomaliesWrapper(anomalies2NF, anomalies3NF, anomaliesBCNF);
	}

	public boolean is2NF() {
		Variable var = new Variable("Anomalias");
		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());

		Query query = new Query(new Compound("test_2fn_detail",new Term[]{contexto,dependencias,var}));
		
		Term[] anomalias = Util.listToTermArray(((Term)query.oneSolution().get(var.name())));
		return anomalias.length == 0;
	}

	public boolean is3NF() {
		Variable var2fn = new Variable("Anomalias2FN");
		Variable var3fn = new Variable("Anomalias3FN");
		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());

		Query query = new Query(new Compound("test_3fn_detail",new Term[]{contexto,dependencias,var2fn,var3fn}));
		
		Term[] anomalias2nf = Util.listToTermArray(((Term)query.oneSolution().get(var2fn.name())));
		Term[] anomalias3nf = Util.listToTermArray(((Term)query.oneSolution().get(var3fn.name())));
		
		return anomalias2nf.length == 0 && anomalias3nf.length == 0;
	}

	public boolean isBCNF() {
		Variable var2fn = new Variable("Anomalias2FN");
		Variable var3fn = new Variable("Anomalias3FN");
		Variable varfnbc = new Variable("AnomaliasFNBC");
		/*Obtenemos la lista de atributos*/
		Term contexto = PrologParser.getAttrSetAsTerm(relation.getAttrset());
		/*Lista de dependencias*/
		Term dependencias = PrologParser.getDepSetAsTerm(relation.getDepset());

		Query query = new Query(new Compound("test_fnbc_detail",new Term[]{contexto,dependencias,var2fn,var3fn,varfnbc}));
		
		Term[] anomalias2nf = Util.listToTermArray(((Term)query.oneSolution().get(var2fn.name())));
		Term[] anomalias3nf = Util.listToTermArray(((Term)query.oneSolution().get(var3fn.name())));
		Term[] anomaliasbcnf = Util.listToTermArray(((Term)query.oneSolution().get(varfnbc.name())));
		
		return anomalias2nf.length == 0 && anomalias3nf.length == 0 && anomaliasbcnf.length == 0;
	}
	

	
}
