import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Sensor-klasse
 * 
 * Indhenter m�ling fra en serielt tilsluttet sensor Arduinoen m�ler hvert 5.
 * ms.
 * 
 * Indeholder en metode til at hente m�linger, til at rense den f�rste m�ling og
 * en ops�tningsmetode. Returnerer en liste med sample_size st�rrelse med
 * m�linger.
 */

public class Sensor {

	private SerialPort serialPort;
	private ArrayList<String> m�linger;
	private String buffer = "";
	private boolean test = true;

	/**
	 * ops�tter den serielle port og melder at data terminalen er klar til at
	 * modtage data
	 */
	public void setup() {
		try {
			String[] portNames = SerialPortList.getPortNames();
			String port = portNames[0];
			this.serialPort = new SerialPort(port);
			serialPort.openPort(); // Open serial port
			serialPort.setParams(9600, 8, 1, 0); // Set params.
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.setDTR(true);
		} catch (ArrayIndexOutOfBoundsException b) {
			System.out.println("Der var ikke tilsluttet nogen enheder");
		} catch (SerialPortException ex) {
			System.out.println("Serial Port Exception: " + ex);
		}
	}

	/**
	 * Kalder m�l() og returnerer en liste med det antal m�linger, som
	 * parameteren bestemmer
	 */
	public ArrayList<String> getValue(int sampleSize) {
		m�l(sampleSize);
		return m�linger;
	}

	/**
	 * Modtager m�linger fra sensoren, skiller dem ad og tilf�jer dem til en
	 * liste
	 */
	public void m�l(int sampleSize) {
		int point = 0;
		// instantierer listen - s�ledes gemmes kun sample_size for hvert kald
		m�linger = new ArrayList<>(sampleSize);
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
			// Udskriver et punktum for hvert 200 m�ling (ca. hvert sekund)
			if (test) {
				if (m�linger.size() - point >= 200) {
					System.out.print(".");
					point = (m�linger.size() / 200) * 200;
				}
				System.out.println(m�linger.size());
			}
		} while (m�linger.size() <= sampleSize);
		System.out.println();
	}

	/** Rydder den f�rste m�ling, da de f�rste m�linger typisk er fejlagtige */
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
