package aco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

import rundweg.Edge;
import rundweg.Places;

public class AntColonyOptimization {

	private double c = 1.0;
	private double alpha = 1;
	private double beta = 5;
	private double evaporation = 0.5;
	private double Q = 500;
	private double antFactor = 0.8;
	private double randomFactor = 0.01;

	private int maxIterations = 100000;

	private int numberOfCities;
	private int numberOfAnts;
	private double graph[][];
	ArrayList<Edge> graph2 = new ArrayList<>();
	ArrayList<Places> pl = new ArrayList<>();
	private double trails[][];
	private List<Ant> ants = new ArrayList<>();
	private Random random = new Random();
	private double probabilities[];

	private int currentIndex;

	private int[] bestTourOrder;
	private double bestTourLength;

	public AntColonyOptimization(ArrayList<Places> place, ArrayList<Edge> tabu) {
		pl = place;
		graph = generatePlacesMatrix(place.size());
		numberOfCities = place.size();
		numberOfAnts = (int) (numberOfCities * antFactor);
		trails = new double[numberOfCities][numberOfCities];
		probabilities = new double[numberOfCities];
		IntStream.range(0, numberOfAnts).forEach(i -> ants.add(new Ant(numberOfCities)));
	}

	public double getWeight(Places sor, Places des) {

		double weight = Math.sqrt(Math.pow((sor.getBreite() - des.getBreite()) * 111.3, 2)
				+ Math.pow((sor.getLaenge() - des.getLaenge()) * 71.5, 2));
		return weight;
	}

	public ArrayList<Integer> sort(int[] list) {
		ArrayList<Integer> before = new ArrayList<>();
		ArrayList<Integer> sorted = new ArrayList<>();
		for (int i = 0; i < list.length; i++) {
			if (list[i] == 0 || sorted.size() > 0) {
				sorted.add(list[i]);
			} else {
				before.add(list[i]);
			}
		}
		sorted.addAll(before);
		System.out.println(sorted.toString());
		return sorted;
	}

	/**
	 * Generate initial solution
	 */
	public double[][] generatePlacesMatrix(int n) {
		double[][] placeMatrix = new double[n][n];
		IntStream.range(0, n)
				.forEach(i -> IntStream.range(0, n).forEach(j -> placeMatrix[i][j] = getWeight(pl.get(i), pl.get(j))));

		return placeMatrix;
	}

	/**
	 * Perform ant optimization
	 */
	public void startAntOptimization() {
		IntStream.rangeClosed(1, 3).forEach(i -> {
			System.out.println("Attempt #" + i);
			int[] k = solve();
			sort(k);
		});
	}
	// public void startAntOptimization() {
	// IntStream.rangeClosed(1, 3).forEach(i -> {
	// System.out.println("Attempt #" + i);
	//
	// });
	// }

	/**
	 * Use this method to run the main logic
	 */
	public int[] solve() {
		setupAnts();
		clearTrails();
		IntStream.range(0, maxIterations).forEach(i -> {
			moveAnts();
			updateTrails();
			updateBest();
		});
		System.out.println("Best tour length: " + (bestTourLength - numberOfCities));
		System.out.println("Best tour order: " + Arrays.toString(bestTourOrder));
		return bestTourOrder.clone();
	}

	/**
	 * Prepare ants for the simulation
	 */
	private void setupAnts() {
		IntStream.range(0, numberOfAnts).forEach(i -> {
			ants.forEach(ant -> {
				ant.clear();
				ant.visitCity(-1, random.nextInt(numberOfCities));
			});
		});
		currentIndex = 0;
	}

	/**
	 * At each iteration, move ants
	 */
	private void moveAnts() {
		IntStream.range(currentIndex, numberOfCities - 1).forEach(i -> {
			ants.forEach(ant -> ant.visitCity(currentIndex, selectNextCity(ant)));
			currentIndex++;
		});
	}

	/**
	 * Select next city for each ant
	 */
	private int selectNextCity(Ant ant) {
		int t = random.nextInt(numberOfCities - currentIndex);
		if (random.nextDouble() < randomFactor) {
			OptionalInt cityIndex = IntStream.range(0, numberOfCities).filter(i -> i == t && !ant.visited(i))
					.findFirst();
			if (cityIndex.isPresent()) {
				return cityIndex.getAsInt();
			}
		}
		calculateProbabilities(ant);
		double r = random.nextDouble();
		double total = 0;
		for (int i = 0; i < numberOfCities; i++) {
			total += probabilities[i];
			if (total >= r) {
				return i;
			}
		}

		throw new RuntimeException("There are no other cities");
	}

	/**
	 * Calculate the next city picks probabilites
	 */
	public void calculateProbabilities(Ant ant) {
		int i = ant.trail[currentIndex];
		double pheromone = 0.0;
		for (int l = 0; l < numberOfCities; l++) {
			if (!ant.visited(l)) {
				pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta);

			}
		}
		for (int j = 0; j < numberOfCities; j++) {
			if (ant.visited(j)) {
				probabilities[j] = 0.0;
			} else {
				double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta);
				probabilities[j] = numerator / pheromone;
			}
		}
	}

	/**
	 * Update trails that ants used
	 */
	private void updateTrails() {
		for (int i = 0; i < numberOfCities; i++) {
			for (int j = 0; j < numberOfCities; j++) {
				trails[i][j] *= evaporation;
			}
		}
		for (Ant a : ants) {
			double contribution = Q / a.trailLength(graph);
			for (int i = 0; i < numberOfCities - 1; i++) {
				trails[a.trail[i]][a.trail[i + 1]] += contribution;
			}
			trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
		}
	}

	/**
	 * Update the best solution
	 */
	private void updateBest() {
		if (bestTourOrder == null) {
			bestTourOrder = ants.get(0).trail;
			bestTourLength = ants.get(0).trailLength(graph);
		}
		for (Ant a : ants) {
			if (a.trailLength(graph) < bestTourLength) {
				bestTourLength = a.trailLength(graph);
				bestTourOrder = a.trail.clone();
			}
		}
	}

	/**
	 * Clear trails after simulation
	 */
	private void clearTrails() {
		IntStream.range(0, numberOfCities).forEach(i -> {
			IntStream.range(0, numberOfCities).forEach(j -> trails[i][j] = c);
		});
	}
}
