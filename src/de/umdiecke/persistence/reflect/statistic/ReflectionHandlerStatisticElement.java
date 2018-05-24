/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.reflect.statistic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.umdiecke.persistence.reflect.ReflectionCacheKey;
import de.umdiecke.protocol.ProtocolHandler;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:48:11
 *
 */
public class ReflectionHandlerStatisticElement extends AbstractReflectionHandlerStatisticElement {

	/**
	 * FIXME eitc0033 JavaDoc
	 */
	private final Map<String, ReflectionHandlerStatisticThreadElement> threadCallTimesMap;

	/**
	 * FIXME eitc0033 JavaDoc
	 */
	private final boolean detailedStatistic;

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param reflectionCacheKey
	 */
	public ReflectionHandlerStatisticElement(final ReflectionCacheKey reflectionCacheKey, final boolean detailedStatistic) {
		super(reflectionCacheKey);
		this.detailedStatistic = detailedStatistic;
		this.threadCallTimesMap = new HashMap<>();
	}

	@Override
	void updateStatisticElement(final long callTime, final boolean isCachedCall) {
		super.updateStatisticElement(callTime, isCachedCall);

		if (this.detailedStatistic) {
			String threadName = Thread.currentThread().getName();

			if (this.threadCallTimesMap.containsKey(threadName)) {
				this.threadCallTimesMap.get(threadName).updateStatisticElement(callTime, isCachedCall);
			} else {
				ReflectionHandlerStatisticThreadElement reflectionHandlerStatisticThreadElement = new ReflectionHandlerStatisticThreadElement(getReflectionCacheKey(), threadName);
				reflectionHandlerStatisticThreadElement.updateStatisticElement(callTime, isCachedCall);
				this.threadCallTimesMap.put(threadName, reflectionHandlerStatisticThreadElement);
			}
		}
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @param id
	 * @param statistic
	 * @param protocolHandler
	 * @return
	 */
	protected void createProtocolElements(final String statistic, final ProtocolHandler protocolHandler) {
		protocolHandler.addProtocolElement(super.createProtocolElement(statistic, "alle"));

		if (this.detailedStatistic) {
			for (Entry<String, ReflectionHandlerStatisticThreadElement> element : this.threadCallTimesMap.entrySet()) {
				protocolHandler.addProtocolElement(element.getValue().createProtocolElement(statistic, element.getValue().getThreadName()));
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Statistik f�r: ");
		result.append(getReflectionClassPath() + "." + getReflectionMethod() + "(" + getArgTypesCommaSeparated() + ")\n");
		result.append("Alle Threads\n");
		result.append(super.toString());

		if (this.detailedStatistic) {
			for (Entry<String, ReflectionHandlerStatisticThreadElement> element : this.threadCallTimesMap.entrySet()) {
				result.append("Thread: " + element.getValue().getThreadName() + "\n");
				result.append(element.getValue().toString());
			}
		}

		result.append("\n");
		return result.toString();
	}
}