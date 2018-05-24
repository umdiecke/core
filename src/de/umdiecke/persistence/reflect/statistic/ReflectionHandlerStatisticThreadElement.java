/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.reflect.statistic;

import de.umdiecke.persistence.reflect.ReflectionCacheKey;
import de.umdiecke.protocol.ProtocolElement;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:48:00
 *
 */
public class ReflectionHandlerStatisticThreadElement extends AbstractReflectionHandlerStatisticElement {

	/**
	 * FIXME eitc0033 JavaDoc
	 */
	private String threadName;

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param reflectionCacheKey
	 */
	public ReflectionHandlerStatisticThreadElement(final ReflectionCacheKey reflectionCacheKey, final String threadName) {
		super(reflectionCacheKey);
		this.threadName = threadName;
	}

	@Override
	protected ProtocolElement createProtocolElement(final String statistic) {
		return super.createProtocolElement(statistic, this.threadName);
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return this.threadName;
	}
}