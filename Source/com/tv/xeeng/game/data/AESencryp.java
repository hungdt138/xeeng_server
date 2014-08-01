package com.tv.xeeng.game.data;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESencryp {

	private static final String ALGO = "AES";
	/*private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B',
			'e', 's', 't','S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };*/
	private static final byte[] keyValue = "9jW4Mv0Bqm3f6eC2".getBytes();
	public static String encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = new String(Base64.encodeBase64(encVal));
		return encryptedValue;
	}

	public static String decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = Base64.decodeBase64(encryptedData.getBytes());
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}

}
