
package rundweg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import aco.AntColonyOptimization;
import darstellung.Map2DRenderer;

public class Main {

	public static void main(String args[]) {

		JFrame frame = new JFrame("Test Render2D");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1000, 750);
		frame.setLocationRelativeTo(null);

		// Einlesen der Standorte
		ArrayList<Places> pl = ReadCSV.getCsvList();
		// Erstellen eines kompletten Wegenetzes
		ArrayList<Edge> tabu = createWegenetz(pl);

		// Code Angepasst aus https://www.baeldung.com/java-ant-colony-optimization als
		// Vergleich zum eigentlichen Algorithmus
		AntColonyOptimization aco = new AntColonyOptimization(pl, tabu);
		aco.startAntOptimization();

		// Start in Ismaning
		Places current = pl.get(0);
		ArrayList<Edge> currentpath = new ArrayList<Edge>();
		ArrayList<Places> reachedPlaces = new ArrayList<Places>();
		//
		ArrayList<Edge> path = nearestNeigbor(pl, current, currentpath, reachedPlaces);
		path = simulatingAnnealing(path, reachedPlaces, tabu);
		Map2DRenderer render2D = new Map2DRenderer(reachedPlaces, path);
		frame.add(render2D);
		frame.setVisible(true);
		render2D.setParamWorld2Screen(-1, -1, 10);
		render2D.repaint();

	}

	public static ArrayList<Edge> createWegenetz(ArrayList<Places> o) {
		int k = 0;
		ArrayList<Edge> ways = new ArrayList<Edge>();

		for (int i = 0; i < o.size(); i++) {
			for (int j = i + 1; j < o.size(); j++) {
				k++;
				double weight = getWeight(o.get(i), o.get(j));
				Edge z = new Edge(o.get(i), o.get(j), weight, k);
				ways.add(z);
			}
		}
		Collections.sort(ways, new SortEdges());

		return ways;
	}

	private static ArrayList<Edge> nearestNeigbor(ArrayList<Places> allPlaces, Places currentPlace,
			ArrayList<Edge> path, ArrayList<Places> reachedPlaces) {
		Places zw = null;
		int edgeID = 0;
		double minDis = Double.MAX_VALUE;

		allPlaces.remove(currentPlace);
		reachedPlaces.add(currentPlace);

		if (allPlaces.size() == 0) {
			edgeID++;
			double weight = getWeight(currentPlace, reachedPlaces.get(0));
			Edge addway = new Edge(currentPlace, reachedPlaces.get(0), weight, edgeID);
			path.add(addway);
		} else {
			// Suche des besten verfügbaren Weges
			for (Places a : allPlaces) {
				double weight = getWeight(currentPlace, a);
				if (weight < minDis && weight > 0) {
					zw = a;
					minDis = weight;
				}
			}
			edgeID++;
			Edge newway = new Edge(currentPlace, zw, minDis, edgeID);

			path.add(newway);
			if (allPlaces.size() > 0) {
				currentPlace = zw;
				nearestNeigbor(allPlaces, currentPlace, path, reachedPlaces);
			}
		}

		return path;
	}

	public static ArrayList<Edge> simulatingAnnealing(ArrayList<Edge> ein, ArrayList<Places> reach,
			ArrayList<Edge> tab) {
		//"Anfangstemperatur"
		double temp = 100000;
		int k = 0;
		// "Cooling rate"
		double coolingRate = 0.00003;

		ArrayList<Integer> swappedSolution = new ArrayList<>();
		
		ArrayList<Edge> currentSolution = new ArrayList<>(ein);
		ArrayList<Places> currentOrder = new ArrayList<>(reach);

		double discurrent = calcDistance(currentSolution);
		double bestdis = calcDistance(currentSolution);

		System.out.println("Initial solution distance: " + discurrent);

		//Initialisierung der Lösung
		ArrayList<Edge> bestSol = new ArrayList<>(currentSolution);
		ArrayList<Places> bestOrder = new ArrayList<>(currentOrder);

		//Einschränkung der wählbaren Wege
		ArrayList<Edge> good = new ArrayList<Edge>();
		ArrayList<Edge> bad = new ArrayList<Edge>();

		for (int i = 0; i < tab.size(); i++) {
			if (i < 50) {
				good.add(tab.get(i));
			} else {
				bad.add(tab.get(i));
			}
		}
		
		
		int durchläufe = 0;

		// Algorithmus läuft bis "Temperatur" runter gekühlt ist
		while (temp > 1) {

			durchläufe++;
			
			ArrayList<Edge> newSolution = new ArrayList<Edge>();
			ArrayList<Places> newOrder = new ArrayList<Places>();
			
			newOrder.addAll(currentOrder);
			
			//2 Standorte der aktuellen Tour zufällig tauschen
			int tourPos1 = (int) (newOrder.size() * Math.random());
			int tourPos2 = (int) (newOrder.size() * Math.random());

			Places citySwap1 = newOrder.get(tourPos1);
			Places citySwap2 = newOrder.get(tourPos2);
			int badSwap = Integer.parseInt(citySwap1.getId() + "" + citySwap2.getId());

			double wei = getWeight(newOrder.get(tourPos1), newOrder.get(tourPos2));
			//Testen ob der zufällige Tausch sinnvoll ist 
			while (tourPos1 == tourPos2 || (tourPos1 == 0 || tourPos2 == 0) || bad.get(0).getWeight() < wei
					|| swappedSolution.contains(badSwap)) {
				
				tourPos1 = (int) (newOrder.size() * Math.random());
				tourPos2 = (int) (newOrder.size() * Math.random());

				wei = getWeight(newOrder.get(tourPos1), newOrder.get(tourPos2));

				citySwap1 = newOrder.get(tourPos1);
				citySwap2 = newOrder.get(tourPos2);
				badSwap = Integer.parseInt(citySwap1.getId() + "" + citySwap2.getId());
				
				if (swappedSolution.size() > 50) {
					break;
				}
			}

			// Get the cities at selected positions in the tour
			citySwap1 = newOrder.get(tourPos1);
			citySwap2 = newOrder.get(tourPos2);

			swappedSolution.add(badSwap);

			// Positionstausch
			newOrder.set(tourPos2, citySwap1);
			newOrder.set(tourPos1, citySwap2);

			for (int i = 0; i < newOrder.size(); i++) {
				int n = 1;
				if (i == newOrder.size() - 1) {
					n = -newOrder.size() + 1;
				}
				k++;
				double weight = getWeight(newOrder.get(i), newOrder.get(i + n));
				Edge z = new Edge(newOrder.get(i), newOrder.get(i + n), weight, k);
				newSolution.add(z);
			}
			// Gesamtdistanz berechnen
			double currentEnergy = calcDistance(currentSolution);
			double neighbourEnergy = calcDistance(newSolution);

			double rand = randomDouble();
			//Falls die neue Lösung kürzer ist, wird diese genutzt  
			if (neighbourEnergy < currentEnergy) {
				currentSolution = new ArrayList<>(newSolution);
				currentOrder = new ArrayList<>(newOrder);
				discurrent = calcDistance(currentSolution);
				swappedSolution.removeAll(swappedSolution);
			
			//Ist die neue Lösung nicht besser wird über eiine Wahrscheinlichkeit berechnet, ob diese trotzdem genutzt wird
			} else if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > rand) {
				currentSolution = new ArrayList<>(newSolution);
				currentOrder = new ArrayList<>(newOrder);
				discurrent = calcDistance(currentSolution);
				swappedSolution.removeAll(swappedSolution);
			}

			// Aktualisierung der besten Lösung
			if (calcDistance(currentSolution) < calcDistance(bestSol) && newSolution.size() == 21) {
				bestSol = currentSolution;
				bestOrder = newOrder;
				bestdis = calcDistance(currentSolution);
			}

			// Cool system
			temp *= 1 - coolingRate;
		}
		System.out.println(durchläufe + " Final solution distance: " + bestdis);
		return bestSol;
	}

	private static Double calcDistance(ArrayList<Edge> path) {
		double dis = 0;
		for (Edge i : path) {
			dis += i.getWeight();
		}
		return dis;
	}

	public static double acceptanceProbability(double currentDistance, double newDistance, double temperature) {
		return Math.exp((currentDistance - newDistance) / temperature);
	}

	static double randomDouble() {
		Random r = new Random();
		return r.nextInt(1000) / 1000.0;
	}

	public static double getWeight(Places sor, Places des) {
		return Math.sqrt(Math.pow((sor.getBreite() - des.getBreite()) * 111.3, 2)
				+ Math.pow((sor.getLaenge() - des.getLaenge()) * 71.5, 2));
	}

	// public static ArrayList<Edge> randomSolution(ArrayList<Places> pl) {
	// Places cur = pl.get(0);
	// int i = 0;
	// ArrayList<Places> reached = new ArrayList<>();
	// ArrayList<Edge> path = new ArrayList<>();
	// reached.add(cur);
	// for (int j = 0; j < pl.size(); j++) {
	// Places tar = pl.get((int) (pl.size() * Math.random()));
	// while (reached.contains(tar)) {
	// tar = pl.get((int) (pl.size() * Math.random()));
	// }
	// double w = getWeight(cur, tar);
	// Edge g = new Edge(cur, tar, w, i++);
	// path.add(g);
	// reached.add(tar);
	// cur = tar;
	// if (reached.size() == 21) {
	// break;
	// }
	//
	// }
	// double w = getWeight(cur, pl.get(0));
	// Edge g = new Edge(cur, pl.get(0), w, i++);
	// path.add(g);
	// System.out.println(path.size() + " " + calcDis(path));
	// return path;
	//
	// }

}
