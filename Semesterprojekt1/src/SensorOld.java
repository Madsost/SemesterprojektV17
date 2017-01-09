import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/*
 * Sensor-klasse 
 * 
 * Indhenter m�ling fra en serielt tilsluttet sensor
 * Arduinoen m�ler hvert 5. ms. 
 * 
 * Der modtages ca. 40 m�linger per kald - derfor venter m�l() 200 
 * ms mellem hvert kald. 
 */

public class SensorOld {

	private SerialPort serialPort;
	private ArrayList<String> m�linger = new ArrayList<>();
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

	/** kalder m�l og returnerer m�lingen som en streng */
	public ArrayList<String> getValue(int sample_size) {
		m�l(sample_size);
		return m�linger;
	}

	/** m�ler p� sensoren og gemmer i 'm�ling' */
	public void m�l(int sample_size) {
		int point = 0;
		do {
			if (test)
				System.out.println("M�l!");
			try {
				// hvis der er m�linger p� vej
				if (serialPort.getInputBufferBytesCount() > 0) {
					buffer += serialPort.readString();
					if (test)
						System.out.println("buffer: " + buffer);
					int pos = -1;
					while ((pos = buffer.indexOf("!")) > -1) {
						m�linger.add(buffer.substring(0, pos));
						buffer = buffer.substring(pos + 1);
					}
				}
				// Venter p� at input-bufferen bliver fyldt igen
				else
					try {
						Thread.sleep(75);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}
			if (m�linger.size() - point > 200) {
				System.out.print(".");
				point = (m�linger.size() / 200) * 200;
			}
			if (test)
				System.out.println(m�linger.size());
		} while (m�linger.size() <= sample_size);
		System.out.println();
	}

	public void clear() {
		try {
			// hvis der er m�linger p� vej
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
