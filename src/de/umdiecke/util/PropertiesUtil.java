/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.util;

import java.util.Map.Entry;
import java.util.Properties;

/**
 * <p>
 * Class with methods for handling properties objects.
 * </p>
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:25:31
 *
 */
public abstract class PropertiesUtil {

	/**
	 * <p>
	 * Formats a properties object to a string.
	 * </p>
	 *
	 * @param prop the properties object
	 * @param includeType true, the object type of the entry VALUE will be included in the return else false
	 *
	 * @return a string in the format <em>"[KEY: abc | VALUE: def]"</em> or if includeType = FALSE <em>"[KEY: abc | VALUE: def | TYPE: java.lang.String]"</em>.
	 */
	public static String getPropertyAsString(final Properties prop, final boolean includeType) {
		StringBuilder result = new StringBuilder();
		if (prop != null) {
			for (Entry<Object, Object> entry : prop.entrySet()) {
				result.append(PropertiesUtil.entryToString(entry, includeType));
			}
		}
		return result.toString();
	}

	/**
	 * <p>
	 * Formats a EntrySet to a string.
	 * </p>
	 *
	 * @param entry the entry
	 * @param includeType true, the object type of the entry VALUE will be included in the return else false
	 *
	 * @return a string in the format <em>"[KEY=abc VALUE=def]"</em> or if includeType = FALSE <em>"[KEY=abc VALUE=def TYPE=String]"</em>.
	 */
	protected static String entryToString(final Entry<Object, Object> entry, final boolean includeType) {
		StringBuilder result = new StringBuilder();
		result.append("[KEY=");
		result.append(entry.getKey());
		result.append(" VALUE=");
		result.append(entry.getValue());
		if (includeType) {
			result.append(" TYPE=");
			result.append(entry.getValue().getClass().getSimpleName());
		}
		result.append("] ");
		return result.toString();
	}
}