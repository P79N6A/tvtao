package com.aliyun.base.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final String ENCODE_CHARSET = "UTF-8"; // 编码字符集
	private static final String MESSAGEDIGEST_TYPE = "MD5"; // 签名协议

	public static String getSubStr(String s, int length) throws Exception {
		return getSubStr(s, length, true);
	}

	public static String getSubStr(String s, int length, Boolean ellipsis) throws Exception {

		byte[] bytes = s.getBytes("Unicode");
		int n = 0; // 表示当前的字节数
		int i = 2; // 要截取的字节数，从第3个字节开始
		for (; i < bytes.length && n < length; i++) {
			// 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
			if (i % 2 != 0) {
				n++; // 在UCS2第二个字节时n加1
			} else {
				// 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
				if (bytes[i] != 0) {
					n++;
				}
			}
		}
		// 如果i为奇数时，处理成偶数
		if (i % 2 != 0)

		{
			// 该UCS2字符是汉字时，去掉这个截一半的汉字
			if (bytes[i - 1] != 0)
				i = i - 1;
			// 该UCS2字符是字母或数字，则保留该字符
			else
				i = i + 1;
		}
		String result = new String(bytes, 0, i, "Unicode");
		if (strLength(s) > length && ellipsis == true) {
			result += "...";
		}
		return result;
	}

	public static int strLength(String value) {
		double valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < value.length(); i++) {
			// 获取一个字符
			String temp = value.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				// 中文字符长度为1
				valueLength += 1;
			} else {
				// 其他字符长度为0.5
				valueLength += 0.5;
			}
		}
		// 进位取整
		return (int) Math.ceil(valueLength) * 2;
	}

	/**
	 * 将字符串转为字节,然后进行md5加密,返回加密后的16进制数组
	 * 
	 * @param source
	 *            要加密的字符串
	 * @return
	 */
	public static String md5Hex(String source) {
		if (source == null) {
			return null;
		}

		byte[] bytes = null;
		try {
			bytes = source.getBytes(ENCODE_CHARSET);
			return md5Hex(bytes);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	/**
	 * 对字节进行MD5加密,返回加密后的16进制数组
	 * 
	 * @param bytes
	 *            加密的字节
	 * @return
	 */
	public static String md5Hex(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		return String.valueOf(encode2Hex(encode2MD5(bytes)));
	}

	/**
	 * 计算二进制数组MD5签名
	 * 
	 * @return
	 */
	public static byte[] encode2MD5(byte[] source) {
		byte[] result = new byte[0];
		try {
			MessageDigest md = MessageDigest.getInstance(MESSAGEDIGEST_TYPE);
			md.update(source);

			// MD5 的计算结果是一个 128 位的长整数，用字节表示就是 16 个字节
			result = md.digest();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 将字节数组转为16进制字符<br>
	 * 实现细节:<br>
	 * 1.每个byte转成2个char字符<br>
	 * 2.先将byte与0xF0做与操作，得到高四位，右移四位，高位补0，得到高四位的字符<br>
	 * 3.再将byte与0x0F做与操作,得到低四位
	 * 
	 */
	private static char[] encode2Hex(byte[] data) {
		int len = data.length;
		char[] out = new char[len << 1];
		for (int i = 0, j = 0; i < len; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}

	/**
	 * 比较两个字符串是否相同,两个都为null则认为是相同的
	 * 
	 * @param compareFrom
	 * @param compareTo
	 * @return
	 */
	public static boolean isEqual(String compareFrom, String compareTo) {
		// 同一个字符串或者都为null
		if (compareFrom == compareTo) {
			return true;
		}
		// 一个null,一个不null
		if (compareFrom == null || compareTo == null) {
			return false;
		}
		// 两个都不null
		return compareFrom.equals(compareTo);
	}

	/**
	 * 比较两个字符串数组是否相同
	 * 
	 * @param comparesFrom
	 * @param comparesTo
	 * @return
	 */
	public static boolean isArrEqual(String[] comparesFrom, String[] comparesTo) {

		// 两个都为null或者是同一个对象
		if (comparesFrom == comparesTo) {
			return true;
		}

		if (comparesFrom == null || comparesTo == null) {
			return false;
		}

		if (comparesFrom.length != comparesFrom.length) {
			return false;
		}

		for (int i = 0; i < comparesFrom.length; i++) {
			if (!isEqual(comparesFrom[i], comparesTo[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumeric(String input) {
		if (isEmpty(input)) {
			return false;
		}

		for (int i = 0; i < input.length(); i++) {
			char charAt = input.charAt(i);

			if (!Character.isDigit(charAt)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumeric2(String str) {
		if (isEmpty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static boolean isEmpty(String input) {
		return (input == null || input.length() == 0);
	}

	/**
	 * Android 根据屏幕大小设置字体
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 * @return
	 */
	public static int adjustFontSize(int screenWidth, int screenHeight) {
		if (screenWidth <= 240) { // 240X320 屏幕
			return 10;
		} else if (screenWidth <= 320) { // 320X480 屏幕
			return 14;
		} else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
			return 24;
		} else if (screenWidth <= 540) { // 540X960 屏幕
			return 26;
		} else if (screenWidth <= 800) { // 800X1280 屏幕
			return 30;
		} else { // 大于 800X1280
			return 30;
		}
	}

	// =====================判断时间是否正确格式

	public static boolean checkDate(String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = df.parse(date);
		} catch (Exception e) {
			// 如果不能转换,肯定是错误格式
			return false;
		}
		String s1 = df.format(d);
		// 转换后的日期再转换回String,如果不等,逻辑错误.如format为"yyyy-MM-dd",date为
		// "2006-02-31",转换为日期后再转换回字符串为"2006-03-03",说明格式虽然对,但日期
		// 逻辑上不对.
		return date.equals(s1);
	}

	// =====================判断时间是否正确格式

	// =====================判断邮件email是否正确格式

	public boolean checkEmail(String email) {

		Pattern pattern = Pattern.compile("^/w+([-.]/w+)*@/w+([-]/w+)*/.(/w+([-]/w+)*/.)*[a-z]{2,3}$");
		Matcher matcher = pattern.matcher(email);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	// =====================判断邮件email是否正确格式

	// =====================判断手机号phone是否正确格式

	public boolean checkPhone(String phone) {
		Pattern pattern = Pattern.compile("^13/d{9}||15[8,9]/d{8}$");
		Matcher matcher = pattern.matcher(phone);

		if (matcher.matches()) {
			return true;
		}
		return false;
	}

}
