package de.unikiel.npr.thorup.ds.graph;

/**
 * An edge in any graph, pointing from one vertex to another.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class Edge {
	/**
	 * The index of the source vertex of this edge.
	 */
	int source;
	
	/**
	 * The index of the target vertex of this edge.
	 */
	int target;
	
	
	/**
	 * Constructs a new edge, pointing from the vertex with index
	 * <code>source</code> to the vertex with index <code>target</code>.
	 * 
	 * @param source
	 * 		the index of the source vertex of the new edge
	 * @param target
	 * 		the index of the target vertex of the new edge
	 */
	public Edge(int source, int target) {
		this.source = source;
		this.target = target;
	}

	/**
	 * Constructs a new edge with the same <code>source</code> and
	 * <code>target</code> as the passed one.
	 * 
	 * @param other
	 * 		the edge to clone
	 */
	public Edge(Edge other) {
		source = other.source;
		target = other.target;
	}
	
	
	/**
	 * Gets the index of the source vertex of this edge.
	 * 
	 * @return
	 * 		the index of the source vertex of this edge
	 */
	public int getSource() {
		return source;
	}

	/**
	 * Gets the index of the target vertex of this edge.
	 * 
	 * @return
	 * 		the index of the target vertex of this edge
	 */
	public int getTarget() {
		return target;
	}
	
	/**
	 * Returns <code>true</code>, if the indices of the source and target
	 * vertices of this edge and the passed one are equal, and
	 * <code>false</code> otherwise.
	 * 
	 * @param other
	 * 		the edge to check for equality
	 * @return
	 * 		<code>true</code>, if the indices of the source and target
	 * 		vertices of this edge and the passed one are equal, and<br>
	 * 		<code>false</code> otherwise
	 */
	public boolean equals(Edge other) {
		return (source == other.source && target == other.target);
	}
}
