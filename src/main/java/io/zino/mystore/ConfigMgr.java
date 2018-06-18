package io.zino.mystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class ConfigMgr.
 */
final public class ConfigMgr {
	
	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(ConfigMgr.class);
	
	/** The instance. */
	private static ConfigMgr instance = new ConfigMgr();
	
	/** The prop. */
	private Properties prop; 
	
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
		try {
			File file = new File("config");
			if(file.exists()){
				prop.load(new FileInputStream(file));
			}else{
				prop.load(this.getClass().getResourceAsStream("config"));
			}
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
		return this.prop.getProperty(key);
	}
}
