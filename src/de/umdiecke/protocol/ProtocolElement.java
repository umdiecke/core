/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.umdiecke.util.DataFormatter;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:27:39
 *
 */
public class ProtocolElement {

	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final String id;

	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final Map<String, Object> contentValueMap;

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 */
	public ProtocolElement(final String id) {
		this.id = id;
		this.contentValueMap = new LinkedHashMap<>();
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 * @param value
	 */
	public void addContentValue(final String key, final Object value) {

		if (StringUtils.isNotBlank(key)) {

			// Check Value
			Object contentValue = value;
			if (contentValue == null) {
				contentValue = "";
			}

			this.contentValueMap.put(key, contentValue);
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 * @param value
	 */
	public void addToContentValue(final String key, final Object value) {

		if (StringUtils.isNotBlank(key)) {

			// Check Value
			Object contentValue = value;
			if (contentValue == null) {
				contentValue = "";
			}

			if (this.contentValueMap.containsKey(key)) {
				String currentContent = DataFormatter.formatObjectToString(this.contentValueMap.get(key));
				String newContent = DataFormatter.formatObjectToString(contentValue);
				currentContent = StringUtils.isNotBlank(currentContent) ? currentContent + ", " + newContent : newContent;
				this.contentValueMap.put(key, currentContent);
			} else {
				this.contentValueMap.put(key, contentValue);
			}
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 */
	public void addToCounterValue(final String key) {
		addToCounterValue(key, 1L);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 * @param value
	 */
	public void addToCounterValue(final String key, final Long value) {

		if (StringUtils.isNotBlank(key)) {

			// Check Value
			Long counterValue = value;
			if (counterValue == null) {
				counterValue = 0L;
			}

			// Summiere den Wert zum bereits bestehenden Wert
			if (this.contentValueMap.containsKey(key)) {
				Object curCounterValueObj = this.contentValueMap.get(key);
				if (curCounterValueObj instanceof Long) {
					counterValue = ((Long) curCounterValueObj) + counterValue;
				}
			}

			this.contentValueMap.put(key, counterValue);
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 * @param value
	 */
	public void subtractFromCounterValue(final String key, final Long value) {

		if (StringUtils.isNotBlank(key)) {

			// Check Value
			Long counterValue = value;
			if (counterValue == null) {
				counterValue = 0L;
			}

			// Subtrahiere den Wert vom bereits bestehenden Wert
			if (this.contentValueMap.containsKey(key)) {

				Object curCounterValueObj = this.contentValueMap.get(key);
				if (curCounterValueObj instanceof Long) {
					counterValue = ((Long) curCounterValueObj) - counterValue;
				}
			}

			this.contentValueMap.put(key, counterValue);
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the contentValueMap
	 */
	public Map<String, Object> getContentValueMap() {
		return new LinkedHashMap<>(this.contentValueMap);
	}
}