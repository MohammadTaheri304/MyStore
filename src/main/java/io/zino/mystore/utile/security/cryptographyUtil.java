package io.zino.mystore.utile.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class cryptographyUtil {

	final static Logger logger = LogManager.getLogger(cryptographyUtil.class);

	private static KeyPair keyPair;

	private static void generateTheUniqueKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
		keyGenerator.initialize((int) System.currentTimeMillis());
		cryptographyUtil.keyPair = keyGenerator.generateKeyPair();
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
			Cipher encrypt = Cipher.getInstance("RSA");
			encrypt.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedMessage = encrypt.doFinal(data.getBytes());
			return new String(encryptedMessage);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		} catch (NoSuchPaddingException e) {
			logger.error(e);
		} catch (InvalidKeyException e) {
			logger.error(e);
		} catch (IllegalBlockSizeException e) {
			logger.error(e);
		} catch (BadPaddingException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static String Encrypt(String data) {
		return cryptographyUtil.Encrypt(data, cryptographyUtil.keyPair.getPrivate());
	}

	public static String Sign(String data) {
		return cryptographyUtil.Encrypt(data, cryptographyUtil.keyPair.getPublic());
	}

	public static String Decrypt(String encryptedData, Key key){
		try {
			Cipher decrypt = Cipher.getInstance("RSA");
			decrypt.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedMessage=decrypt.doFinal(encryptedData.getBytes());
			return new String(decryptedMessage);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		} catch (NoSuchPaddingException e) {
			logger.error(e);
		} catch (InvalidKeyException e) {
			logger.error(e);
		} catch (IllegalBlockSizeException e) {
			logger.error(e);
		} catch (BadPaddingException e) {
			logger.error(e);
		}
		return null;
	}

	public static String Decrypt(String encryptedData){
		return cryptographyUtil.Decrypt(encryptedData, cryptographyUtil.keyPair.getPrivate());
	}
	
}
