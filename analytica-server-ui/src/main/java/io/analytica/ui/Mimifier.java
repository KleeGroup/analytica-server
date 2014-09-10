/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>
 */
package io.analytica.ui;

import io.analytica.ui.JSMin.UnterminatedCommentException;
import io.analytica.ui.JSMin.UnterminatedRegExpLiteralException;
import io.analytica.ui.JSMin.UnterminatedStringLiteralException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
