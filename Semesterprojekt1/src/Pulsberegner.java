import java.util.ArrayList;

public class Pulsberegner {
	private double middelVal, standardAfv, puls;
	private int sampleSize = 1000;
	private int sampleTime; 
	private ArrayList<Double> data;
	private ArrayList<Double> toppe = new ArrayList<Double>();

	/** konstrukt�r, der modtager en liste som parameter */
	public Pulsberegner(int sampleSize,int sampleTime) {
		this.sampleSize = sampleSize;
		this.sampleTime = sampleTime;
	}

	/**
	 * udregner pulsen baseret p� b�lgetoppe og returnerer pulsen som et
	 * flydende tal
	 */
	public double beregnPuls(ArrayList<Double> m�linger) {
		this.data = m�linger;
		this.middelVal = beregnMiddelVal(data, sampleSize);
		this.standardAfv = beregnAfvigelse(data, middelVal, sampleSize);
		System.out.println("Middelv�rdi: " + middelVal + ".\nAfvigelse: " + standardAfv);
		double max = middelVal + 1.1 * standardAfv;
		System.out.println("Max er defineret til: " + max);
		
		toppe = findTop(data, max);
		int peakCount = toppe.size();
		int j = peakCount -1;
		double temp = 0;
		while (j > 0) {
			temp += (toppe.get(j) - toppe.get(j - 1));
			j--;
		}
		temp = temp * sampleTime;
		temp /= (peakCount - 1);
		this.puls = 60000 / temp;

		if (peakCount >= 3)
			return puls;
		if (peakCount < 3 && peakCount > 0) {
			puls = 7;
			return puls;
		} else
			return 0;
	}

	/** udregner den laveste m�ling */
	public static double beregnMin(ArrayList<Double> data) {
		double min = 4.995;
		for (double tal : data) {
			min = (tal < min) ? tal : min;
		}
		return min;
	}

	/** udregner den h�jeste m�ling */
	public static double beregnMax(ArrayList<Double> data) {
		double max = 0;
		for (double tal : data) {
			max = (tal > max) ? tal : max;
		}
		return max;
	}

	/** finder antallet og positionen p� b�lgetoppe = pulsslag */
	public static ArrayList<Double> findTop(ArrayList<Double> data, double max) {
		double peakCount = 0;
		boolean fundet = false;
		ArrayList<Double> peaks = new ArrayList<>();
		int i = 0;
		while (!fundet && i < data.size()) {
			fundet = (data.get(i) > max);

			// Problem: Hvis pulsslaget registreres p� den f�rste plads
			boolean f�rste = (i == 0);
			if (fundet && !f�rste) {
				double a = (data.get(i) - data.get(i - 1)) / (i - (i - 1));
				if (a > 0) {
					double x = max * (1 / a) + i;
					peaks.add(x);
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

	/** genneml�ber listen og udregner middelv�rdien af m�lingerne */
	public static double beregnMiddelVal(ArrayList<Double> data, int sampleSize) {
		double sum = 0;
		for (double tal : data) {
			sum = sum + tal;
		}
		double resultat = sum / sampleSize;
		System.out.println(resultat);
		return resultat;
	}

	public static double beregnAfvigelse(ArrayList<Double> data, double middelVal, int sampleSize) {
		double temp = 0;
		for (double tal : data) {
			temp += ((middelVal - tal) * (middelVal - tal));
		}
		double afv = Math.sqrt(temp / (sampleSize - 1));
		System.out.println(afv);
		return afv;
	}
}
