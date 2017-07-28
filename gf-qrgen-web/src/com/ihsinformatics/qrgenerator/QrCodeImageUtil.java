package com.ihsinformatics.qrgenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.Image;

public class QrCodeImageUtil {

	public static BufferedImage drawQrImage(String str, int width, int height,
			int length) {
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = null;
		try {
			byteMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, width,
					height, hintMap);

			int matrixWidth = byteMatrix.getWidth();
			int matrixHeight = byteMatrix.getHeight();
			BufferedImage image = new BufferedImage(matrixWidth, matrixHeight,
					BufferedImage.TYPE_INT_RGB);
			image.createGraphics();
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, matrixWidth + 5, matrixHeight + 5);
			graphics.setFont(graphics.getFont().deriveFont(13f));
			graphics.setColor(Color.BLACK);
			graphics.drawString(str, length, height - 10);
			for (int i = 0; i < matrixHeight; i++) {
				for (int j = 0; j < matrixHeight; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect((i), j, 1, 1);
					}
				}
			}
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Image getImageForPdf(String str, int width, int height,
			int length) {
		BufferedImage image = drawQrImage(str, width, height, length);
		Image itextImage = null;
		try {
			itextImage = Image.getInstance(Toolkit.getDefaultToolkit()
					.createImage(image.getSource()), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itextImage;
	}

}
