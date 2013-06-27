package application;

import java.util.Scanner;

import kasper.kernel.Home;

import application.museum.Museum;
import application.museum.PageListener;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.server.ServerManager;

/**
 * @author statchum
 */
public final class App {

	public static void main(String[] args) {
		Home.start(AppConfig.createHomeConfig());

		loadDatas();

		try {
			System.out.println("Taper sur entrée pour sortir");
			final Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
		} finally {
			Home.stop();
		}
	}

	private static void loadDatas() {
		final ServerManager serverManager = Home.getContainer().getManager(ServerManager.class);
		new Museum(new PageListener() {
			@Override
			public void onPage(KProcess process) {
				serverManager.push(process);

			}
		}).load();
	}
}
