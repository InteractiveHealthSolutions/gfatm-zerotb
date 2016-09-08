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
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import com.sun.media.sound.InvalidFormatException;

/**
 * @author Haris Asif - haris.asif@ihsinformatics.com
 *
 */
public class NumberGenerator {

	DatabaseUtil datebaseUtil = new DatabaseUtil();
	QrGeneratorServlet qrGenerator;
	String date = "";
	String qrCodeText = "";
	String qrText = "";
	int width = 140;
	int height = 140;
	boolean isDuplicate = false;

	/**
	 * Parameterized Constructor
	 * @param qrGeneratorServlet is object passed QrGeneratorServelet
	 */
	public NumberGenerator(QrGeneratorServlet qrGeneratorServlet) {
		this.qrGenerator = qrGeneratorServlet;
	}

	/**
	 * Default Constructor
	 */
	public NumberGenerator() {
 
	}

	/**
	 * This function creates qrcode serialized version
	 * @param serialList is the serial number format
	 * @param dateCheck is the parameter to check if date needs to be append
	 * @param date1 is date that appends in qrcode text
	 * @param connection is database connection for communicating with the database
	 * @return
	 */
	public boolean generateSerial(ArrayList<String> serialList,
			boolean dateCheck, Date date1, Connection connection) {

		String serialFormat = serialList.get(0);
		int width = 140;
		int height = 140;
		String limit = serialList.get(1);
		String duplicate = serialList.get(2);
		boolean allowDuplicates = false;
		int from = Integer.parseInt(serialList.get(3));
		int to = Integer.parseInt(serialList.get(4));
		String dateFormat = serialList.get(5);
		String locationText = serialList.get(6);
		int tableCount = 1;

		char old = serialFormat.charAt(2);
		serialFormat = serialFormat.replace(old, limit.charAt(0));

		if (duplicate == null) {
			allowDuplicates = false;
		} else {
			allowDuplicates = true;
		}

		if (dateCheck) {
			date = formatDate(dateFormat, date1);
			qrText = locationText + date;
		}

		else {
			qrText = locationText;
		}

		for (int i = from; i <= to; i++) {
			try {
				qrCodeText = qrText + String.format(serialFormat, i);
				qrCodeText += "-" + calculateLuhnDigit(qrCodeText);
				isDuplicate = datebaseUtil.insertQrCode(qrCodeText, connection);

				if (!allowDuplicates) {
					if (isDuplicate) {
						qrGenerator.createQRImage(qrCodeText, width, height);
					}

					else {
						datebaseUtil.deleteQrCode(connection);
						return false;
					}
				}

				else {
					datebaseUtil.insertQrCode(qrCodeText, connection);
					qrGenerator.createQRImage(qrCodeText, width, height);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * This function creates random qrcode
	 * @param randomList is array list which has required values for random qr code 
	 * @param dateCheck is used to check if user needs date in qrcode
	 * @param date1 is date that needs to be appended
	 * @param connection is database connection
	 * @return
	 * @throws IOException
	 */
	public boolean generateRandom(ArrayList<String> randomList,
			boolean dateCheck, Date date1, Connection connection)
			throws IOException {

		String dateFormat = randomList.get(0);
		String locationText = randomList.get(1);
		String alphanumeric = randomList.get(2);
		String casesensitive = randomList.get(3);
		String initialRange = randomList.get(4);
		String limit = randomList.get(5);

		int range = Integer.parseInt(limit);
		boolean alphaNu = false;
		boolean caseSe = false;
		HashSet<String> qrCollection = new HashSet<String>();
		int countValue = 0;
		int randomRange = Integer.parseInt(initialRange);

		if (alphanumeric != null) {
			alphaNu = true;

			if (casesensitive != null) {
				caseSe = true;
			}
		}

		if (dateCheck) {
			date = formatDate(dateFormat, date1);
			qrText = locationText + date;
		}

		else {
			qrText = locationText;
		}

		while (qrCollection.size() != randomRange) {
			if (countValue <= 20) {

				qrCodeText = qrText
						+ StringUtil.randomString(range, true, alphaNu, caseSe);
				qrCodeText += "-" + calculateLuhnDigit(qrCodeText);

				isDuplicate = !(datebaseUtil.insertQrCode(qrCodeText,
						connection));

				if (!isDuplicate) {
					try {
						qrGenerator.createQRImage(qrCodeText, width, height);
						qrCollection.add(qrCodeText);
						countValue = 0;
					} catch (WriterException | DocumentException e) {
						e.printStackTrace();
					}
				}

				else {
					countValue++;
				}
			}

			else {
				datebaseUtil.deleteQrCode(connection);
				return false;
			}
		}

		return true;
	}

	/**
	 * @param format is the format that user selected on interface
	 * @param date is date user selected
	 * @return
	 */
	private String formatDate(String format, Date date) {

		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * This function calculates luhn digit which is appended in qrcode
	 * @param idWithoutCheckdigit
	 * @return
	 * @throws InvalidFormatException
	 */
	public static int calculateLuhnDigit(String idWithoutCheckdigit)
			throws InvalidFormatException {
		String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVYWXZ_-";
		idWithoutCheckdigit = idWithoutCheckdigit.trim().toUpperCase();
		int sum = 0;
		for (int i = 0; i < idWithoutCheckdigit.length(); i++) {
			char ch = idWithoutCheckdigit.charAt(idWithoutCheckdigit.length()
					- i - 1);
			if (validChars.indexOf(ch) == -1)
				throw new InvalidFormatException("\"" + ch
						+ "\" is an invalid character");
			int digit = (int) ch - 48;
			int weight;
			if (i % 2 == 0) {
				weight = (2 * digit) - (int) (digit / 5) * 9;
			} else {
				weight = digit;
			}
			sum += weight;
		}
		sum = Math.abs(sum) + 10;
		return (10 - (sum % 10)) % 10;

	}
}
