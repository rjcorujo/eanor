package es.ull.etsii.eanor.prolog.tests;

import org.junit.Test;

import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;
import es.ull.etsii.eanor.prolog.utils.AttributeSetParserSimple;
import es.ull.etsii.eanor.prolog.utils.DependencySetParserSimple;
import static org.junit.Assert.*;

public class EanorMiscellaneousTests {
	
	@Test
	public void testAttrSetParserSimple() {
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[A,B,C,D,E]");
		assertEquals(attrSet.toString(),"a,b,c,d,e");
	}
	
	@Test
	public void testAttrSetParserSpaces() {
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[  A , B  , C  ,   D ,	E]");
		assertEquals(attrSet.toString(),"a,b,c,d,e");
	}
	
	@Test
	public void testAttrSetParserNoStartEnd() {
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("",",","","A,B,C,D,E");
		assertTrue(attrSet.toString().equals("a,b,c,d,e"));
	}
	
	@Test
	public void testAttrSetParserNullStr() {
		AttributeSet attrSet = AttributeSetParserSimple.parseStr(null);
		assertNull(attrSet);
	}
	
	@Test
	public void testAttrSetParserSingleElement() {
		AttributeSet attrSet = AttributeSetParserSimple.parseStr("[a]");
		assertEquals(attrSet.toString(),"a" );
	}
	
	@Test
	public void testDepSetParserSimple() {
    	DependencySet depSet = DependencySetParserSimple.parseStr("","",",","","-",";","","A,B,F-C;D-E");
		System.out.println(depSet);

		assertEquals(depSet.toString(),"{(a,b,f)->(c),(d)->(e)}");
	}
	
	@Test
	public void testDepSetParserSimpleRare() {
		DependencySet depSet = DependencySetParserSimple.parseStr("{ A,B,F -> C ; D -> E }");
		System.out.println(depSet);
		assertEquals(depSet.toString(),"{(a,b,f)->(c),(d)->(e)}");
	}
	
	@Test
	public void testDepSetParserSpaces() {
		DependencySet depSet = DependencySetParserSimple.parseStr("{ A		,B,F	 ->    C  ;    D 		->	 E }");
		DependencySet depSet2 = DependencySetParserSimple.parseStr("{ A,B,F -> C ; D -> E }");
		System.out.println(depSet);
		assertEquals(depSet,depSet2);
	}
	
	@Test
	public void testDepSetParserNoStartEnd() {
		DependencySet depSet = DependencySetParserSimple.parseStr("","",",","","->",";","","A,B->C;D->E");
		System.out.println(depSet);
		assertEquals(depSet.toString(),"{(a,b)->(c),(d)->(e)}");
	}
	
	@Test
	public void testDepSetParserNullStr() {
		DependencySet depSet = DependencySetParserSimple.parseStr(null);
		assertNull(depSet);
	}
	
	@Test
	public void testDepSetParserSingleElement() {
		DependencySet attrSet = DependencySetParserSimple.parseStr("{ A,B->D }");
		assertEquals(attrSet.toString(),"{(a,b)->(d)}" );
	}
	
}
