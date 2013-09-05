package com.bnsoft.cafe_cmd_server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Config {
//	private static Logger log = Logger.getLogger(Config.class.getName());
	public static final String PROPERTIES_FILE = "server.properties";
	
	public static int PORT = 8800;
	public static int UPDATE_PORT = PORT+1;
	public static String APP_DB_NAME = "goods.xls";
	
	static {
		Properties props = new Properties();
		try {
			File propsFile = new File(PROPERTIES_FILE);
			if (!propsFile.exists()) {
				try {
					propsFile.createNewFile();
					props.setProperty("PORT", ""+PORT);
					props.setProperty("UPDATE-PORT", ""+(PORT+1));
					props.setProperty("APP-DB-NAME", APP_DB_NAME);
					
					FileOutputStream propsFSout = new FileOutputStream(propsFile);
					props.store(propsFSout, "BNSoft Cafe Server configuration file");
					propsFSout.flush();
					propsFSout.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			FileInputStream propsFS = new FileInputStream(propsFile);
			props.load(propsFS);
			
			PORT				=	Integer.parseInt(props.getProperty("PORT"));
			UPDATE_PORT			=	Integer.parseInt(props.getProperty("UPDATE-PORT"));
			APP_DB_NAME			=	props.getProperty("APP-DB-NAME");
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
