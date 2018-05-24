/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.net;

import java.net.URL;

/**
 * POJO-Klasse für Authentifizierungsdaten.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:54:59
 *
 */
public class PasswordAuthenticationData {

	private final URL url;
	private final String username;
	private final String password;

	/**
	 * Default-Konstruktor
	 *
	 * @param url
	 * @param username
	 * @param password
	 */
	public PasswordAuthenticationData(final URL url, final String username, final String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	/**
	 * @return the url
	 */
	public URL getEpr() {
		return this.url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}
}