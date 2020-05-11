package de.plasmawolke.dmxpi.qlc;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualConsoleButton {

	private final static Logger logger = LoggerFactory.getLogger(VirtualConsoleButton.class);

	private static final String url = "ws://192.168.23.121:9999/qlcplusWS";

	private Integer id;

	private String name;

	public void click() {
		logger.info("Clicking button " + this);

		String message1 = id + "|0";
		String message2 = id + "|255";

		/// ----

		WebSocketClient client = new WebSocketClient();
		QlcWebSocket socket = new QlcWebSocket();

		socket.setMessage1(message1);
		socket.setMessage2(message2);

		try {
			client.start();

			URI echoUri = new URI(url);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			logger.info("Connecting to : %s%n", echoUri);

			// wait for closed socket connection.
			socket.awaitClose(1, TimeUnit.SECONDS);
		} catch (Throwable t) {
			logger.error("Could not speak with websocket:", t);
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				logger.error("Could not stop websocket client:", e);
			}
		}
	}

	/**
	 * @return the id
	 */
	public final Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VirtualConsoleButton other = (VirtualConsoleButton) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VirtualConsoleButton (" + id + ", '" + name + "')";
	}

}
