package io.zino.mystore.utile.security;

import java.io.UnsupportedEncodingException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class cryptographyUtil {

	final static Logger logger = LogManager.getLogger(cryptographyUtil.class);
	final static private int RSA_KEY_SIZE = 512;
	private static KeyPair keyPair;

	private static void generateTheUniqueKeys() throws NoSuchAlgorithmException {
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(RSA_KEY_SIZE);
			cryptographyUtil.keyPair = keyGenerator.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			cryptographyUtil.generateTheUniqueKeys();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
	}

	public static PublicKey getPublicKey() {
		return cryptographyUtil.keyPair.getPublic();
	}

	private static String EncryptRSA(String data, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return new String(cipher.doFinal(data.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (BadPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

	public static String SignByOwnedKeyAndEncryptWithGivenKey(String data, Key key) {
		data = cryptographyUtil.EncryptRSA(data, key);
		return cryptographyUtil.EncryptRSA(data, cryptographyUtil.keyPair.getPrivate());
	}

	public static String DecryptByGivenKeyANDDecryptByOwnedKey(String data, Key key) {
		data = cryptographyUtil.DecryptRSA(data, key);
		return cryptographyUtil.DecryptRSA(data, cryptographyUtil.keyPair.getPrivate());
	}

	private static String DecryptRSA(String encryptedData, Key key) {
		try {
			Cipher decrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decrypt.init(Cipher.DECRYPT_MODE, key);
			return new String(decrypt.doFinal(encryptedData.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (BadPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

	public static SecretKey generateAESKey(){
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256); 
			return keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	private static String EncryptAES(String data, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return new String(cipher.doFinal(data.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (BadPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

	
	private static String DecryptAES(String encryptedData, Key key) {
		try {
			Cipher decrypt = Cipher.getInstance("AES");
			decrypt.init(Cipher.DECRYPT_MODE, key);
			return new String(decrypt.doFinal(encryptedData.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (BadPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

	
	public static void main(String[] args) {
		String data = "Hello there";
		System.out.println(data.length()+":: "+data);
		
		SecretKey aeskey = generateAESKey();
		
		String encrypData = new String(EncryptAES(data, aeskey));
		System.out.println(encrypData.length()+":: "+encrypData);
		String decrypData = new String(DecryptAES(encrypData, aeskey));
		System.out.println(decrypData);
	}

}
