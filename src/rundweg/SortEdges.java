package rundweg;

import java.util.Comparator;

public class SortEdges implements Comparator<Edge> {

	@Override
	public int compare(Edge a1, Edge a2) {
		return (int) (a1.getWeight()*1000000 - a2.getWeight()*1000000);
	}

}