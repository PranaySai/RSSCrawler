package com.feeds.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBConnection {

	final static Logger logger = Logger.getLogger(DBConnection.class);

	public static Connection getConnection() {
		Properties props = new Properties();
		FileInputStream fis = null;
		Connection con = null;
		try {
			// Get file from resources folder
			ClassLoader classLoader = DBConnection.class.getClassLoader();
			File file = new File(classLoader.getResource("db.properties")
					.getFile());
			fis = new FileInputStream(file);
			props.load(fis);

			// load the Driver Class
			Class.forName(props.getProperty("DB_DRIVER_CLASS"));

			// create the connection now
			con = DriverManager.getConnection(props.getProperty("DB_URL"),
					props.getProperty("DB_USERNAME"),
					props.getProperty("DB_PASSWORD"));
			logger.debug("Successfull opened connection "
					+ props.getProperty("DB_URL"));
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return con;
	}
	
	 public static Connection getConnection(String propertiesFileName) {
			Properties props = new Properties();
			FileInputStream fis = null;
			Connection con = null;
			try {
				// Get file from resources folder
				ClassLoader classLoader = DBConnection.class.getClassLoader();
				File file = new File(classLoader.getResource(propertiesFileName)
						.getFile());
				fis = new FileInputStream(file);
				props.load(fis);

				// load the Driver Class
				Class.forName(props.getProperty("DB_DRIVER_CLASS"));

				// create the connection now
				con = DriverManager.getConnection(props.getProperty("DB_URL"),
						props.getProperty("DB_USERNAME"),
						props.getProperty("DB_PASSWORD"));
				logger.debug("Successfull opened connection "
						+ props.getProperty("DB_URL"));
			} catch (IOException e) {
				logger.error(e.getMessage());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
			return con;
		}

}