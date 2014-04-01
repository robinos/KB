package Controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;

import Model.GetSensor;

/**
 * The ReadArduino class reads data from Arduino.  It extends thread, and therefore
 * can be started with the .start() method.  It implements the SerialPortEventListener
 * in order to read data from the serial port of Arduino.
 * 
 **IMPORTANT NOTE: The COM port needed varies between Arduino units and no automatic
 **detection is coded, so it must be set manually**
 * 
 * @authors: Christer Evervall and Mei Ha
 * alterations by Robin Osborne - added the stopRecording & stopPlaying checks
 */
public class ReadArduino extends Thread implements SerialPortEventListener {	
	SerialPort serialPort;
	Thread t = new Thread();
	
    /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM4", // Windows
	};
	
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	
	//The GetSensor object that receives the data
	private GetSensor getsensor;
	
	public void initialize(MainLogic mlMainLogic, GetSensor getsensor) {
		this.getsensor = getsensor;
		
		CommPortIdentifier portId = null;
		System.out.println("1");
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers(); // GOES WRONG
		System.out.println("2");
		//First, Find an instance of serial port as set in PORT_NAMES.
		//**But break out of this loop if stop has been called
		while (portEnum.hasMoreElements() && !mlMainLogic.getStopRecording()
				&& mlMainLogic.getStopPlaying(mlMainLogic.getCurrentProfile())) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				getsensor.setSensorData(inputLine);
				System.out.println(inputLine);
				//delay in 2 seconds
				Thread.sleep(2000);
		
			} catch (IOException e) {
				System.err.println(e.toString());
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
}