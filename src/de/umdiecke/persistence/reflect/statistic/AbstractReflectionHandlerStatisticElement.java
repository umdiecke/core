/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.reflect.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.umdiecke.persistence.reflect.ReflectionCacheKey;
import de.umdiecke.protocol.ProtocolElement;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:48:31
 *
 */
abstract class AbstractReflectionHandlerStatisticElement {

	private final ReflectionCacheKey reflectionCacheKey;

	private final List<Long> callTimeList;
	private final List<Long> callTimeListWithoutCachedCalls;

	private long maxCallTime;
	private long minCallTime;

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param reflectionCacheKey
	 */
	AbstractReflectionHandlerStatisticElement(final ReflectionCacheKey reflectionCacheKey) {
		this.reflectionCacheKey = reflectionCacheKey;
		this.callTimeList = new ArrayList<>();
		this.callTimeListWithoutCachedCalls = new ArrayList<>();
		this.maxCallTime = 0l;
		this.minCallTime = 0l;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param callTime
	 * @param isCachedCall
	 */
	void updateStatisticElement(final long callTime, final boolean isCachedCall) {

		this.callTimeList.add(callTime);
		if (!isCachedCall) {
			this.callTimeListWithoutCachedCalls.add(callTime);
		}

		if (!isCachedCall && callTime > this.maxCallTime) {
			this.maxCallTime = callTime;
		}
		if (!isCachedCall && callTime > 0l && (this.minCallTime == 0l || callTime < this.minCallTime)) {
			this.minCallTime = callTime;
		}
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @param callTimeList
	 * @return
	 */
	private long calculateSumCallTime(final List<Long> callTimeList) {
		long sumCallTime = 0l;
		for (Long callTime : callTimeList) {
			sumCallTime += callTime.longValue();
		}
		return sumCallTime;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param callTimeList
	 * @return
	 */
	private long calculateAvgCallTime(final List<Long> callTimeList) {
		long avgCallTime = 0l;

		long sumCallTime = calculateSumCallTime(callTimeList);
		long callCount = callTimeList.size();
		if (sumCallTime > 0l && !callTimeList.isEmpty()) {
			avgCallTime = sumCallTime / callCount;
		}
		return avgCallTime;
	}

	/**
	 * @return the reflectionCacheKey
	 */
	public ReflectionCacheKey getReflectionCacheKey() {
		return this.reflectionCacheKey;
	}

	/**
	 * @return the reflectionName
	 */
	public String getReflectionName() {
		return (this.reflectionCacheKey != null) ? getReflectionClassPath() + "." + getReflectionMethod() + "(" + getArgTypesCommaSeparated() + ")" : "";
	}

	/**
	 * @return the reflectionClass
	 */
	public String getReflectionClass() {
		return this.reflectionCacheKey != null ? this.reflectionCacheKey.getCallerClass().getSimpleName() : "";
	}

	/**
	 * @return the reflectionClassPath
	 */
	public String getReflectionClassPath() {
		return this.reflectionCacheKey != null ? this.reflectionCacheKey.getCallerClass().getName() : "";
	}

	/**
	 * @return the reflectionMethod
	 */
	public String getReflectionMethod() {
		return this.reflectionCacheKey != null ? this.reflectionCacheKey.getMethodName() : "";
	}

	/**
	 * @return the reflectionMethodParamArgTypes
	 */
	public Class<?>[] getReflectionMethodParamArgTypes() {
		return this.reflectionCacheKey != null ? this.reflectionCacheKey.getArgTypes() : null;
	}

	/**
	 * @return the callCount
	 */
	public long getCallCount() {
		return this.callTimeList.size();
	}

	/**
	 * @return the callCountWithoutCachedCalls
	 */
	public long getCallCountWithoutCachedCalls() {
		return this.callTimeListWithoutCachedCalls.size();
	}

	/**
	 * @return the maxCallTime
	 */
	public long getMaxCallTime() {
		return this.maxCallTime;
	}

	/**
	 * @return the minCallTime
	 */
	public long getMinCallTime() {
		return this.minCallTime;
	}

	/**
	 * @return the avgCallTime
	 */
	public long getAvgCallTime() {
		return calculateAvgCallTime(this.callTimeList);
	}

	/**
	 * @return the avgCallTimeWithoutCachedCalls
	 */
	public long getAvgCallTimeWithoutCachedCalls() {
		return calculateAvgCallTime(this.callTimeListWithoutCachedCalls);
	}

	/**
	 * @return the sumCallTime
	 */
	public long getSumCallTime() {
		return calculateSumCallTime(this.callTimeList);
	}

	/**
	 * @return the sumCallTimeWithoutCachedCalls
	 */
	public long getSumCallTimeWithoutCachedCalls() {
		return calculateSumCallTime(this.callTimeListWithoutCachedCalls);
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @param id
	 * @param statistic
	 * @return
	 */
	protected ProtocolElement createProtocolElement(final String statistic) {
		return createProtocolElement(statistic, null);
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @param id
	 * @param statistic
	 * @param thread
	 * @return
	 */
	protected ProtocolElement createProtocolElement(final String statistic, final String thread) {
		ProtocolElement protocolElement = new ProtocolElement(Integer.toString(hashCode()));
		protocolElement.addContentValue("Statistik", StringUtils.isNotBlank(thread) ? statistic : "global");
		protocolElement.addContentValue("Thread", StringUtils.isNotBlank(thread) ? thread : "alle");
		String reflection = getReflectionName();
		protocolElement.addContentValue("Reflection", StringUtils.isNotBlank(reflection) ? reflection : "-");
		protocolElement.addContentValue("Anzahl Aufrufe (gesamt)", getCallCount());
		protocolElement.addContentValue("Anzahl Aufrufe (ungecachte)", getCallCountWithoutCachedCalls());
		protocolElement.addContentValue("Anzahl Aufrufe (gecachte)", (getCallCount() - getCallCountWithoutCachedCalls()));
		protocolElement.addContentValue("Aufrufzeit (sum gesamt ms)", getSumCallTime());
		protocolElement.addContentValue("Aufrufzeit (sum ungecachte ms)", getSumCallTimeWithoutCachedCalls());
		protocolElement.addContentValue("Aufrufzeit (sum gecachte ms)", (getSumCallTime() - getSumCallTimeWithoutCachedCalls()));
		protocolElement.addContentValue("Aufrufzeit (max ms)", getMaxCallTime());
		protocolElement.addContentValue("Aufrufzeit (min ms)", getMinCallTime());
		protocolElement.addContentValue("Aufrufzeit (avg gesamt ms)", getAvgCallTime());
		protocolElement.addContentValue("Aufrufzeit (avg ungecachte ms)", getAvgCallTimeWithoutCachedCalls());
		return protocolElement;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("\tAnzahl Aufrufe (gesamt): " + getCallCount() + "\n");
		result.append("\tAnzahl Aufrufe (ungecachte): " + getCallCountWithoutCachedCalls() + "\n");
		result.append("\tAnzahl Aufrufe (gecachte): " + (getCallCount() - getCallCountWithoutCachedCalls()) + "\n");
		result.append("\tAufrufzeit (sum gesamt): " + getSumCallTime() + "ms\n");
		result.append("\tAufrufzeit (sum ungecachte): " + getSumCallTimeWithoutCachedCalls() + "ms\n");
		result.append("\tAufrufzeit (sum gecachte): " + (getSumCallTime() - getSumCallTimeWithoutCachedCalls()) + "ms\n");
		result.append("\tAufrufzeit (max): " + getMaxCallTime() + "ms\n");
		result.append("\tAufrufzeit (min): " + getMinCallTime() + "ms\n");
		result.append("\tAufrufzeit (avg gesamt): " + getAvgCallTime() + "ms\n");
		result.append("\tAufrufzeit (avg ungecachte): " + getAvgCallTimeWithoutCachedCalls() + "ms\n\n");
		return result.toString();
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @return
	 */
	String getArgTypesCommaSeparated() {
		String argTypes = getReflectionMethodParamArgTypes() != null
				? Arrays.stream(getReflectionMethodParamArgTypes()).map(Class::getName).collect(Collectors.joining(", ")) : "";
				return argTypes;
	}
}