/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.qrgenerator;

import java.awt.Color;
import java.util.Properties;
import java.sql.*;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.Hashtable;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Servlet implementation class QrGenerator
 */
/**
 * @author Haris Asif - haris.asif@ihsinformatics.com
 *
 */
public class QrGeneratorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	int length = 0;
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	PdfPTable table;
	Document document = new Document();
	int count = 0;
	String dateFormat = "";
	String partialDate = "";
	DateFormat df = null;
	Connection connection = null;
	Date date1 = null;
	int copiesImage = 1;
	int columnLimit = 1;
	DateFormat dateFo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	boolean isNotDuplicate = false;
	boolean allowDuplicates = false;
	Date dbDate = new Date();
	Properties property;

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

		String serialFormat = "%02d";
		dbDate = new Date();
		count = 0;
		document = new Document();
		document.setMargins(90f, 0, 30f, 30f);

		NumberGenerator numberGenerator = new NumberGenerator(this);
		DatabaseUtil databaseUtil = new DatabaseUtil();

		connection = databaseUtil.connectDatabase();
		databaseUtil.checkTable(connection);

		String typeSelection = request.getParameter("typeSelection");
		String duplicate = request.getParameter("duplicates");
		String from = request.getParameter("from");
		String to = request.getParameter("to");

		String locationText = request.getParameter("prefix");
		String limit = request.getParameter("serialNumberList");
		copiesImage = Integer.parseInt(request.getParameter("copiesList"));
		columnLimit = Integer.parseInt(request.getParameter("column"));

		boolean dateCheck = false;
		String checkBoxValue = request.getParameter("appendDate");

		if ("on".equals(checkBoxValue)) {
			dateFormat = request.getParameter("dateFormatList");
			partialDate = request.getParameter("date");
			df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				date1 = df.parse(partialDate);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			dateCheck = true;
		}

		try {
			PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		if (typeSelection.equals("serial")) {

			table = new PdfPTable(columnLimit);
			table.setTotalWidth(100f);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);

			ArrayList<String> serialList = new ArrayList<String>();
			serialList.add(serialFormat);
			serialList.add(limit);
			serialList.add(duplicate);
			serialList.add(from);
			serialList.add(to);
			serialList.add(dateFormat);
			serialList.add(locationText);
			boolean action = false;

			action = numberGenerator.generateSerial(serialList, dateCheck,
					date1, connection);

			if (action) {
				downloadFile(response);
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

		else {

			String alphanumeric = request.getParameter("alphanumeric");
			String casesensitive = request.getParameter("casesensitive");
			String initialRange = request.getParameter("rangeForRandom");
			boolean action = false;
			ArrayList<String> randomList = new ArrayList<String>();
			randomList.add(dateFormat);
			randomList.add(locationText);
			randomList.add(alphanumeric);
			randomList.add(casesensitive);
			randomList.add(initialRange);
			randomList.add(limit);
			int randomRange = Integer.parseInt(initialRange);

			action = numberGenerator.generateRandom(randomList, dateCheck,
					date1, connection);

			if (action) {
				downloadFile(response);
			}

			else {
				String status = "All combinations already created or cannot create "
						+ randomRange
						+ " unique combinations using these values. Please choose different values.";
				ServletContext sc = this.getServletContext();
				RequestDispatcher rd = sc.getRequestDispatcher("/");
				request.setAttribute("errorMsg", status);
				rd.forward(request, response);
				return;
			}

		}

	}

	/**
	 * This function sends pdf downloadable file back to client
	 * @param response is HttpServletResponse passed by doPost
	 * @throws IOException
	 */
	public void downloadFile(HttpServletResponse response) throws IOException {
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ "QrCode" + ".pdf");

		try {
			for (int i = 0; i < 6; i++) {

				if (count % columnLimit != 0) {
					PdfPCell cell = new PdfPCell(new Phrase());
					cell.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell);
					count++;
				}
			}

			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		document.close();
		OutputStream os = response.getOutputStream();
		byteArrayOutputStream.writeTo(os);
		os.flush();
		os.close();
	}
	
	

	/**
	 * This function creates qrcode image so that server can send back to client
	 * @param qrCodeText is qrText passed by generateNumber function
	 * @param width is image width
	 * @param height is image height
	 * @throws IOException
	 * @throws WriterException
	 * @throws DocumentException
	 */
	public void createQRImage(String qrCodeText, int width, int height)
			throws IOException, WriterException, DocumentException {
		

		if (qrCodeText.length() > 0 && qrCodeText.length() <= 5) {
			length = 57;
		}

		else if (qrCodeText.length() >= 6 && qrCodeText.length() <= 9) {
			length = 47;
		}

		else if (qrCodeText.length() >= 10 && qrCodeText.length() <= 11) {
			length = 39;
		}

		else if (qrCodeText.length() >= 12 && qrCodeText.length() <= 14) {
			length = 30;
		}

		else if (qrCodeText.length() >= 15 && qrCodeText.length() <= 17) {
			length = 25;
		}

		else {
			length = 18;
		}

		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
				BarcodeFormat.QR_CODE, width, height, hintMap);

		int matrixWidth = byteMatrix.getWidth();
		int matrixHeight = byteMatrix.getHeight();
		BufferedImage image = new BufferedImage(matrixWidth, matrixHeight,
				BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth + 5, matrixHeight + 5);

		graphics.setFont(graphics.getFont().deriveFont(10f));

		graphics.setColor(Color.BLACK);
		graphics.drawString(qrCodeText, length, height - 10);

		for (int i = 0; i < matrixHeight; i++) {
			for (int j = 0; j < matrixHeight; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect((i), j, 1, 1);
				}
			}
		}

		Image itextImage = Image.getInstance(Toolkit.getDefaultToolkit()
				.createImage(image.getSource()), null);

		for (int i = 0; i < copiesImage; i++) {
			PdfPCell cell = new PdfPCell(itextImage);
			cell.setBorder(Rectangle.NO_BORDER);
			count++;
			table.addCell(cell);
		}
	}

}
