package com.kleegroup.analytica.core;

import java.text.MessageFormat;

/**
 * Permet de g�rer les Assertions. C'est � dire les : <ul>
 * <li>invariants</li>
 * <li>pr� conditions</li>
 * <li>post conditions</li>
 * </ul>
 * Ces notions ont �t� introduites avec le langage Eiffel par B. Meyer. Elles sont relatives � la notion de contrat.
 * Il s'agit de v�rifier
 * les contrats en entr�e <b>-pr�condition-</b>
 * les contrats en sortie <b>-postcondition-</b>
 * les conditions obligatoirement v�rifi�es � l'int�rieur des m�thodes <b>-invariant-</b>.
 * <br>
 * Le non respect d'un contrat est un <b>bug</b>, les assertions peuvent et doivent �tre d�sactiv�es en production.
 * Il ne faut pas confondre les assertions avec les exceptions, ces derni�res ne sont jamais d�sactiv�es.
 * <br> On utilise pour ce faire le pattern de code suivant :
 * <br><dir><i>if (Assertion.enable) Assertion.xxxxx</i></dir>
 * <br>Pour aller plus loin lire articles et ouvrages autour des travaux de B. Meyer et M. Fowler concernant la programmation par contrat (design by contract).
 *
 * @author fconstantin
 * @version $Id: Assertion.java,v 1.2 2013/01/28 11:53:27 npiedeloup Exp $
 */
public final class Assertion extends Error {
	private static final long serialVersionUID = -5602553431315453582L;

	private enum Type {
		/**Cas des invariants.*/
		invariant,
		/**Cas des postconditions (ce qui est produit par un processus).*/
		postcondition,
		/**Cas des preconditions (ce qui arrive en entr�e d'un processus).*/
		precondition,
		/**El�ments obligatoire.*/
		notNull,
		/**El�ments obligatoirement vide.*/
		isNull,
		/**Chaines non null et non vide.*/
		notEmpty,
		/**Cas non impl�ment�s dans des switchs par exemple.*/
		notImplemented,
	}

	/**
	 * Constructeur.
	 * @param type Type de l'assertion
	 * @param msg Message d'erreur
	 * @param params Param�tres du message
	 */
	private Assertion(final Type type, final String msg, final Object... params) {
		super("[Assertion." + type + "] " + format(msg, params));
	}

	/** Permet de tester un �tat obligatoire.
	 * S'utilise de mani�re courante dans les <i>switch</i> pour v�rifer les cas non impl�ment�s.
	 * <br><br>
	 * <i>
	 * switch (state) {
	 * <dir>
	 * case 1:
	 * <br>
	 * case 2:
	 * <br>
	 * ....
	 * <br>
	 * case else :
	 * <dir>if (Assertion.enable) Assertion.invariant(false, "case non implemented in .....");
	 * <br> break;
	 * </dir>
	 * }</i>
	 * </dir>
	 *  @param test Expression bool�enne qui doit �tre v�rifi�e
	 * @param message Message affich� si le test <b>n'est pas</b> v�rifi�.
	 */
	public static void invariant(final boolean test, final String message) {
		if (!test) {
			// Optimis� pour message sans formattage
			throw new Assertion(Type.invariant, message);
		}
	}

	/**
	 * Assertion de type invariant.
	 * @param test t Expression bool�enne qui doit �tre v�rifi�e
	 * @param message Message affich� si le test <b>n'est pas</b> v�rifi�.
	 * @param params Param�tres du message
	 */
	public static void invariant(final boolean test, final String message, final Object... params) {
		if (!test) {
			throw new Assertion(Type.invariant, message, params);
		}
	}

	/**
	 * Permet de tester une pr�condition.
	 * @param test Expression bool�enne qui doit �tre v�rifi�e
	 * @param message Message affich� si le test <b>n'est pas</b> v�rifi�.
	 */
	public static void precondition(final boolean test, final String message) {
		if (!test) {
			// Optimis� pour message sans formattage
			throw new Assertion(Type.precondition, message);
		}
	}

	/**
	 * Permet de tester une pr�condition.
	 * @param test Expression bool�enne qui doit �tre v�rifi�e
	 * @param message Message affich� si le test <b>n'est pas</b> v�rifi�.
	 * @param params Param�tres du message
	 */
	public static void precondition(final boolean test, final String message, final Object... params) {
		if (!test) {
			throw new Assertion(Type.precondition, message, params);
		}
	}

	/**
	 * Permet de tester une postcondition.
	 * @param test Expression bool�enne qui doit �tre v�rifi�e
	 * @param message Message affich� si le test <b>n'est pas</b> v�rifi�.
	 */
	public static void postcondition(final boolean test, final String message) {
		if (!test) {
			// Optimis� pour message sans formattage
			throw new Assertion(Type.postcondition, message);
		}
	}

	/**
	 * Permet de tester une postcondition.
	 * @param test Expression bool�enne qui doit �tre v�rifi�e
	 * @param message Message affich� si le test <b>n'est pas</b> v�rifi�.
	 * @param params Param�tres du message
	 */
	public static void postcondition(final boolean test, final String message, final Object... params) {
		if (!test) {
			throw new Assertion(Type.postcondition, message, params);
		}
	}

	/**
	 * Permet de tester le caract�re obligatoire (non null) d'un objet.
	 * @param o Object Objet obligatoire
	 */
	public static void notNull(final Object o) {
		if (o == null) {
			// Optimis� pour message sans formattage
			throw new Assertion(Type.notNull, "un param�tre obligatoire est null");
		}
	}

	/**
	 * Permet de tester le caract�re obligatoire (non null) d'un objet.
	 * @param o Object Objet obligatoire
	 * @param msg Message d'erreur
	 * @param params Param�tres du message
	 */
	public static void notNull(final Object o, final String msg, final Object... params) {
		if (o == null) {
			throw new Assertion(Type.notNull, msg, params);
		}
	}

	/**
	 * Permet de tester le caract�re null d'un objet.
	 * @param o Object Objet dont on veut v�rifi� qu'il est null
	 */
	public static void isNull(final Object o) {
		if (o != null) {
			// Optimis� pour message sans formattage
			throw new Assertion(Type.isNull, "un param�tre est d�ja renseign� alors qu'il devrait �tre null");
		}
	}

	/**
	 * Permet de tester le caract�re null d'un objet.
	 * @param o Object Objet dont on veut v�rifi� qu'il est null
	 * @param msg Message d'erreur
	 * @param params Param�tres du message
	 */
	public static void isNull(final Object o, final String msg, final Object... params) {
		if (o != null) {
			throw new Assertion(Type.isNull, msg, params);
		}
	}

	/**
	 * Permet de v�rifier que tous les cas sont test�s dans les switch/case ou if/elseif.
	 */
	public static void caseNotImplemented() {
		throw new Assertion(Type.notImplemented, "Cas de test non impl�ment� !");
	}

	/**
	 * Permet de tester le caract�re renseign� (non vide) d'une chaine.
	 * @param str String Chaine non vide
	 */
	public static void notEmpty(final String str) {
		notNull(str);
		if (isEmpty(str)) {
			throw new Assertion(Type.notEmpty, "une chaine obligatoire est vide");
		}
	}

	/**
	 * Permet de tester le caract�re renseign� (non vide) d'une chaine.
	 * @param str String Chaine non vide
	 * @param msg Message d'erreur
	 * @param params Param�tres du message
	 */
	public static void notEmpty(final String str, final String msg, final Object... params) {
		notNull(str, msg, params);
		if (isEmpty(str)) {
			throw new Assertion(Type.notEmpty, msg, params);
		}
	}

	/**
	 * Teste si la chaine non null est vide.
	 * Cad : pas de caract�re > <code>'\u0020'</code> (equivalant au trim() de String)
	 * @param str String Chaine non null
	 * @return <code>true</code> si la chaine est vide.
	 */
	private static boolean isEmpty(final CharSequence str) {
		//On prefere cette implementation qui ne cr�e pas de nouvelle chaine (contrairement au trim())
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) > ' ') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Fusionne une chaine compatible avec les param�tres.
	 * Les caract�res { }  sont interdits ou doivent �tre echapp�s avec \\.
	 * @param msg Chaine au format MessageFormat
	 * @param params Param�tres du message
	 * @return Chaine fusionn�e
	 */
	public static String format(final String msg, final Object... params) {
		Assertion.notNull(msg);
		//------------------------------------------------------------------------
		if (params == null || params.length == 0) {
			return msg;
		}
		//Gestion des doubles quotes 
		//On simple quotes les doubles quotes d�j� pos�es.
		//Puis on double toutes les simples quotes ainsi il ne reste plus de simple quote non doubl�e.
		final StringBuilder newMsg = new StringBuilder(msg);
		replace(newMsg, "''", "'");
		replace(newMsg, "'", "''");
		replace(newMsg, "\\{", "'{'");
		replace(newMsg, "\\}", "'}'");
		return MessageFormat.format(newMsg.toString(), params);
	}

	/**
	 * Remplacement au sein d'une chaine d'un motif par un autre.
	 * Le remplacement avance, il n'est pas r�cursif !!.
	 * Le StringBuilder est modifi� !! c'est pourquoi il n'y a pas de return.
	 * @param str StringBuilder
	 * @param oldStr Chaine � remplacer
	 * @param newStr Chaine de remplacement
	 */
	public static void replace(final StringBuilder str, final String oldStr, final String newStr) {
		Assertion.notNull(str);
		Assertion.notNull(oldStr);
		Assertion.precondition(oldStr.length() > 0, "La chaine a remplacer ne doit pas �tre vide");
		Assertion.notNull(newStr);
		//------------------------------------------------------------------------
		int index = str.indexOf(oldStr);
		if (index == -1) {
			return;
		}

		final int oldStrLength = oldStr.length();
		final int newStrLength = newStr.length();
		StringBuilder result = str;
		do {
			result = result.replace(index, index + oldStrLength, newStr);
			index = str.indexOf(oldStr, index + newStrLength);
		} while (index != -1);
	}
}
