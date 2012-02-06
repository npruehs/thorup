package de.unikiel.npr.thorup.util;

import java.io.IOException;
import java.io.InputStream;

import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;

/**
 * A parser for parsing a graph in DIMACS input format from any input
 * stream to its correponding memory representation.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class DIMACSGraphParser {
	/**
	 * The resulting graph.
	 */
	private AdjacencyListWeightedDirectedGraph<WeightedEdge> graph;
	
	/**
	 * The number of vertices of the graph.
	 */
	private int numberOfVertices;
	
	/**
	 * Whether parsing from the input stream is commented on the console,
	 * or not.
	 */
	private boolean verbose;
	
	
	/**
	 * Constructs a new parser for parsing a graph in DIMACS input format
	 * from any input stream to its correponding memory representation.
	 * 
	 * @param verbose
	 * 		whether parsing from the input stream should be commented on the
	 * 		console, or not
	 */
	public DIMACSGraphParser(boolean verbose) {
		this.verbose = verbose;
	}
	
	
	/**
	 * Parses a graph in DIMACS input format from the specified input
	 * stream to its correponding memory representation.
	 * 
	 * @param in
	 * 		the stream containg the graph in DIMACS input format
	 * @return
	 * 		the memory representation of the read graph
	 * @throws IOException
	 * 		if an I/O error occurs reading from the specified input stream
	 * @throws IllegalArgumentException
	 * 		if the input stream does not contain a graph in DIMACS input format
	 */
	public AdjacencyListWeightedDirectedGraph<WeightedEdge> readDIMACSGraph
		(InputStream in) throws IOException, IllegalArgumentException {
		
		// nothing read yet
		String s = "";
		
		// the byte most recently read from the input stream
		int i;
		
		// read everything from the input stream
		while ((i = in.read()) != -1) {
			// get the read character
			char c = (char)i;
			
			if (c == '\r' || c == '\n') {
				// end of line - check the line type
				if (s.startsWith("c")) {
					if (s.length() > 1) {
						readComment(s.substring(2));
					}
				} else if (s.startsWith("p")) {
					readProblemLine(s.substring(2));
				} else if (s.startsWith("a")) {
					readArcDescriptor(s.substring(2));
				}
				
				// empty buffer
				s = "";
			} else {
				// buffer the read character until the end of the line
				s += c;
			}
		}
		
		return graph;
	}
	
	
	/**
	 * Called whenever a comment line has been read. Comments don't affect the
	 * resulting graph, but may contain useful information on the problem to
	 * solve.<br>
	 * The passed comment is written to the console if {@link #verbose} is set
	 * to <code>true</code>.
	 * 
	 * @see #write(String)
	 * @param s
	 * 		the comment that has been read
	 */
	private void readComment(String s) {
		write(s);
	}
	
	/**
	 * Called whenever a problem line has been read. Problem lines contain
	 * information on the problem to solve, such as the number of vertices and
	 * edges of the graph.

	 * @param s
	 * 		the problem line that has been read
	 * @throws IllegalArgumentException
	 * 		if the passed problem line does not describe a DIMACS graph
	 */
	private void readProblemLine(String s) throws IllegalArgumentException {
		// split the read information
		String[] info = s.split(" ");
		
		if (info[0].equals("sp")) {
			write("Shortest paths problem encountered.");
		} else {
			throw new IllegalArgumentException
				("The passed problem is no shortest paths problem.");
		}
		
		write("Found " + info[1] + " vertices and " + info[2] + " edges.");
		
		// remember the number of vertices of the flow network
		numberOfVertices = Integer.parseInt(info[1]);
	}
	
	/**
	 * Called whenever an arc descriptor line has been read. Arc descriptor
	 * lines specify which vertices are connected in the graph, and they
	 * describe the weights of these connections.
	 * 
	 * @param s
	 * 		the arc descriptor line that has been read
	 */
	private void readArcDescriptor(String s) {
		// lazily create a new flow network
		if (graph == null) {
			graph = new AdjacencyListWeightedDirectedGraph<WeightedEdge>
				(numberOfVertices);
		}
		
		// split the read information
		String[] info = s.split(" ");
		
		// get the source and target vertices of the new edge
		int u = Integer.parseInt(info[0]) - 1;
		int v = Integer.parseInt(info[1]) - 1;
		
		// get the weight of the new edge
		int w = Integer.parseInt(info[2]);
		
		// add the edge to the flow network
		try {
			graph.addEdge(new WeightedEdge(u, v, w));
		} catch (IllegalArgumentException e) {
			if (verbose) {
				System.out.println("WARNING: " + e.getMessage());
			}
		}
		
		write("Found an egde from " + u + " to " + v + " with weight " +
				w + ".");
	}
	
	/**
	 * Writes the passed string to the console, if {@link #verbose} is set to
	 * <code>true</code>.
	 * 
	 * @see #verbose
	 * @param s
	 * 		the string to write to the console
	 */
	private void write(String s) {
		if (verbose) {
			System.out.println(s);
		}
	}
}
