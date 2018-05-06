package io.zino.mystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigMgr {
	private static ConfigMgr instance = new ConfigMgr();
	private Properties prop; 
	
	public static ConfigMgr getInstance() {
		return instance;
	}
	
	private ConfigMgr() {
		this.prop = new Properties();
		try {
			File file = new File("config");
			if(file.exists()){
				prop.load(new FileInputStream(file));
			}else{
				prop.load(this.getClass().getResourceAsStream("config"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("ConfigMgr Started! " + System.currentTimeMillis());
	}
	
	public String get(String key){
		return this.prop.getProperty(key);
	}
}