package de.unikiel.npr.thorup.ds.graph;

/**
 * An edge in any graph, pointing from one vertex to another, with an integer
 * edge weight.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class WeightedEdge extends Edge {
	/**
	 * The maximum weight edge instances of this class can have:
	 * 2<sup>31</sup>-1.
	 */
	public static final int MAXIMUM_EDGE_WEIGHT = Integer.MAX_VALUE;
	
	
	/**
	 * The integer weight of this edge.
	 */
	private int weight;
	
	
	/**
	 * Constructs a new edge, pointing from the vertex with index
	 * <code>source</code> to the vertex with index <code>target</code>, with
	 * weight <code>1</code>.
	 * 
	 * @param source
	 * 		the index of the source vertex of the new edge
	 * @param target
	 * 		the index of the target vertex of the new edge
	 */
	public WeightedEdge(int source, int target) {
		this(source, target, 1);
	}
	
	/**
	 * Constructs a new edge, pointing from the vertex with index
	 * <code>source</code> to the vertex with index <code>target</code>, with
	 * the passed weight.
	 * 
	 * @param source
	 * 		the index of the source vertex of the new edge
	 * @param target
	 * 		the index of the target vertex of the new edge
	 * @param weight
	 * 		the weight of the new edge
	 */
	public WeightedEdge(int source, int target, int weight) {
		super(source, target);
		
		this.weight = weight;
	}

	/**
	 * Constructs a new edge with the same <code>source</code>,
	 * <code>target</code> and weight as the passed one.
	 * 
	 * @param other
	 * 		the edge to clone
	 */
	public WeightedEdge(WeightedEdge other) {
		super(other);
		
		weight = other.weight;
	}
	
	
	/**
	 * Gets the integer weight of this edge.
	 *  
	 * @return
	 * 		the integer weight of this edge
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Returns a <code>String</code> representation of this edge, including
	 * the indices of its source and target vertices, as well as its weight,
	 * in the following form:<br>
	 * <br>
	 * <i>&lt;source vertex index&gt;</i><code> -> </code>
	 * <i>&lt;target vertex index&gt;</i><code>: </code>
	 * <i>&lt;edge weight&gt;</i>
	 * 
	 * @return
	 * 		a <code>String</code> representation of this edge
	 */
	public String toString() {
		return getSource() + " -> " + getTarget() + ": " + weight;
	}
	
	/**
	 * Returns <code>true</code>, if the indices of the source and target
	 * vertices and the weights of this edge and the passed one are equal, and
	 * <code>false</code> otherwise.
	 * 
	 * @param other
	 * 		the edge to check for equality
	 * @return
	 * 		<code>true</code>, if the indices of the source and target
	 * 		vertices and the weights of this edge and the passed one are equal,
	 * 		and<br>
	 * 		<code>false</code> otherwise
	 * @see Edge#equals(Edge)
	 */
	public boolean equals(WeightedEdge other) {
		return (super.equals(other) && weight == other.weight);
	}
}
