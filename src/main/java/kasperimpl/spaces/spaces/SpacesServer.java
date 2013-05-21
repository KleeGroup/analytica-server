package kasperimpl.spaces.spaces;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.lang.Activeable;
import kasperimpl.spaces.spaces.kraft.HomeServices;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

/**
 * Serveur du tableau de bord.
 * 
 * @author pchretien
 * @version $Id: SpacesServer.java,v 1.2 2013/05/15 17:35:48 pchretien Exp $
 */
final class SpacesServer implements Activeable {
	private HttpServer httpServer;
	private final int port;

	SpacesServer(final int port) {
		//---------------------------------------------------------------------
		this.port = port;
	}

	public void start() {
		try {
			httpServer = createServer( port);
		} catch (final IOException e) {
			throw new KRuntimeException(e);
		}
	}

	public void stop() {
		httpServer.stop();
	}

	private static HttpServer createServer(final int port) throws IOException {
		final ResourceConfig rc = new ClassNamesResourceConfig(HomeServices.class);
		final URI baseURI = UriBuilder.fromUri("http://localhost/").port(port).build();
		System.out.println(String.format("Jersey app started with WADL available at " + "%sapplication.wadl\nenter to stop it...", baseURI, baseURI));
		final HttpHandler  g = null;
		if (g == null){System.out.println("hello");}
		final HttpServer httpServer = GrizzlyServerFactory.createHttpServer(baseURI, rc);
		//		final StaticHttpHandler css = new StaticHttpHandler();
		//		css.addDocRoot(new File(KraftManager.class.getResource("webapp/css").getFile()));
		//		httpServer.getServerConfiguration().addHttpHandler(css, "/css");
		//
		//		final StaticHttpHandler js = new StaticHttpHandler();
		//		js.addDocRoot(new File(KraftManager.class.getResource("webapp/js").getFile()));
		//		httpServer.getServerConfiguration().addHttpHandler(js, "/js");
		//
		//		final StaticHttpHandler img = new StaticHttpHandler();
		//		img.addDocRoot(new File(KraftManager.class.getResource("webapp/img").getFile()));
		//		httpServer.getServerConfiguration().addHttpHandler(img, "/img");
		//
		return httpServer;
	}
}
