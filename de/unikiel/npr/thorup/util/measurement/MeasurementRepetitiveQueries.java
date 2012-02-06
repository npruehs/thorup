package de.unikiel.npr.thorup.util.measurement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import de.unikiel.npr.thorup.algs.Dijkstra;
import de.unikiel.npr.thorup.algs.Kruskal;
import de.unikiel.npr.thorup.algs.Thorup;
import de.unikiel.npr.thorup.ds.FibonacciHeap;
import de.unikiel.npr.thorup.ds.SplitFindminStructureGabow;
import de.unikiel.npr.thorup.ds.UnionFindStructureTarjan;
import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;
import de.unikiel.npr.thorup.util.DIMACSGraphParser;

/**
 * A series of measurent for running the algorithms by <i>Dijkstra</i>
 * and <i>Thorup</i> several times on the same instance, measuring their
 * performance for repetitive queries.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class MeasurementRepetitiveQueries {
	/**
	 * The message to show whenever a wrong number of command-line parameters
	 * is passed.
	 */
	public static final String USAGE = "MeasurementRepetitiveQueries " +
			"<zippedDIMACSGraphFile> [maximumNumberOfQueries]";

	/**
	 * The default maximum number of queries done in this series of measurement.
	 */
	public static final int DEFAULT_MAXIMUM_NUMBER_OF_QUERIES = 10;


	/**
	 * Reads a connected, weighted, undirected graph in DIMACS format from the
	 * passed file and runs a series of measurent for running the algorithms by
	 * <i>Dijkstra</i> and <i>Thorup</i> several times on the read graph,
	 * measuring their performance for repetitive queries.<br>
	 * <br>
	 * The series of measurent finishes as soon as <i>Thorup</i>'s algorithm has
	 * caught up with the one by <i>Dijkstra</i>, or when the maximum number
	 * of queries has been made.
	 * 
	 * @see #DEFAULT_MAXIMUM_NUMBER_OF_QUERIES
	 * @param args
	 * 		<code>args[0]</code> is the file to read the input graph from, and
	 * 		<br>
	 * 		<code>args[1]</code> is the maximum number of queries made
	 * 		(optional)
	 */
	public static void main(String[] args) {
		// check the number of command-line arguments
		if (args.length < 1 || args.length > 2) {
			System.out.println(USAGE);
			System.exit(1);
		}
		
		// initialize all variables
		int numberOfQueries = 0;
		int maximumNumberOfQueries = DEFAULT_MAXIMUM_NUMBER_OF_QUERIES;
		
		long start;
		long stop;
		
		LinkedList<Long> accumulatedTimesDijkstraFibHeap =
			new LinkedList<Long>();
		LinkedList<Long> accumulatedTimesThorupVisit =
			new LinkedList<Long>();
		
		long mostRecentTimeDijkstraFibHeap = 0;
		long mostRecentTimeThorupVisit = 0;
		
		int[] distancesDijsktra;
		int[] distancesThorup;
		
		
		// try to get the passed file
		File f = new File(args[0]);
		
		if (!f.exists() || f.isDirectory()) {
			System.err.println("File not found or is a directory: " + args[0]);
			System.out.println(USAGE);
			System.exit(1);
		}
		
		// try to read the maximum number of queries
		if (args.length > 1) {
			try {
				maximumNumberOfQueries = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println(args[1] + " is no valid maximum number " +
						"of queries.");
				System.out.println(USAGE);
				System.exit(1);
			}
		}
		
		// try to read the input graph
		System.out.println("Reading graph from " + args[0] + "...");
		AdjacencyListWeightedDirectedGraph<WeightedEdge> graph = null;
		
		try {
			// construct new input stream to the file
			GZIPInputStream zipIn = new GZIPInputStream(new FileInputStream(f));
			
			// create a memory representation of the graph
			boolean verbose = (args.length == 2 && args[1].equals("-verbose"));
		graph =	new DIMACSGraphParser(verbose).readDIMACSGraph(zipIn);
		} catch (IOException e) {
			System.err.println("An I/O error has occured reading from the " +
					"specified file.");
			System.exit(1);
		} catch (IllegalArgumentException e) {
			System.err.println("The specified file does not contain a " +
					"graph in DIMACS input format.");
			System.exit(1);
		}

		System.out.println("Graph has been read: Has " +
				graph.getNumberOfVertices() + " vertices and " +
				graph.getNumberOfEdges() + " edges.");
		
		
		// prepare the algorithms
		Dijkstra dijkstra = new Dijkstra();
		
		System.out.print("Preparing Thorup...");
		Thorup thorup = new Thorup();
		
		start = System.currentTimeMillis();
		thorup.constructMinimumSpanningTree(graph,
				new Kruskal(new UnionFindStructureTarjan<Integer>()));
		stop = System.currentTimeMillis();
		
		mostRecentTimeThorupVisit += stop - start;

		// show the result
		System.out.print(" took " + mostRecentTimeThorupVisit +
				" ms for constructing the MST, ");

		start = System.currentTimeMillis();
		thorup.constructOtherDataStructures
			(new UnionFindStructureTarjan<Integer>(),
			 new SplitFindminStructureGabow<Integer>
				(graph.getNumberOfVertices()));
		stop = System.currentTimeMillis();
		
		mostRecentTimeThorupVisit += stop - start;

		// show the result
		System.out.println("and " + mostRecentTimeThorupVisit +
				" ms for constructing the other data structures.");

		
		while (mostRecentTimeThorupVisit > mostRecentTimeDijkstraFibHeap &&
			   numberOfQueries <= maximumNumberOfQueries) {
			
			// run Dijkstra's algorithm with a Fibonacci heap and take the time
			System.out.print("Running Dijkstra with a Fibonacci heap...");
			
			start = System.currentTimeMillis();
			dijkstra.findShortestPaths(graph, numberOfQueries,
					new FibonacciHeap<Integer>());
			stop = System.currentTimeMillis();
			
			mostRecentTimeDijkstraFibHeap += stop - start;
			accumulatedTimesDijkstraFibHeap.add(mostRecentTimeDijkstraFibHeap);
	
			// show the result
			System.out.println(" took " + (stop - start) +
					" ms for this query and " + mostRecentTimeDijkstraFibHeap +
					" ms in total.");
			distancesDijsktra = dijkstra.getDistances();
			
			
			// run Thorup's algorithm and take the time
			System.out.print("Running Thorup...");
			
			if (numberOfQueries > 0) {
				start = System.currentTimeMillis();
				thorup.cleanUpBetweenQueries
					(new SplitFindminStructureGabow<Integer>
						(graph.getNumberOfVertices()));
				stop = System.currentTimeMillis();
				
				mostRecentTimeThorupVisit += stop - start;
				
				System.out.print(" took " + (stop - start) +
						" ms for tidying up the data strcutures and ");
			}
			
			start = System.currentTimeMillis();
			distancesThorup = thorup.findShortestPaths(numberOfQueries);
			stop = System.currentTimeMillis();
			
			mostRecentTimeThorupVisit += stop - start;
			accumulatedTimesThorupVisit.add(mostRecentTimeThorupVisit);
					
			// show the result
			System.out.println(" took " + (stop - start) +
					" ms for this query and " + mostRecentTimeThorupVisit +
					" ms in total.");

			
			// check the integrety of all results
			System.out.println("Checking the results... ");
			
			LinkedList<Integer> differentDistances = new LinkedList<Integer>();
			
			for (int i = 0; i < distancesDijsktra.length; i++) {
				if (distancesDijsktra[i] != distancesThorup[i]) {
					differentDistances.add(i);
				}
			}
			
			if (differentDistances.isEmpty()) {
				System.out.println("The distances of all vertices computed " +
						"with Thorup's algorithm are equal to ones computed " +
						"with Dijkstra's algorithm.");
			} else {
				for (Integer i : differentDistances) {
					System.err.println("ERROR: The distances of the vertex "
							+ i + " differ! (Dijkstra = " +
							distancesDijsktra[i] + "   Thorup = " +
							distancesThorup[i] + ")");
				}
			}
			
			System.out.println();
			
			// prepare next iteration
			numberOfQueries++;
		}
		
		if (mostRecentTimeThorupVisit > mostRecentTimeDijkstraFibHeap) {
			System.out.println("Thorup caught up with Dijkstra after " +
					numberOfQueries + " queries.");
		}
		
		// write all accumulated running times to the console
		System.out.println("Accumulated running times of Dijkstra with " +
				"Fibonacci heap:");
		for (Long cumulatedRunningTime : accumulatedTimesDijkstraFibHeap) {
			System.out.println(cumulatedRunningTime);
		}
		
		System.out.println();
		
		System.out.println("Accumulated running times of Thorup:");
			
		for (Long cumulatedRunningTime : accumulatedTimesThorupVisit) {
			System.out.println(cumulatedRunningTime);
		}
	}
}
