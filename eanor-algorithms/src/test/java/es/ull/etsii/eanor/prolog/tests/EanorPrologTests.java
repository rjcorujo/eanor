package es.ull.etsii.eanor.prolog.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jpl.Query;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.ull.etsii.eanor.prolog.AnomaliesWrapper;
import es.ull.etsii.eanor.prolog.Anomaly2NF;
import es.ull.etsii.eanor.prolog.Anomaly3NF;
import es.ull.etsii.eanor.prolog.AnomalyBCNF;
import es.ull.etsii.eanor.prolog.Attribute;
import es.ull.etsii.eanor.prolog.Dependency;
import es.ull.etsii.eanor.prolog.Relation;
import es.ull.etsii.eanor.prolog.RelationSet;
import es.ull.etsii.eanor.prolog.impl.KeyListSet;
import es.ull.etsii.eanor.prolog.impl.PrologRelNormalizer;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;
import es.ull.etsii.eanor.prolog.interfaces.KeySet;
import es.ull.etsii.eanor.prolog.interfaces.RelationNormalizer;
import es.ull.etsii.eanor.prolog.utils.AttributeSetParserSimple;
import es.ull.etsii.eanor.prolog.utils.DependencySetParserSimple;
import es.ull.etsii.eanor.prolog.utils.PrologParser;
import static org.junit.Assert.*;

/**
 * Para que funcione en Mac se debe poner en lso argumentos de la JVM en run configurations, -Djava.library.path=/opt/local/lib/swipl-5.10.1/lib/i386-darwin10.4.0/ que es donde se instalaron
 * las librerias nativas de prolog.
 * 
 * En unix se setea la variable de entorno LD_LIBRARY_PATH=/opt/local/lib/swipl-5.10.1/lib/i386-darwin10.4.0/ (DONDE SE HAYA INSTALADO)
 * 
 * Para windows hay que declararse unas variables de entorno
 * 
 * 
 * @author robertcorujo
 *
 */
public class EanorPrologTests {
	public static Query query;
	
    @BeforeClass
    public static void loadPLFile() throws Exception
    {
    	URL url = EanorPrologTests.class.getResource("/eanor.pl");
    	String filename = url.getFile();
    	System.out.println(filename);
    	PrologRelNormalizer.loadPLFile(filename);
    	AttributeSetParserSimple.configure("[", ",", "]");
    	DependencySetParserSimple.configure("{","",",","","->",";","}");
    }
    
    @Test
    public void calculaCierre() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C; C -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		
		AttributeSet attrSetCierre = AttributeSetParserSimple.parseStr("[A]");
		
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		AttributeSet attrSetRes = relNorm.getClosure(attrSetCierre);
		
		assertEquals(attrSetRes,attrSetCierre);
    }
    
    @Test
    public void calculaMinimalCover() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C; B -> A; C -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		
		DependencySet depSetMinimal = DependencySetParserSimple.parseStr("{ B -> C; B -> A; C -> D}");
		
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		DependencySet depSetRes = relNorm.getMinimalCover();
		
		assertEquals(depSetRes,depSetMinimal);
    }
    
    @Test
    public void calculaClaves() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C; C -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		KeySet keySet = relNorm.getKeys();
		
		AttributeSet attrSetRes = AttributeSetParserSimple.parseStr("[A,B]");
		KeySet keySetRes = new KeyListSet();
		keySetRes.add(attrSetRes);
		assertEquals(keySet, keySetRes);
		
    }
    
    @Test
    public void calculaClaves_AttrsIndep() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C; C -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D,E,F]");
		
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		KeySet keySet = relNorm.getKeys();
		
		AttributeSet attrSetRes = AttributeSetParserSimple.parseStr("[A,B,E,F]");
		KeySet keySetRes = new KeyListSet();
		keySetRes.add(attrSetRes);
		assertEquals(keySet, keySetRes);
		
    }
    
    @Test
    public void test2NF() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C; C -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		Set<Anomaly2NF> anomalies2NF = relNorm.test2NF().getAnomalies2NF();
		assertTrue(anomalies2NF.size() == 0);
    }
    
    @Test
    public void test2NF_fail() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C; B -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		Set<Anomaly2NF> anomalies2NF = relNorm.test2NF().getAnomalies2NF();
		
		Set<Anomaly2NF> anomaliesRes = new HashSet<Anomaly2NF>();
		AttributeSet antSetRes = AttributeSetParserSimple.parseStr("[B]");
		AttributeSet consSetRes = AttributeSetParserSimple.parseStr("[D]");
		anomaliesRes.add(new Anomaly2NF(new Attribute("D"),  new Dependency(antSetRes,consSetRes), AttributeSetParserSimple.parseStr("[A,B]")));
		assertTrue(EanorTestsUtils.equalsSetAnomalies2NF(anomalies2NF, anomaliesRes));
    }
    
    @Test
    public void test2NF_fail2Keys() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ E -> A; A -> E; A,B -> C; B -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D,E]");
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		Set<Anomaly2NF> anomalies2NF = relNorm.test2NF().getAnomalies2NF();
		
		Set<Anomaly2NF> anomaliesRes = new HashSet<Anomaly2NF>();
		
		AttributeSet antSetRes = AttributeSetParserSimple.parseStr("[B]");
		AttributeSet consSetRes = AttributeSetParserSimple.parseStr("[D]");
		anomaliesRes.add(new Anomaly2NF(new Attribute("D"),  new Dependency(antSetRes,consSetRes), AttributeSetParserSimple.parseStr("[A,B]")));
		
		antSetRes = AttributeSetParserSimple.parseStr("[B]");
		consSetRes = AttributeSetParserSimple.parseStr("[D]");
		anomaliesRes.add(new Anomaly2NF(new Attribute("D"),  new Dependency(antSetRes,consSetRes), AttributeSetParserSimple.parseStr("[E,B]")));
		
		assertTrue(EanorTestsUtils.equalsSetAnomalies2NF(anomalies2NF, anomaliesRes));
		
    }
    
    @Test
    public void test3NF() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C,D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		AnomaliesWrapper anomalies = relNorm.test3NF();
		Set<Anomaly2NF> anomalies2NF = anomalies.getAnomalies2NF();
		Set<Anomaly3NF> anomalies3NF = anomalies.getAnomalies3NF();
		assertTrue(anomalies2NF.size() == 0 && anomalies3NF.size() == 0);
    }
    
    
    @Test
    public void test3NF_fail() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C; C -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		AnomaliesWrapper anomalies = relNorm.test3NF();
		Set<Anomaly2NF> anomalies2NF = anomalies.getAnomalies2NF();
		Set<Anomaly3NF> anomalies3NF = anomalies.getAnomalies3NF();
		
		Set<Anomaly3NF> anomaliesRes = new HashSet<Anomaly3NF>();
		AttributeSet antSetRes = AttributeSetParserSimple.parseStr("[C]");
		AttributeSet consSetRes = AttributeSetParserSimple.parseStr("[D]");
		anomaliesRes.add(new Anomaly3NF(new Dependency(antSetRes,consSetRes)));
		
		assertTrue(anomalies2NF.size() == 0 && EanorTestsUtils.equalsSetAnomalies3NF(anomaliesRes, anomalies3NF));
    }
    
    @Test
    public void testBCNF() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B -> C,D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D]");
		RelationNormalizer relNorm = new PrologRelNormalizer(new Relation(attrSet, depSet));
		
		AnomaliesWrapper anomalies = relNorm.testBCNF();
		Set<Anomaly2NF> anomalies2NF = anomalies.getAnomalies2NF();
		Set<Anomaly3NF> anomalies3NF = anomalies.getAnomalies3NF();
		Set<AnomalyBCNF> anomaliesBCNF = anomalies.getAnomaliesBCNF();
		assertTrue(anomalies2NF.size() == 0 && anomalies3NF.size() == 0 && anomaliesBCNF.size() == 0);
    }
    
    @Test
    public void synthesis() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("{ E -> A; A -> E; A,B -> C; B -> D}");
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D,E]");
		Relation rel = new Relation(attrSet,depSet);
		
		PrologRelNormalizer relNorm = new PrologRelNormalizer(rel);
		
		RelationSet relSet = relNorm.applySynthesis();
		
		System.out.println(relSet);
    }
}


