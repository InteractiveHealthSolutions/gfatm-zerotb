package com.ihsinformatics.qrgenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ihsinformatics.qrgenerator.StringUtil;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.media.sound.InvalidFormatException;

/**
 * Servlet implementation class QrGenerator
 */
@WebServlet("/qrgenerator")
public class QrGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	int length = 0;
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	PdfPTable table;
	Document document = new Document();
	int count = 0;
	boolean 

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QrGenerator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String a = request.getParameter("prefix");
		System.out.println(a);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String serialFormat = "%02d";
		count = 0;
		document = new Document();
		document.setMargins(90f, 0, 30f, 30f);
		
		boolean numeric = true;
		boolean alphaNu = false;
		boolean caseSe = false;
		
		int width = 140;
		int height = 140;
		String typeSelection = request.getParameter("typeSelection");

		table.setTotalWidth(100f);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		String locationText = request.getParameter("prefix");
		String limit = request.getParameter("serialNumberList");
		int range = Integer.parseInt(limit);
		int copiesImage = Integer.parseInt(request.getParameter("copiesList"));
		int columnLimit = Integer.parseInt(request.getParameter("column"));
		
		boolean dateCheck = false;	
		String checkBoxValue = request.getParameter("appendDate");

		if ("on".equals(checkBoxValue)) {
			String dateFormat = request.getParameter("dateFormatList");
			String partialDate = request.getParameter("date");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = null;
			try {
				date1 = df.parse(partialDate);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			dateCheck = true;
		}
		
		
	    response.setHeader("Expires", "0");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ "QrCode" + ".pdf");

		try {
			PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		if(typeSelection.equals("serial")){
			
			char old = serialFormat.charAt(2);
			serialFormat = serialFormat.replace(old, limit.charAt(0));
			
			int from = Integer.parseInt(request.getParameter("from"));
			int to = Integer.parseInt(request.getParameter("to"));

			table = new PdfPTable(columnLimit);
			
			
			if (dateCheck) {
				String date = formatDate(dateFormat, date1);
				for (int i = from; i <= to; i++) {
					try {
						String qrCodeText = locationText + date
								+ String.format(serialFormat, i);
						qrCodeText += "-" + calculateLuhnDigit(qrCodeText);
						createQRImage(qrCodeText, width, height, response);
						System.out.println(qrCodeText + "\t");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				for (int i = from; i <= to; i++) {
					try {
						String qrCodeText = locationText
								+ String.format(serialFormat, i);
						qrCodeText += "-" + calculateLuhnDigit(qrCodeText);
						createQRImage(qrCodeText, width, height, response);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			
		}
		
		else {
			
			String alphanumeric = request.getParameter("alphanumeric");
			String casesensitive = request.getParameter("casesensitive");
			int randomRange = Integer.parseInt(request.getParameter("random"));
			
			if(alphanumeric.equals("on")){
				numeric = false;
				alphaNu = true;
				
				if(casesensitive.equals("on")){
					caseSe = true
				}
			}
			
			
			String qrCode = StringUtil.randomString(range, numeric, alphaNu, caseSe);
		}
		

		try {
			for (int i = 0; i < 6 ; i++) {
				
				if (count % columnLimit != 0) {
					PdfPCell cell = new PdfPCell(new Phrase());
					cell.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell);
					count++;
				}
			}

			document.add(table);
			// document.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		document.close();
		OutputStream os = response.getOutputStream();
		byteArrayOutputStream.writeTo(os);
		os.flush();
		os.close();
	}

	private String formatDate(String format, Date date) {

		return new SimpleDateFormat(format).format(date);
	}

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

	public void createQRImage(String qrCodeText, int width, int height,
			HttpServletResponse response) throws IOException, WriterException,
			DocumentException {

		if (qrCodeText.length() > 0 && qrCodeText.length() <= 9) {
			length = 47;
		}

		else if (qrCodeText.length() >= 10 && qrCodeText.length() <= 11) {
			length = 39;
		}

		else if (qrCodeText.length() >= 12 && qrCodeText.length() <= 14) {
			length = 34;
		}

		else if (qrCodeText.length() >= 15 && qrCodeText.length() <= 17) {
			length = 27;
		}

		else {
			length = 18;
		}

		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
				BarcodeFormat.QR_CODE, width, height, hintMap);

		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		int matrixHeight = byteMatrix.getHeight();
		BufferedImage image = new BufferedImage(matrixWidth, matrixHeight,
				BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth + 5, matrixHeight + 5);

		graphics.setFont(graphics.getFont().deriveFont(10f));

		double codeRepeatation = qrCodeText.length() / 11.0;

		graphics.setColor(Color.BLACK);
		graphics.drawString(qrCodeText, length, height - 10);

		for (int i = 0; i < matrixHeight; i++) {
			for (int j = 0; j < matrixHeight; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect((i), j, 1, 1);
				}
			}

		}

		int width1 = 1000;
		int height1 = 1000;
		Rectangle pageSize = new Rectangle(width1, height1);

		Image itextImage = Image.getInstance(Toolkit.getDefaultToolkit()
				.createImage(image.getSource()), null);
		
		for(int i=0 ; i < copiesImage ; i++ ){
			PdfPCell cell = new PdfPCell(itextImage);
			cell.setBorder(Rectangle.NO_BORDER);
			count++;
			table.addCell(cell);
		}
	}

}
