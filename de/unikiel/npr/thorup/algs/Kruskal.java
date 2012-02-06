package de.unikiel.npr.thorup.algs;

import java.util.LinkedList;

import de.unikiel.npr.thorup.ds.UnionFindNode;
import de.unikiel.npr.thorup.ds.UnionFindStructure;
import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;
import de.unikiel.npr.thorup.ds.graph.WeightedGraph;

/**
 * A modified version of <i>Kruskal</i>'s algorithm for the
 * computation of <i>msb</i>-minimum spanning trees.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class Kruskal implements MSTAlgorithm {
	/**
	 * The union-find structure used to compute the <i>msb</i>-minimum spanning
	 * tree.
	 */
	@SuppressWarnings("unchecked")
	private UnionFindStructure uf;

	/**
	 * Constructs a new instance of <i>Kruskal</i>'s algorithm for the
	 * computation of <i>msb</i>-minimum spanning trees which will use the
	 * passed union-find structure.
	 * 
	 * @param uf
	 * 		the union-find structure to use to compute the <i>msb</i>-minimum
	 * 		spanning tree
	 */
	@SuppressWarnings("unchecked")
	public Kruskal(UnionFindStructure uf) {
		this.uf = uf;
	}
	
	/**
	 * Computes and returns an <i>msb</i>-minimum spanning tree of the passed
	 * weighted, undirected graph.
	 * 
	 * @param g
	 * 		the graph to compute an <i>msb</i>-minimum spanning tree of
	 * @return
	 * 		an <i>msb</i>-minimum spanning tree of g
	 * @throws IllegalArgumentException
	 * 		if the passed graph is <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public AdjacencyListWeightedDirectedGraph<WeightedEdge> findSolution
		(WeightedGraph<? extends WeightedEdge> g)
		throws IllegalArgumentException {
		
		int n = g.getNumberOfVertices();
		
		UnionFindNode<Integer>[] ufNodes = new UnionFindNode[n];
		
		for (int i = 0; i < n; i++) {
			ufNodes[i] = uf.makeSet(i);
		}
		
		// presort edges accoding to their msb-weights
		LinkedList<WeightedEdge> q = bucketSortEdges(g);
		
		// prepare the resulting msb-minimum spanning tree
		AdjacencyListWeightedDirectedGraph<WeightedEdge> mst =
			new AdjacencyListWeightedDirectedGraph<WeightedEdge>(n);
		
		while (mst.getNumberOfEdges() < (n - 1) * 2) {
			WeightedEdge e = q.removeFirst();
			
			int cu = (Integer)uf.find(ufNodes[e.getSource()]).getItem();
			int cv = (Integer)uf.find(ufNodes[e.getTarget()]).getItem();
			
			if (cu != cv) {
				mst.addEdge(new WeightedEdge
						(e.getSource(),
						 e.getTarget(),
						 e.getWeight()));
				
				mst.addEdge(new WeightedEdge
						(e.getTarget(),
						 e.getSource(),
						 e.getWeight()));
				
				uf.union(ufNodes[cu], ufNodes[cv]);
			}
			
		}
		
		return mst;
	}

	/**
	 * Sorts the edges of the passed graph according to the most significant
	 * bits of their weights in <i>O(m)</i>.
	 * 
	 * @param g
	 * 		the graph to sort the edges of
	 * @return
	 * 		a list containing the ordered edges of the passed graph
	 */
	@SuppressWarnings("unchecked")
	private LinkedList<WeightedEdge> bucketSortEdges
		(WeightedGraph<? extends WeightedEdge> g) {
		
		// initialize buckets
		LinkedList<WeightedEdge>[] buckets =
			new LinkedList[Thorup.msb(WeightedEdge.MAXIMUM_EDGE_WEIGHT)];
		
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new LinkedList<WeightedEdge>();
		}
		
		// bucket edges
		for (int v = 0; v < g.getNumberOfVertices(); v++) {
			for (WeightedEdge e : g.getIncidentEdges(v)) {
				if (e.getSource() < e.getTarget()) {
					buckets[Thorup.msb(e.getWeight())].add(e);
				}
			}
		}
		
		// create sorted edge array
		LinkedList<WeightedEdge> q = new LinkedList<WeightedEdge>();
		
		for (int i = 0; i < buckets.length; i++) {
			for (WeightedEdge e : buckets[i]) {
				q.addLast(e);
			}
		}
		
		return q;
	}
}
