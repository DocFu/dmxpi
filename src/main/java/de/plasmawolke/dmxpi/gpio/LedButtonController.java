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

	private Map<LedButton, Long> lastHighStateTimestamps = new HashMap<>();

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
					logger.info("PinStateChanged " + event);
					String pinName = event.getPin().getName();
					LedButton ledButton = LedButton.valueOf(pinName);

					if (PinState.HIGH == event.getState()) {
						lastHighStateTimestamps.put(ledButton, System.currentTimeMillis());
						onButtonPress(ledButton);
					}

					if (PinState.LOW == event.getState()) {
						long stop = System.currentTimeMillis();
						long start = lastHighStateTimestamps.put(ledButton, null);
						long millis = stop - start;
						logger.info("Press duraction: " + millis);

						if (millis > 3000 && millis < 6000) {
							shutdown();
						}

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

		logger.info(ledButton + " was pressed.");

		switch (ledButton) {
		case B1:
			VirtualConsole.get().clickButton(5);
			break;
		case B2:
			VirtualConsole.get().clickButton(6);
			break;
		case B3:
			VirtualConsole.get().clickButton(10);
			break;
		case B4:
			VirtualConsole.get().clickButton(9);
			break;
		case B5:
			VirtualConsole.get().clickButton(3);
			break;
		case B6:
			VirtualConsole.get().clickButton(2);
			break;
		case B7:
			VirtualConsole.get().clickButton(30);
			break;
		case B8:
			VirtualConsole.get().clickButton(31);
			break;

		default:
			break;
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (VirtualConsoleButton.CHANGE_PROPERTY_NAME.equals(evt.getPropertyName())) {
			VirtualConsoleButton vcb = (VirtualConsoleButton) evt.getSource();
			logger.info("Switched " + ((boolean) evt.getNewValue() ? "ON" : "OFF") + " " + vcb);

			Integer vcbId = vcb.getId();

			switch (vcbId) {
			case 5:
				outputs.get(LedButton.B1).setState((boolean) evt.getNewValue());
				break;
			case 6:
				outputs.get(LedButton.B2).setState((boolean) evt.getNewValue());
				break;
			case 10:
				outputs.get(LedButton.B3).setState((boolean) evt.getNewValue());
				break;
			case 9:
				outputs.get(LedButton.B4).setState((boolean) evt.getNewValue());
				break;
			case 3:
				outputs.get(LedButton.B5).setState((boolean) evt.getNewValue());
				break;
			case 2:
				outputs.get(LedButton.B6).setState((boolean) evt.getNewValue());
				break;
			case 30:
				outputs.get(LedButton.B7).setState((boolean) evt.getNewValue());
				break;
			case 31:
				outputs.get(LedButton.B8).setState((boolean) evt.getNewValue());
				break;

			default:
				break;
			}

		}

	}

	private static long blinkDelay = 200;
	private static long blinkDuration = 1000;

	private void shutdown() {
		logger.info("Starting DMXPi shutdown sequence...");

		outputs.get(LedButton.B1).setState(true);
		outputs.get(LedButton.B2).setState(true);
		outputs.get(LedButton.B3).setState(true);
		outputs.get(LedButton.B4).setState(true);
		outputs.get(LedButton.B5).setState(true);
		outputs.get(LedButton.B6).setState(true);
		outputs.get(LedButton.B7).setState(true);

		// Stop VirtualConsole WebSocket
		try {
			outputs.get(LedButton.B8).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B8).setState(false);
			logger.info("8");
		} catch (InterruptedException e) {
			logger.error("ThreadSleep:", e);
		}

		// Shutdown HAP Server
		try {
			outputs.get(LedButton.B7).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B7).setState(false);
			logger.info("7");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Kill QLC+
		try {
			outputs.get(LedButton.B6).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B6).setState(false);
			logger.info("6");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		try {
			outputs.get(LedButton.B5).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B5).setState(false);
			logger.info("5");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			outputs.get(LedButton.B4).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B4).setState(false);
			logger.info("4");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			outputs.get(LedButton.B3).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B3).setState(false);
			logger.info("3");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Execute custom shutdown
		try {
			outputs.get(LedButton.B2).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B2).setState(false);
			logger.info("2");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
		// Shutdown GPIO
		try {
			outputs.get(LedButton.B1).blink(blinkDelay, blinkDuration);
			Thread.sleep(blinkDuration);
			outputs.get(LedButton.B1).setState(false);
			logger.info("1");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logger.info("Sending shutdown signal to system now! CU :)");

	}

}
