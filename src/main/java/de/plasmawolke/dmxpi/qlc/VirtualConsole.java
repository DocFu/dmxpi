package de.plasmawolke.dmxpi.qlc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of the QLC+ Virtual Console Web Interface. It connects to
 * the QLC+ Web Page, collects all buttons and establishes a connection to its
 * web socket.
 * 
 * @author fu
 *
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class VirtualConsole implements PropertyChangeListener {

	private final static Logger logger = LoggerFactory.getLogger(VirtualConsole.class);

	private static VirtualConsole instance = new VirtualConsole();

	private Session wsSession = null;

	private boolean initialized = false;

	private Map<Integer, VirtualConsoleButton> buttons = new HashMap<Integer, VirtualConsoleButton>();

	private VirtualConsole() {

	}

	public Collection<VirtualConsoleButton> getButtons() {
		return buttons.values();
	}

	public static VirtualConsole get() {
		if (!instance.initialized) {
			throw new IllegalStateException("VirtualConsole was not initialized. Call init(...) first!");
		}
		return instance;
	}

	private static boolean initx(String url, String wsUrl) throws InterruptedException {
		logger.info("Initializing Virtual Console...");
		try {
			instance.collectButtons(url);
			instance.connectWithWebSocket(wsUrl);
			instance.initialized = true;
			return false;
		} catch (Exception e) {
			Thread.sleep(12000);
			return true;
		}

	}

	public static void init(String url, String wsUrl) throws Exception {

		boolean retry = false;

		do {
			retry = initx(url, wsUrl);
		} while (retry);

	}

	public void clickButton(Integer id) {

		if (wsSession == null) {
			logger.warn(
					"QLC+ WebSocket session is not available (yet). The button [" + id + "] click will be discarded.");
			return;
		}

		String message1 = id + "|0";
		String message2 = id + "|255";

		try {
			Future<Void> fut;
			fut = wsSession.getRemote().sendStringByFuture(message1);
			fut.get(1, TimeUnit.SECONDS); // wait for send to complete.

			fut = wsSession.getRemote().sendStringByFuture(message2);
			fut.get(1, TimeUnit.SECONDS); // wait for send to complete.

		} catch (Exception e) {
			logger.error("Error on button [" + id + "] click...");
			logger.error("QLC+ WebSocket messages [" + message1 + "," + message2 + "] could not be sent: ", e);
		}

	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.info("QLC+ WebSocket session is established!");
		this.wsSession = session;
	}

	@OnWebSocketMessage
	public void onMessage(String message) {

		logger.debug("Got QLC+ WebSocket message '" + message + "'");

		String[] parts = StringUtils.split(message, "|");

		int id = Integer.parseInt(parts[0]); // e.g. 31
		// String name = parts[1]; // e.g. BUTTON
		int value = Integer.parseInt(parts[2]); // e.g. 127

		VirtualConsoleButton vcb = buttons.get(id);

		if (vcb == null) {
			logger.warn("VirtualConsoleButton with id [" + id + "] is null.");
			return;
		}

		if (value == 0) {
			vcb.updateState(false);
		} else if (value == 255) {
			vcb.updateState(true);
		}

	}

	private void collectButtons(String url) throws Exception {

		// Instantiate HttpClient
		HttpClient httpClient = new HttpClient();

		// Configure HttpClient, for example:
		httpClient.setFollowRedirects(false);

		// Start HttpClient
		httpClient.start();
		ContentResponse response = httpClient.newRequest(url).method(HttpMethod.GET)
				.agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0").send();

		String html = response.getContentAsString();

		httpClient.stop();

		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Elements links = doc.select("a.vcbutton");

		logger.info("Adding " + links.size() + " buttons...");

		for (Element element : links) {

			Integer id = Integer.parseInt(element.attr("id"));
			String name = element.text().trim();
			boolean enabled = element.attr("style").contains("#00E600");

			VirtualConsoleButton vcb = new VirtualConsoleButton();
			vcb.addPropertyChangeListener(this);

			vcb.setId(id);
			vcb.setName(name);
			vcb.updateState(enabled);

			buttons.put(id, vcb);

			logger.info(vcb + " is " + (vcb.getState() ? "ON" : "OFF"));

		}

	}

	public void addButtonListener(final PropertyChangeListener listener) {
		for (VirtualConsoleButton vcb : getButtons()) {
			vcb.addPropertyChangeListener(listener);
		}
	}

	private void connectWithWebSocket(String wsUrl) throws Exception {

		WebSocketClient client = new WebSocketClient();
		client.start();

		URI wsUri = new URI(wsUrl);
		ClientUpgradeRequest request = new ClientUpgradeRequest();
		logger.debug("Connecting to QLC+ WebSocket uri: " + wsUri);
		client.connect(this, wsUri, request);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (VirtualConsoleButton.CHANGE_PROPERTY_NAME.equals(evt.getPropertyName())) {
			VirtualConsoleButton vcb = (VirtualConsoleButton) evt.getSource();
			logger.info("Switched " + ((boolean) evt.getNewValue() ? "ON" : "OFF") + " " + vcb);
		}

	}

	public void shutdown() {
		wsSession.close();
	}

}
