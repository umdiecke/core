/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.net;

import java.util.Properties;

/**
 * Webservicekonfiguration
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:51:38
 *
 */
public class WebServiceConfig {

	/** URL zur WSDL des Webservices. */
	public static final String PROP_WS_BINDINGURL = "ws.bindingurl";

	/** Nutzername f�r den Login am Webservice. */
	public static final String PROP_WS_LOGIN_USERNAME = "ws.login.username";

	/** Passwort f�r den Login am Webservice. */
	public static final String PROP_WS_LOGIN_PASSWORD = "ws.login.password";

	/** Proxy-Host, falls der Webservice nur �ber einen Proxy erreichbar ist. */
	public static final String PROP_WS_PROXY_HOST = "ws.proxy.host";

	/** Proxy-Port, falls der Webservice nur �ber einen Proxy erreichbar ist. */
	public static final String PROP_WS_PROXY_PORT = "ws.proxy.port";

	private final String loginUsername;
	private final String loginPassword;
	private final String bindingUrl;
	private final String proxyHost;
	private final String proxyPort;

	/**
	 * Default-Konstruktor
	 *
	 * @param bindingUrl
	 *            URL zur WSDL des Webservices.
	 */
	public WebServiceConfig(final String bindingUrl) {
		super();
		this.bindingUrl = bindingUrl;
		this.loginUsername = "";
		this.loginPassword = "";
		this.proxyHost = "";
		this.proxyPort = "";
	}

	/**
	 * Konstruktor
	 *
	 * @param bindingUrl
	 *            URL zur WSDL des Webservices.
	 * @param loginUsername
	 *            Nutzername f�r den Login am Webservice.
	 * @param loginPassword
	 *            Passwort f�r den Login am Webservice.
	 */
	public WebServiceConfig(final String bindingUrl, final String loginUsername, final String loginPassword) {
		super();
		this.bindingUrl = bindingUrl;
		this.loginUsername = loginUsername;
		this.loginPassword = loginPassword;
		this.proxyHost = "";
		this.proxyPort = "";
	}

	/**
	 * Konstruktor
	 *
	 * @param bindingUrl
	 *            URL zur WSDL des Webservices.
	 * @param loginUsername
	 *            Nutzername f�r den Login am Webservice.
	 * @param loginPassword
	 *            Passwort f�r den Login am Webservice.
	 * @param proxyHost
	 *            Proxy-Host, falls der Webservice nur �ber einen Proxy
	 *            erreichbar ist.
	 * @param proxyPort
	 *            Proxy-Port, falls der Webservice nur �ber einen Proxy
	 *            erreichbar ist.
	 */
	public WebServiceConfig(final String bindingUrl, final String loginUsername, final String loginPassword, final String proxyHost, final String proxyPort) {
		super();
		this.bindingUrl = bindingUrl;
		this.loginUsername = loginUsername;
		this.loginPassword = loginPassword;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	/**
	 * Konstruktor
	 *
	 * @param configProperties
	 *            Property mit den Konfigurationsparametern
	 *            <ul>
	 *            <li>{@link #PROP_WS_BINDINGURL}</li>
	 *            <li>{@link #PROP_WS_LOGIN_USERNAME}</li>
	 *            <li>{@link #PROP_WS_LOGIN_PASSWORD}</li>
	 *            <li>{@link #PROP_WS_PROXY_HOST}</li>
	 *            <li>{@link #PROP_WS_PROXY_PORT}</li>
	 *            </ul>
	 */
	public WebServiceConfig(final Properties configProperties) {
		this.loginUsername = configProperties.getProperty(WebServiceConfig.PROP_WS_LOGIN_USERNAME);
		this.loginPassword = configProperties.getProperty(WebServiceConfig.PROP_WS_LOGIN_PASSWORD);
		this.bindingUrl = configProperties.getProperty(WebServiceConfig.PROP_WS_BINDINGURL);
		this.proxyHost = configProperties.getProperty(WebServiceConfig.PROP_WS_PROXY_HOST);
		this.proxyPort = configProperties.getProperty(WebServiceConfig.PROP_WS_PROXY_PORT);
	}

	/**
	 * Export der Webservicekonfiguration in eine Property mit folgenden
	 * Parametern.
	 * <ul>
	 * <li>{@link #PROP_WS_BINDINGURL}</li>
	 * <li>{@link #PROP_WS_LOGIN_USERNAME}</li>
	 * <li>{@link #PROP_WS_LOGIN_PASSWORD}</li>
	 * <li>{@link #PROP_WS_PROXY_HOST}</li>
	 * <li>{@link #PROP_WS_PROXY_PORT}</li>
	 * </ul>
	 *
	 * @return Property mit den Konfigurationsparametern
	 */
	public Properties createProperties() {
		Properties configProperties = new Properties();
		configProperties.setProperty(WebServiceConfig.PROP_WS_LOGIN_USERNAME, this.loginUsername);
		configProperties.setProperty(WebServiceConfig.PROP_WS_LOGIN_PASSWORD, this.loginPassword);
		configProperties.setProperty(WebServiceConfig.PROP_WS_BINDINGURL, this.bindingUrl);
		configProperties.setProperty(WebServiceConfig.PROP_WS_PROXY_HOST, this.proxyHost);
		configProperties.setProperty(WebServiceConfig.PROP_WS_PROXY_PORT, this.proxyPort);
		return configProperties;
	}

	/**
	 * @return the bindingUrl
	 */
	public String getBindingUrl() {
		return this.bindingUrl;
	}

	/**
	 * @return the loginUsername
	 */
	public String getLoginUsername() {
		return this.loginUsername;
	}

	/**
	 * @return the loginPassword
	 */
	public String getLoginPassword() {
		return this.loginPassword;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return this.proxyHost;
	}

	/**
	 * @return the proxyPort
	 */
	public String getProxyPort() {
		return this.proxyPort;
	}
}