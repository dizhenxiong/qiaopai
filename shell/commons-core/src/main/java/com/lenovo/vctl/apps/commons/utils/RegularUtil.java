package com.lenovo.vctl.apps.commons.utils;


public class RegularUtil {

	/**
	 * 匹配由数字、英文字母、特殊字符组成的6-20位字符串，不包括汉字及全角字符 
	 * @param content
	 * @return
	 */
	public static boolean isValidContent(String content){
		//匹配由数字、26个英文字母或者下划线组成的字符串    \w是指单词字符：[a-zA-Z_0-9]
		//String regular = "^\\w+$";  
		//Pattern pattern = Pattern.compile(regular);  
		//return pattern.matcher(content).matches(); 
		
		if(content == null) return false;
		//String r = "[\\x01-\\xfe]+"; //长度不限
		String r = "[\\x01-\\xfe]{6,20}"; 
		return content.matches(r);
	}
	
	public static void main(String[] args) {
		System.out.println(RegularUtil.isValidContent("基材aa基材aa基材aa"));
		System.out.println(RegularUtil.isValidContent("基aaaaaaaa"));
		System.out.println(RegularUtil.isValidContent("aaaaaaaaa"));
		System.out.println(RegularUtil.isValidContent("a_a"));
		System.out.println(RegularUtil.isValidContent("a_a&a_a&a_a&"));
		System.out.println(RegularUtil.isValidContent("122~!@#$%^&*()222222a"));
		System.out.println(RegularUtil.isValidContent("122~!@#$%^&*()22"));
		System.out.println(RegularUtil.isValidContent("122aa_"));
		System.out.println(RegularUtil.isValidContent("122aa_ａａ"));
		System.out.println(RegularUtil.isValidContent(""));
		System.out.println(RegularUtil.isValidContent(null));
		System.out.println(RegularUtil.isValidContent("１２３４＠＃４"));
		System.out.println(RegularUtil.isValidContent("＠＃"));
		System.out.println(RegularUtil.isValidContent("１２３４"));
		
		/* String s ="122~!@#$%^&*()222222a";
		 String r = "^(?=.*\\d.*)(?=.*[a-zA-Z].*)(?=.*[-`~!@#$%^&*()_+\\|\\\\=,./?><\\{\\}\\[\\]].*).*$";
		 System.out.println(s.matches(r));*/
	}
}
