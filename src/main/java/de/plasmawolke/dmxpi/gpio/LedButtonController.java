package de.plasmawolke.dmxpi.gpio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
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

		wire();

		runLedTest();
		
		updateLedsByVirtualConsole();

		VirtualConsole.get().addButtonListener(this);

	}

	private void updateLedsByVirtualConsole() {
		Collection<VirtualConsoleButton> buttons = VirtualConsole.get().getButtons();
		
		for (VirtualConsoleButton virtualConsoleButton : buttons) {
			
			if(virtualConsoleButton.getState()) {
				
				Integer vcbId = virtualConsoleButton.getId();

				switch (vcbId) {
				case 11:
					outputs.get(LedButton.B1).setState(true);
					break;
				case 5:
					outputs.get(LedButton.B2).setState(true);
					break;
				case 12:
					outputs.get(LedButton.B3).setState(true);
					break;
				case 25:
					outputs.get(LedButton.B4).setState(true);
					break;
				case 22:
					outputs.get(LedButton.B5).setState(true);
					break;
				case 23:
					outputs.get(LedButton.B6).setState(true);
					break;
				case 24:
					outputs.get(LedButton.B7).setState(true);
					break;

				default:
					break;
				}

				
			}
			
		}
		
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
					logger.info("PinStateChanged " + event.getPin());
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
							onButtonLongPress(ledButton);
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

	private void runLedTest() {

		for (int i = 0; i < 5; i++) {

			for (int j = 0; j < LedButton.values().length; j++) {
				
				LedButton btn = LedButton.values()[j];
				outputs.get(btn).setState(true);
				try {
					Thread.sleep(200);
				} catch (Exception e) {
					logger.error("xxx",e);
				}

				outputs.get(btn).setState(false);

			}
		}

	}

	protected void onButtonPress(LedButton ledButton) {

		logger.info(ledButton + " was pressed.");

		switch (ledButton) {
		case B1:
			VirtualConsole.get().clickButton(11); // Hütte - Baulicht
			break;
		case B2:
			VirtualConsole.get().clickButton(5); // Hütte - Rotlicht
			break;
		case B3:
			VirtualConsole.get().clickButton(12); // Hütte - Arbeitslicht
			break;
		case B4:
			VirtualConsole.get().clickButton(25); // Garten - Spot
			break;
		case B5:
			VirtualConsole.get().clickButton(22); // Garten - Taghell
			break;
		case B6:
			VirtualConsole.get().clickButton(23); // Garten - Schlafenszeit
			break;
		case B7:
			VirtualConsole.get().clickButton(24); // Garten - Blackout
			break;
		case B8:
			// Hier nix machen
			break;

		default:
			break;
		}

	}

	protected void onButtonLongPress(LedButton ledButton) {
		logger.info(ledButton + " was pressed for a long time.");

		switch (ledButton) {
		case B1:

			break;
		case B2:

			break;
		case B3:

			break;
		case B4:

			break;
		case B5:

			break;
		case B6:

			break;
		case B7:

			break;
		case B8:
			shutdown();
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
			case 11:
				outputs.get(LedButton.B1).setState((boolean) evt.getNewValue());
				break;
			case 5:
				outputs.get(LedButton.B2).setState((boolean) evt.getNewValue());
				break;
			case 12:
				outputs.get(LedButton.B3).setState((boolean) evt.getNewValue());
				break;
			case 25:
				outputs.get(LedButton.B4).setState((boolean) evt.getNewValue());
				break;
			case 22:
				outputs.get(LedButton.B5).setState((boolean) evt.getNewValue());
				break;
			case 23:
				outputs.get(LedButton.B6).setState((boolean) evt.getNewValue());
				break;
			case 24:
				outputs.get(LedButton.B7).setState((boolean) evt.getNewValue());
				break;

			default:
				break;
			}

		}

	}

	private static long blinkDelay = 200;
	private static long blinkDuration = 10000;

	private void shutdown() {
		logger.info("Starting DMXPi shutdown sequence...");

		outputs.get(LedButton.B1).blink(blinkDelay, blinkDuration);
		outputs.get(LedButton.B2).blink(blinkDelay, blinkDuration);
		outputs.get(LedButton.B3).blink(blinkDelay, blinkDuration);
		outputs.get(LedButton.B4).blink(blinkDelay, blinkDuration);
		outputs.get(LedButton.B5).blink(blinkDelay, blinkDuration);
		outputs.get(LedButton.B6).blink(blinkDelay, blinkDuration);
		outputs.get(LedButton.B7).blink(blinkDelay, blinkDuration);
		outputs.get(LedButton.B8).setState(true);

		// Stop VirtualConsole WebSocket
		try {
			logger.info("Stopping Virtual Console...");
			VirtualConsole.get().shutdown();
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.error("Error while stopping virtual console:", e);
		}

		// Shutdown HAP Server
		try {
			logger.info("Stopping HAP Server...");
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.error("Error while stopping HAP Server:", e);
		}

		// Kill QLC+
		// pkill -9 qlcplus
		try {
			logger.info("Killing QLC+...");
			int exitVal = executeCommand("pkill -9 qlcplus");
			logger.info("Killed QLC+ with exit code " + exitVal);
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.error("Error while killing QLC+:", e);
		}

		// Execute custom shutdown
		try {
			logger.info("Stopping other stuff...");
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.error("Error while stopping other stuff:", e);
		}

		// Shutdown -h now
		try {
			logger.info("Sending shutdown signal to system now! CU :)");
			executeCommand("shutdown -h now");
		} catch (Exception e) {
			logger.error("Error while shutting down dmxpi:", e);
		}

	}

	private static int executeCommand(String command) throws IOException, InterruptedException {

		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.command("bash", "-c", command);

		Process process = processBuilder.start();

		StringBuilder output = new StringBuilder("\n");

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}

		logger.info(output.toString());

		return process.waitFor();
	}

}
