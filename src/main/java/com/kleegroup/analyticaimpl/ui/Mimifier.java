package com.kleegroup.analyticaimpl.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.kleegroup.analyticaimpl.ui.JSMin.UnterminatedCommentException;
import com.kleegroup.analyticaimpl.ui.JSMin.UnterminatedRegExpLiteralException;
import com.kleegroup.analyticaimpl.ui.JSMin.UnterminatedStringLiteralException;

final class Mimifier {

	private static OutputStream out;

	public static void mimifyAllIn(final File inputDir, final OutputStream out) throws IOException, UnterminatedRegExpLiteralException, UnterminatedCommentException, UnterminatedStringLiteralException {
		JSMin jsmin;
		InputStream in;
		Mimifier.out = out;

		for (final File fileEntry : inputDir.listFiles()) {
			if (fileEntry.isDirectory()) {
				mimifyAllIn(fileEntry, Mimifier.out);
			} else {
				String ext = fileEntry.getName();
				ext = ext.substring(ext.lastIndexOf("."));
				if (ext.equals(".js")) {
					in = new FileInputStream(fileEntry);
					jsmin = new JSMin(in, out);
					jsmin.jsmin();
				}
			}
		}

	}

//	public static void main(final String[] args) {
//		final File inputDir = new File("D:\\test\\scripts");
//		final File outPutDir = new File("D:\\test\\results\\out.js");
//		try {
//			Mimifier.mimifyAllIn(inputDir, new FileOutputStream(outPutDir));
//		} catch (IOException | UnterminatedRegExpLiteralException | UnterminatedCommentException | UnterminatedStringLiteralException e) {
//			e.printStackTrace();
//		}
//	}
}
