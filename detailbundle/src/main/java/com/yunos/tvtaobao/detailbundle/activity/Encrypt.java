package com.yunos.tvtaobao.detailbundle.activity;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 数据加密类 支持SHA-1,SHA-2(SHA-256,SHA-384,SHA-512),MD2,MD5加密。.java加密方式.
 * 
 * @author mty
 * @time 2012-3-2下午5:32:51
 */
public class Encrypt {

	private Encrypt() {
	}

	/**
	 * MD2加密算法
	 *
	 * @param plantText 需要加密的字符串
	 * @return String型 加密后的字符串
	 */
	public static String GetMD2EncString(String plantText) {
		return GetEncString(plantText, "MD2");
	}

	/**
	 * MD5加密算法
	 *
	 * @param plantText 需要加密的字符串
	 * @return String型 加密后的字符串
	 */
	public static String GetMD5EncString(String plantText) {
		return GetEncString(plantText, "MD5");
	}

	/**
	 * SHA-1加密算法
	 *
	 * @param plantText 需要加密的字符串
	 * @return String型 加密后的字符串
	 */
	public static String GetSHA1EncString(String plantText) {
		return GetEncString(plantText, "SHA-1");
	}

	/**
	 * SHA-256加密算法
	 *
	 * @param plantText 需要加密的字符串
	 * @return String型 加密后的字符串
	 */
	public static String GetSHA256EncString(String plantText) {
		return GetEncString(plantText, "SHA-256");
	}

	/**
	 * SHA-384加密算法
	 *
	 * @param plantText 需要加密的字符串
	 * @return String型 加密后的字符串
	 */
	public static String GetSHA384EncString(String plantText) {
		return GetEncString(plantText, "SHA-384");
	}

	/**
	 * SHA-512加密算法
	 *
	 * @param plantText 需要加密的字符串
	 * @return String型 加密后的字符串
	 */
	public static String GetSHA512EncString(String plantText) {
		return GetEncString(plantText, "SHA-512");
	}

	/**
	 * 数据加密算法
	 *
	 * @param plainText 加密字符串
	 * @param algorithm 加密算法，支持SHA-1,SHA-2(SHA-256,SHA-384,SHA-512),MD2,MD5
	 * @return 加密结果
	 */
	private static String GetEncString(String plainText, String algorithm) {
		if (plainText == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(plainText.getBytes());
			byte[] b = md.digest();
			StringBuilder output = new StringBuilder(32);
			for (int i = 0; i < b.length; i++) {
				String temp = Integer.toHexString(b[i] & 0xff);
				if (temp.length() < 2) {
					output.append("0");// 不足两位，补0
				}
				output.append(temp);
			}
			return output.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static String decryptAESCipherText(String cipherText, String encryptionKey) {
		//NOTICE: both of URL_SAFE and NO_WRAP mode are used to encrypt URL data
		byte[] cipherBytes = Base64.decode(cipherText, Base64.URL_SAFE | Base64.NO_WRAP);
		byte[] plainBytes = decryptAES(cipherBytes, encryptionKey);
		if (plainBytes != null) {
			return new String(plainBytes);
		}
		return null;
	}

	private static byte[] decryptAES(byte[] cipherBytes, String encryptionKey) {
		Cipher cipher;
		SecretKeySpec key;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(encryptionKey.getBytes("UTF-8")));
			return cipher.doFinal(cipherBytes);
		} catch (Exception e) {
			return null;
		}
	}
}