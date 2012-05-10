package es.ull.etsii.eanor.prolog.utils;

import java.util.StringTokenizer;
import es.ull.etsii.eanor.prolog.Attribute;
import es.ull.etsii.eanor.prolog.impl.AttrListSet;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;

public class AttributeSetParserSimple {
		
	private static String startStr;
	private static String endStr;
	private static String separatorStr;
	
	static {
		startStr = "[";
		endStr = "]";
		separatorStr = ",";
	}

	public static void configure(String pstartStr, String pseparatorStr, String pendStr) {
		startStr = pstartStr;
		endStr = pendStr;
		separatorStr = pseparatorStr;
	}
	
	public static AttributeSet parseStr(String startStr, String separatorStr, String endStr, String str) {
		return getAttributeSetFromStr(startStr,separatorStr,endStr,str);
	}
	
	public static AttributeSet parseStr(String str) {
		return getAttributeSetFromStr(startStr,separatorStr,endStr,str);
	}

	private static AttributeSet getAttributeSetFromStr(String startStr, String separatorStr, String endStr, String str) {
		if (str == null || !str.startsWith(startStr) || !str.endsWith(endStr))
			return null;
		AttributeSet attrSet = new AttrListSet();
		String strMiddle = str.substring(startStr.length(), str.length()-endStr.length());
		StringTokenizer strTok = new StringTokenizer(strMiddle,separatorStr);
		while (strTok.hasMoreTokens()) {
			String tok = strTok.nextToken();
			attrSet.add(new Attribute(tok.trim()));
		}
		return attrSet;
	}

}
