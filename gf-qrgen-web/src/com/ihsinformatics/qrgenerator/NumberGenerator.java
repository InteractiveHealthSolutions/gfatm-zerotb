/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.qrgenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ihsinformatics.util.ChecksumUtil;
import com.ihsinformatics.util.CommandType;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;
import com.ihsinformatics.util.StringUtil;

/**
 * @author Haris Asif - haris.asif@ihsinformatics.com
 *
 */
public class NumberGenerator {

	DatabaseUtil dbUtil;

	/**
	 * Default Constructor
	 */
	public NumberGenerator(String url, String dbName, String driverName,
			String userName, String password) {
		dbUtil = new DatabaseUtil();
		dbUtil.setConnection(url, dbName, driverName, userName, password);
	}

	public List<String> generateSerial(int length, int rangeFrom, int rangeTo,
			boolean allowDuplicates, boolean allowCheckDigit) {
		return generateSerial(null, length, rangeFrom, rangeTo,
				allowDuplicates, allowCheckDigit);
	}

	public List<String> generateSerial(String prefix, int length,
			int rangeFrom, int rangeTo, boolean allowDuplicates,
			boolean allowCheckDigit) {
		return generateSerial(prefix, length, rangeFrom, rangeTo, null, null,
				allowDuplicates, allowCheckDigit);
	}

	/**
	 * This function generates Strings in a series
	 * 
	 * @param prefix
	 * @param length
	 * @param rangeFrom
	 * @param rangeTo
	 * @param hasDate
	 * @param date
	 * @return
	 */
	public List<String> generateSerial(String prefix, int length,
			int rangeFrom, int rangeTo, Date date, String dateFormat,
			boolean allowDuplicates, boolean allowCheckDigit) {
		List<String> codes = new ArrayList<String>();
		String serialFormat = "%0" + String.valueOf(length) + "d";
		String datePart = "";
		ResultSet rs = null;
		String prefixPart = "";
		if (date != null) {
			datePart = DateTimeUtil.formatDate(date, dateFormat);
		}
		if (prefix != null) {
			prefixPart = prefix;
		}

		String start = prefixPart + datePart
				+ String.format(serialFormat, rangeFrom);
		String end = prefixPart + datePart
				+ String.format(serialFormat, rangeTo);

		String que = "select * from _identifier where id BETWEEN '" + start
				+ "%' AND '" + end + "%';";
		try {
			Object res = dbUtil.runCommand(CommandType.SELECT, que);
			if (allowDuplicates == false && !res.equals("false")) {
				return null;
			}

		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		for (int i = rangeFrom; i <= rangeTo; i++) {
			String newCode = prefixPart + datePart
					+ String.format(serialFormat, i);
			try {
				if (allowCheckDigit == true) {
					newCode += "-" + ChecksumUtil.getLuhnChecksum(newCode);
				}
				String query = "insert into _identifier values ('" + newCode
						+ "', current_timestamp())";
				dbUtil.runCommand(CommandType.INSERT, query);
				codes.add(newCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return codes;
	}

	
	public List<String> generateRandom(int length, int range,
			boolean alphanumeric, boolean casesensitive, boolean allowCheckDigit)
			throws Exception {
		return generateRandom(null, length, range, null, null, alphanumeric,
				casesensitive, allowCheckDigit);
	}

	public List<String> generateRandom(String prefix, int length, int range,
			boolean alphanumeric, boolean casesensitive, boolean allowCheckDigit)
			throws Exception {
		return generateRandom(prefix, length, range, null, null, alphanumeric,
				casesensitive, allowCheckDigit);
	}

	public List<String> generateRandom(String prefix, int length, int range,
			Date date, String dateFormat, boolean alphanumeric,
			boolean caseSensitive, boolean allowCheckDigit) {

		String prefixPart = "";
		String newCode = "";
		String datePart = "";
		int countValue = 0;

		List<String> codes = new ArrayList<String>();

		if (prefix != null) {
			prefixPart = prefix;
		}

		if (date != null) {
			datePart = DateTimeUtil.formatDate(date, dateFormat);
		}

		Double failRange = Math.pow(length, length);
		int totalAttemps = failRange.intValue();
		StringUtil strUtil = new StringUtil();
		while (codes.size() != range) {
			if (countValue <= totalAttemps) {

				newCode = prefixPart
						+ datePart
						+ strUtil.randomString(length, true, alphanumeric,
								caseSensitive);
				try {

					if (allowCheckDigit == true) {
						newCode += "-" + ChecksumUtil.getLuhnChecksum(newCode);
					}
					String query = "insert into _identifier values ('"
							+ newCode + "', current_timestamp())";
					Object result = dbUtil
							.runCommand(CommandType.INSERT, query);
					if (!result.toString().equals("1")) {
						countValue++;
					}

					else {
						codes.add(newCode);
						countValue = 0;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (codes.size() > 0) {
				return codes;
			}

			else {
				return null;
			}
		}
		return codes;
	}
}
