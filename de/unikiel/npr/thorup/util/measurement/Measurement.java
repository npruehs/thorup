package de.unikiel.npr.thorup.util.measurement;

import java.util.Scanner;

import de.unikiel.npr.thorup.algs.Dijkstra;
import de.unikiel.npr.thorup.algs.Kruskal;
import de.unikiel.npr.thorup.algs.Thorup;
import de.unikiel.npr.thorup.ds.ArrayPriorityQueue;
import de.unikiel.npr.thorup.ds.FibonacciHeap;
import de.unikiel.npr.thorup.ds.SplitFindminStructureGabow;
import de.unikiel.npr.thorup.ds.UnionFindStructureTarjan;
import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;

/**
 * An abstract series of measurent for running the algorithms by <i>Dijkstra</i>
 * and <i>Thorup</i> several times, taking the average over all passes, and
 * increasing a single value step by step.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public abstract class Measurement {
	/**
	 * The number of passes to take the average of.
	 */
	int numberOfPasses;
	
	/**
	 * The maximum number of vertices of this series of measurement. 
	 */
	int numberOfVertices;
	
	/**
	 * The number of vertices of the graph the current performance test runs on.
	 */
	int numberOfVerticesCurrent;
	
	/**
	 * The maximum number of edges per vertex of this series of measurement.
	 */
	int numberOfEdgesPerVertex;
	
	/**
	 * The maximum edge weight of this series of measurement.
	 */
	int maximumEdgeWeight;
	
	/**
	 * The value of the current performance test which is increased step by
	 * step.
	 */
	int currentStepValue;
	
	/**
	 * The maximum value of all performance tests.
	 */
	int maximumStepValue;
	
	/**
	 * All values that have been used so far for the performance tests.
	 */
	int[] allStepValues;
	
	/**
	 * The number of the current iteration of this series of measurement.
	 */
	int currentStep;
	
	/**
	 * The total number of the iterations of this series of measurement.
	 */
	int numberOfSteps;
	
	/**
	 * The difference, measured in milliseconds, between the start time of the
	 * current performance test and midnight, January 1, 1970 UTC.
	 */
	long start;
	
	/**
	 * The difference, measured in milliseconds, between the stop time of the
	 * current performance test and midnight, January 1, 1970 UTC.
	 */
	long stop;

	/**
	 * The running times of the most recent performance tests to compute the
	 * average of.
	 */
	long[] timesToComputeTheAverageOf;
	
	/**
	 * The running times of <i>Dijkstra</i>'s algorithm using an array heap
	 * in the most recent performance tests.
	 */
	long[] timesDijkstraArrayHeap;
	
	/**
	 * The running times of <i>Dijkstra</i>'s algorithm using a Fibonacci heap
	 * in the most recent performance tests.
	 */
	long[] timesDijkstraFibHeap;
	
	/**
	 * The times required for constructing the <i>msb</i>-minimum spanning trees
	 * for <i>Thorup</i>'s algorithm in the most recent performance tests.
	 */
	long[] timesThorupMST;
	
	/**
	 * The times required for constructing the component tree, initializing the
	 * bucket structure, and preparing the unvisited data structure for
	 * <i>Thorup</i>'s algorithm in the most recent performance tests.
	 */
	long[] timesThorupDS;
	
	/**
	 * The running times of the visiting part of <i>Thorup</i>'s algorithm in
	 * the most recent performance tests.
	 */
	long[] timesThorupVisit;
	
	/**
	 * The scanner used to read the user input.
	 */
	Scanner in;
	
	/**
	 * Whether all values and running times are written to the console again
	 * after this series of measurement.
	 */
	boolean writeTableColumns;
	
	
	/**
	 * Runs the algorithms by <i>Dijkstra</i> and <i>Thorup</i> several times,
	 * taking the average over all passes, and increasing a single value step
	 * by step.
	 */
	public void execute(){
		// initialize all variables
		numberOfSteps = maximumStepValue / currentStepValue;
		
		timesToComputeTheAverageOf = new long[numberOfPasses];
		
		timesDijkstraArrayHeap = new long[numberOfSteps];
		timesDijkstraFibHeap = new long[numberOfSteps];
		timesThorupMST = new long[numberOfSteps];
		timesThorupDS = new long[numberOfSteps];
		timesThorupVisit = new long[numberOfSteps];
		
		allStepValues = new int[numberOfSteps];
		
		System.out.println();
		
		// run this series of measurement until the maximum step value
		while (currentStepValue <= maximumStepValue) {
			// remember the current step value for evaluation purposes
			allStepValues[currentStep] = currentStepValue;
			
			// generate the next test instance
			System.out.println();
			System.out.print("Generating a random graph with " +
					numberOfVerticesCurrent + " vertices...");
			
			AdjacencyListWeightedDirectedGraph<WeightedEdge> graph =
				generateGraph();
			
			System.out.println(" done: Generated graph has " +
					graph.getNumberOfEdges() + " edges.");

			
			// run Dijkstra's algorithm with an array heap and take the time
			System.out.print("Running Dijkstra with an array priority " +
					"queue...");
			
			Dijkstra dijkstra = new Dijkstra();
			
			for (int pass = 0; pass < numberOfPasses; pass++) {
				start = System.currentTimeMillis();
				dijkstra.findShortestPaths(graph, 0,
						new ArrayPriorityQueue<Integer>
							(numberOfVerticesCurrent));
				stop = System.currentTimeMillis();
				
				timesToComputeTheAverageOf[pass] = stop - start;
			}
	
			// compute the average
			timesDijkstraArrayHeap[currentStep] =
				getAverage(timesToComputeTheAverageOf);
					
			// show the result
			System.out.println(" took " + timesDijkstraArrayHeap[currentStep] +
					" ms (average of " + numberOfPasses + " passes).");
			
			
			// run Dijkstra's algorithm with a Fibonacci heap and take the time
			System.out.print("Running Dijkstra with a Fibonacci heap...");
			
			for (int pass = 0; pass < numberOfPasses; pass++) {
				start = System.currentTimeMillis();
				dijkstra.findShortestPaths(graph, 0,
						new FibonacciHeap<Integer>());
				stop = System.currentTimeMillis();
				
				timesToComputeTheAverageOf[pass] = stop - start;
			}
	
			// compute the average
			timesDijkstraFibHeap[currentStep] =
				getAverage(timesToComputeTheAverageOf);
					
			// show the result
			System.out.println(" took " + timesDijkstraFibHeap[currentStep] +
					" ms (average of " + numberOfPasses + " passes).");
	
			
			/*
			 * construct the msb-minimum spanning tree for Thorup's algorithm
			 * and take the time
			 */
			System.out.print("Running Thorup...");
			
			Thorup thorup = new Thorup();
			
			for (int pass = 0; pass < numberOfPasses; pass++) {
				start = System.currentTimeMillis();
				thorup.constructMinimumSpanningTree(graph,
						new Kruskal(new UnionFindStructureTarjan<Integer>()));
				stop = System.currentTimeMillis();
				
				timesToComputeTheAverageOf[pass] = stop - start;
			}
	
			// compute the average
			timesThorupMST[currentStep] =
				getAverage(timesToComputeTheAverageOf);
					
			// show the result
			System.out.print(" took " + timesThorupMST[currentStep] +
					" ms for constructing the MST, ");
			
			
			/*
			 * construct all other data structures for Thorup's algorithm and
			 * take the time
			 */
			for (int pass = 0; pass < numberOfPasses; pass++) {
				start = System.currentTimeMillis();
				thorup.constructOtherDataStructures
					(new UnionFindStructureTarjan<Integer>(),
					 new SplitFindminStructureGabow<Integer>
						(numberOfVerticesCurrent));
				stop = System.currentTimeMillis();
				
				timesToComputeTheAverageOf[pass] = stop - start;
			}
	
			// compute the average
			timesThorupDS[currentStep] =
				getAverage(timesToComputeTheAverageOf);
					
			// show the result
			System.out.print(timesThorupDS[currentStep] +
					" ms for constructing the other data structures,");
			
			
			// run Thorup's algorithm and take the time
			for (int pass = 0; pass < numberOfPasses; pass++) {
				if (pass > 0) {
					thorup.cleanUpBetweenQueries
						(new SplitFindminStructureGabow<Integer>
							(graph.getNumberOfVertices()));
				}

				start = System.currentTimeMillis();
				thorup.findShortestPaths(0);
				stop = System.currentTimeMillis();
				
				timesToComputeTheAverageOf[pass] = stop - start;
			}
	
			// compute the average
			timesThorupVisit[currentStep] =
				getAverage(timesToComputeTheAverageOf);
					
			// show the result
			System.out.println(" and " + timesThorupVisit[currentStep] +
					" ms for visiting all vertices (average of " +
					numberOfPasses + " passes).");
			
			
			// prepare next iteration
			currentStepValue += allStepValues[0];
			currentStep++;
			
			customUpdate();
		}
		
		if (writeTableColumns) {
			// write all values and running times to the console
			System.out.println();
			
			System.out.println("All step values:");
			writeTableColumn(allStepValues);
			writeLatexTableRow(allStepValues);
			System.out.println();
			
			System.out.println("All times of Dijkstra with array heap:");
			writeTableColumn(timesDijkstraArrayHeap);
			writeLatexTableRow(timesDijkstraArrayHeap);
			System.out.println();
			
			System.out.println("All times of Dijkstra with Fibonacci heap:");
			writeTableColumn(timesDijkstraFibHeap);
			writeLatexTableRow(timesDijkstraFibHeap);
			System.out.println();
			
			System.out.println("All times of Thorup (construct MST):");
			writeTableColumn(timesThorupMST);
			writeLatexTableRow(timesThorupMST);
			System.out.println();
			
			System.out.println("All times of Thorup (construct other DS):");
			writeTableColumn(timesThorupDS);
			writeLatexTableRow(timesThorupDS);
			System.out.println();
			
			System.out.println("All times of Thorup (visit)");
			writeTableColumn(timesThorupVisit);
			writeLatexTableRow(timesThorupVisit);
			System.out.println();
		}
	}
	
	/**
	 * Generates the connected, weighted, undirected graph for the next
	 * iteration of this series of measurement.
	 * 
	 * @return
	 * 		the connected, weighted, undirected graph for the next iteration of
	 * 		this series of measurement
	 */
	public abstract AdjacencyListWeightedDirectedGraph<WeightedEdge>
		generateGraph();
	
	/**
	 * Reads some values from user input, depending on the type of this series
	 * of measurement.
	 */
	public abstract void readValuesFromUserCustom();
	
	/**
	 * Allows subclasses to update some values in every iteration, depending on
	 * the type series of measurement.
	 */
	public abstract void customUpdate();
	
	/**
	 * Reads the parameters for this series of measurement from user input.
	 */
	public void readValuesFromUser() {
		System.out.println("This application allows measuring the " +
				"performance of Thorup's SSSP algorithm compared to " +
				"the one of the algorithm by Dijkstra.");
		
		System.out.println();
		
		in = new Scanner(System.in);
		
		System.out.print("Please specify the number of passes for each " +
				"experiment to take the average of: ");
		numberOfPasses = in.nextInt();
		
		System.out.print("Please specify the maximum number of vertices: ");
		numberOfVertices = in.nextInt();
		numberOfVerticesCurrent = numberOfVertices;
		
		System.out.print("Please specify the maximum number of edges per" +
				" vertex: ");
		numberOfEdgesPerVertex = in.nextInt();
		
		System.out.print("Please specify the the maximum edge weight: ");
		maximumEdgeWeight = in.nextInt();

		readValuesFromUserCustom();
		
		System.out.print("Do you want to write the table columns to the " +
				"console (Y/N)? ");
		in.nextLine();
		writeTableColumns = (in.nextLine()).equals("Y");
	}
	
	/**
	 * Computes and returns the average of the values in the passed
	 * <code>long</code> array.
	 * 
	 * @param values
	 * 		the values to compute the average of
	 * @return
	 * 		the average of the values in the passed <code>long</code> array
	 */
	public long getAverage(long[] values) {
		long avg = values[0];
		
		for (int i = 1; i < values.length; i++) {
			avg += values[i];
		}
		
		avg /= values.length;
		return avg;
	}
	
	/**
	 * Writes a column with the values of the passed <code>int</code> array
	 * to the console.
	 * 
	 * @param values
	 * 		the values to write to the console
	 */
	public void writeTableColumn(int[] values) {
		for (int i = 0; i < values.length; i++) {
			System.out.println(values[i]);
		}
	}
	
	/**
	 * Writes a column with the values of the passed <code>long</code> array
	 * to the console.
	 * 
	 * @param values
	 * 		the values to write to the console
	 */
	public void writeTableColumn(long[] values) {
		for (int i = 0; i < values.length; i++) {
			System.out.println(values[i]);
		}
	}
	
	/**
	 * Writes a latex table row with every second value of the passed
	 * <code>int</code> array to the console.
	 * 
	 * @param values
	 * 		the values to write to the console
	 */
	public void writeLatexTableRow(int[] values) {
		for (int i = 1; i < values.length; i+= 2) {
			System.out.print(values[i]);
			
			if (i < values.length - 1) {
				System.out.print(" & ");
			} else {
				System.out.println("\\\\");
			}
		}
	}
	
	/**
	 * Writes a latex table row with every second value of the passed
	 * <code>long</code> array to the console.
	 * 
	 * @param values
	 * 		the values to write to the console
	 */
	public void writeLatexTableRow(long[] values) {
		for (int i = 1; i < values.length; i+= 2) {
			System.out.print(values[i]);
			
			if (i < values.length - 1) {
				System.out.print(" & ");
			} else {
				System.out.println("\\\\");
			}
		}
	}
}
