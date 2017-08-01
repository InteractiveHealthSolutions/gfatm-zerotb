package com.ihsinformatics.qrgenerator;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class ZipUtil {
	static int length = 0;

	public static ByteArrayOutputStream getZip(List<String> data, int width,
			int height) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);

			String rootPath = System.getProperty("user.dir");
			File directory = new File(rootPath + File.separator + "webapps"
					+ File.separator + "gf-qrgen-web" + File.separator
					+ "ZipFilesTemp");

			// temp checkup
		//	 File directory = new File("E:\\ZipFiles");
			if (!directory.exists()) {
				directory.mkdir();
			}

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

				File qrFile = new File(directory + File.separator + str
						+ ".png");

				BufferedImage img = QrCodeImageUtil.drawQrImage(str, width,
						height, length);
				ImageIO.write(img, "png", qrFile);
			}

			byte bytes[] = new byte[2048];
			String[] files = directory.list();

			for (String fileName : files) {
				FileInputStream fis = new FileInputStream(directory.getPath()
						+ File.separator + fileName);
				BufferedInputStream bis = new BufferedInputStream(fis);

				zos.putNextEntry(new ZipEntry(fileName));

				int bytesRead;
				while ((bytesRead = bis.read(bytes)) != -1) {
					zos.write(bytes, 0, bytesRead);
				}
				zos.closeEntry();
				bis.close();
				fis.close();
			}

			zos.flush();
			baos.flush();
			zos.close();
			baos.close();
			deleteDirectory(directory);
			return baos;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
}
