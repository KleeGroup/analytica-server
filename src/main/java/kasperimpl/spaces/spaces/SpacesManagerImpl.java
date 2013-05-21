package kasperimpl.spaces.spaces;

import javax.inject.Inject;

import kasper.kernel.lang.Activeable;

/**
 * Implémentation du tableau de bord.
 * 
 * @author pchretien
 * @version $Id: SpacesManagerImpl.java,v 1.2 2013/05/15 17:35:48 pchretien Exp $
 */
public final class SpacesManagerImpl implements SpacesManager, Activeable {
	private final SpacesServer spacesServer;

	@Inject
	public SpacesManagerImpl( /*@Named("port") final int port*/) {
		spacesServer = new SpacesServer(8083);
	}

	public void start() {
		spacesServer.start();
	}

	public void stop() {
		spacesServer.stop();
	}
}
