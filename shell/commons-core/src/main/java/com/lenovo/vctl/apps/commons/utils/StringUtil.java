package com.lenovo.vctl.apps.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.lenovo.vctl.apps.commons.constants.SignConstant;

public class StringUtil {
	public static String escapeXml(String source) {
		return StringUtils.replaceEach(source, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
	}
	
	/**
     * 按指定字符串来分割源字符串返回数组
     *
     * @param source String
     * @param sign   String
     * @return 返回值数组型
     * @Exception
     */
    public static String[] getArray(String source, String sign) {
        // 按特定子串s为标记，将子串截成数组。
        int count = getCount(source, sign);
        int j = 0;
        String[] arr = new String[count];
        for (int i = 0; i < count; i++) {
            if (source.indexOf(sign) != -1) {
                j = source.indexOf(sign);
                arr[i] = source.substring(0, j);
                source = source.substring(j + 1);
            } else {
                arr[i] = source;
            }
        }
        return arr;
    }

    /**
     * 查找指定字符串在源字符串中出现的次数
     *
     * @param source String
     * @param sign   String
     * @return 返回值Int型 为含指定字符串次数
     * @Exception
     */
    public static int getCount(String source, String sign) {
        // 查找某一字符串中str，特定子串s的出现次数
        if (source == null)
            return 0;
        StringTokenizer s = new StringTokenizer(source, sign);
        return s.countTokens();
    }

    /**
     * Checks if a String is not empty ("") and not null.
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Checks if a String is empty ("") or null.
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null)
            return true;
        return "".equals(str.trim());
    }

    /**
     * 第一个参数，传入的是要截的中英文字符串，第二个参数，要截取的长度。
     *
     * @param str
     * @param subBytes
     * @return str
     */
    public static String subString(String str, int subBytes) {
        int byteLen = str.length();
        if(byteLen == subBytes) return str;
        int bytes = 0; // 用来存储字符串的总字节数
        for (int i = 0; i < str.length(); i++) {
            if (bytes == subBytes) {
                return str.substring(0, i);
            }
            char c = str.charAt(i);
            if (c < 256) {
                bytes += 1; // 英文字符的字节数看作1
            } else {
                bytes += 2; // 中文字符的字节数看作2
                if (bytes - subBytes == 1) {
                    return str.substring(0, i);
                }
            }
        }
        return str;
    }
    /**
     * 汉字当1位
     * @param str
     * @param beginIndex 
     * @param endIndex
     * @return
     */
    public static String subString(String str, int beginIndex , int endIndex) {
        if (endIndex <= beginIndex) return null;
        
        int bytesLen = str.length();
        String subStr = null;
        int bytes = 0; // 用来存储字符串的总字节数
        for (int i = 0; i < str.length(); i++) {
            if (bytes == beginIndex) {
                subStr =  str.substring(i, bytesLen);
                bytes = i ; 
                break;
            }
            char c = str.charAt(i);
            if (c < 256) {
                bytes += 1; // 英文字符的字节数看作1
            } else {
                bytes += 2; // 中文字符的字节数看作2
                if (bytes - beginIndex == 1) {
                    subStr =  str.substring(i,bytesLen);
                    bytes = i ; 
                    break;
                }
            }
        }
        
        String retStr = subString(subStr,endIndex - bytes);
        return retStr;
    }
    /**
     * 第一个参数，传入的是要截的中英文字符串，第二个参数，要截取的长度。
     *
     * @param str
     * @param subBytes
     * @return str
     */
    public static String subStringWithMore(String str,
                                           int subBytes) {
        int bytes = 0; // 用来存储字符串的总字节数
        for (int i = 0; i < str.length(); i++) {
            if (bytes == subBytes) {
                if (i < str.length() - 1)
                    return str.substring(0, i) + "...";
                else
                    return str.substring(0, i) + "....";
            }
            char c = str.charAt(i);
            if (c < 256) {
                bytes += 1; // 英文字符的字节数看作1
            } else {
                bytes += 2; // 中文字符的字节数看作2
                if (bytes - subBytes == 1) {
                    if (i < str.length() - 1)
                        return str.substring(0, i) + "...";
                    else
                        return str.substring(0, i) + "....";
                }
            }
        }
        return str;
    }
    /**
     * 功能：获取字符串长度，一个汉字等于两个字符
     * @param strParameter
     * @return
     */
    public static int getStrLength(String str) {
        if (str == null || str.length() == 0)
            return 0;

        int bytes = 0; // 用来存储字符串的总字节数
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 256) {
                bytes += 1; // 英文字符的字节数看作1
            } else {
                bytes += 2; // 中文字符的字节数看作2
            }
        }

        return bytes;
    }
    /**
     * 功能：验证字符串长度是否符合要求，一个汉字等于两个字符
     *
     * @param strParameter 要验证的字符串
     * @param limitLength  验证的长度
     * @return 符合长度ture 超出范围false
     */
    public static boolean validateStrByLength(String strParameter, int limitLength) {
        int temp_int = getStrLength(strParameter);

        return temp_int <= limitLength;
    }

    /**
     * 增加软换行
     */
    public static String addSoftNewline(String content) {
        // 跳过标签，跳过转义符，跳过ubb表情
        List<String> tags = StringUtil.getRegex(content, "(?i)<[^>]+>|&\\#?\\w{2,6};|\\[[^\\]]{2,12}\\]", 0);
        for (int i = 0; i < tags.size(); i++) {
            content = StringUtils.replace(content, tags.get(i), "{*" + i + "*}");
        }

        content = content.replaceAll("[^\\{\\}]{10}", "$0<wbr />");

        // 替换回去
        for (int i = 0; i < tags.size(); i++) {
            content = StringUtils.replace(content, "{*" + i + "*}", tags.get(i));
        }
        return content;
    }

    /**
     * 标点优先截断
     *
     * @param content   要被截断的字符串
     * @param reserve   保留长度
     * @param tolerance 允许误差
     * @return
     */
    public static String truncateByPunctuation(String content, int reserve, int tolerance) {
        if (StringUtils.isBlank(content)) return "";
        if (reserve > content.length() || tolerance > content.length() / 2) return content;

        char[] chars = content.toCharArray();
        int length = chars.length;

        int start = reserve - tolerance > 0 ? reserve - tolerance : 0;
        int end = reserve + tolerance < length ? reserve + tolerance : length;
        int cutPosition = -1;
        for (int i = start; i < end; i++) {
            if (String.valueOf(chars[i]).matches("[、，。：；！…\\p{Punct}]")) {
                cutPosition = i;
            }
        }
        if (cutPosition != -1) {
            return content.substring(0, cutPosition);
        } else {
            return content.substring(0, reserve);
        }
    }

    /**
     * 模式匹配
     *
     * @param content
     * @param group   0:包含开始和结尾，1：仅仅包含最终结果
     */
    public static List<String> getRegex(String content, String regex, int group) {
        if (content == null || content.equals("")) {
            return new ArrayList<String>();
        }
        try {
            List<String> list = new ArrayList<String>();
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if (matcher != null) {
                // 开始匹配
                while (matcher.find()) {
                    String str = matcher.group(group);
                    // 得到结果
                    if (str != null && str.length() > 0) {
                        list.add(str);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            System.out.println(e + " getRegex");
            return null;
        }
    }
	
	/**
	 * 指定分隔符把集合转换成字符串
	 * @param source
	 * @param split
	 * @return
	 */
    public static String convertList2String(List<String> source, String split) {
		if(CollectionUtils.isEmpty(source)){
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < source.size(); i++) {
			if(StringUtils.isBlank(source.get(i))){
				continue;
			}
			if(sb.length() > 0){
				sb.append(split);
			}
			sb.append(source.get(i));
		}
		return sb.toString();
		
	}
    
    
    /**
	 * 指定分隔符把集合转换成字符串
	 * @param source
	 * @param split
	 * @return
	 */
    public static String convertList2QuoString(Collection<String> source, String split) {
		if(CollectionUtils.isEmpty(source)){
			return StringUtils.EMPTY;
		}
		String splitSign = StringUtils.isBlank(split) ? SignConstant.SIGN_COMMA : split;
		StringBuilder sb = new StringBuilder();
		for (String s : source) {
			if(StringUtils.isBlank(s)){
				continue;
			}
			if(sb.length() > 0){
				sb.append(splitSign);
			}
			sb.append(SignConstant.SIGN_SINGLE_QUO).append(s).append(SignConstant.SIGN_SINGLE_QUO);
		}
		return sb.toString();
		
	}
    
    /**
     * 数组转换为字符串
     * @param arr 数组
     * @param delim 分隔符
     * @param nullPosition 空值的占位符
     * @return
     */
    public static String arrayToDelimitedString(Object[] arr, String delim,String nullPosition) {
		if (ArrayUtils.isEmpty(arr)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			if(null == arr[i]){
				sb.append(nullPosition);
				continue;
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	public static List<Integer> getYearListByStringRegion(String startYear, String endYear) {
		List<Integer> yearList = new ArrayList<Integer>();
		for(int year = Integer.valueOf(startYear); year <= Integer.valueOf(endYear); year++){
			yearList.add(year);
		}
		return yearList;
	}

	/**
	 * 
	 * @param symbol
	 * @param regionSign
	 * @param spliteSign
	 * @return
	 */
	public static List<String> getListSymbolByRegionAndSingle(String symbol,String regionSign, String spliteSign) {
		String[] symbolRegions = symbol.split(spliteSign);
		if(ArrayUtils.isEmpty(symbolRegions)){
			return new ArrayList<String>();
		}
		List<String> symbolList = new ArrayList<String>();
		for(String symbolRegion : symbolRegions){
			if(symbolRegion.indexOf(regionSign) < 0){
				symbolList.add(symbolRegion);
				continue;
			}
			String[] startAndEndSymbol = symbolRegion.split(regionSign);
			if(StringUtils.isBlank(startAndEndSymbol[0]) || StringUtils.isBlank(startAndEndSymbol[1])){
				if(StringUtils.isBlank(startAndEndSymbol[0])){
					symbolList.add(startAndEndSymbol[0]);
				}
				if(StringUtils.isBlank(startAndEndSymbol[1])){
					symbolList.add(startAndEndSymbol[1]);
				}
				continue;
			}
			
//			hc_201 -- hc_204
			int indexOfUnderscore = startAndEndSymbol[0].indexOf(SignConstant.SIGN_UNDERSCORE);
			if(indexOfUnderscore > 0){
				
				String prefix = startAndEndSymbol[0].substring(0,indexOfUnderscore+1);
				String startSymbol = startAndEndSymbol[0].substring(indexOfUnderscore + 1);
				String endSymbol = startAndEndSymbol[1].substring(indexOfUnderscore + 1);
				for(Integer startS = Integer.valueOf(startSymbol); startS <= Integer.valueOf(endSymbol); startS ++){
					symbolList.add(prefix+""+startS.toString());
				}
				continue;
			}
			for(Integer startSimple = Integer.valueOf(startAndEndSymbol[0]); startSimple <= Integer.valueOf(startAndEndSymbol[1]); startSimple ++){
				symbolList.add(startSimple.toString());
			}
		}
		return symbolList;
	}

	//拼字符串
	public static String appends(Object... fields) {
		if (ArrayUtils.isEmpty(fields)) return StringUtils.EMPTY;
		StringBuilder sb = new StringBuilder();
		for (Object f : fields) {
			sb.append(f);
		}
		
		return sb.toString();
	}
}
