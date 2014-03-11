package com.kleegroup.analyticaimpl.uiswing;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * M�thodes utilitaires pour manipuler les propri�t�s (getter/setter) des JavaBeans (ie tous les types d'objets).
 * @author Joe Walnes, OpenSymphony Group, http://www.opensymphony.com/)
 * @version $Id: BeanUtils.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 */
public class BeanUtils {
	private static final boolean IS_JAVA14_MIN = "1.4".compareTo(System.getProperty("java.version")) <= 0;
	private static final Map BEAN_INFOS;
	private static final Map CLASS_INTERFACES;

	static {
		try {
			final boolean isJava15Min = "1.5".compareTo(System.getProperty("java.version")) <= 0;
			BEAN_INFOS = isJava15Min ? (Map) Class.forName("java.util.concurrent.ConcurrentHashMap").newInstance() : new HashMap();
			CLASS_INTERFACES = isJava15Min ? (Map) Class.forName("java.util.concurrent.ConcurrentHashMap").newInstance() : new HashMap();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructeur.
	 * (private : pas d'instance)
	 */
	private BeanUtils() {
		super();
	}

	/**
	 * Retourne la valeur d'une propri�t� d'un bean
	 * (ex : "name" -> object.getName() ou "country.name" -> object.getCountry().getName()).
	 * @return java.lang.Object
	 * @param object java.lang.Object
	 * @param propertyName java.lang.String
	 * @throws java.lang.Throwable   Erreur d'introspection, de r�flexion ou dans le getter
	 */
	public static Object getValue(final Object object, final String propertyName) throws Throwable {
		if (propertyName == null || object == null || propertyName.length() == 0) {
			return null;
		}

		// Holder for Object at current depth along chain.
		Object result = object;
		if (propertyName.indexOf('.') == -1) {
			result = invokeProperty(result, propertyName);
		} else {
			// Split out property on dots ("person.name.first" -> "person","name","first" -> getPerson().getName().getFirst())
			final StringTokenizer st = new StringTokenizer(propertyName, ".");

			// Loop through properties in chain.
			String currentPropertyName;
			while (st.hasMoreTokens()) {
				currentPropertyName = st.nextToken();
				// Assign to holder the next property in the chain.
				result = invokeProperty(result, currentPropertyName);
			}
		}
		// Return holder Object
		return result;
	}

	/**
	 * Retourne les noms des propri�t�s d'un bean.
	 * @return java.lang.String[]
	 * @param object java.lang.Object
	 * @throws java.beans.IntrospectionException   Erreur dans l'introspection
	 */
	public static String[] getPropertyNames(final Object object) throws IntrospectionException {
		final BeanInfo info = getBeanInfo(object.getClass());
		final PropertyDescriptor[] properties = info.getPropertyDescriptors();
		final int length = properties.length;
		String[] names = new String[length];
		PropertyDescriptor propertyDescriptor;
		boolean indexedFound = false;
		for (int i = 0; i < length; i++) {
			propertyDescriptor = properties[i];
			if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
				indexedFound = true;
			} else {
				names[i] = propertyDescriptor.getName();
			}
		}
		if (indexedFound) {
			final List list = new ArrayList(Arrays.asList(names));
			for (final Iterator it = list.iterator(); it.hasNext();) {
				if (it.next() == null) {
					it.remove();
				}
			}
			names = (String[]) list.toArray(new String[list.size()]);
		}
		return names;
	}

	/**
	 * Retourne une map contenant les noms et valeurs des propri�t�s d'un bean.
	 * @return java.util.Map
	 * @param object java.lang.Object
	 * @param allowedPropertyNames java.lang.String Si NON null, seules les propri�t�s sp�cifi�es seront r�cup�r�es.
	 * @throws java.lang.Throwable   Erreur d'introspection, de r�flexion ou dans un getter
	 */
	public static Map getValues(final Object object, final String[] allowedPropertyNames) throws Throwable {
		return getValues(object, allowedPropertyNames, null);
	}

	/**
	 * Retourne une map contenant les noms et valeurs des propri�t�s d'un bean.
	 * @return java.util.Map
	 * @param object java.lang.Object
	 * @param allowedPropertyNames java.lang.String[] Si NON null, seules les propri�t�s sp�cifi�es seront r�cup�r�es.
	 * @param excludedPropertyNames java.lang.String[] Si NON null, les propri�t�s sp�cifi�es ne seront pas r�cup�r�es.
	 * @throws java.lang.Throwable   Erreur d'introspection, de r�flexion ou dans un getter
	 */
	public static Map getValues(final Object object, final String[] allowedPropertyNames, final String[] excludedPropertyNames) throws Throwable {
		return getValues(object, allowedPropertyNames, excludedPropertyNames, false);
	}

	/**
	 * Retourne une map contenant les noms et valeurs des propri�t�s d'un bean.
	 * @param object Object
	 * @param allowedPropertyNames String[]
	 * @param excludedPropertyNames String[]
	 * @param includeNullValues boolean indique si les propri�t�s dont la valeur est nulle doivent �tre incluses dans le r�sultat
	 * @return Map
	 * @throws Throwable
	 */
	public static Map getValues(final Object object, final String[] allowedPropertyNames, final String[] excludedPropertyNames, final boolean includeNullValues) throws Throwable {
		final Map result = IS_JAVA14_MIN ? (Map) Class.forName("java.util.LinkedHashMap").newInstance() : new HashMap();
		final String[] propertyNames = getPropertyNames(object);
		String propertyName;
		Object propertyValue;
		final int length = propertyNames.length;
		for (int i = 0; i < length; i++) {
			propertyName = propertyNames[i];
			if (isAllowed(propertyName, allowedPropertyNames) && !isExcluded(propertyName, excludedPropertyNames)) {
				propertyValue = getValue(object, propertyName);
				if (propertyName == null || propertyValue == null && !includeNullValues) {
					continue;
				}
				result.put(propertyName, propertyValue);
			}
		}
		return result;
	}

	/**
	 * D�finit des propri�t�s d'un bean en utilisant une map.
	 * @param object java.lang.Object
	 * @param valueMap java.util.Map
	 * @param allowedPropertyNames Si NON null, seules les propri�t�s sp�cifi�es seront d�finies.
	 * @throws java.lang.Throwable   Erreur d'introspection, de r�flexion ou dans un setter
	 */
	public static void setValues(final Object object, final Map valueMap, final String[] allowedPropertyNames) throws Throwable {
		final Iterator entries = valueMap.entrySet().iterator();
		Map.Entry entry;
		String property;
		Object value;
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			property = entry.getKey().toString();
			value = entry.getValue();
			if (isAllowed(property, allowedPropertyNames)) {
				setValue(object, property, value);
			}
		}
	}

	/**
	 * D�finit des propri�t�s d'un bean en utilisant les propri�t�s d'un bean.
	 * Les beans peuvent �tres d'un type diff�rent.
	 * @param object The object to be manipulated.
	 * @param src The object containing the properties to be copied.
	 * @param allowedPropertyNames Si NON null, seules les propri�t�s sp�cifi�es seront d�finies.
	 * @throws java.lang.Throwable   Erreur d'introspection, de r�flexion ou dans un getter ou setter
	 */
	public static void setValues(final Object object, final Object src, final String[] allowedPropertyNames) throws Throwable {
		setValues(object, getValues(src, allowedPropertyNames), allowedPropertyNames);
	}

	/**
	 * D�code un objet par d�s�rialisation.
	 * @return java.lang.Object
	 * @param bytes byte[]
	 * @see #encodeObject
	 * @throws java.io.IOException   Erreur disque
	 * @throws java.lang.ClassNotFoundException   Une classe � d�sarialis�e n'a pu �tre trouv�e
	 */
	public static Object decodeObject(final byte[] bytes) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		final ObjectInputStream stream = new ObjectInputStream(byteStream);
		final Object result = stream.readObject();
		stream.close();
		return result;
	}

	/**
	 * Encode un objet par s�rialisation.
	 * @return byte[]
	 * @param object java.lang.Object
	 * @see #decodeObject
	 * @throws java.io.IOException   Erreur disque
	 */
	public static byte[] encodeObject(final Object object) throws IOException {
		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(2048);
		final ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
		stream.writeObject(object);
		stream.close();
		byteStream.flush();
		return byteStream.toByteArray();
	}

	/**
	 * Invoque le getter d'un objet.
	 * @return java.lang.Object
	 * @param object java.lang.Object
	 * @param propertyName java.lang.String
	 * @throws java.lang.Throwable   Erreur d'introspection, de r�flexion ou dans le getter
	 */
	protected static Object invokeProperty(final Object object, final String propertyName) throws Throwable {
		if (object == null || propertyName == null || propertyName.length() == 0) {
			return null; // just in case something silly happens.
		}

		final PropertyDescriptor pd = getPropertyDescriptor(propertyName, object.getClass(), false);
		return invokeMethod(pd.getReadMethod(), object, null);
	}

	/**
	 * Retourne un bool�en suivant que le tableau contient une cha�ne.
	 * @return boolean
	 * @param string java.lang.String
	 * @param array java.lang.String[]
	 */
	protected static boolean isAllowed(final String string, final String[] array) {
		if (array == null) {
			return true;
		}
		if (string == null) {
			return false;
		}

		final int length = array.length;
		for (int i = 0; i < length; i++) {
			if (string.equals(array[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retourne un bool�en suivant que le tableau contient une cha�ne.
	 * @return boolean
	 * @param string java.lang.String
	 * @param array java.lang.String[]
	 */
	protected static boolean isExcluded(final String string, final String[] array) {
		return array != null && isAllowed(string, array);
	}

	/**
	 * Retourne le beanInfo d'une classe � partir du cache.
	 * @return java.beans.BeanInfo
	 * @param beanClass java.lang.Class
	 * @throws java.beans.IntrospectionException   Erreur dans l'introspection
	 */
	protected static BeanInfo getBeanInfo(final Class beanClass) throws IntrospectionException {
		if (BEAN_INFOS.size() > 300) {
			BEAN_INFOS.clear(); // pour �viter une fuite m�moire potentielle
		}
		BeanInfo result = (BeanInfo) BEAN_INFOS.get(beanClass);

		if (result == null) {
			// On veut tout le BeanInfo sauf Object (pas la propri�t� de getClass())
			result = Introspector.getBeanInfo(beanClass, Object.class);
			BEAN_INFOS.put(beanClass, result);
		}

		return result;
	}

	/**
	 * Retourne le PropertyDescriptor d'une propri�t�.
	 * @return java.beans.PropertyDescriptor
	 * @param propertyName java.lang.String
	 * @param beanClass java.lang.Class
	 * @param isForSetter boolean
	 * @throws java.beans.IntrospectionException   Erreur dans l'introspection
	 * @throws java.lang.NoSuchMethodException   La m�thode recherch�e n'a pas �t� trouv�e
	 */
	public static PropertyDescriptor getPropertyDescriptor(final String propertyName, final Class beanClass, final boolean isForSetter) throws IntrospectionException, NoSuchMethodException {

		// on pourrait faire new PropertyDescriptor(propertyName, beanClass)
		// mais si jamais il a �t� d�fini des BeanInfo pour certaines classes, autant les utiliser.

		final PropertyDescriptor[] descriptors = getBeanInfo(beanClass).getPropertyDescriptors();

		String name;
		final int length = descriptors.length;
		for (int i = 0; i < length; i++) {
			name = descriptors[i].getName();
			if (name.equals(propertyName)) {
				if (!isForSetter && descriptors[i].getReadMethod() == null) {
					throw new NoSuchMethodException("Getter non trouv� pour l'attribut \"" + propertyName + "\" sur classe \"" + beanClass.getName() + '\"');
				}
				if (isForSetter && descriptors[i].getWriteMethod() == null) {
					throw new NoSuchMethodException("Setter non trouv� pour l'attribut \"" + propertyName + "\" sur classe \"" + beanClass.getName() + '\"');
				}

				return descriptors[i];
			}
		}
		throw new NoSuchMethodException("Aucune m�thode trouv�e pour l'attribut \"" + propertyName + "\" sur classe \"" + beanClass.getName() + '\"');
	}

	/**
	 * D�finit la valeur d'une propri�t� d'un bean
	 * (ex : "name" -> object.setName(value) ou "country.name" -> object.getCountry().setName(value)).
	 * @param object java.lang.Object
	 * @param propertyName java.lang.String
	 * @param value java.lang.Object
	 * @throws java.lang.Throwable   Erreur d'introspection, de r�flexion ou dans le setter
	 */
	public static void setValue(final Object object, final String propertyName, final Object value) throws Throwable {
		if (propertyName == null || object == null || propertyName.length() == 0) {
			return;
		}

		PropertyDescriptor pd;
		if (propertyName.indexOf('.') == -1) {
			pd = getPropertyDescriptor(propertyName, object.getClass(), true);
			invokeMethod(pd.getWriteMethod(), object, new Object[] { value });
		} else {
			// Split out property on dots ("person.name.first" -> "person","name","first" -> getPerson().getName().getFirst())
			final StringTokenizer st = new StringTokenizer(propertyName, ".");

			// Holder for Object at current depth along chain.
			Object current = object;
			// Loop through properties in chain.
			String currentPropertyName;
			while (st.hasMoreTokens()) {
				currentPropertyName = st.nextToken();
				if (st.hasMoreTokens()) {
					// This is a getter
					current = invokeProperty(current, currentPropertyName);
				} else {
					// Final property in chain, hence setter
					pd = getPropertyDescriptor(currentPropertyName, current.getClass(), true);

					invokeMethod(pd.getWriteMethod(), current, new Object[] { value });
				}
			}
		}
	}

	/**
	 * Vide le cache des BeanInfos pour le recharger, par exemple apr�s des modifications de classes en d�veloppement.
	 */
	public static void clearCache() {
		BEAN_INFOS.clear();
		CLASS_INTERFACES.clear();
	}

	/**
	 * Renvoie le nom de la classe sans le package.
	 * @return java.lang.String
	 * @param classe java.lang.Class
	 */
	public static final String getClassName(final Class classe) {
		String className = classe != null ? classe.getName() : "null";
		if (className.indexOf('_') > 0) {
			className = className.substring(0, className.indexOf('_'));
		}
		return className.substring(className.lastIndexOf('.') + 1);
	}

	/**
	 * Invoque une m�thode par r�flection sur l'objet sp�cifi� et avec les arguments de m�thode sp�cifi�s.
	 * <br>Si une InvocationTargetException survient, l'exception d'origine dans la m�thode appel�e est d�sencapsul�e.
	 * @param method java.lang.reflect.Method
	 * @param object java.lang.Object
	 * @param args java.lang.Object[]
	 * @return java.lang.Object
	 * @throws java.lang.Throwable
	 * @throws java.lang.IllegalArgumentException
	 * @throws java.lang.IllegalAccessException
	 */
	public static Object invokeMethod(final Method method, final Object object, final Object[] args) throws Throwable, IllegalArgumentException, IllegalAccessException {
		try {
			return method.invoke(object, args);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	/**
	 * Cr�e un proxy des interfaces sp�cifi�es en param�tre.
	 * @param classLoader java.lang.ClassLoader
	 * @param interfaces java.lang.Class[]
	 * @param invocationHandler java.lang.reflect.InvocationHandler
	 * @return java.lang.Object
	 * @see java.lang.reflect.Proxy#newProxyInstance
	 */
	public static Object createProxy(final ClassLoader classLoader, final Class[] interfaces, final InvocationHandler invocationHandler) {
		return Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
	}

	/**
	 * R�cup�re l'ensemble des interfaces impl�ment�es par cette class.
	 * Cette m�thode est utilis� pour la cr�ation dynamique des Proxy (sae.util.kanap).
	 * @param classe Class Class dont on veut les interfaces
	 * @return Class[] Tableau des interfaces de cette classe (il peut y avoir des doublons)
	 */
	public static Class[] getAllInterfaces(final Class classe) {
		if (CLASS_INTERFACES.size() > 150) {
			CLASS_INTERFACES.clear(); // pour �viter une fuite m�moire potentielle
		}
		Class[] arrayInterfaces = (Class[]) CLASS_INTERFACES.get(classe);
		if (arrayInterfaces == null) {
			final Set interfaces = new HashSet();
			Class superClass = classe;
			while (superClass != null) {
				interfaces.addAll(Arrays.asList(superClass.getInterfaces()));
				superClass = superClass.getSuperclass();
			}
			arrayInterfaces = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
			CLASS_INTERFACES.put(classe, arrayInterfaces);
		}
		return arrayInterfaces;
	}

}
