import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/*
 * Sensor-klasse 
 * 
 * Indhenter måling fra en serielt tilsluttet sensor
 * Arduinoen måler hvert 5. ms. 
 * 
 * Der modtages ca. 40 målinger per kald - derfor venter mål() 200 
 * ms mellem hvert kald. 
 */

public class SensorOld {

	private SerialPort serialPort;
	private ArrayList<String> målinger = new ArrayList<>();
	private String buffer = "";
	private boolean test = false;

	public void setup() {
		try {
			String[] portNames = SerialPortList.getPortNames();
			String port = portNames[0];
			this.serialPort = new SerialPort(port);
			serialPort.openPort(); // Open serial port
			serialPort.setParams(115200, 8, 1, 0); // Set params.
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.setDTR(true);
		} catch (ArrayIndexOutOfBoundsException b) {
			System.out.println("Der var ikke tilsluttet nogen enheder");
		} catch (SerialPortException ex) {
			System.out.println("Serial Port Exception: " + ex);
		}
	}

	/** kalder mål og returnerer målingen som en streng */
	public ArrayList<String> getValue(int sample_size) {
		mål(sample_size);
		return målinger;
	}

	/** måler på sensoren og gemmer i 'måling' */
	public void mål(int sample_size) {
		int point = 0;
		do {
			if (test)
				System.out.println("Mål!");
			try {
				// hvis der er målinger på vej
				if (serialPort.getInputBufferBytesCount() > 0) {
					buffer += serialPort.readString();
					if (test)
						System.out.println("buffer: " + buffer);
					int pos = -1;
					while ((pos = buffer.indexOf("!")) > -1) {
						målinger.add(buffer.substring(0, pos));
						buffer = buffer.substring(pos + 1);
					}
				}
				// Venter på at input-bufferen bliver fyldt igen
				else
					try {
						Thread.sleep(75);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}
			if (målinger.size() - point > 200) {
				System.out.print(".");
				point = (målinger.size() / 200) * 200;
			}
			if (test)
				System.out.println(målinger.size());
		} while (målinger.size() <= sample_size);
		System.out.println();
	}

	public void clear() {
		try {
			// hvis der er målinger på vej
			if (serialPort.getInputBufferBytesCount() > 0) {
				buffer += serialPort.readString();
				int pos = -1;
				if ((pos = buffer.lastIndexOf("!")) > -1) {
					buffer = buffer.substring(pos + 1);
				}
			}
		} catch (SerialPortException e2) {
			e2.printStackTrace();
		}
	}
}
