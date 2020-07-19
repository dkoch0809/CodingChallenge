package rundweg;

import java.util.ArrayList;

public class Edge {
	private Places source;

	// End-Knoten der Kante
	private Places destination;

	// Gewicht der Kante
	private double weight = 0.0;

	// id der Kante
	private int id;

	public Edge() {

	}

	public Edge(Places places, Places places2) {
		this.source = places;
		this.destination = places2;
	}

	// Konstruktor
	public Edge(Places places, Places places2, double weight2, int id) {

		this.source = places;
		this.destination = places2;
		this.weight = weight2;
		this.id = id;
	}

	// getter, setter und toString
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Places getDestination() {
		return destination;
	}

	public void setDestination(Places destination) {
		this.destination = destination;
	}

	public void setSource(Places source) {
		this.source = source;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Places getSource() {
		return source;
	}

	public double getWeight() {
		return weight;
	}

	

	public Places getSourcebyD(ArrayList<Edge> list, Places sor) {
		Places k = null;
		for (Edge edge : list) {
			if (edge.getSource() == sor) {
				k = edge.getDestination();
			}
			if (edge.getDestination() == sor) {
				k = edge.getSource();
			}
		}
		return k;
	}

	@Override
	public String toString() {
		return source + " " + destination + " " + weight;
	}

}
