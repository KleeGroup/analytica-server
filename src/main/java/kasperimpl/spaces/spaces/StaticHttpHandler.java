package kasperimpl.spaces.spaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import kasper.kernel.util.Assertion;

import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.io.OutputBuffer;
import org.glassfish.grizzly.http.server.util.MimeType;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.utils.ArraySet;

public class StaticHttpHandler extends HttpHandler {
	private static final Logger LOGGER = Grizzly.logger(StaticHttpHandler.class);
	private final ArraySet<File> docRoots = new ArraySet<File>(File.class);
	private final File[] files;
	private long last;

	/**
	 * Create a new instance which will look for static pages located
	 * under the <tt>docRoot</tt>. If the <tt>docRoot</tt> is <tt>null</tt> -
	 * static pages won't be served by this <tt>HttpHandler</tt>
	 *
	 * @param docRoots the folder(s) where the static resource are located.
	 * If the <tt>docRoot</tt> is <tt>null</tt> - static pages won't be served
	 * by this <tt>HttpHandler</tt>
	 */
	public StaticHttpHandler(File... files) {
		Assertion.notNull(files);
		Assertion.notNull(docRoots);
		//-----------------------------------------------------------------
		this.files = files;
		docRoots.addAll(files);
		last = System.currentTimeMillis();
	}

	private void reload() {
		long now = System.currentTimeMillis();
		if ((now - last) > 2000) {
			System.out.println(">>reloading");
			docRoots.removeAll(files);
			docRoots.addAll(files);
			last = now;
		}
	}

	/**
	 * Based on the {@link Request} URI, try to map the file from the
	 * {@link #getDocRoots()}, and send it back to a client.
	 * @param request the {@link Request}
	 * @param response the {@link Response}
	 * @throws Exception
	 */
	@Override
	public void service(final Request request, final Response response) throws Exception {
		reload();
		//---
		final String uri = getRelativeURI(request);

		if (uri == null || !handle(uri, request, response)) {
			onMissingResource(request, response);
		}
	}

	private static String getRelativeURI(final Request request) {
		String uri = request.getRequestURI();
		if (uri.indexOf("..") >= 0) {
			return null;
		}

		final String resourcesContextPath = request.getContextPath();
		if (resourcesContextPath.length() > 0) {
			if (!uri.startsWith(resourcesContextPath)) {
				return null;
			}

			uri = uri.substring(resourcesContextPath.length());
		}

		return uri;
	}

	/**
	 * The method will be called, if the static resource requested by the {@link Request}
	 * wasn't found, so {@link StaticHttpHandler} implementation may try to
	 * workaround this situation.
	 * The default implementation - sends a 404 response page by calling {@link #customizedErrorPage(Request, Response)}.
	 *
	 * @param request the {@link Request}
	 * @param response the {@link Response}
	 * @throws Exception
	 */
	private void onMissingResource(final Request request, final Response response) throws Exception {
		response.setStatus(HttpStatus.NOT_FOUND_404);
		customizedErrorPage(request, response);
	}

	/**
	 * Lookup a resource based on the request URI, and send it using send file.
	 *
	 * @param uri The request URI
	 * @param req the {@link Request}
	 * @param res the {@link Response}
	 * @throws Exception
	 */
	private boolean handle(final String uri, final Request req, final Response res) throws Exception {

		boolean found = false;

		final File[] fileFolders = docRoots.getArray();
		if (fileFolders == null) {
			return false;
		}

		File resource = null;

		for (int i = 0; i < fileFolders.length; i++) {
			final File webDir = fileFolders[i];
			// local file
			resource = new File(webDir, uri);
			final boolean exists = resource.exists();
			final boolean isDirectory = resource.isDirectory();

			if (exists && isDirectory) {
				final File f = new File(resource, "/index.html");
				if (f.exists()) {
					resource = f;
					found = true;
					break;
				}
			}

			if (isDirectory || !exists) {
				found = false;
			} else {
				found = true;
				break;
			}
		}

		if (!found) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "File not found  {0}", resource);
			}
			return false;
		}
		sendFile(res, resource);
		return true;
	}

	private static void sendFile(final Response response, final File file) throws IOException {
		final String path = file.getPath();
		final FileInputStream fis = new FileInputStream(file);

		try {
			response.setStatus(HttpStatus.OK_200);
			String substr;
			int dot = path.lastIndexOf('.');
			if (dot < 0) {
				substr = file.toString();
				dot = substr.lastIndexOf('.');
			} else {
				substr = path;
			}
			if (dot > 0) {
				String ext = substr.substring(dot + 1);
				String ct = MimeType.get(ext);
				if (ct != null) {
					response.setContentType(ct);
				}
			} else {
				response.setContentType(MimeType.get("html"));
			}

			final long length = file.length();
			response.setContentLengthLong(length);

			final OutputBuffer outputBuffer = response.getOutputBuffer();

			byte b[] = new byte[8192];
			int rd;
			while ((rd = fis.read(b)) > 0) {
				//chunk.setBytes(b, 0, rd);
				outputBuffer.write(b, 0, rd);
			}
		} finally {
			try {
				fis.close();
			} catch (IOException ignore) {
			}
		}
	}
}
