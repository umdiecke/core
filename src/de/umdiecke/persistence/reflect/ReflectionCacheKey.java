/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.reflect;

import java.util.Arrays;

/**
 * Klasse zur Erzeugung eines eineindeutigen Schlüssel für den Reflection-Cache.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:45:30
 *
 */
public class ReflectionCacheKey {

	/**
	 * Class-Objekt der Klasse, auf die via Java-Reflections zugegriffen werden
	 * soll.
	 */
	private final Class<?> callerClass;
	/**
	 * Name der Klassen-Methode, die via Java-Reflections aufgerufen werden
	 * soll. (z.B. rtvVorgangsadresse)
	 */
	private final String methodName;
	/**
	 * Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
	 * Parameterliste. Die Reihenfolge der Parameterliste muss �bereinstimmen!
	 * (z.B.[IEnv.class, String.class, ...])
	 */
	private final Class<?>[] argTypes;

	/**
	 * Default-Konstruktor
	 *
	 * @param callerClass
	 *            Class-Objekt der Klasse, auf die via Java-Reflections
	 *            zugegriffen werden soll.
	 * @param methodName
	 *            Name der Klassen-Methode, die via Java-Reflections aufgerufen
	 *            werden soll. (z.B. rtvVorgangsadresse)
	 * @param argTypes
	 *            Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen! (z.B.[IEnv.class, String.class, ...])
	 */
	public ReflectionCacheKey(final Class<?> callerClass, final String methodName, final Class<?>[] argTypes) {
		super();
		this.callerClass = callerClass;
		this.methodName = methodName;
		this.argTypes = argTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.argTypes);
		result = prime * result + ((this.callerClass == null) ? 0 : this.callerClass.hashCode());
		result = prime * result + ((this.methodName == null) ? 0 : this.methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ReflectionCacheKey other = (ReflectionCacheKey) obj;
		if (!Arrays.equals(this.argTypes, other.argTypes)) {
			return false;
		}
		if (this.callerClass == null) {
			if (other.callerClass != null) {
				return false;
			}
		} else if (!this.callerClass.equals(other.callerClass)) {
			return false;
		}
		if (this.methodName == null) {
			if (other.methodName != null) {
				return false;
			}
		} else if (!this.methodName.equals(other.methodName)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the callerClass
	 */
	public Class<?> getCallerClass() {
		return this.callerClass;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return this.methodName;
	}

	/**
	 * @return the argTypes
	 */
	public Class<?>[] getArgTypes() {
		return this.argTypes;
	}
}