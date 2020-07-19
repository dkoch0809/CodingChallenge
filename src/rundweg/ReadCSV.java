package rundweg;

import java.util.ArrayList;

public class ReadCSV {
	public static ArrayList<Places> getCsvList() {

		ArrayList<Places> allPLaces = new ArrayList<Places>();
		try {
			java.io.BufferedReader FileReader = new java.io.BufferedReader(
					new java.io.FileReader(new java.io.File("msg_standorte_deutschland.csv")));
			String zeile = "";
			zeile = FileReader.readLine(); // 1.Zeile überlesen
			while ((zeile = FileReader.readLine()) != null) {
				String[] split2 = zeile.split(",");
				int id = Integer.parseInt(split2[0]);

				String place = split2[1];
				String street = split2[2] + " " + split2[3];
				String city = split2[4] + " " + split2[5];
				double breite = Double.parseDouble(split2[6]);
				double laenge = Double.parseDouble(split2[7]);
				// System.out.println(city+ " "+ breite);
				Places places = new Places(id, place, street, city, breite, laenge);
				allPLaces.add(places);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allPLaces;
	}

}
