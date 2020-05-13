package de.plasmawolke.dmxpi.gpio;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

//private static final int[] bcmInputPinNumbers = { 26, 19, 13, 6, 5, 22, 27, 17 };
//private static final int[] bcmOutputPinNumbers = { 21, 20, 16, 12, 25, 24, 23, 18 };

//private static final int[] inputPinNumbers = { 25, 24, 23, 22, 21, 3, 2, 0 };
//private static final int[] outputPinNumbers = { 29, 28, 27, 26, 6, 5, 4, 1 };

public enum LedButton {
	

	B1(RaspiPin.GPIO_25, RaspiPin.GPIO_29),
	B2(RaspiPin.GPIO_24, RaspiPin.GPIO_28),
	B3(RaspiPin.GPIO_23, RaspiPin.GPIO_27),
	B4(RaspiPin.GPIO_22, RaspiPin.GPIO_26),
	B5(RaspiPin.GPIO_21, RaspiPin.GPIO_06),
	B6(RaspiPin.GPIO_03, RaspiPin.GPIO_05),
	B7(RaspiPin.GPIO_02, RaspiPin.GPIO_04),
	B8(RaspiPin.GPIO_00, RaspiPin.GPIO_01);
	
	private final Pin buttonPin;
	private final Pin ledPin;
	
	private LedButton(Pin buttonPin, Pin ledPin) {
		this.buttonPin = buttonPin;
		this.ledPin = ledPin;
	}

	/**
	 * @return the buttonPin
	 */
	public final Pin getButtonPin() {
		return buttonPin;
	}

	/**
	 * @return the ledPin
	 */
	public final Pin getLedPin() {
		return ledPin;
	}
	
	

}
