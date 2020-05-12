package de.plasmawolke.dmxpi.gpio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.dmxpi.qlc.VirtualConsole;
import de.plasmawolke.dmxpi.qlc.VirtualConsoleButton;

public class LedButtonController implements PropertyChangeListener {

	private final static Logger logger = LoggerFactory.getLogger(LedButtonController.class);

	public LedButtonController() {

		VirtualConsole.get().addButtonListener(this);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (VirtualConsoleButton.CHANGE_PROPERTY_NAME.equals(evt.getPropertyName())) {
			VirtualConsoleButton vcb = (VirtualConsoleButton) evt.getSource();
			logger.info("Switched " + ((boolean) evt.getNewValue() ? "ON" : "OFF") + " " + vcb);
		}

	}

}
