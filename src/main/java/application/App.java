package application;

import java.util.Scanner;

import kasper.kernel.Home;

/**
 * @author statchum
 */
public final class App {

	public static void main(String[] args) {
		Home.start(AppConfig.createHomeConfig());
		try {
			System.out.println("Taper sur entrée pour sortir");
			final Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
		} finally {
			Home.stop();
		}
	}
}
