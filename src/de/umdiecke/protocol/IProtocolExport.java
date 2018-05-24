/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.protocol;

import java.util.List;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:27:52
 *
 */
public interface IProtocolExport {

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @return
	 */
	public boolean doExport(final ProtocolElement commonProtocolElement, List<ProtocolElement> protocolElementList) throws Exception;
}