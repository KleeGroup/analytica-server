package application;

import java.util.Scanner;

import kasper.kernel.Starter;
import kasper.kernel.lang.Option;

/**
 * 
 */

/**
 * @author statchum
 */
public final class App {
	public static void main(String[] args) {
		//		final Injector injector = new Injector();
		Starter starter = new Starter("./managers-test.xml", Option.<String> none(), App.class, 0L);
		starter.start();

		//On injecte les managers sur la classe de test.
		//		injector.injectMembers(this, Home.getContainer());
		try {
			System.out.println("Taper sur entrée pour sortir");
			final Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
		} finally {
			//Home.stop();
		}

	}
}
