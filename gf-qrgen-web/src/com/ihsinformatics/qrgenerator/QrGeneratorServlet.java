/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.qrgenerator;

import java.util.Properties;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class QrGenerator
 */
/**
 * @author Haris Asif - haris.asif@ihsinformatics.com
 *
 */
public class QrGeneratorServlet extends HttpServlet {
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QrGeneratorServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/qrgenerator.jsp").forward(
				request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Properties property = new Properties();
		InputStream propFile = QrGeneratorServlet.class
				.getResourceAsStream("/qrgenerator.properties");
		boolean allowDuplicates = (request.getParameter("duplicates") != null) ? true
				: false;
		String typeSelection = request.getParameter("typeSelection");

		String appendDate = request.getParameter("appendDate");
		int range = 0;
		String prefix = request.getParameter("prefix");
		int length = Integer.parseInt(request.getParameter("serialNumberList"));
		int copiesImage = Integer.parseInt(request.getParameter("copiesList"));
		int columnLimit = Integer.parseInt(request.getParameter("column"));
		Date date = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateFormat = request.getParameter("dateFormatList");
		String partialDate = request.getParameter("date");
		boolean alphanumeric = (request.getParameter("alphanumeric") == null) ? false
				: true;
		boolean casesensitive = (request.getParameter("casesensitive") == null) ? false
				: true;
		String Stringrange = request.getParameter("rangeForRandom");
		property.load(propFile);

		String url = property
				.getProperty(PropertyName.CONNECTION_URL);
		String dbName = property.getProperty(PropertyName.DB_NAME);
		String driverName = property.getProperty(PropertyName.JDBC_DRIVER);
		String userName = property.getProperty(PropertyName.USERNAME);
		String password = property
				.getProperty(PropertyName.PASSWORD);

		NumberGenerator numberGenerator = new NumberGenerator(url, dbName,
				driverName, userName, password);
		List<String> numberList = new ArrayList<String>();

		if (partialDate != null) {
			try {
				date = df.parse(partialDate);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}

		if (typeSelection.equals("serial")) {

			int from = Integer.parseInt(request.getParameter("from"));
			int to = Integer.parseInt(request.getParameter("to"));

			if (prefix == null && appendDate == null) {
				numberList = numberGenerator.generateSerial(length, from, to,
						allowDuplicates);
			}

			else if (prefix != null && appendDate == null) {
				numberList = numberGenerator.generateSerial(prefix, length,
						from, to, allowDuplicates);
			}

			else {
				numberList = numberGenerator.generateSerial(prefix, length,
						from, to, date, dateFormat, allowDuplicates);
			}

		}

		else {

		 range = Integer.parseInt(Stringrange);

			if (prefix == null && appendDate == null) {
				try {
					numberList = numberGenerator.generateRandom(length, range,
							alphanumeric, casesensitive);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (prefix != null && appendDate == null) {
				try {
					numberList = numberGenerator.generateRandom(prefix, length,
							range, alphanumeric, casesensitive);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else {
				numberList = numberGenerator.generateRandom(prefix, length,
						range, date, dateFormat, alphanumeric, casesensitive);
			}

		}

		if (numberList != null) {
			PdfUtil pdfUtil = new PdfUtil();
			byteArrayOutputStream = pdfUtil.generatePdf(numberList, 140, 140,
					copiesImage, columnLimit);

			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ "QrCode" + ".pdf");
			
			
//			if(numberList.size() != range){
//				String status = "Warning!! System was able to generate " + numberList.size() + " qrcodes.";
//				request.setAttribute("errorMsg", status);
//			}
//			
//			else {
//				
//			}

			OutputStream os = response.getOutputStream();
			byteArrayOutputStream.writeTo(os);
			os.flush();
			os.close();
		}

		else {
			String status = "Duplicate exists. Use Different Values.";
			ServletContext sc = this.getServletContext();
			RequestDispatcher rd = sc.getRequestDispatcher("/");
			request.setAttribute("errorMsg", status);
			rd.forward(request, response);
			return;
		}

	}

}
