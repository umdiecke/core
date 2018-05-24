/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.reflect.statistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.umdiecke.persistence.reflect.ReflectionCacheKey;
import de.umdiecke.protocol.IProtocolExport;
import de.umdiecke.protocol.ProtocolElement;
import de.umdiecke.protocol.ProtocolHandler;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:48:20
 *
 */
public class ReflectionHandlerStatistic {

	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final boolean statisticEnabled;

	/**
	 * FIXME eitc0033 JavaDoc
	 */
	private final boolean detailedStatisticEnabled;

	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final Map<ReflectionCacheKey, ReflectionHandlerStatisticElement> reflectionStatisticMap;

	/**
	 * FIXME eitc0033 JavaDoc
	 */
	private final Map<String, ReflectionHandlerStatisticThreadElement> threadCallTimesMap;

	private long callCounts;
	private long callCountsWithoutCachedCalls;
	private long sumCallTime;
	private long sumCallTimeWithoutCachedCalls;
	private long maxCallTime;
	private long minCallTime;

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 */
	public ReflectionHandlerStatistic(final boolean statisticEnabled, final boolean detailedStatisticEnabled) {
		this.statisticEnabled = statisticEnabled;
		this.detailedStatisticEnabled = detailedStatisticEnabled;
		this.reflectionStatisticMap = new HashMap<>();
		this.threadCallTimesMap = new HashMap<>();
		this.callCounts = 0l;
		this.callCountsWithoutCachedCalls = 0l;
		this.sumCallTime = 0l;
		this.sumCallTimeWithoutCachedCalls = 0l;
		this.maxCallTime = 0l;
		this.minCallTime = 0l;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param reflectionCacheKey
	 * @param callTime
	 * @param isCachedCall
	 */
	public void createUpdateStatisticElement(final ReflectionCacheKey reflectionCacheKey, final long callTime, final boolean isCachedCall) {
		if(this.statisticEnabled){
			synchronized (this) {

				updateGlobalStatistic(callTime, isCachedCall);

				if (this.reflectionStatisticMap.containsKey(reflectionCacheKey)) {
					this.reflectionStatisticMap.get(reflectionCacheKey).updateStatisticElement(callTime, isCachedCall);
				} else {
					ReflectionHandlerStatisticElement reflectionHandlerStatisticElement = new ReflectionHandlerStatisticElement(reflectionCacheKey, this.detailedStatisticEnabled);
					reflectionHandlerStatisticElement.updateStatisticElement(callTime, isCachedCall);
					this.reflectionStatisticMap.put(reflectionCacheKey, reflectionHandlerStatisticElement);
				}
			}
		}
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @param callTime
	 * @param isCachedCall
	 */
	private void updateGlobalStatistic(final long callTime, final boolean isCachedCall) {
		this.callCounts++;
		this.sumCallTime += callTime;
		if (!isCachedCall) {
			this.callCountsWithoutCachedCalls++;
			this.sumCallTimeWithoutCachedCalls += callTime;
		}

		if (!isCachedCall && callTime > this.maxCallTime) {
			this.maxCallTime = callTime;
		}
		if (!isCachedCall && callTime > 0l && (this.minCallTime == 0l || callTime < this.minCallTime)) {
			this.minCallTime = callTime;
		}

		if (this.detailedStatisticEnabled) {
			String threadName = Thread.currentThread().getName();
			if (this.threadCallTimesMap.containsKey(threadName)) {
				this.threadCallTimesMap.get(threadName).updateStatisticElement(callTime, isCachedCall);
			} else {
				ReflectionHandlerStatisticThreadElement reflectionHandlerStatisticElement = new ReflectionHandlerStatisticThreadElement(null, threadName);
				reflectionHandlerStatisticElement.updateStatisticElement(callTime, isCachedCall);
				this.threadCallTimesMap.put(threadName, reflectionHandlerStatisticElement);
			}
		}
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @return
	 * @throws Exception
	 */
	public void exportStatistic(final List<IProtocolExport> protocolExporterList) throws Exception {
		createStatistic().exportProtocols(protocolExporterList, true);
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @return
	 */
	private ProtocolHandler createStatistic() {
		ProtocolHandler protocolHandler = new ProtocolHandler("Statistik");

		// Allgemeine Statistik
		protocolHandler.addProtocolElement(createProtocolElement());

		// Detailierte Allgemeine Statistik
		if (this.detailedStatisticEnabled) {
			for (Entry<String, ReflectionHandlerStatisticThreadElement> element : this.threadCallTimesMap.entrySet()) {
				protocolHandler.addProtocolElement(element.getValue().createProtocolElement("global"));
			}
		}

		for (Entry<ReflectionCacheKey, ReflectionHandlerStatisticElement> entry : this.reflectionStatisticMap.entrySet()) {
			entry.getValue().createProtocolElements("einzel", protocolHandler);
		}
		return protocolHandler;
	}

	/**
	 * FIXME eitc0033 JavaDoc
	 *
	 * @param id
	 * @return
	 */
	private ProtocolElement createProtocolElement() {
		ProtocolElement protocolElement = new ProtocolElement(hashCode() + "");
		protocolElement.addContentValue("Statistik", "global");
		protocolElement.addContentValue("Thread", "alle");
		protocolElement.addContentValue("Reflection", "-");
		protocolElement.addContentValue("Anzahl Aufrufe (gesamt)", getCallCounts());
		protocolElement.addContentValue("Anzahl Aufrufe (ungecachte)", getCallCountsWithoutCachedCalls());
		protocolElement.addContentValue("Anzahl Aufrufe (gecachte)", (getCallCounts() - getCallCountsWithoutCachedCalls()));
		protocolElement.addContentValue("Aufrufzeit (sum gesamt ms)", getSumCallTime());
		protocolElement.addContentValue("Aufrufzeit (sum ungecachte ms)", getSumCallTimeWithoutCachedCalls());
		protocolElement.addContentValue("Aufrufzeit (sum gecachte ms)", (getSumCallTime() - getSumCallTimeWithoutCachedCalls()));
		protocolElement.addContentValue("Aufrufzeit (max ms)", getMaxCallTime());
		protocolElement.addContentValue("Aufrufzeit (min ms)", getMinCallTime());
		protocolElement.addContentValue("Aufrufzeit (avg gesamt ms)", ((getSumCallTime() > 0l && getCallCounts() > 0l) ? (getSumCallTime() / getCallCounts()) : 0l));
		protocolElement.addContentValue("Aufrufzeit (avg ungecachte ms)", ((getSumCallTimeWithoutCachedCalls() > 0l && getCallCountsWithoutCachedCalls() > 0l)
				? (getSumCallTimeWithoutCachedCalls() / getCallCountsWithoutCachedCalls()) : 0l));
		return protocolElement;
	}

	/**
	 * Erzeugt eine formatierte Ausgabe der Statistik.
	 *
	 * @return Formatierte Ausgabe der Statistik als String
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("Globale Statistik\n");
		result.append("Alle Threads\n");
		result.append("\tAnzahl Aufrufe (gesamt): " + getCallCounts() + "\n");
		result.append("\tAnzahl Aufrufe (ungecachte): " + getCallCountsWithoutCachedCalls() + "\n");
		result.append("\tAnzahl Aufrufe (gecachte): " + (getCallCounts() - getCallCountsWithoutCachedCalls()) + "\n");
		result.append("\tAufrufzeit (sum gesamt): " + getSumCallTime() + "ms\n");
		result.append("\tAufrufzeit (sum ungecachte): " + getSumCallTimeWithoutCachedCalls() + "ms\n");
		result.append("\tAufrufzeit (sum gecachte): " + (getSumCallTime() - getSumCallTimeWithoutCachedCalls()) + "ms\n");
		result.append("\tAufrufzeit (max): " + getMaxCallTime() + "ms\n");
		result.append("\tAufrufzeit (min): " + getMinCallTime() + "ms\n");
		result.append("\tAufrufzeit (avg gesamt): " + ((getSumCallTime() > 0l && getCallCounts() > 0l) ? (getSumCallTime() / getCallCounts()) : 0l) + "ms\n");
		result.append("\tAufrufzeit (avg ungecachte): " + ((getSumCallTimeWithoutCachedCalls() > 0l && getCallCountsWithoutCachedCalls() > 0l)
				? (getSumCallTimeWithoutCachedCalls() / getCallCountsWithoutCachedCalls()) : 0l) + "ms\n\n");

		if (this.detailedStatisticEnabled) {
			for (Entry<String, ReflectionHandlerStatisticThreadElement> element : this.threadCallTimesMap.entrySet()) {
				result.append("Thread: " + element.getValue().getThreadName() + "\n");
				result.append(element.getValue().toString());
			}
		}

		result.append("Einzel Statistik\n\n");
		for (Entry<ReflectionCacheKey, ReflectionHandlerStatisticElement> entry : this.reflectionStatisticMap.entrySet()) {
			result.append(entry.getValue().toString());
		}

		return result.toString();
	}

	/**
	 * @return the reflectionStatisticMap
	 */
	public Map<ReflectionCacheKey, ReflectionHandlerStatisticElement> getReflectionStatisticMap() {
		return this.reflectionStatisticMap;
	}

	/**
	 * @return the callCounts
	 */
	public long getCallCounts() {
		return this.callCounts;
	}

	/**
	 * @return the callCountsWithoutCachedCalls
	 */
	public long getCallCountsWithoutCachedCalls() {
		return this.callCountsWithoutCachedCalls;
	}

	/**
	 * @return the sumCallTime
	 */
	public long getSumCallTime() {
		return this.sumCallTime;
	}

	/**
	 * @return the sumCallTimeWithoutCachedCalls
	 */
	public long getSumCallTimeWithoutCachedCalls() {
		return this.sumCallTimeWithoutCachedCalls;
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
}