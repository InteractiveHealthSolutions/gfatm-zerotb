/**
 * 
 */
package com.ihsinformatics.qrgenerator;

import java.util.Hashtable;
import java.util.List;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author Haris
 *
 */
public class PdfUtil {

	private static final float[] MARGINS = { 90f, 0, 30f, 30f };
	private static final float TABLE_WIDTH = 100f;

	public ByteArrayOutputStream generatePdf(List<String> data, int width,
			int height, int copiesImage, int columnLimit) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Document document = new Document();
		document.setMargins(MARGINS[0], MARGINS[1], MARGINS[2], MARGINS[3]);
		try {
			PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();
		} catch (DocumentException e2) {
			e2.printStackTrace();
		}

		PdfPTable table = new PdfPTable(columnLimit);
		table.setTotalWidth(TABLE_WIDTH);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);

		int length = 0;
		int count = 0;

		for (String str : data) {
			if (str.length() > 0 && str.length() <= 5) {
				length = 57;
			} else if (str.length() >= 6 && str.length() <= 9) {
				length = 47;
			} else if (str.length() >= 10 && str.length() <= 11) {
				length = 39;
			} else if (str.length() >= 12 && str.length() <= 14) {
				length = 30;
			} else if (str.length() >= 15 && str.length() <= 17) {
				length = 25;
			} else {
				length = 18;
			}
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = null;
			try {
				byteMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE,
						width, height, hintMap);
			} catch (WriterException e1) {
				e1.printStackTrace();
			}
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
			graphics.drawString(str, length, height - 10);
			for (int i = 0; i < matrixHeight; i++) {
				for (int j = 0; j < matrixHeight; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect((i), j, 1, 1);
					}
				}
			}
			Image itextImage = null;
			try {
				itextImage = Image.getInstance(Toolkit.getDefaultToolkit()
						.createImage(image.getSource()), null);
			} catch (BadElementException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < copiesImage; i++) {
				PdfPCell cell = new PdfPCell(itextImage);
				cell.setBorder(Rectangle.NO_BORDER);
				count++;
				table.addCell(cell);
			}
		}
		for (int i = 0; i < 6; i++) {
			if (count % columnLimit != 0) {
				PdfPCell cell = new PdfPCell(new Phrase());
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);
				count++;
			}
		}
		try {
			document.add(table);
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return byteArrayOutputStream;
	}
}