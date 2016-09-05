package com.ihsinformatics.qrgenerator;

import java.awt.Color;
import java.util.Properties;
import java.sql.*;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
public class QrGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	int length = 0;
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	PdfPTable table;
	Document document = new Document();
	int count = 0;
	String dateFormat = "";
	Statement stmt = null;
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
	public QrGenerator() {
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
		HashSet<String> qrCollection = new HashSet<String>();
		dbDate = new Date();
		count = 0;
		document = new Document();
		document.setMargins(90f, 0, 30f, 30f);

		boolean numeric = true;
		boolean alphaNu = false;
		boolean success = true;
		boolean caseSe = false;

		connection = connectDatabase();

		int width = 140;
		int height = 140;
		String typeSelection = request.getParameter("typeSelection");

		String locationText = request.getParameter("prefix");
		String limit = request.getParameter("serialNumberList");
		int range = Integer.parseInt(limit);
		copiesImage = Integer.parseInt(request.getParameter("copiesList"));
		columnLimit = Integer.parseInt(request.getParameter("column"));

		table = new PdfPTable(columnLimit);
		table.setTotalWidth(100f);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);

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

			char old = serialFormat.charAt(2);
			serialFormat = serialFormat.replace(old, limit.charAt(0));
			String duplicate = request.getParameter("duplicates");

			if (duplicate == null) {
				allowDuplicates = false;
			} else {
				allowDuplicates = true;
			}

			int from = Integer.parseInt(request.getParameter("from"));
			int to = Integer.parseInt(request.getParameter("to"));

			if (dateCheck) {
				String date = formatDate(dateFormat, date1);
				for (int i = from; i <= to; i++) {
					try {
						String qrCodeText = locationText + date
								+ String.format(serialFormat, i);
						qrCodeText += "-" + calculateLuhnDigit(qrCodeText);
						isNotDuplicate = insertQrCode(qrCodeText);

						if (!allowDuplicates) {
							if (isNotDuplicate) {
								createQRImage(qrCodeText, width, height);
							}

							else {
								success = false;
								deleteQrCode();
								String status = "Duplicate exists. Use Different Values.";
								ServletContext sc = this.getServletContext();
								RequestDispatcher rd = sc
										.getRequestDispatcher("/");
								request.setAttribute("errorMsg", status);
								rd.forward(request, response);
								return;
							}
						}

						else {
							insertQrCode(qrCodeText);
							createQRImage(qrCodeText, width, height);
						}

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
						isNotDuplicate = insertQrCode(qrCodeText);

						if (!allowDuplicates) {
							if (isNotDuplicate) {
								createQRImage(qrCodeText, width, height);
							}

							else {
								success = false;
								deleteQrCode();
								String status = "Duplicate exists. Use Different Values.";
								ServletContext sc = this.getServletContext();
								RequestDispatcher rd = sc
										.getRequestDispatcher("/");
								request.setAttribute("errorMsg", status);
								rd.forward(request, response);
								return;
							}
						}

						else {
							insertQrCode(qrCodeText);
							createQRImage(qrCodeText, width, height);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

		}

		else {

			String alphanumeric = request.getParameter("alphanumeric");
			String casesensitive = request.getParameter("casesensitive");
			String initialRange = request.getParameter("rangeForRandom");
			int breakValue = 0;
			int mulValue = 0;
			int countValue = 0;
			int randomRange = Integer.parseInt(initialRange);

			if (alphanumeric != null && casesensitive != null) {
				breakValue = 62;
				mulValue = 62;
			}

			else if (alphanumeric != null) {
				breakValue = 32;
				mulValue = 32;
			}

			else {
				breakValue = 10;
				mulValue = 10;
			}

			for (int i = 1; i < randomRange; i++) {
				breakValue *= mulValue;
			}

			if (alphanumeric != null) {
				alphaNu = true;

				if (casesensitive != null) {
					caseSe = true;
				}
			}

			if (dateCheck) {
				String date = formatDate(dateFormat, date1);
				while (qrCollection.size() != randomRange) {
					if (countValue < breakValue) {
						String qrCode = locationText + date;
						qrCode += StringUtil.randomString(range, numeric,
								alphaNu, caseSe);
						qrCode += "-" + calculateLuhnDigit(qrCode);

						isNotDuplicate = insertQrCode(qrCode);

						if (isNotDuplicate) {
							try {
								createQRImage(qrCode, width, height);
								qrCollection.add(qrCode);
								countValue++;
							} catch (WriterException | DocumentException e) {
								e.printStackTrace();
							}
						}
					}

					else {
						deleteQrCode();
						success = false;
						String status = "All combinations already created. Choose different values.";
						ServletContext sc = this.getServletContext();
						RequestDispatcher rd = sc.getRequestDispatcher("/");
						request.setAttribute("errorMsg", status);
						rd.forward(request, response);
						return;
					}
				}
			}

			else {
				while (qrCollection.size() != randomRange) {
					if (countValue < breakValue) {
						String qrCode = locationText;
						qrCode += StringUtil.randomString(range, numeric,
								alphaNu, caseSe);
						qrCode += "-" + calculateLuhnDigit(qrCode);

						isNotDuplicate = insertQrCode(qrCode);

						if (isNotDuplicate) {
							try {
								createQRImage(qrCode, width, height);
								qrCollection.add(qrCode);
								countValue++;
							} catch (WriterException | DocumentException e) {
								e.printStackTrace();
							}
						}
					}

					else {
						deleteQrCode();
						success = false;
						String status = "All combinations already created. Choose different values.";
						ServletContext sc = this.getServletContext();
						RequestDispatcher rd = sc.getRequestDispatcher("/");
						request.setAttribute("errorMsg", status);
						rd.forward(request, response);
						return;
					}
				}
			}
		}

		if (success) {

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
				stmt.close();
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

	public boolean insertQrCode(String qrCode) {
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

	public void deleteQrCode() {
		try {
			stmt = connection.createStatement();
			String sql = "delete from _identifier where qr_dateTime='"
					+ dateFo.format(dbDate) + "';";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Connection connectDatabase() {
		property = new Properties();
		InputStream propFile = QrGenerator.class
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

		for (int i = 0; i < copiesImage; i++) {
			PdfPCell cell = new PdfPCell(itextImage);
			cell.setBorder(Rectangle.NO_BORDER);
			count++;
			table.addCell(cell);
		}
	}

}
