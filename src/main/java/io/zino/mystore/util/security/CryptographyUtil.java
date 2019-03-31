package io.zino.mystore.util.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptographyUtil {

	final static Logger logger = LoggerFactory.getLogger(CryptographyUtil.class);
	final static private int RSA_KEY_SIZE = 1024;
	private static KeyPair keyPair;

	private static void generateTheUniqueKeys() throws NoSuchAlgorithmException {
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(RSA_KEY_SIZE);
			CryptographyUtil.keyPair = keyGenerator.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			CryptographyUtil.generateTheUniqueKeys();
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating TheUniqueKeys",e);
		}
	}

	public static PublicKey getPublicKey() {
		return CryptographyUtil.keyPair.getPublic();
	}
	
	public static byte[] EncryptRSA(byte[] data, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("Exception while encrypting", e);
		}
		return null;
	}

	public static byte[] SignByOwnedKeyAndEncryptWithGivenKey(byte[] data, Key key) {
		data = CryptographyUtil.EncryptRSA(data, key);
		return CryptographyUtil.EncryptRSA(data, CryptographyUtil.keyPair.getPrivate());
	}

	public static byte[] DecryptByGivenKeyANDDecryptByOwnedKey(byte[] data, Key key) {
		data = CryptographyUtil.DecryptRSA(data, key);
		return CryptographyUtil.DecryptRSA(data, CryptographyUtil.keyPair.getPrivate());
	}

	public static byte[] DecryptRSA(byte[] encryptedData) {
		return CryptographyUtil.DecryptRSA(encryptedData, CryptographyUtil.keyPair.getPrivate());
	}
	
	public static byte[] DecryptRSA(byte[] encryptedData, Key key) {
		try {
			Cipher decrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decrypt.init(Cipher.DECRYPT_MODE, key);
			return decrypt.doFinal(encryptedData);
		} catch (Exception e) {
			logger.error("Exception while decrypting",e);
		} 
		return null;
	}

	public static SecretKey generateDESKey() {
		try {
			return KeyGenerator.getInstance("DES").generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String EncryptDES(String str, Key key) {
		try {
			Cipher ecipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] utf8 = str.getBytes("UTF8");
			byte[] enc = ecipher.doFinal(utf8);
			enc = BASE64EncoderStream.encode(enc);

			return new String(enc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String DecryptDES(String str, Key key) {
		try {
			Cipher dcipher = Cipher.getInstance("DES");
			dcipher.init(Cipher.DECRYPT_MODE, key);
			byte[] dec = BASE64DecoderStream.decode(str.getBytes());
			byte[] utf8 = dcipher.doFinal(dec);
			return new String(utf8, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static void main(String[] args) {
//		String data = "Hello there 304";
//		System.out.println(data.length() + ":: " + data);
//
//		SecretKey aeskey = generateDESKey();
//
//		String encrypData = new String(EncryptDES(data, aeskey));
//		System.out.println(encrypData.length() + ":: " + encrypData);
//		String decrypData = new String(DecryptDES(encrypData, aeskey));
//		System.out.println(decrypData.length() + ":: " +decrypData);
//	}
	
//	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
//		keyGenerator.initialize(RSA_KEY_SIZE);
//		KeyPair outKeyPair = keyGenerator.generateKeyPair();
//		
//		// create new key
//		SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();
//		String test= "hello Word!";
//		System.out.println("test:: "+test);
//		test = EncryptDES(test, secretKey);
//		
//		// get base64 encoded version of the key
//		byte[] data = secretKey.getEncoded();
//	
//		System.out.println(data.length + ":: " + data);
//		byte[] encrypData = EncryptRSA(data, outKeyPair.getPublic());
//		System.out.println(encrypData.length + ":: " + encrypData);
//		byte[] decrypData = DecryptRSA(encrypData, outKeyPair.getPrivate());
//		System.out.println(decrypData.length + ":: " +decrypData);
//		
//		// rebuild key using SecretKeySpec
//		SecretKey originalKey = new SecretKeySpec(decrypData, 0, decrypData.length, "DES"); 
//		
//		test = DecryptDES(test, originalKey);
//		System.out.println("test:: "+test);
//	}

}
