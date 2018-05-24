/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Eine {@link LinkedHashMap} mit der Cache-Strategie Least recently used (LRU).
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:24:31
 *
 * @param <K>
 *           Typ der Schlüssel (keys), die von der Map verwaltet werden.
 * @param <V>
 *           Typ der zugeordneten Werte (values).
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

	/**
	 * UId
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Maximale Größe der Map
	 */
	private int capacity;

	/**
	 * Default-Konstruktor
	 *
	 * @param capacity
	 *            Maximale Gr��e der Map
	 */
	public LRUCache(final int capacity) {
		super(capacity, 0.5f, true);
		this.capacity = capacity;
	}

	@Override
	public boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
		return size() > this.capacity;
	}
}