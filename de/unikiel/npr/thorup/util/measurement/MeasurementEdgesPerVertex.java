package de.unikiel.npr.thorup.util.measurement;

import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;
import de.unikiel.npr.thorup.util.RandomGraphGenerator;

/**
 * A series of measurent for running the algorithms by <i>Dijkstra</i>
 * and <i>Thorup</i> several times, taking the average over all passes, and
 * increasing the number of edges per vertex step by step.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class MeasurementEdgesPerVertex extends Measurement {
	/**
	 * Generates the connected, weighted, undirected graph with the next number
	 * of edges per vertex.
	 * 
	 * @return
	 * 		the connected, weighted, undirected graph with the next number
	 * 		of edges per vertex
	 */
	public AdjacencyListWeightedDirectedGraph<WeightedEdge> generateGraph() {
		return RandomGraphGenerator.generateConnectedWeightedUndirectedGraph
			(numberOfVertices, maximumEdgeWeight, currentStepValue);
	}
	
	/**
	 * Reads the size of the steps the number of edges per vertex are increased
	 * in each iteration.
	 */
	public void readValuesFromUserCustom() {
		System.out.print("Please specify the steps the number of edges per " +
		"vertex of the instances should be increased (step size): ");
		
		currentStepValue = in.nextInt();
		maximumStepValue = numberOfEdgesPerVertex;
	}
	
	/**
	 * Does nothing.
	 */
	public void customUpdate() {}
	
	
	/**
	 * Reads all required parameters from user input and runs a series of
	 * measurent for running the algorithms by <i>Dijkstra</i> and
	 * <i>Thorup</i> several times, taking the average over all passes, and
	 * increasing the number of edges per vertex step by step.
	 * 
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		MeasurementEdgesPerVertex m = new MeasurementEdgesPerVertex();
		m.readValuesFromUser();
		m.execute();
	}
}
