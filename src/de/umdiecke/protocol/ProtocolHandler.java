/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:49:07
 *
 */
public class ProtocolHandler implements Cloneable {

	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final ProtocolElement commonProtocolElement;

	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final List<ProtocolElement> protocolElementList;

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 */
	public ProtocolHandler() {
		this("ID_" + System.currentTimeMillis());
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 */
	public ProtocolHandler(final String id) {
		this.commonProtocolElement = new ProtocolElement(id);
		this.protocolElementList = Collections.synchronizedList(new ArrayList<ProtocolElement>());
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 * @param value
	 */
	public synchronized void addCommonContentValue(final String key, final String value) {
		this.commonProtocolElement.addContentValue(key, value);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 * @param value
	 */
	public synchronized void addToCommonCounterValue(final String key, final Long value) {
		this.commonProtocolElement.addToCounterValue(key, value);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param key
	 * @param value
	 */
	public void subtractFromCommonCounterValue(final String key, final Long value) {
		this.commonProtocolElement.subtractFromCounterValue(key, value);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolElement
	 */
	public void addProtocolElement(final ProtocolElement protocolElement) {
		synchronized (this) {
			this.protocolElementList.add(protocolElement);
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolElement
	 */
	public void removeProtocolElement(final ProtocolElement protocolElement) {
		synchronized (this) {
			this.protocolElementList.remove(protocolElement);
		}
	}

	/**
	 * Leeren der protokollierten Elemente
	 *
	 */
	public void clearProtocol() {
		synchronized (this) {
			this.protocolElementList.clear();
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolExport
	 * @param clearAfter
	 * @throws Exception
	 */
	public void exportProtocol(final IProtocolExport protocolExport, final boolean clearAfter) throws Exception {
		ProtocolElement curCommonProtocolElement;
		List<ProtocolElement> curProtocolElementList;
		synchronized (this) {
			curCommonProtocolElement = getCommonProtocolElement();
			curProtocolElementList = getProtocolElementList();

			if (clearAfter) {
				clearProtocol();
			}
		}

		if (protocolExport != null) {
			exportProtocol(protocolExport, curCommonProtocolElement, curProtocolElementList);
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolExportList
	 * @param clearAfter
	 * @throws Exception
	 */
	public void exportProtocols(final List<IProtocolExport> protocolExportList, final boolean clearAfter) throws Exception {
		ProtocolElement curCommonProtocolElement;
		List<ProtocolElement> curProtocolElementList;

		synchronized (this) {
			curCommonProtocolElement = getCommonProtocolElement();
			curProtocolElementList = getProtocolElementList();

			if (clearAfter) {
				clearProtocol();
			}
		}

		if (protocolExportList != null) {
			for (IProtocolExport iProtocolExport : protocolExportList) {
				exportProtocol(iProtocolExport, curCommonProtocolElement, curProtocolElementList);
			}
		}

	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolExport
	 * @param curCommonProtocolElement
	 * @param curProtocolElementList
	 * @throws Exception
	 */
	private void exportProtocol(final IProtocolExport protocolExport, final ProtocolElement curCommonProtocolElement, final List<ProtocolElement> curProtocolElementList)
			throws Exception {

		synchronized (protocolExport) {
			if (protocolExport != null) {
				protocolExport.doExport(curCommonProtocolElement, curProtocolElementList);
			}
		}
	}

	/**
	 * @return the commonProtocolElement
	 */
	public ProtocolElement getCommonProtocolElement() {
		return this.commonProtocolElement;
	}

	/**
	 * @return the protocolElementSet
	 */
	public List<ProtocolElement> getProtocolElementList() {
		return new ArrayList<>(this.protocolElementList);
	}
}