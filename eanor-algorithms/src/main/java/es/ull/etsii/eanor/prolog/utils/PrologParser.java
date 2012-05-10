package es.ull.etsii.eanor.prolog.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import jpl.Compound;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import es.ull.etsii.eanor.prolog.Anomaly2NF;
import es.ull.etsii.eanor.prolog.Anomaly3NF;
import es.ull.etsii.eanor.prolog.AnomalyBCNF;
import es.ull.etsii.eanor.prolog.Attribute;
import es.ull.etsii.eanor.prolog.Dependency;
import es.ull.etsii.eanor.prolog.impl.AttrListSet;
import es.ull.etsii.eanor.prolog.impl.DepListSet;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;

public class PrologParser {
	
	public static final int ANOMALY_2NF_IMPLIED_KEY_ARG = 1;
	public static final int ANOMALY_2NF_DEPENDENT_ATTR_ARG = 3;
	public static final int ANOMALY_2NF_IMPLIED_DEPENDENCY_ARG = 2;
	public static final int ANOMALY_3NF_ANTECEDENT_ARG = 1;
	public static final int ANOMALY_3NF_CONSEC_ARG = 2;
	public static final int ANOMALY_BCNF_ANTECEDENT_ARG = 1;
	public static final int ANOMALY_BCNF_CONSEC_ARG = 2;
	public static final int DEPENDENCY_ANTECEDENT_ARG = 1;
	public static final int DEPENDENCY_CONSEC_ARG = 2;
	public static final int SYNTHESIS_ATTRIBUTES_ARG = 1;
	public static final int SYNTHESIS_DEPENDENCIES_ARG = 2;

	
	public static Set<Anomaly2NF> parseAnomalies2NF(Term[] anomalias) {
		Set<Anomaly2NF> anomList = new HashSet<Anomaly2NF>();
		for (int i = 0; i < anomalias.length; i++) {
			Attribute depenAttr = new Attribute(anomalias[i].arg(ANOMALY_2NF_DEPENDENT_ATTR_ARG).name());
			AttributeSet impliedKey = new AttrListSet();
			
			Term[] term_clave = Util.listToTermArray(anomalias[i].arg(1));
			for (int j = 0; j < term_clave.length; j++) 
				impliedKey.add(new Attribute(term_clave[j].name()));
			
			AttributeSet impliedDepAnt = new AttrListSet();
			Term[] term_partclave = Util.listToTermArray(anomalias[i].arg(2));
			for (int j = 0; j < term_partclave.length; j++)
				impliedDepAnt.add(new Attribute(term_partclave[j].name()));
			
			AttributeSet impliedDepCons = new AttrListSet();
			impliedDepCons.add(depenAttr);
			Dependency impliedDep = new Dependency(impliedDepAnt,impliedDepCons);
			
			Anomaly2NF anomaly = new Anomaly2NF(depenAttr, impliedDep, impliedKey);
			anomList.add(anomaly);
			//"El Atributo no primo \""+anomalias[i].arg(3)+"\" depende parcialmente ("+partclabuf.toString()+" -> "+ anomalias[i].arg(3)+") de la clave ("+clavebuf.toString()+")";
		}
		return anomList;
	}
	
	public static Set<Anomaly3NF> parseAnomalies3NF(Term[] anomalias) {
		Set<Anomaly3NF> anomList = new HashSet<Anomaly3NF>();
		
		for (int i = 0; i < anomalias.length; i++) {
			Term[] term_antec = Util.listToTermArray(anomalias[i].arg(ANOMALY_3NF_ANTECEDENT_ARG));
			AttributeSet antecSet = PrologParser.parseAttributeSet(term_antec);
			
			Term[] term_cons = Util.listToTermArray(anomalias[i].arg(ANOMALY_3NF_CONSEC_ARG));
			AttributeSet consecSet = PrologParser.parseAttributeSet(term_cons);
			
			anomList.add(new Anomaly3NF(new Dependency(antecSet,consecSet)));
		}
		return anomList;
	}
	
	public static AttributeSet parseAttributeSet(Term[] attributesTerm) {
		AttributeSet attrSet = new AttrListSet();
		for (int j = 0; j < attributesTerm.length; j++) {
			attrSet.add(new Attribute(attributesTerm[j].name()));
		}
		return attrSet;
	}
	
	public static DependencySet parseDependencySet(Term[] dependenciesTerm) {
		DependencySet depSet = new DepListSet();
		for (int i = 0; i < dependenciesTerm.length; i++) {
			AttributeSet antecSet = parseAttributeSet(Util.listToTermArray(dependenciesTerm[i].arg(DEPENDENCY_ANTECEDENT_ARG)));
			AttributeSet consecSet = parseAttributeSet(Util.listToTermArray(dependenciesTerm[i].arg(DEPENDENCY_CONSEC_ARG)));
			depSet.add(new Dependency(antecSet,consecSet));
		}
		return depSet;
	}
	
	
	public static Set<AnomalyBCNF> parseAnomaliesBCNF(Term[] anomalias)  {
		Set<AnomalyBCNF> anomList = new HashSet<AnomalyBCNF>();
		for (int i = 0; i < anomalias.length; i++) {
	
			Term[] term_antec = Util.listToTermArray(anomalias[i].arg(ANOMALY_BCNF_ANTECEDENT_ARG));
			AttributeSet antecSet = PrologParser.parseAttributeSet(term_antec);
			
			Term[] term_cons = Util.listToTermArray(anomalias[i].arg(ANOMALY_BCNF_CONSEC_ARG));
			AttributeSet consecSet = PrologParser.parseAttributeSet(term_cons);
			
			anomList.add(new AnomalyBCNF(new Dependency(antecSet,consecSet)));
		}
		return anomList;
	}
	
	public static Term getAttrSetAsTerm (AttributeSet attrset) {
		/*Obtenemos la lista de atributos*/
		String[] contex = new String[attrset.getSize()];
		int i = 0;
		attrset.reset();
		while (attrset.hasNext()) {
			contex[i] = attrset.getNext().toString();
			i++;
		}
		/*Lo convertimos en una lista de prolog*/
		return Util.stringArrayToList(contex);
	}
	

	public static Term getDepSetAsTerm(DependencySet depset) {
		/*Obtener la lista de dependencias*/
		Term[] listdeps = new Term[depset.getSize()];
		depset.reset();
		int i = 0;
		while(depset.hasNext()) {
			Dependency dep = depset.getNext();
			AttributeSet vec_antec = dep.getAnts();
			String[] str_ant = new String[vec_antec.getSize()];
			vec_antec.reset();
			int j = 0;
			while(vec_antec.hasNext()) {
				str_ant[j] = vec_antec.getNext().toString().toLowerCase();
				j++;
			}
			Term antecs = Util.stringArrayToList(str_ant);
			
			AttributeSet vec_cons = dep.getCons();
			String[] str_cons = new String[vec_cons.getSize()];
			vec_cons.reset();
			j = 0;
			while (vec_cons.hasNext()){
				str_cons[j] = vec_cons.getNext().toString().toLowerCase();
				j++;
			}
			Term cons = Util.stringArrayToList(str_cons);

			listdeps[i] = new Compound("dep",new Term[] {antecs,cons});
			i++;
		}
		return Util.termArrayToList(listdeps);
	}
}
