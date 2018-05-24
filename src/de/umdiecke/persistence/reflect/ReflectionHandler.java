/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.umdiecke.persistence.reflect.statistic.ReflectionHandlerStatistic;
import de.umdiecke.util.LRUCache;

/**
 * Handler-Klasse für den Zugriff auf Java Klassen und Methoden via Java_Reflections. Zugriffe können dabei gecacht werden.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:44:23
 *
 */
public class ReflectionHandler {

	/**
	 * Information, ob das Caching aktiviert ist.
	 */
	private final boolean cachingEnabled;
	/**
	 * Map mit bereits ermittelten und gecachten Reflection-Daten.
	 */
	private final Map<ReflectionCacheKey, ReflectionData> reflectionDataCache;
	/**
	 * Map mit �ber Java-Reflections angefragten und gecachten Ergebnisobjekten.
	 * Die Inhalte werden mit der Cache-Strategie LRU (Least recently used)
	 * vorgehalten.
	 */
	private final Map<ResultCacheKey, Object> methodCallResultDataCache;
	/**
	 * Gibt an, ob der Reflection-Handler seine angefragten Methoden mit
	 * geteilten Ressourcen �ber mehrere Threads ausf�hren k�nnen soll.
	 */
	private boolean runThreadSave;
	/**
	 * Handler zum erfassen der Statistik
	 */
	private ReflectionHandlerStatistic statistic;

	/**
	 * Default-Konstruktor
	 *
	 * @param cachingEnabled
	 *            Information, ob das Caching aktiviert ist.
	 * @param cacheCapacity
	 *            Angabe der Anzahl der max. gecachten Ergebnisobjekte, die im
	 *            LRU-Cache vorgehalten werden k�nnen.
	 * @param statisticEnabled
	 *            Aktivierung der Erfassung einer statistischen Auswertung
	 * @param detailedStatisticEnabled
	 *            Aktivierung der Erfassung einer detailierten statistischen
	 *            Auswertung
	 */
	public ReflectionHandler(final boolean cachingEnabled, final int cacheCapacity, final boolean statisticEnabled, final boolean detailedStatisticEnabled) {
		this(cachingEnabled, cacheCapacity, statisticEnabled, detailedStatisticEnabled, false);
	}

	/**
	 * Default-Konstruktor
	 *
	 * @param cachingEnabled
	 *            Information, ob das Caching aktiviert ist.
	 * @param cacheCapacity
	 *            Angabe der Anzahl der max. gecachten Ergebnisobjekte, die im
	 *            LRU-Cache vorgehalten werden k�nnen.
	 * @param statisticEnabled
	 *            Aktivierung der Erfassung einer statistischen Auswertung
	 * @param detailedStatisticEnabled
	 *            Aktivierung der Erfassung einer detailierten statistischen
	 *            Auswertung
	 * @param runThreadSave
	 *            Gibt an, ob der Reflection-Handler seine angefragten Methoden
	 *            mit geteilten Ressourcen �ber mehrere Threads ausf�hren k�nnen
	 *            soll.
	 */
	public ReflectionHandler(final boolean cachingEnabled, final int cacheCapacity, final boolean statisticEnabled, final boolean detailedStatisticEnabled,
			final boolean runThreadSave) {
		super();

		int initialCapacity;
		if (cacheCapacity <= 0 || !cachingEnabled) {
			initialCapacity = 1;
			this.cachingEnabled = false;
		} else {
			initialCapacity = cacheCapacity;
			this.cachingEnabled = cachingEnabled;
		}
		this.reflectionDataCache = Collections.synchronizedMap(new HashMap<ReflectionCacheKey, ReflectionData>());
		this.methodCallResultDataCache = Collections.synchronizedMap(new LRUCache<ResultCacheKey, Object>(initialCapacity));
		this.runThreadSave = runThreadSave;

		this.statistic = new ReflectionHandlerStatistic(statisticEnabled, detailedStatisticEnabled);
	}

	/**
	 * Aufruf einer Klassen-Methode via Java-Reflections und R�ckgabe des
	 * jeweiligen R�ckgabeobjekts.
	 *
	 * @param absClassPath
	 *            Abs. Klassenpfad zur Klasse, auf die via Java-Reflections
	 *            zugegriffen werden soll. (z.B.
	 *            de.bghw.mub.dssrv.business.srvdomain.datenservices.cds.CdsVorgangsadresse)
	 * @param methodName
	 *            Name der Klassen-Methode, die aufgerufen werden soll. (z.B.
	 *            rtvVorgangsadresse)
	 * @param customArgTypes
	 *            Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen! (z.B.[IEnv.class, String.class, ...])
	 * @param parameters
	 *            Array mit den Objekten der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen!
	 * @return R�ckgabeobjekt
	 * @throws ReflectionHandlerException
	 */
	public Object callClassMethodReflection(final String absClassPath, final String methodName, final Class<?>[] customArgTypes, final Object[] parameters)
			throws ReflectionHandlerException {
		return callClassMethodReflection(absClassPath, methodName, customArgTypes, parameters, this.cachingEnabled, this.cachingEnabled);
	}

	/**
	 * Aufruf einer Klassen-Methode via Java-Reflections und R�ckgabe des
	 * jeweiligen R�ckgabeobjekts.
	 *
	 * @param absClassPath
	 *            Abs. Klassenpfad zur Klasse, auf die via Java-Reflections
	 *            zugegriffen werden soll. (z.B.
	 *            de.bghw.mub.dssrv.business.srvdomain.datenservices.cds.CdsVorgangsadresse)
	 * @param methodName
	 *            Name der Klassen-Methode, die via Java-Reflections aufgerufen
	 *            werden soll. (z.B. rtvVorgangsadresse)
	 * @param customArgTypes
	 *            Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen! (z.B.[IEnv.class, String.class, ...])
	 * @param parameters
	 *            Array mit den Objekten der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen!
	 * @param useCachedResult
	 *            Verwendung eines ggf. schon fr�heren gecachten
	 *            R�ckgabeobjekts, welches bereits fr�her mit den selben
	 *            Abfrageparametern ermittelt wurde. (true/false)
	 * @param useCachedReflections
	 *            Verwendung von fr�heren gecachten Reflections, welche bereits
	 *            fr�her mit den selben Informationen erzeugt wurde.
	 *            (true/false)
	 * @return R�ckgabeobjekt
	 * @throws ReflectionHandlerException
	 */
	public Object callClassMethodReflection(final String absClassPath, final String methodName, final Class<?>[] customArgTypes, final Object[] parameters,
			final boolean useCachedResult, final boolean useCachedReflections) throws ReflectionHandlerException {
		Object fieldValue;
		Class<?>[] argTypes = customArgTypes;

		try {

			argTypes = checkCustomArgTypes(customArgTypes, parameters);

			// Abbruchbedingungen
			if (StringUtils.isEmpty(absClassPath)) {
				throw new ReflectionHandlerException("Der abs. Klassenpfad zur Klasse, auf die via Java-Reflections zugegriffen werden soll ist nicht gesetzt.", methodName,
						argTypes,
						parameters);
			}
			if (StringUtils.isEmpty(methodName)) {
				throw new ReflectionHandlerException("Der Name der Klassen-Methode, die via Java-Reflections aufgerufen werden soll ist nicht gesetzt.", methodName, argTypes,
						parameters);
			}

			Object callerObject = null;
			Class<?> callerClass = Class.forName(absClassPath);

			// Use cached object
			if (this.cachingEnabled && useCachedReflections) {
				ReflectionCacheKey reflectionCacheKey = new ReflectionCacheKey(callerClass, methodName, argTypes);
				if (this.reflectionDataCache.containsKey(reflectionCacheKey)) {
					ReflectionData reflectionData = this.reflectionDataCache.get(reflectionCacheKey);
					callerObject = (reflectionData != null && reflectionData.getCallerObject() != null) ? reflectionData.getCallerObject() : null;
				}
			}

			// Use new object
			if (callerObject == null) {
				callerObject = callerClass.newInstance();
			}

			fieldValue = callMethodByReflection(callerObject, methodName, argTypes, parameters, useCachedResult, useCachedReflections);

		} catch (ClassNotFoundException e) {
			throw new ReflectionHandlerException("Die Klasse '" + absClassPath + "' kann nicht gefunden werden.", e, methodName, argTypes, parameters);
		} catch (InstantiationException e) {
			throw new ReflectionHandlerException("Die Klasse '" + absClassPath + "' kann nicht instanziiert werden.", e, methodName, argTypes, parameters);
		} catch (IllegalAccessException e) {
			throw new ReflectionHandlerException("Auf die Klasse '" + absClassPath + "' darf nicht zugegriffen werden.", e, methodName, argTypes, parameters);
		}

		return fieldValue;
	}


	/**
	 * Aufruf einer Klassen-Methode von einem initialisierten Objekt via
	 * Java-Reflections und R�ckgabe des jeweiligen R�ckgabeobjekts.
	 *
	 * @param callerObject
	 *            Initialisiertes Objekt einer Klasse, von dem via
	 *            Java-Reflections eine Methode aufgerufen werden soll.
	 * @param methodName
	 *            Name der Klassen-Methode, die via Java-Reflections aufgerufen
	 *            werden soll. (z.B. rtvVorgangsadresse)
	 * @param customArgTypes
	 *            Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen! (z.B.[IEnv.class, String.class, ...])
	 * @param parameters
	 *            Array mit den Objekten der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen!
	 * @return R�ckgabeobjekt
	 * @throws ReflectionHandlerException
	 */
	public Object callMethodByReflection(final Object callerObject, final String methodName, final Class<?>[] customArgTypes, final Object[] parameters)
			throws ReflectionHandlerException {
		return callMethodByReflection(callerObject, methodName, customArgTypes, parameters, this.cachingEnabled, this.cachingEnabled);
	}


	/**
	 * Aufruf einer Klassen-Methode von einem initialisierten Objekt via
	 * Java-Reflections und R�ckgabe des jeweiligen R�ckgabeobjekts.
	 *
	 * @param callerObject
	 *            Initialisiertes Objekt einer Klasse, von dem via
	 *            Java-Reflections eine Methode aufgerufen werden soll.
	 * @param methodName
	 *            Name der Klassen-Methode, die via Java-Reflections aufgerufen
	 *            werden soll. (z.B. rtvVorgangsadresse)
	 * @param customArgTypes
	 *            Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen! (z.B.[IEnv.class, String.class, ...])
	 * @param parameters
	 *            Array mit den Objekten der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen!
	 * @param useCachedResult
	 *            Verwendung eines ggf. schon fr�heren gecachten
	 *            R�ckgabeobjekts, welches bereits fr�her mit den selben
	 *            Abfrageparametern ermittelt wurde. (true/false)
	 * @param useCachedReflections
	 *            Verwendung von fr�heren gecachten Reflections, welche bereits
	 *            fr�her mit den selben Informationen erzeugt wurde.
	 *            (true/false)
	 * @return R�ckgabeobjekt
	 * @throws ReflectionHandlerException
	 */
	public Object callMethodByReflection(final Object callerObject, final String methodName, final Class<?>[] customArgTypes, final Object[] parameters,
			final boolean useCachedResult,
			final boolean useCachedReflections) throws ReflectionHandlerException {
		Object resultObject;
		Class<?>[] argTypes = customArgTypes;

		long startMillis = System.currentTimeMillis();
		boolean isCachedCall = false;

		// Define method parameter types
		argTypes = checkCustomArgTypes(customArgTypes, parameters);

		Class<?> callerClass = callerObject.getClass();
		ReflectionCacheKey reflectionCacheKey = new ReflectionCacheKey(callerClass, methodName, argTypes);

		try {

			// Abbruchbedingungen
			if (callerObject == null) {
				throw new ReflectionHandlerException("Das initialisiertes Objekt einer Klasse, von dem via Java-Reflections eine Methode aufgerufen werden soll ist nicht gesetzt.",
						methodName, argTypes, parameters);
			}
			if (StringUtils.isEmpty(methodName)) {
				throw new ReflectionHandlerException("Der Name der Klassen-Methode, die via Java-Reflections aufgerufen werden soll ist nicht gesetzt.", methodName, argTypes,
						parameters);
			}

			// Get cached result object
			ResultCacheKey resultCacheKey = new ResultCacheKey(callerObject, methodName, argTypes, parameters);
			if (this.cachingEnabled && useCachedResult && this.methodCallResultDataCache.containsKey(resultCacheKey)) {
				isCachedCall = true;
				return this.methodCallResultDataCache.get(resultCacheKey);
			}

			// Get cached reflection data
			ReflectionData reflectionData = null;
			if (this.cachingEnabled && useCachedReflections && this.reflectionDataCache.containsKey(reflectionCacheKey)) {
				reflectionData = this.reflectionDataCache.get(reflectionCacheKey);
			}

			// Getting Method
			Method declaredMethod;

			if (reflectionData != null && reflectionData.getDeclaredMethod() != null) {
				declaredMethod = reflectionData.getDeclaredMethod();
			} else {

				if (this.runThreadSave) {
					// Synchronisiert, wenn dieser Code von verschiedenen
					// Threads verwendet wird
					synchronized (this) {
						startMillis = System.currentTimeMillis();

						// Versuche die gecachte Reflection erneut zu holen, um
						// zu gucken, ob der vorhergehende Thread dieses im
						// synchroniesierten Block bereits angefragt hat.
						if (this.cachingEnabled && useCachedReflections && this.reflectionDataCache.containsKey(reflectionCacheKey)) {
							reflectionData = this.reflectionDataCache.get(reflectionCacheKey);
						}

						if (reflectionData != null && reflectionData.getDeclaredMethod() != null) {

							// Versuche das gecachte Ergebnisobject erneut zu
							// holen,
							// um
							// zu gucken, ob der vorhergehende Thread dieses im
							// synchroniesierten Block bereits angefragt hat.
							if (this.cachingEnabled && useCachedResult && this.methodCallResultDataCache.containsKey(resultCacheKey)) {
								isCachedCall = true;
								return this.methodCallResultDataCache.get(resultCacheKey);
							}

							declaredMethod = reflectionData.getDeclaredMethod();
						} else {
							if (argTypes != null) {
								declaredMethod = callerClass.getDeclaredMethod(methodName, argTypes);
							} else {
								declaredMethod = callerClass.getDeclaredMethod(methodName);
							}

							if (this.cachingEnabled && useCachedReflections) {
								reflectionData = new ReflectionData(callerObject, declaredMethod);
								this.reflectionDataCache.put(reflectionCacheKey, reflectionData);
							}
						}
					}
				} else {
					// Unsynchronisiert, wenn dieser Code nicht von
					// verschiedenen Threads verwendet wird
					if (argTypes != null) {
						declaredMethod = callerClass.getDeclaredMethod(methodName, argTypes);
					} else {
						declaredMethod = callerClass.getDeclaredMethod(methodName);
					}

					if (this.cachingEnabled && useCachedReflections) {
						reflectionData = new ReflectionData(callerObject, declaredMethod);
						this.reflectionDataCache.put(reflectionCacheKey, reflectionData);
					}
				}
			}

			if (declaredMethod != null) {

				if (this.runThreadSave) {
					// Synchronisiert, wenn dieser Code von verschiedenen
					// Threads verwendet wird
					synchronized (this) {
						startMillis = System.currentTimeMillis();

						// Versuche das gecachte Ergebnisobject erneut zu holen, um
						// zu gucken, ob der vorhergehende Thread dieses im
						// synchroniesierten Block bereits angefragt hat.
						if (this.cachingEnabled && useCachedResult && this.methodCallResultDataCache.containsKey(resultCacheKey)) {
							isCachedCall = true;
							return this.methodCallResultDataCache.get(resultCacheKey);
						}

						// Call method
						declaredMethod.setAccessible(true);
						if (argTypes != null) {
							resultObject = declaredMethod.invoke(callerObject, parameters);
						} else {
							resultObject = declaredMethod.invoke(callerObject);
						}

						if (this.cachingEnabled && useCachedResult) {
							this.methodCallResultDataCache.put(resultCacheKey, resultObject);
						}
					}
				} else {
					// Call method
					declaredMethod.setAccessible(true);
					startMillis = System.currentTimeMillis();
					if (argTypes != null) {
						resultObject = declaredMethod.invoke(callerObject, parameters);
					} else {
						resultObject = declaredMethod.invoke(callerObject);
					}

					if (this.cachingEnabled && useCachedResult) {
						this.methodCallResultDataCache.put(resultCacheKey, resultObject);
					}
				}

			} else {
				throw new ReflectionHandlerException("Es konnte keine passende Klassen-Methode '" + methodName + "' mit der angeforderten Parameterliste in der gegebenen Klasse '"
						+ callerObject.getClass().getName() + "' gefunden werden.", methodName, argTypes, parameters);
			}

		} catch (IllegalAccessException | SecurityException e) {
			throw new ReflectionHandlerException("Auf die Methode '" + methodName + "' darf nicht zugegriffen werden.", e, methodName, argTypes, parameters);
		} catch (NoSuchMethodException e) {
			throw new ReflectionHandlerException("Es konnte keine passende Klassen-Methode '" + methodName + "' mit der angeforderten Parameterliste in der gegebenen Klasse '"
					+ callerObject.getClass().getName() + "' gefunden werden.", e, methodName, argTypes, parameters);
		} catch (IllegalArgumentException e) {
			throw new ReflectionHandlerException("Die bei dem Aufruf der Methode '" + methodName + "' �bergebenen Parameter sind ung�ltig.", e, methodName, argTypes, parameters);
		} catch (InvocationTargetException e) {
			throw new ReflectionHandlerException("Die aufgerufene Methode '" + methodName + "' hat einen Fehler zur�ckgeliefert.", e.getTargetException(), methodName, argTypes,
					parameters);
		} finally {
			long callTime = System.currentTimeMillis() - startMillis;
			this.statistic.createUpdateStatisticElement(reflectionCacheKey, callTime, isCachedCall);
		}

		return resultObject;
	}

	/**
	 * @return the statistic
	 */
	public ReflectionHandlerStatistic getStatistic() {
		return this.statistic;
	}

	/**
	 * Pr�fung, ob die Anzahl der Parametertypen zu der Anzahl der
	 * Parameter-Objekte passt. Wenn nicht, wird versucht das Array der
	 * Parametertypen aus den Parameter-Objekte erzeugt. Das kann jedoch zu
	 * Fehlern f�hren, gerade wenn Methoden gewissen Schnittstellen erwarten.
	 *
	 * @param customArgTypes
	 *            Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen! (z.B.[IEnv.class, String.class, ...])
	 * @param parameters
	 *            Array mit den Objekten der zur Klassen-Methode zugeh�rigen
	 *            Parameterliste. Die Reihenfolge der Parameterliste muss
	 *            �bereinstimmen!
	 * @return Array der Parametertypen
	 */
	private Class<?>[] checkCustomArgTypes(final Class<?>[] customArgTypes, final Object[] parameters) {
		Class<?>[] argTypes = null;
		if (parameters != null && parameters.length > 0) {

			if (customArgTypes != null && customArgTypes.length == parameters.length) {
				argTypes = customArgTypes;
			} else {
				argTypes = new Class[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					Object parameter = parameters[i];
					if (parameter != null) {
						argTypes[i] = parameter.getClass();
					}
				}
			}
		}
		return argTypes;
	}

	/**
	 * Innere Klasse zur Speicherung von ermittelten Reflection-Daten.
	 *
	 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
	 * @version 1.0
	 * @since 24.05.2018, 12:45:15
	 *
	 */
	private static class ReflectionData {

		/**
		 * Initialisiertes Objekt einer Klasse, von dem via Java-Reflections
		 * eine Methode aufgerufen werden soll.
		 */
		final Object callerObject;
		/**
		 * Reflection-Objekt einer Klassen-Methode, auf die zugegriffen werden
		 * kann.
		 */
		final Method declaredMethod;

		/**
		 * Default-Konstruktor
		 *
		 * @param callerObject
		 *            Initialisiertes Objekt einer Klasse, von dem via
		 *            Java-Reflections eine Methode aufgerufen werden soll.
		 * @param declaredMethod
		 *            Reflection-Objekt einer Klassen-Methode, auf die
		 *            zugegriffen werden kann.
		 */
		private ReflectionData(final Object callerObject, final Method declaredMethod) {
			super();
			this.callerObject = callerObject;
			this.declaredMethod = declaredMethod;
		}

		/**
		 * @return the callerObject
		 */
		public Object getCallerObject() {
			return this.callerObject;
		}

		/**
		 * @return the declaredMethod
		 */
		public Method getDeclaredMethod() {
			return this.declaredMethod;
		}
	}

	/**
	 * Klasse zur Erzeugung eines eineindeutigen Schl�ssels f�r den Ergebnis-Cache.
	 *
	 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
	 * @version 1.0
	 * @since 24.05.2018, 12:45:04
	 *
	 */
	private static class ResultCacheKey {

		/**
		 * Initialisiertes Objekt einer Klasse, von dem via Java-Reflections
		 * eine Methode aufgerufen werden soll.
		 */
		final Object callerObject;
		/**
		 * Name der Klassen-Methode, die via Java-Reflections aufgerufen werden
		 * soll. (z.B. rtvVorgangsadresse)
		 */
		final String methodName;
		/**
		 * Array mit den Class-Typen der zur Klassen-Methode zugeh�rigen
		 * Parameterliste. Die Reihenfolge der Parameterliste muss
		 * �bereinstimmen! (z.B.[IEnv.class, String.class, ...])
		 */
		final Class<?>[] argTypes;
		/**
		 * Array mit den Objekten der zur Klassen-Methode zugeh�rigen
		 * Parameterliste. Die Reihenfolge der Parameterliste muss
		 * �bereinstimmen!
		 */
		final Object[] parameters;

		/**
		 * Default-Konstruktor
		 *
		 * @param callerObject
		 *            Initialisiertes Objekt einer Klasse, von dem via
		 *            Java-Reflections eine Methode aufgerufen werden soll.
		 * @param methodName
		 *            Name der Klassen-Methode, die via Java-Reflections
		 *            aufgerufen werden soll. (z.B. rtvVorgangsadresse)
		 * @param argTypes
		 *            Array mit den Class-Typen der zur Klassen-Methode
		 *            zugeh�rigen Parameterliste. Die Reihenfolge der
		 *            Parameterliste muss �bereinstimmen! (z.B.[IEnv.class,
		 *            String.class, ...])
		 * @param parameters
		 *            Array mit den Objekten der zur Klassen-Methode zugeh�rigen
		 *            Parameterliste. Die Reihenfolge der Parameterliste muss
		 *            �bereinstimmen!
		 */
		private ResultCacheKey(final Object callerObject, final String methodName, final Class<?>[] argTypes, final Object[] parameters) {
			super();
			this.callerObject = callerObject;
			this.methodName = methodName;
			this.argTypes = argTypes;
			this.parameters = parameters;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(this.argTypes);
			result = prime * result + ((this.callerObject == null) ? 0 : this.callerObject.hashCode());
			result = prime * result + ((this.methodName == null) ? 0 : this.methodName.hashCode());
			result = prime * result + Arrays.hashCode(this.parameters);
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
			ResultCacheKey other = (ResultCacheKey) obj;
			if (!Arrays.equals(this.argTypes, other.argTypes)) {
				return false;
			}
			if (this.callerObject == null) {
				if (other.callerObject != null) {
					return false;
				}
			} else if (this.callerObject != other.callerObject) {
				return false;
			}
			if (this.methodName == null) {
				if (other.methodName != null) {
					return false;
				}
			} else if (!this.methodName.equals(other.methodName)) {
				return false;
			}
			if (!Arrays.equals(this.parameters, other.parameters)) {
				return false;
			}
			return true;
		}
	}
}