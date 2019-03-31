package io.zino.mystore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Class ConfigMgr.
 */
final public class ConfigMgr {
	
	/** The Constant logger. */
	final static Logger logger = LoggerFactory.getLogger(ConfigMgr.class);
	
	/** The instance. */
	private static ConfigMgr instance = new ConfigMgr();
	
	/** The prop. */
	private Properties prop;
	
	/** The embedded prop. */
	private Properties embeddedProp; 
	
	/**
	 * Gets the single instance of ConfigMgr.
	 *
	 * @return single instance of ConfigMgr
	 */
	public static ConfigMgr getInstance() {
		return instance;
	}
	
	/**
	 * Instantiates a new config mgr.
	 * load prop from config file.
	 * if config file exist next to jar file, then use it
	 * otherwise use built-in config file.
	 * System.exit on any IOException.
	 */
	private ConfigMgr() {
		this.prop = new Properties();
		this.embeddedProp = new Properties();
		try {
			File file = new File("config");
			if(file.exists()){
				prop.load(new FileInputStream(file));
			}
			
			embeddedProp.load(this.getClass().getResourceAsStream("config"));
		
		} catch (IOException e) {
			logger.error("Error on loading config", e);
			System.exit(-1);
		} 
		
		System.out.println("ConfigMgr Started! " + System.currentTimeMillis());
	}
	
	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String get(String key){
		String property = this.prop.getProperty(key);
		return property==null ? this.embeddedProp.getProperty(key) : property;
	}
}
