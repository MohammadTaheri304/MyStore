package io.zino.mystore.utile.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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

	public static String Encrypt(String data, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
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

	public static String Encrypt(String data) {
		return cryptographyUtil.Encrypt(data, cryptographyUtil.keyPair.getPrivate());
	}

	public static String SignByOwnedKeyAndEncryptWithGivenKey(String data, Key key) {
		data = cryptographyUtil.Encrypt(data, key);
		return cryptographyUtil.Encrypt(data, cryptographyUtil.keyPair.getPrivate());
	}

	public static String DecryptByGivenKeyANDDecryptByOwnedKey(String data, Key key) {
		data = cryptographyUtil.Decrypt(data, key);
		return cryptographyUtil.Decrypt(data, cryptographyUtil.keyPair.getPrivate());
	}

	public static String Decrypt(String encryptedData, Key key) {
		try {
			Cipher decrypt = Cipher.getInstance("RSA");
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
			e.printStackTrace();
		}
		return null;
	}

	public static String Decrypt(String encryptedData) {
		return cryptographyUtil.Decrypt(encryptedData, cryptographyUtil.keyPair.getPrivate());
	}

	public static void main(String[] args) {
		String data = "Hello there";
		System.out.println(data);
		String encrypData = new String(SignByOwnedKeyAndEncryptWithGivenKey(data, getPublicKey()));
		System.out.println(encrypData);
		String decrypData = new String(DecryptByGivenKeyANDDecryptByOwnedKey(encrypData, getPublicKey()));
		System.out.println(decrypData);
	}

}
