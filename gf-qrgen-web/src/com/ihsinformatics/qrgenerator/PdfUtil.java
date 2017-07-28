/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.qrgenerator;

import java.util.List;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/*
 import com.itextpdf.text.BadElementException;
 import java.awt.Color;
 import java.awt.Graphics2D;
 import java.awt.Toolkit;
 import java.awt.image.BufferedImage;
 import java.io.IOException;
 import java.util.Hashtable;
 import com.google.zxing.BarcodeFormat;
 import com.google.zxing.EncodeHintType;
 import com.google.zxing.WriterException;
 import com.google.zxing.common.BitMatrix;
 import com.google.zxing.qrcode.QRCodeWriter;
 import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
 */

/**
 * @author Haris Asif - haris.asif@ihsinformatics.com
 *
 */
public class PdfUtil {

	// private static final float[] A4MARGINS = { 0f, 0, 0f, 0f };
	int selectedColumnCount = 0;
	int maxColumns = 0;

	public ByteArrayOutputStream generatePdf(List<String> data, int width,
			int height, int copiesImage, int columnLimit, String pageType,
			String orientation) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Document document = new Document();
		PdfPTable table = new PdfPTable(6);

		if (pageType.equals("A4")) {
			if (orientation.equals("portrait")) {
				maxColumns = 6;
				document.setPageSize(PageSize.A4);
				table = new PdfPTable(maxColumns);
				table.setTotalWidth(PageSize.A4.getWidth());
				document.setMargins(2, -148, 20, 2);
			} else {
				maxColumns = 8;
				document.setPageSize(PageSize.A4.rotate());
				table = new PdfPTable(maxColumns);
				table.setTotalWidth(PageSize.A4.rotate().getWidth());
				document.setMargins(2, -200, 29, 20);
			}
		}
		try {
			PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();
		} catch (DocumentException e2) {
			e2.printStackTrace();
		}

		table.setHorizontalAlignment(Element.ALIGN_LEFT);

		int length = 0;
		int count = 0;

		for (String str : data) {
			if (str.length() > 0 && str.length() <= 5) {
				length = 58;
			} else if (str.length() >= 6 && str.length() <= 9) {
				length = 38;
			} else if (str.length() >= 10 && str.length() <= 11) {
				length = 36;
			} else if (str.length() >= 12 && str.length() <= 14) {
				length = 27;
			} else if (str.length() >= 15 && str.length() <= 17) {
				length = 22;
			} else {
				length = 15;
			}

			Image itextImage = QrCodeImageUtil.getImageForPdf(str, width,
					height, length);
			for (int i = 0; i < copiesImage; i++) {
				selectedColumnCount++;
				PdfPCell cell = new PdfPCell(itextImage);
				cell.setBorder(Rectangle.NO_BORDER);
				count++;
				table.addCell(cell);
			}

			if (selectedColumnCount == columnLimit) {
				for (int i = columnLimit; i < maxColumns; i++) {
					PdfPCell cell = new PdfPCell(new Phrase());
					cell.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell);
				}
				selectedColumnCount = 0;
			}
		}
		for (int i = 0; i < 6; i++) {
			if (count % maxColumns != 0) {
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

		catch (Exception e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream;
	}
}