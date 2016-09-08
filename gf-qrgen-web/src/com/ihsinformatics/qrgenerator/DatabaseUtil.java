/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.qrgenerator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.mysql.jdbc.DatabaseMetaData;

public class DatabaseUtil {
	
	Properties property;
	Statement stmt = null;
	Connection connection = null;
	DateFormat dateFo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date dbDate = new Date();
	
	/**
	 * This function inserts qrcode in the database
	 * @param qrCode is qrcode which needs to be inserted in the database
	 * @param connection is database connection
	 * @return
	 */
	public boolean insertQrCode(String qrCode, Connection connection) {
		try {
			stmt = connection.createStatement();
			String sql = "insert into _identifier values('" + qrCode + "','"
					+ dateFo.format(dbDate) + "');";

			stmt.executeUpdate(sql);
			
		} catch (SQLException e2) {
			return false;
		}

		return true;
	}

	/**This function deletes qrcode from the database
	 * @param connection is database connection
	 */
	public void deleteQrCode(Connection connection) {
		try {
			stmt = connection.createStatement();
			String sql = "delete from _identifier where qr_dateTime='"
					+ dateFo.format(dbDate) + "';";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This function creates database connection
	 * @return
	 */
	public Connection connectDatabase() {
		property = new Properties();
		InputStream propFile = QrGeneratorServlet.class
				.getResourceAsStream("/qrgenerator.properties");
		try {
			property.load(propFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			Class.forName(property.getProperty(PropertyName.JDBC_DRIVER));
			Connection connection = DriverManager.getConnection(property
					.getProperty(PropertyName.QRGENERATOR_CONNECTION_URL),
					property.getProperty(PropertyName.QRGENERATOR_USER),
					property.getProperty(PropertyName.QRGENERATOR_PASSWORD));
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This function checks if table exists in database or not. If table doesnt exist. It creates the table 
	 * @param connection
	 */
	public void checkTable(Connection connection) {
		try {
			DatabaseMetaData databaseMetaData = (DatabaseMetaData) connection
					.getMetaData();
			ResultSet tables = databaseMetaData.getTables(null, null,
					"_identifier", null);
			if (tables.next()) {
			} else {
				stmt = connection.createStatement();
				String sql = "CREATE TABLE _identifier"
						+ "(qrcode VARCHAR(255)not NULL UNIQUE, "
						+ " qr_dateTime DATETIME not NULL)";
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
	}
}
