package de.plasmawolke.dmxpi.gpio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.plasmawolke.dmxpi.qlc.VirtualConsole;
import de.plasmawolke.dmxpi.qlc.VirtualConsoleButton;

public class LedButtonController implements PropertyChangeListener {

	private final static Logger logger = LoggerFactory.getLogger(LedButtonController.class);

	private GpioController gpio = null;

	private Map<LedButton, GpioPinDigitalOutput> outputs = new HashMap<>();

	public LedButtonController() {

		VirtualConsole.get().addButtonListener(this);

		wire();

	}

	private void wire() {
		logger.info("Wiring things...");

		boolean runningOnPi = System.getProperty("os.arch").startsWith("arm");

		if (runningOnPi) {
			gpio = GpioFactory.getInstance();
		} else {
			logger.warn("Wrong platform detected! Using GPIO Mock. Expect some Errors (NPEs)...");
			gpio = new MockGpioController();
		}

		LedButton[] ledButtons = LedButton.values();

		for (int i = 0; i < ledButtons.length; i++) {

			LedButton ledButton = ledButtons[i];

			GpioPinDigitalInput inputPin = gpio.provisionDigitalInputPin(ledButton.getButtonPin(), ledButton.name(),
					PinPullResistance.PULL_DOWN);

			if (inputPin == null) {
				// TODO remove after improved MockImplementation
				continue;
			}

			inputPin.addListener(new GpioPinListenerDigital() {

				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

					String pinName = event.getPin().getName();
					LedButton ledButton = LedButton.valueOf(pinName);

					if (PinState.HIGH == event.getState()) {
						onButtonPress(ledButton);
					}

				}
			});

			GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(ledButton.getLedPin(), ledButton.name(),
					PinState.LOW);

			if (outputPin == null) {
				// TODO remove after improved MockImplementation
				continue;
			}

			outputs.put(ledButton, outputPin);
		}

		logger.info("Done");

	}

	protected void onButtonPress(LedButton ledButton) {
		// VirtualConsole.get().clickButton(30);
		logger.info(ledButton + " was pressed.");
		outputs.get(ledButton).blink(1000);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (VirtualConsoleButton.CHANGE_PROPERTY_NAME.equals(evt.getPropertyName())) {
			VirtualConsoleButton vcb = (VirtualConsoleButton) evt.getSource();
			logger.info("Switched " + ((boolean) evt.getNewValue() ? "ON" : "OFF") + " " + vcb);
		}

	}

	private void shutdown() {

	}

}
