package de.plasmawolke.dmxpi;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.dmxpi.gpio.LedButtonController;
import de.plasmawolke.dmxpi.qlc.VirtualConsole;
import de.plasmawolke.dmxpi.qlc.VirtualConsoleButton;

public class App {

	private final static Logger logger = LoggerFactory.getLogger(App.class);

	private static final String url = "http://192.168.23.121:9999/";
	private static final String wsUrl = "ws://192.168.23.121:9999/qlcplusWS";

	public static void main(String[] args) {

		try {

			VirtualConsole.init(url, wsUrl);
			
			
			new LedButtonController();
			
			
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

	}

}
