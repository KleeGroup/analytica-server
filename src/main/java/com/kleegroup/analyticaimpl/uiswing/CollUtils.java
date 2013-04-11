package com.kleegroup.analyticaimpl.uiswing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.logging.Logger;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

/**
 * Méthodes utilitaires pour manipuler et filtrer des collections.
 * @version $Id: CollUtils.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author evernat
 * @see java.util.Collections
 */
public class CollUtils {
	private static final Logger LOG = Logger.getLogger(CollUtils.class.getName());

	/**
	 * Constructeur.
	 * (private : pas d'instance)
	 */
	private CollUtils() {
		super();
	}

	/**
	 * Clone une liste de manière aussi performante que possible et synchronisée.
	 * (ce clone est un shallow-clone et non un deep-clone : il ne clone pas les éléments).
	 * @return java.util.List
	 * @param list java.util.Collection
	 */
	public static List cloneList(final Collection list) {
		synchronized (list) {
			// on construit par un clone une List de même type (ex : TypedArrayList), que la liste originale
			if (list instanceof ArrayList) {
				return (List) ((ArrayList) list).clone();
			} else if (list instanceof LinkedList) {
				return (List) ((LinkedList) list).clone();
			} else {
				return new ArrayList(list); // type de List inconnu
			}
		}
	}

	/**
	 * Recherche un objet dans une liste suivant la valeur d'une propriété.
	 * @return java.util.List
	 * @param list java.util.Collection
	 * @param propertyName java.lang.String
	 * @param value java.lang.Object
	 * @throws KSystemException
	 */
	public static Object findInList(final Collection list, final String propertyName, final Object value) {
		final List result = filterList(list, propertyName, value);
		Assertion.postcondition(!result.isEmpty(), "Aucune valeur trouvée");
		Assertion.postcondition(result.size() == 1, "Plus d'une valeur trouvée");
		return result.get(0);
	}

	/**
	 * Filtre une liste suivant les valeurs d'une propriété.
	 * @return java.util.List
	 * @param list java.util.Collection
	 * @param propertyName java.lang.String
	 * @param filter java.lang.Object
	 * @throws KSystemException
	 */
	public static List filterList(final Collection list, final String propertyName, final Object filter) {
		return filterList(list, propertyName, new Object[] { filter });
	}

	/**
	 * Filtre une liste suivant les valeurs d'une propriété.
	 * @return java.util.List
	 * @param list java.util.Collection
	 * @param propertyName java.lang.String
	 * @param filters java.lang.Object[]
	 * @throws KSystemException
	 */
	public static List filterList(final Collection list, final String propertyName, final Object[] filters) {
		Object object;
		Object value;
		final List values = Arrays.asList(filters);
		final List result = cloneList(list);
		result.clear();
		for (final Iterator it = list.iterator(); it.hasNext();) {
			object = it.next();
			value = getValue(object, propertyName);
			if (values.contains(value)) {
				result.add(object);
			}
		}
		return result;
	}

	/**
	 * Renvoie à partir d'une collection, une map dont les clés sont les valeurs d'une propriété.
	 * @return java.util.Map
	 * @param list java.util.Collection
	 * @param propertyName java.lang.String
	 * @throws KSystemException
	 */
	public static Map mapList(final Collection list, final String propertyName) {
		if (list == null) {
			return null;
		}

		final Map result = new LinkedHashMap(list.size());
		Object object;
		Object key;

		for (final Iterator it = list.iterator(); it.hasNext();) {
			object = it.next();
			key = getValue(object, propertyName);
			result.put(key, object);
		}

		return result;
	}

	/**
	 * Returns a fixed-size list backed by the specified array.  (Changes to
	 * the returned list "write through" to the array.)  This method acts
	 * as bridge between array-based and collection-based APIs, in
	 * combination with <tt>Collection.toArray</tt>.  The returned list is
	 * serializable and implements {@link RandomAccess}.
	 *
	 * @param array the array by which the list will be backed.
	 * @return a list view of the specified array.
	 * @see Collection#toArray()
	 */
	public static List asList(final Object[] array) {
		return Arrays.asList(array);
	}

	/**
	 * Trie une collection suivant les propriétés fournies.
	 * @return java.util.List
	 * @param list java.util.Collection
	 * @param properties java.lang.String[]
	 * @see java.util.Collections#sort(List)
	 */
	public static List sortList(final Collection list, final String[] properties) {

		final boolean sortNullFirst = true;
		final boolean sortIgnoreCase = true;

		class ComparisonWrapper implements Comparable {
			private final Object object;
			private final Map values = new HashMap(10);
			private final String[] props;

			ComparisonWrapper(final Object object, final String[] properties) {
				this.object = object;
				props = properties;
			}

			private Object getValue(final String propertyName) {
				Object value;

				if (values.containsKey(propertyName)) {
					value = values.get(propertyName);
				} else {
					value = CollUtils.getValue(object, propertyName);
					values.put(propertyName, value);
				}

				return value;
			}

			public int compareTo(final Object object2) {
				String propertyName;
				Object value;
				Object value2;
				int comparison;

				try {
					final int length = props.length;
					for (int i = 0; i < length; i++) {
						propertyName = props[i];
						value = getValue(propertyName);
						value2 = ((ComparisonWrapper) object2).getValue(propertyName);
						// object2 est forcément un ComparisonWrapper

						if (value == value2) {
							comparison = 0;
						} else if (value == null) { // null est avant tout le reste (par défaut)
							comparison = sortNullFirst ? -1 : 1;
						} else if (value2 == null) {
							comparison = sortNullFirst ? 1 : -1;
						} else {
							if (value instanceof String) {
								if (sortIgnoreCase) {
									comparison = ((String) value).compareToIgnoreCase(value2.toString());
								} else {
									comparison = ((String) value).compareTo(value2.toString());
								}
							} else if (value instanceof Comparable && value.getClass() == value2.getClass()) {
								// on évite les classes Comparable mais différentes
								// (ex: java.util.Date et java.sql.Timestamp)
								comparison = ((Comparable) value).compareTo(value2);
								// Number, Date, Character implémentent Comparable
							} else if (value instanceof Calendar && value2 instanceof Calendar) {
								comparison = ((Calendar) value).getTime().compareTo(((Calendar) value2).getTime());
							} else if (value instanceof Date && value2 instanceof Date) {
								final long time = ((Date) value).getTime();
								final long time2 = ((Date) value2).getTime();
								comparison = time < time2 ? -1 : time == time2 ? 0 : 1;
							} else if (sortIgnoreCase) { // compare tout le reste dont les Boolean
								comparison = value.toString().compareToIgnoreCase(value2.toString());
							} else {
								comparison = value.toString().compareTo(value2.toString());
							}
						}

						if (comparison != 0) {
							return comparison;
						}
					}
				} catch (final Throwable throwable) {
					throw new RuntimeException(throwable);
				}

				return 0;
				// aucune des comparaisons n'a donné de différences
			}
		}

		if (list == null) {
			return null;
		}
		if (properties == null || properties.length == 0) {
			return new ArrayList(list);
		}

		final List temp = new ArrayList(list.size());
		for (final Iterator it = list.iterator(); it.hasNext();) {
			temp.add(new ComparisonWrapper(it.next(), properties));
		}
		Collections.sort(temp);

		final List result = cloneList(list);
		result.clear();
		for (final Iterator it = temp.iterator(); it.hasNext();) {
			result.add(((ComparisonWrapper) it.next()).object);
		}
		return result;
	}

	private static Object getValue(final Object object, final String propertyName) {
		try {
			return BeanUtils.getValue(object, propertyName);
		} catch (final Throwable throwable) {
			throw new KRuntimeException(throwable.toString(), throwable);
		}
	}
}
