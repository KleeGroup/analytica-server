package kasperimpl.spaces.spaces.kraft;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import kasper.kernel.exception.KRuntimeException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Path("/dashboard")
public final class Dashboard {
	private static final MustacheFactory MUSTACHE_FACTORY = new DefaultMustacheFactory();

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String hello() {
		System.out.println(">>>dashboard/index");
		return process("index", new HashMap());
	}

	private static final String process(final String name, final Map<String, ?> context) {
		try {
			final StringWriter writer = new StringWriter();
			final Reader reader = new InputStreamReader(Dashboard.class.getResourceAsStream(name + ".mustache"));
			try {
				final Mustache mustache = MUSTACHE_FACTORY.compile(reader, name);
				mustache.execute(writer, context);
			} finally {
				reader.close();
			}
			return writer.toString();
		} catch (final Exception e) {
			throw new KRuntimeException(e);
		}
	}
}
