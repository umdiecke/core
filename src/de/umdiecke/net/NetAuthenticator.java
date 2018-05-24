/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler-Klasse zur Authentifizierung für Netzwerkverbindungen.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:53:37
 *
 */
public final class NetAuthenticator extends Authenticator {

	private static final Map<String, PasswordAuthenticationData> authenticationMap = new LinkedHashMap<>();
	private static final NetAuthenticator authenticator = new NetAuthenticator();

	/**
	 * Default constructor is not visible
	 */
	private NetAuthenticator() {
		// Nicht genutzt
	}

	@Override
	protected synchronized PasswordAuthentication getPasswordAuthentication() {
		PasswordAuthentication result = null;

		URL requestingURL = getRequestingURL();
		String authority = requestingURL.getAuthority();
		if (NetAuthenticator.authenticationMap.containsKey(authority)) {
			PasswordAuthenticationData paData = NetAuthenticator.authenticationMap.get(authority);
			result = new PasswordAuthentication(paData.getUsername(), paData.getPassword().toCharArray());
		}
		return result;
	}

	/**
	 * Authentifizierungsdaten für Anforderungen gegen eine bestimmte URL werden im globalen statischen Authentifizierungsobjekt gespeichert. Die Authentifizierungsdaten für bereits registrierte URLs
	 * werden überschrieben.
	 *
	 * @param url
	 *           Für diese Adresse werden die Authentifizierungsdaten gespeichert
	 * @param username
	 *           Der Benutzername für die Authentifizierung über die Netzwerkadresse
	 * @param password
	 *           Das Kennwort zur Authentifizierung gegen die Netzwerkadresse
	 */
	public static synchronized void addAuthenticationData(final URL url, final String username, final String password) {
		PasswordAuthenticationData auth = new PasswordAuthenticationData(url, username, password);
		NetAuthenticator.authenticationMap.put(url.getAuthority(), auth);
		Authenticator.setDefault(NetAuthenticator.authenticator);
	}
}