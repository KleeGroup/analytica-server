package kasperimpl.spaces.spaces;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.lang.Activeable;
import kasperimpl.spaces.spaces.kraft.Dashboard;
import kasperimpl.spaces.spaces.kraft.HomeServices;

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
			httpServer = createServer(port);
			httpServer.start();
		} catch (final IOException e) {
			throw new KRuntimeException(e);
		}
	}

	public void stop() {
		httpServer.stop();
	}

	private static final String STATIC_ROUTE = "/web";

	private static HttpServer createServer(final int port) throws IOException {
		//Configuration de la servlet jersey.
		// Attention tous les chemins /home/ sont bindés sur jersey.
		final ResourceConfig rc = new ClassNamesResourceConfig(Dashboard.class, HomeServices.class);
		final URI baseURI = UriBuilder.fromUri("http://localhost/").port(port).build();
		System.out.println(String.format("Jersey app started with WADL available at " + "%sapplication.wadl\nenter to stop it...", baseURI, baseURI));
		final HttpServer httpServer = GrizzlyServerFactory.createHttpServer(baseURI, rc);

		//Handler afin de servir les fichiers statics(html js).
		// Attention, il faut qui les fichiers soient dans les sources et qu'on les retroube dans le bin.
		final File rootFile = new File(SpacesServer.class.getResource(STATIC_ROUTE).getFile());
		final StaticHttpHandler staticHttpHandler = new StaticHttpHandler(rootFile);
		httpServer.getServerConfiguration().addHttpHandler(staticHttpHandler, STATIC_ROUTE);

		httpServer.getServerConfiguration().addHttpHandler(staticHttpHandler, "/static");
		return httpServer;
	}
}
