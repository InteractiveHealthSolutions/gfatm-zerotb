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
import java.io.File;
import java.io.FileOutputStream;
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
import javax.servlet.http.HttpSession;

import com.ihsinformatics.util.VersionUtil;
import com.itextpdf.text.pdf.qrcode.GF256;

/**
 * Servlet implementation class QrGenerator
 */
/**
 * @author Haris Asif - haris.asif@ihsinformatics.com
 *
 */
public class QrGeneratorServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -875504184015776771L;
	VersionUtil version = new VersionUtil(false, true, false, 0, 9, 1);
	
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
		
		boolean checkDigit = (request.getParameter("checkdigitBox") != null) ? true
				: false;
		String typeSelection = request.getParameter("typeSelection");

		String appendDate = request.getParameter("appendDate");
		String fileType = request.getParameter("fileType");
		int range = 0;
		String prefix = request.getParameter("prefix");
		int length = Integer.parseInt(request.getParameter("serialNumberList"));
		int copiesImage = Integer.parseInt(request.getParameter("copiesList"));
		int columnLimit = Integer.parseInt(request.getParameter("column"));
		Date date = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateFormat = request.getParameter("dateFormatList");
		String partialDate = request.getParameter("date");
		String pageType = request.getParameter("pagetype");
		String pageOrientation = request.getParameter("pageorientation");
		boolean alphanumeric = (request.getParameter("alphanumeric") == null) ? false
				: true;
		boolean casesensitive = (request.getParameter("casesensitive") == null) ? false
				: true;
		String Stringrange = request.getParameter("rangeForRandom");
		property.load(propFile);

		String url = property.getProperty(PropertyName.CONNECTION_URL);
		String dbName = property.getProperty(PropertyName.DB_NAME);
		String driverName = property.getProperty(PropertyName.JDBC_DRIVER);
		String userName = property.getProperty(PropertyName.USERNAME);
		String password = property.getProperty(PropertyName.PASSWORD);

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
						allowDuplicates, checkDigit);
			} else if (prefix != null && appendDate == null) {
				numberList = numberGenerator.generateSerial(prefix, length,
						from, to, allowDuplicates, checkDigit);
			} else {
				numberList = numberGenerator.generateSerial(prefix, length,
						from, to, date, dateFormat, allowDuplicates, checkDigit);
			}
		} else {
			range = Integer.parseInt(Stringrange);
			if (prefix == null && appendDate == null) {
				try {
					numberList = numberGenerator.generateRandom(length, range,
							alphanumeric, casesensitive, checkDigit);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (prefix != null && appendDate == null) {
				try {
					numberList = numberGenerator.generateRandom(prefix, length,
							range, alphanumeric, casesensitive, checkDigit);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				numberList = numberGenerator.generateRandom(prefix, length,
						range, date, dateFormat, alphanumeric, casesensitive, checkDigit);
			}
		}
		

		if (numberList != null) {

			PdfUtil pdfUtil = new PdfUtil();
			
			String fileName = "QrCode" + String.valueOf(new Date().getTime());
			
			if(fileType.equals("pdfButton")){
				fileName+=".pdf";
				byteArrayOutputStream = pdfUtil.generatePdf(numberList, 140, 140,
						copiesImage, columnLimit,pageType,pageOrientation);
			}
			else{
				fileName+=".zip";
				byteArrayOutputStream = ZipUtil.getZip(numberList, 140, 140);
			}

			
			if (numberList.size() != range && typeSelection.equals("random")) {

				String rootPath = System.getProperty("user.dir");
				File directory = new File(rootPath + File.separator + "webapps"
						+ File.separator + "gf-qrgen-web" + File.separator
						+ "QrGeneratorFiles");
				if (!directory.exists()) {
					directory.mkdir();
				}

				FileOutputStream fileOutputStream = new FileOutputStream(
						directory.toString() + File.separator + fileName);
				fileOutputStream.write(byteArrayOutputStream.toByteArray());
				fileOutputStream.close();

				String link = request.getRequestURI();
				link = link.substring(0, 13) + File.separator
						+ "QrGeneratorFiles" + File.separator + fileName;
				String status = "Warning!! System was able to generate "
						+ numberList.size() + " QR Codes only.";
				ServletContext sc = this.getServletContext();
				RequestDispatcher rd = sc.getRequestDispatcher("/");
				request.setAttribute("linkDownload", link);
				request.setAttribute("errorMsg", status);
				request.setAttribute("linkTitle", "Download QR Codes File");
				rd.forward(request, response);
				return;
			}

			else {
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control",
						"must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "public");
				if(fileType.equals("pdfButton"))
					response.setContentType("application/pdf");
				else
					response.setContentType("application/zip");
				
				response.setHeader("Content-Disposition",
						"attachment; filename=" + fileName);
				OutputStream os = response.getOutputStream();
				byteArrayOutputStream.writeTo(os);
				os.flush();
				os.close();
			}
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
