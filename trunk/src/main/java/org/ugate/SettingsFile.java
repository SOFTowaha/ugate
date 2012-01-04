package org.ugate;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

/**
 * Settings are used to store simple key/value pair data to disk. The file approach is taken
 * versus the typical {@link java.util.prefs.Preferences} so they can be readily accessible for
 * manual editing when needed.
 */
public class SettingsFile {

	private static final Logger log = Logger.getLogger(SettingsFile.class);
	public static final String ENCRYPTION_TYPE = "AES";
	public static final String ENCRYPTION_POSTFIX = ".encrypted";
	private final Properties properties;
	private final String fileName;
	private SecretKeySpec skeySpec;
	private boolean isLoaded = false;

	/**
	 * Settings file that can be used to store simple key/value pairs (generated when non-existent)
	 * 
	 * @param fileName the file name
	 * @param createIfNotExists true to create the settings file when it doesn't exist
	 */
	public SettingsFile(final String fileName, final boolean createIfNotExists) {
		this.fileName = fileName;
		this.properties = new Properties();
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(ENCRYPTION_TYPE);
			kgen.init(128);
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			this.skeySpec = new SecretKeySpec(raw, ENCRYPTION_TYPE);
		} catch (NoSuchAlgorithmException e) {
			log.warn(e);
		}
		final String filePath = fileName + ".properties";
		try {
			this.properties.load(new FileInputStream(filePath));
			this.isLoaded = true;
		} catch (FileNotFoundException e) {
			if (!createIfNotExists) {
				log.debug(String.format("Unable to find settings file: %1$s", filePath));
				return;
			}
	        BufferedWriter out = null;
			try {
				out = new BufferedWriter(new FileWriter(filePath));
		        out.write("");
			} catch (final IOException e2) {
				throw new RuntimeException(String.format("Unable to create settings file %1$s", filePath), e2);
			} finally {
				if (out != null) {
					try {
						out.close();
						this.properties.load(new FileInputStream(filePath));
						this.isLoaded = true;
					} catch (final IOException e3) {
						log.error(String.format("Unable to load settings file %1$s", filePath), e3);
					}
				}
			}
		} catch (IOException e) {
			log.error("Unable to retrieve/create settings file", e);
		}
	}
	
	/**
	 * @return true when the settings is loaded
	 */
	public boolean isLoaded() {
		return this.isLoaded;
	}
	
	/**
	 * @param key the key to check for
	 * @return true when the key exists
	 */
	public boolean hasKey(final String key) {
		return properties.containsKey(key);
	}
	
	/**
	 * Gets a list of settings values by key using a delimiter
	 * 
	 * @param key the settings key
	 * @param delimiter the delimiter for the multi-value preference
	 * @return the list of values
	 */
	public List<String> get(final String key, final String delimiter) {
		final String value = get(key);
		return value != null ? Arrays.asList(value.split(delimiter)) : new ArrayList<String>();
	}

	/**
	 * Gets a settings value by key. If the key does not exist it will be created
	 * 
	 * @param key the key
	 * @return the value
	 */
	public String get(final String key) {
		if (hasKey(key)) {
			return properties.getProperty(key);
		}
		String value = "";
		String eValue = properties.getProperty(key + ENCRYPTION_POSTFIX);
		if (eValue != null) {
			try {
				value = new String(encryptDecrypt(eValue, Cipher.DECRYPT_MODE));
			} catch (Exception e) {
				throw new RuntimeException("Unable to encrypt", e);
			}
		}
		return value;
	}

	/**
	 * Sets a key/value settings
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void set(final String key, final String value) {
		set(key, value, false);
	}
	
	/**
	 * Sets a a key/value settings
	 * 
	 * @param key the key
	 * @param value the value
	 * @param encrypt true to encrypt the value
	 */
	private void set(String key, String value, final boolean encrypt) {
		try {
			if (encrypt) {
				try {
					value = asHex(encryptDecrypt(value, Cipher.ENCRYPT_MODE));
					key += ENCRYPTION_POSTFIX;
				} catch (Exception e) {
					throw new RuntimeException("Unable to encrypt", e);
				}
			}
			properties.setProperty(key, value);
			properties.store(new FileOutputStream(fileName + ".properties"),
					null);
		} catch (IOException e) {
			log.error("Unable to save settings file", e);
		}
	}

	private byte[] encryptDecrypt(final String value, final int encryptDecryptMode) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(ENCRYPTION_TYPE);
		cipher.init(encryptDecryptMode, skeySpec);
		return cipher.doFinal(value.getBytes());
	}

	/**
	 * Turns array of bytes into string
	 * 
	 * @param buf
	 *            array of bytes to convert to hex string
	 * @return generated hex string
	 */
	private static String asHex(final byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}
}