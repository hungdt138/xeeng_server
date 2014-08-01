package com.tv.xeeng.game.data;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;


public class AES {

	public static String encrypt(String[] input) {
		StringBuilder dataToSign = new StringBuilder();
		for(String s : input){
			dataToSign.append(s+"&");
		}
		if(dataToSign.length() >0) dataToSign.deleteCharAt(dataToSign.length()-1);
		String sig = "";
		System.out.println(dataToSign);
		final byte[] rawKey = "9jW4nv0Bqm3z6eCd".getBytes();
		final SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
		try {
			final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding",
					"SunJCE");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			final byte[] encrypted = cipher.doFinal(dataToSign.toString().getBytes());
			sig = new String(Base64.encodeBase64(encrypted));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(sig);
		return sig;
	}
	public static String encrypt(String input) {
		
		String sig = "";
		//System.out.println(input);
		final byte[] rawKey = "9jW4nv0Bqm3z6eCd".getBytes();
		final SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
		try {
			final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding",
					"SunJCE");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			final byte[] encrypted = cipher.doFinal(input.getBytes());
			sig = new String(Base64.encodeBase64(encrypted));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		//System.out.println(sig);
		return sig;
	}
}
