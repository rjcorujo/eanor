package es.ull.etsii.eanor.prolog.utils;

import java.util.StringTokenizer;
import java.util.logging.Logger;
import es.ull.etsii.eanor.prolog.Dependency;
import es.ull.etsii.eanor.prolog.impl.DepListSet;
import es.ull.etsii.eanor.prolog.interfaces.AttributeSet;
import es.ull.etsii.eanor.prolog.interfaces.DependencySet;

public class DependencySetParserSimple {
	private static String startStr;
	private static String endStr;
	private static String separatorInterDepStr;
	private static String separatorIntraDepStr;
	private static String startAttrStr;
	private static String endAttrStr;
	private static String separatorAttrStr;
	
	static {
		startStr = "{";
		endStr = "}";
		separatorIntraDepStr = "->";
		separatorInterDepStr = ";";
		startAttrStr = "";
		endAttrStr = "";
		separatorAttrStr = ",";
	}
	
	public static void configure(String pstartStr, String pstartAttrStr, String psepAttrStr, String pendAttrStr, String psepIntraStr, String psepInterStr, String pendStr) {
		startStr = pstartStr;
		endStr = pendStr;
		separatorIntraDepStr = psepIntraStr;
		separatorInterDepStr = psepInterStr;
		startAttrStr = pstartAttrStr;
		endAttrStr = pendAttrStr;
		separatorAttrStr = psepAttrStr;
	}
	
	/**
	 * 
	 * @param startStr
	 * @param startAttrStr
	 * @param sepAttrStr
	 * @param endAttrStr
	 * @param sepIntraStr
	 * @param sepInterStr
	 * @param endStr
	 * @param str
	 * @return
	 */
	public static DependencySet parseStr(String startStr, String startAttrStr, String sepAttrStr, String endAttrStr,String sepIntraStr, String sepInterStr, String endStr, String str) {
		return getDependencySetFromStr(startStr,startAttrStr,sepAttrStr,endAttrStr, sepIntraStr,sepInterStr,endStr,str);
	}
	
	public static DependencySet parseStr(String str) {
		return getDependencySetFromStr(startStr,startAttrStr,separatorAttrStr,endAttrStr, separatorIntraDepStr,separatorInterDepStr,endStr,str);
	}

	
	private static DependencySet getDependencySetFromStr(String startStr, String startAttrStr, String sepAttrStr, String endAttrStr, String sepIntraStr, String sepInterStr, String endStr,String str) {
		if (str == null || !str.startsWith(startStr) || !str.endsWith(endStr))
			return null;
		DependencySet depSet = new DepListSet();
		String strMiddle = str.substring(startStr.length(), str.length()-endStr.length());
		StringTokenizer strTok = new StringTokenizer(strMiddle,sepInterStr);
		while (strTok.hasMoreTokens()) {
			String depStr = strTok.nextToken();
			int indexSep = depStr.indexOf(sepIntraStr);
			String antecStr = depStr.substring(0, indexSep);
			String consecStr = depStr.substring(indexSep + sepIntraStr.length(), depStr.length());
			AttributeSet antecSet = AttributeSetParserSimple.parseStr(startAttrStr,sepAttrStr,endAttrStr,antecStr);
			AttributeSet consecSet =AttributeSetParserSimple.parseStr(startAttrStr,sepAttrStr,endAttrStr,consecStr);
			if (antecSet != null && consecSet != null)
				depSet.add(new Dependency(antecSet,consecSet));
		}
		return depSet;
	}

}
