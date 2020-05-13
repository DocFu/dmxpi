package de.plasmawolke.dmxpi.gpio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.plasmawolke.dmxpi.qlc.VirtualConsole;
import de.plasmawolke.dmxpi.qlc.VirtualConsoleButton;

public class LedButtonController implements PropertyChangeListener {

	private final static Logger logger = LoggerFactory.getLogger(LedButtonController.class);

	private static final int[] bcmInputPinNumbers = { 26, 19, 13, 6, 5, 22, 27, 17 };
	private static final int[] bcmOutputPinNumbers = { 21, 20, 16, 12, 25, 24, 23, 18 };

	private static final int[] inputPinNumbers = { 25, 24, 23, 22, 21, 3, 2, 0 };
	private static final int[] outputPinNumbers = { 29, 28, 27, 26, 6, 5, 4, 1 };

	private static final GpioPinDigitalOutput[] outputPins = new GpioPinDigitalOutput[8];

	private GpioController gpio = null;

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

		for (int i = 0; i < inputPinNumbers.length; i++) {

			Pin pin = RaspiPin.getPinByAddress(inputPinNumbers[i]);

			GpioPinDigitalInput inputPin = gpio.provisionDigitalInputPin(pin, "Button " + (i + 1),
					PinPullResistance.PULL_DOWN);

			if (inputPin == null) {
				// TODO remove after improved MockImplementation
				continue;
			}

			inputPin.addListener(new GpioPinListenerDigital() {

				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

					// display pin state on console
					logger.info("GPIO PIN STATE CHANGE (" + event.getSource() + "): " + event.getPin() + " = "
							+ event.getState());

					if (PinState.HIGH == event.getState()) {
						VirtualConsole.get().clickButton(30);
					}

				}
			});
		}

		for (int i = 0; i < outputPinNumbers.length; i++) {
			Pin pin = RaspiPin.getPinByAddress(outputPinNumbers[i]);
			GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(pin, "Led " + (i + 1), PinState.LOW);

			if (outputPin == null) {
				// TODO remove after improved MockImplementation
				continue;
			}
			
			outputPins[i] = outputPin;
		}

		logger.info("Done");

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (VirtualConsoleButton.CHANGE_PROPERTY_NAME.equals(evt.getPropertyName())) {
			VirtualConsoleButton vcb = (VirtualConsoleButton) evt.getSource();
			logger.info("Switched " + ((boolean) evt.getNewValue() ? "ON" : "OFF") + " " + vcb);
		}

	}

}
