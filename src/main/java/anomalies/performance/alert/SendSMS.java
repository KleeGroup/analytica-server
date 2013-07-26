/**
 * 
 */
package anomalies.performance.alert;

/**
 * @author statchum
 *
 */
public class SendSMS {

	/**
	 * 
	 */
	static String url = ""; // url du web service d'envoie du message
	static String textMessage="";
	static String mesg="";

	public static boolean send(final String msg,final String to,final String token) {

		final boolean bool = false;
		mesg = msg;
		url = url+""; // url complète

		return bool;

	}

}
