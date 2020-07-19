package rundweg;

import java.util.ArrayList;

public class Places {

	private int id;
	private String place, street, city;
	private double breite, laenge;
	

	public Places(int id, String place, String street, String city, double breite, double laenge) {
		this.id = id;
		this.place = place;
		this.street = street;
		this.city = city;
		this.breite = breite;
		this.laenge = laenge;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlace() {
		return place;
	}
	public Places getPlacebyid(ArrayList<Places> list, int id) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId()==id) {
				return list.get(i);
			} 
		}
		return null;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double getBreite() {
		return breite;
	}

	public void setBreite(double breite) {
		this.breite = breite;
	}

	public double getLaenge() {
		return laenge;
	}

	public void setLaenge(double laenge) {
		this.laenge = laenge;
	}

	
	
	

}
