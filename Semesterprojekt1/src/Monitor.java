import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Monitor {
	public static int counter = 0;
	public static int sampleTime = 5;
	public static int sampleSize;
	public static ArrayList<Double> data;
	
	/*
	public static returParameter f�rsteKonvertering(parameter){
		// Konverter den f�rste m�ling (fx 1000 m�linger)
		// Indf�r tjek p� om m�lingen faktisk er et tal 
		// Returner data-arraylisten
	}
	
	public static returParameter konverter(parameter){
		// Fjerner et antal m�linger
		// Inds�tter det samme antal nye m�linger
		// konverter tal
		// tjek at m�lingen er et tal
		// Returner data-arraylisten
	}
	
	public static void opretFil(parameter){
		// tjek om fil eksisterer
		// hvis filen eksisterer, slet den
		// ellers opret filen
	}
	*/
	
	/** sp�rgsm�l om test er i gang */
	public static boolean test() {
		String spm = "K�res med test-udskrifter?";
		String svar = javax.swing.JOptionPane.showInputDialog(spm, "ja");
		if (svar != null && svar.equals("ja"))
			return true;
		else
			return false;
	}

	/** sp�rgsm�l om der skal bruges en testsensor */
	public static boolean testsensor() {
		String spm = "K�res med test-sensor?";
		String svar = javax.swing.JOptionPane.showInputDialog(spm, "ja");
		if (svar != null && svar.equals("ja"))
			return true;
		else
			return false;
	}
	
	/** gem til fil */
	public static void gemListeTilFil(ArrayList<Double> liste) {
		// TODO: Tjek om filen eksisterer - hvis den g�r, slet
		try {
			FileWriter fil = new FileWriter("Raa data.txt", true);
			PrintWriter ud = new PrintWriter(fil);
			for (int i = 0; i < liste.size(); i++) {
				ud.println(liste.get(i));
			}

			ud.close();
			System.out.println("Skrevet til fil");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/** gem til fil */
	public static void gemTilFil(double puls) {
		// TODO: Tjek om filen eksisterer - hvis den g�r, slet
		try {
			FileWriter fil = new FileWriter("Maalinger.txt", true);
			PrintWriter ud = new PrintWriter(fil);
			int temp1 = (int) puls;
			int temp2 = (int) (puls * 100) % 100;
			String skriver = "Tid " + (counter * sampleSize / 200) + "s: " + temp1 + "." + temp2 + " bpm";
			ud.println(skriver);
			ud.close();
			System.out.println("Skrevet til fil");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// Tjekker om programmet skal k�res med
		// testudskrifter
		boolean test = test();
		// tjekker om programmet skal k�red med testsensor
		boolean testsensor = testsensor();

		Sensor t;
		Pulsberegner pulsB;

		if (testsensor) // ops�tter testprogram
		{
			t = new TestSensor();
		} else // hovedprogram starter her
		{
			t = new Sensor();
			t.setup();
		}

		if (test)
			System.out.println("start");
		ArrayList<String> listen = new ArrayList<>();
		data = new ArrayList<>();

		// fjerner begyndelsesst�js
		if (!testsensor)
			t.clear();
		if (test)
			System.out.println("Klar!");
		double prevPuls = 0;
		double puls = 0;
		sampleSize = 1000;

		listen = t.getValue(sampleSize);
		for (String p : listen) {
			data.add(Double.parseDouble(p));
		}
		System.out.println("Vi er begyndt!");
		pulsB = new Pulsberegner(sampleSize, sampleTime);
		//System.out.println("Pulsen er inden start: " + puls);

		sampleSize = 600;
		for (;;) {
			for (int i = 0; i < sampleSize; i++) {
				data.remove(i);
			}

			listen = t.getValue(sampleSize);
			double t1 = System.currentTimeMillis();
			for (String p : listen) {
				// System.out.println(p);
				data.add(Double.parseDouble(p));
			}
			System.out.println("Data er nu " + data.size() + " lang");

			puls = pulsB.beregnPuls(data);
			gemListeTilFil(data);

			if (puls == 7 && prevPuls != 0)
				puls = prevPuls;
			if (puls > 0) {
				// if (puls > (1.1 * prevPuls) && prevPuls != 0)
				// puls = 1.1 * prevPuls;
				gemTilFil(puls);
			} else
				System.out.println("D�rlig m�ling - forts�tter");
			counter++;
			double t2 = System.currentTimeMillis();
			System.out.println("Det tog: " + (t2 - t1) + "ms");
			System.out.println("Pulsen var: " + puls + " bpm");
			prevPuls = puls;
		}
	}
}
