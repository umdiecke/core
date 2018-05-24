/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Klasse zum Hinzufügen von Proxies zu bestimmten URLs.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:51:50
 *
 */
public final class NetProxySelector extends ProxySelector {

	// Keep a reference on the previous default
	private static ProxySelector defaultProxySelector = null;

	// A list of proxies, indexed by their address.
	private Map<SocketAddress, InnerProxy> proxyMap = new LinkedHashMap<>();

	/**
	 * ProxySelector initialisieren
	 *
	 * @param urlList
	 *            Liste der URLs, die �ber die angegebene Proxy-Map erreicht
	 *            werden sollen
	 * @param hostPortMap
	 *            Proxy-Map mit verf�gbaren Proxies
	 */
	protected NetProxySelector(final List<URL> urlList, final Map<String, Integer> hostPortMap) {
		// Save the previous default
		setDefaultProxySelector(ProxySelector.getDefault());

		// Populate the HashMap (List of proxies)
		if (hostPortMap != null) {
			for (Entry<String, Integer> hostPortEntry : hostPortMap.entrySet()) {
				if (hostPortEntry.getKey() != null && !hostPortEntry.getKey().isEmpty()) {

					InnerProxy i = new InnerProxy(new InetSocketAddress(hostPortEntry.getKey(), hostPortEntry.getValue()));
					if (this.proxyMap.containsKey(i.getSocketAddress())) {
						i = this.proxyMap.get(i.getSocketAddress());
					}

					i.getUrlList().addAll(urlList);

					this.proxyMap.put(i.getSocketAddress(), i);
				}
			}
		}
	}

	/**
	 * Proxy-Liste initialisieren
	 *
	 * @param urlList
	 *            Liste der URLs, die �ber die angegebene Proxy-Map erreicht
	 *            werden sollen
	 * @param hostPortMap
	 *            Proxy-Karte mit verf�gbaren Proxies
	 */
	public static void initialize(final List<URL> urlList, final Map<String, Integer> hostPortMap) {
		NetProxySelector ps = new NetProxySelector(urlList, hostPortMap);
		ProxySelector.setDefault(ps);
	}

	/**
	 * Auf Standard-Proxy-Selektor zur�cksetzen
	 */
	public static void resetDefaultProxySelector() {
		ProxySelector.setDefault(NetProxySelector.defaultProxySelector);
		NetProxySelector.defaultProxySelector = null;
	}

	@Override
	public List<Proxy> select(final URI uri) {
		// Let's stick to the specs.
		if (uri == null) {
			throw new IllegalArgumentException("URI can't be null.");
		}

		// If it's a http (or https) URL, then we use our own list.
		String protocol = uri.getScheme();
		List<Proxy> l = new ArrayList<>();
		if ("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) {

			for (InnerProxy p : this.proxyMap.values()) {

				List<URL> urlList = p.getUrlList();
				for (URL url : urlList) {
					if (url.getAuthority().equalsIgnoreCase(uri.getAuthority())) {
						l.add(p.toProxy());
						break;
					}
				}
			}
		}

		// Not HTTP or HTTPS (could be SOCKS or FTP) defer to the default selector.
		if (!l.isEmpty()) {
			return l;
		} else if (NetProxySelector.defaultProxySelector != null) {
			return NetProxySelector.defaultProxySelector.select(uri);
		} else {
			l = new ArrayList<>();
			l.add(Proxy.NO_PROXY);
			return l;
		}
	}

	@Override
	public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
		// Let's stick to the specs again.
		if (uri == null || sa == null || ioe == null) {
			throw new IllegalArgumentException("Arguments can't be null.");
		}

		// Let's lookup for the proxy
		InnerProxy p = this.proxyMap.get(sa);
		if (p != null) {

			// It's one of ours, if it failed more than 3 times let's remove it from the list.
			if (p.failed() >= 3) {
				this.proxyMap.remove(sa);
			}
		} else {

			// Not one of ours, let's delegate to the default.
			if (NetProxySelector.defaultProxySelector != null) {
				NetProxySelector.defaultProxySelector.connectFailed(uri, sa, ioe);
			}
		}
	}

	/**
	 * Setzen des Default-ProxySelectors
	 *
	 * @param defaultProxySelector
	 *            Default-ProxySelectors
	 */
	private void setDefaultProxySelector(final ProxySelector defaultProxySelector) {
		if (NetProxySelector.defaultProxySelector == null && defaultProxySelector != null && !(defaultProxySelector instanceof NetProxySelector)) {
			NetProxySelector.defaultProxySelector = defaultProxySelector;
		} else if (defaultProxySelector != null && defaultProxySelector instanceof NetProxySelector) {
			setProxyMap(((NetProxySelector) defaultProxySelector).getProxyMap());
		}
	}

	/**
	 * @return the proxyMap
	 */
	public Map<SocketAddress, InnerProxy> getProxyMap() {
		return this.proxyMap;
	}

	/**
	 * @param proxyMap
	 *          the proxyMap to set
	 */
	private void setProxyMap(final Map<SocketAddress, InnerProxy> proxyMap) {
		this.proxyMap = proxyMap;
	}

	/**
	 * Innere Klasse repräsentiert einen Proxy und einige zusätzliche Daten
	 *
	 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
	 * @version 1.0
	 * @since 24.05.2018, 12:52:09
	 *
	 */
	private static class InnerProxy {
		private Proxy proxy;
		private SocketAddress addr;
		private List<URL> urlList;

		// How many times did we fail to reach this proxy?
		int failedCount = 0;

		public InnerProxy(final InetSocketAddress a) {
			this.addr = a;
			this.proxy = new Proxy(Proxy.Type.HTTP, a);
			this.urlList = new ArrayList<>();
		}

		public SocketAddress getSocketAddress() {
			return this.addr;
		}

		public Proxy toProxy() {
			return this.proxy;
		}

		public int failed() {
			return ++this.failedCount;
		}

		/**
		 * @return the urlList
		 */
		public List<URL> getUrlList() {
			if (this.urlList == null) {
				this.urlList = new ArrayList<>();
			}
			return this.urlList;
		}
	}
}