import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class BenytSensor {

	private static double max;
	private static double min = 4.995;
	private static int sampleTime = 5;
	private static int sampleSize = 800;
	private static int counter, peakCount;

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

	/**
	 * udregner pulsen baseret p� b�lgetoppe og returnerer pulsen som et
	 * flydende tal
	 */
	public static double beregnPuls(ArrayList<Double> list) {
		double middelVal = 0;
		double standardAfv = 0;
		double puls = 0;
		middelVal = beregnMiddelVal(list);
		standardAfv = beregnAfvigelse(list, middelVal);
		System.out.println("Middelv�rdi: " + middelVal + ".\nAfvigelse: " + standardAfv);
		double max = middelVal + 1.1 * standardAfv;
		System.out.println("Max er defineret til: " + max);
		ArrayList<Double> toppe;
		toppe = findTop(list, max);

		int j = peakCount;
		double temp = 0;
		j = j - 1;
		while (j > 0) {
			temp += (toppe.get(j) - toppe.get(j - 1));
			j--;
		}
		temp = temp * sampleTime;
		temp /= (peakCount - 1);
		puls = 60000 / temp;
		
		if (peakCount >= 3)
			return puls;
		if (peakCount < 3 && peakCount > 0) {
			puls = 7;
			return puls;
		} else
			return 0;
	}

	/** udregner den laveste m�ling */
	public static void beregnMin(ArrayList<Double> list) {
		min = 4.995;
		for (double tal : list) {
			min = (tal < min) ? tal : min;
		}
	}

	/** udregner den h�jeste m�ling */
	public static void beregnMax(ArrayList<Double> list) {
		max = 0;
		for (double tal : list) {
			max = (tal > max) ? tal : max;
		}
	}

	/** finder antallet og positionen p� b�lgetoppe = pulsslag */
	public static ArrayList<Double> findTop(ArrayList<Double> list, double max) {
		peakCount = 0;
		boolean fundet = false;
		ArrayList<Double> peaks = new ArrayList<>();
		int i = 0;
		while (!fundet && i < list.size()) {
			fundet = (list.get(i) > max);
			
			// Problem: Hvis pulsslaget registreres p� den f�rste plads
			boolean f�rste = (i == 0);
			if (fundet && !f�rste) {
				double a = (list.get(i) - list.get(i - 1)) / (i - (i - 1));
				if (a > 0) {
					double x = max * (1 / a) + i;
					peaks.add(x);
					//System.out.println(x);
					//System.out.println(i);
					//System.out.println(peaks.get(peakCount));
					i += 44;
					peakCount++;
				} else
					fundet = false;
			}
			i++;
			fundet = false;
		}
		System.out.println(peakCount);
		return peaks;
	}
	
	/** gem til fil */
	public static void gemListeTilFil(ArrayList<Double> liste) {
		// TODO: Tjek om filen eksisterer - hvis den g�r, slet
		try {
			FileWriter fil = new FileWriter("R� data.txt", true);
			PrintWriter ud = new PrintWriter(fil);
			for(int i = 0; i<liste.size(); i++){
				ud.println(i + ": " + liste.get(i));
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

	/** genneml�ber listen og udregner middelv�rdien af m�lingerne */
	public static double beregnMiddelVal(ArrayList<Double> list) {
		double sum = 0;
		for (double tal : list) {
			sum = sum + tal;
		}
		double resultat = sum / sampleSize;
		System.out.println(resultat);
		return resultat;
	}

	public static double beregnAfvigelse(ArrayList<Double> list, double middelVal) {
		double temp = 0;
		for (double tal : list) {
			temp += ((middelVal - tal) * (middelVal - tal));
		}
		double afv = Math.sqrt(temp / (sampleSize - 1));
		System.out.println(afv);
		return afv;
	}
/*
	public static void main(String[] args) {
		// Tjekker om programmet skal k�res med
		// testudskrifter
		boolean test = test();
		// tjekker om programmet skal k�red med testsensor
		boolean testsensor = testsensor();

		Sensor t;

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

		// fjerner begyndelsesst�js
		if (!testsensor)
			t.clear();
		if (test)
			System.out.println("Klar!");
		double prevPuls = 0;
		double puls = 0;
		// henter et antal m�linger, defineret i sample_size
		for (;;) {
			listen = t.getValue(sampleSize);
			if (test)
				System.out.println("L�st!");
			ArrayList<Double> tal = new ArrayList<>();
			if (test)
				System.out.println("Parse!");
			for (int j = 0; j < sampleSize; j++) {
				tal[j] = Double.parseDouble(listen.get(j));
			}
			double t1 = System.currentTimeMillis();
			gemListeTilFil(tal);
			
			// beregner pulsen:
			puls = beregnPuls(tal);
			if (puls == 7 && prevPuls != 0)
				puls = prevPuls;
			if (puls > 0) {
				if (puls > (1.1 * prevPuls) && prevPuls != 0)
					puls = 1.1 * prevPuls;
				gemTilFil(puls);
			} else
				System.out.println("D�rlig m�ling - forts�tter");
			counter++;
			double t2 = System.currentTimeMillis();
			System.out.println("Det tog: " + (t2 - t1) + "ms");
			System.out.println("Pulsen var: " + puls + " bpm");
			prevPuls = puls;
		}
	}*/
}
