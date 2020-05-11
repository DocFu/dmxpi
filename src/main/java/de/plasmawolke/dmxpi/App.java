package de.plasmawolke.dmxpi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.dmxpi.qlc.VirtualConsole;

public class App {

	private final static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {

		VirtualConsole virtualConsole = new VirtualConsole();

		try {
			virtualConsole.populate();
			virtualConsole.clickButton(31);
		} catch (Exception e) {
			logger.error("Failed to populate buttons: ", e);
		}

	}

}
