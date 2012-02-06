package de.unikiel.npr.thorup.util;

import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;

/**
 * A utility class which provides methods for generating (pseudo-)random graphs.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class RandomGraphGenerator {
	/**
	 * Generates a pseudo-random connected, weighted, undirected graph with the
	 * specified number of vertices, about the specified number of edges per
	 * vertex, and the passed maximum edge weight.
	 * 
	 * @see #generateConnectedWeightedUndirectedGraph(int, int, double)
	 * @param n
	 * 		the number of vertices of the generated graph
	 * @param maxWeight
	 * 		the maximum edge weight of the generated graph
	 * @param k
	 * 		the number of edges per vertex of the generated graph
	 * @return
	 * 		a pseudo-random weighted, undirected graph with <code>n</code>
	 * 		vertices, about <code>kn</code> edges and a maximum edge weight
	 * 		of <code>maxWeight</code>
	 */
	public static AdjacencyListWeightedDirectedGraph<WeightedEdge>
		generateConnectedWeightedUndirectedGraph(int n, int maxWeight, int k) {
		double p = (double)(2 * (n * k - n + 1)) / (double)((n - 1) * (n - 2));
		return generateConnectedWeightedUndirectedGraph(n, maxWeight, p);
	}
	
	/**
	 * Generates a pseudo-random connected, weighted, undirected graph with the
	 * specified number of vertices and the passed maximum edge weight, as
	 * follows:
	 * 
	 * <ol>
	 * 		<li>For every pair {i, i + 1}, i < n - 1, of vertex indices
	 * 			a forged coin is thrown.</li>
	 *		<li>For every pair {i, j} of remaining vertex indices a forged coin
	 *			is thrown.</li>
	 * </ol>
	 * 
	 * Every time the coin heads (with probability <code>p</code>), a
	 * pseudo-random number between 1 and <code>maxWeight</code>, both
	 * inclusive, is chosen, and two edges (i, j) and (j, i) with the chosen
	 * weight are added to the graph.<br>
	 * <br>
	 * The first step ensures the connectivity of the resulting graph.
	 * 
	 * @param n
	 * 		the number of vertices of the generated graph
	 * @param maxWeight
	 * 		the maximum edge weight of the generated graph
	 * @param p
	 * 		the probability the coin heads
	 * @return
	 * 		a pseudo-random weighted, undirected graph with <code>n</code>
	 * 		vertices and a maximum edge weight of <code>maxWeight</code>
	 */
	public static AdjacencyListWeightedDirectedGraph<WeightedEdge>
		generateConnectedWeightedUndirectedGraph(int n, int maxWeight,
		double p) {
		
		// construct new graph with the passed number of vertices
		AdjacencyListWeightedDirectedGraph<WeightedEdge> g =
			new AdjacencyListWeightedDirectedGraph<WeightedEdge>(n);
		
		// initialize two pseudo-random number generators
		java.util.Random coin = new java.util.Random();
		java.util.Random scales = new java.util.Random();
		
		for (int i = 0; i < n - 1; i++) {
			int weight = scales.nextInt(maxWeight) + 1;
			
			g.addEdge(new WeightedEdge(i, i + 1, weight));
			g.addEdge(new WeightedEdge(i + 1, i, weight));
		}
		
		// iterate all pairs of vertex indices
		for (int i = 0; i < n; i++) {
			for (int j = i + 2; j < n; j++) {
				// flip a coin
				if (coin.nextDouble() < p) {
					// if the coin heads, generate a random edge weight
					int weight = scales.nextInt(maxWeight) + 1;
					
					// add two edges to the graph
					g.addEdge(new WeightedEdge(i, j, weight));
					g.addEdge(new WeightedEdge(j, i, weight));
				}
			}
		}
		
		return g;
	}
}
