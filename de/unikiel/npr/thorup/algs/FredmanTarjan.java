package de.unikiel.npr.thorup.algs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import de.unikiel.npr.thorup.ds.FibonacciHeap;
import de.unikiel.npr.thorup.ds.FibonacciHeap.FibonacciHeapItem;
import de.unikiel.npr.thorup.ds.graph.AdjacencyListWeightedDirectedGraph;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;
import de.unikiel.npr.thorup.ds.graph.WeightedGraph;

/**
 * An implementation of <i>Fredman and Tarjan</i>'s algorithm for the
 * computation of minimum spanning trees.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 07/31/09
 */
public class FredmanTarjan implements MSTAlgorithm {
	/**
	 * A minimum spanning tree of the input graph.
	 */
	private AdjacencyListWeightedDirectedGraph<WeightedEdge> mst;
	
	/**
	 * A mapping of vertex indices to their containing trees.
	 */
	private Hashtable<Integer, Tree> containingTree;
	
	/**
	 * The number of vertices of the input graph.
	 */
	private int n;
	
	/**
	 * The number of edges of the input graph.
	 */
	private int m;

	
	/**
	 * Computes and returns a minimum spanning tree of the passed weighted,
	 * undirected graph in <i>O(m log(n))</i>.
	 * 
	 * @param g
	 * 		the graph to compute a minimum spanning tree of
	 * @return
	 * 		a minimum spanning tree of g
	 * @throws IllegalArgumentException
	 * 		if the passed graph is <code>null</code>
	 */
	public AdjacencyListWeightedDirectedGraph<WeightedEdge> findSolution
		(WeightedGraph<? extends WeightedEdge> g)
		throws IllegalArgumentException {
		
		// check arguments
		if (g == null) {
			String errorMessage = "The passed graph musn't be null.";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		// get the number of vertices and edges of the passed graph
		n = g.getNumberOfVertices();
		m = g.getNumberOfEdges();
		
		// prepare constructing the minimum spanning tree
		mst = new AdjacencyListWeightedDirectedGraph<WeightedEdge>(n);
		
		/*
		 * initialize the forest to contain each of the n vertices as a
		 * one-vertex tree
		 */
		ArrayList<Tree> initialTrees = new ArrayList<Tree>();
		
		for (int v = 0; v < n; v++) {
			Tree tree = new Tree(v);
			initialTrees.add(tree);
		}
		
		// get a list of all edges in the original graph
		ArrayList<WeightedEdge> initialEdges = new ArrayList<WeightedEdge>();
		
		for (int v = 0; v < n; v++) {
			initialEdges.addAll(g.getIncidentEdges(v));
		}
			
		// compute a minimum spanning tree
		return pass(initialTrees, initialEdges);
	}
	
	
	/**
	 * A single pass of the algorithm. Begins with a forest of previously grown
	 * trees, the <code>oldTrees</code>, and connects there old trees into new
	 * larger trees that become the old trees for the next pass, until only
	 * one tree remains.
	 * 
	 * @param oldTrees
	 * 		the old trees for this pass
	 * @param oldEdges
	 * 		the edges of the original graph that have not been discarded yet
	 * @return
	 * 		a minimum spanning tree of the original graph
	 */
	@SuppressWarnings("unchecked")
	private AdjacencyListWeightedDirectedGraph<WeightedEdge> pass
		(ArrayList<Tree> oldTrees, ArrayList<WeightedEdge> oldEdges) {
		
		// check for termination
		if (oldTrees.size() == 1) {
			return mst;
		} else {
			containingTree = new Hashtable<Integer, Tree>();
			
			for (int i = 0; i < oldTrees.size(); i++) {
				// number the old trees consecutively from one
				Tree oldTree = oldTrees.get(i);
				
				oldTree.number = i;
				
				// and assign to each vertex the tree containing it
				for (Integer v : oldTree.vertices) {
					containingTree.put(v, oldTree);
				}
			}
			
			/*
			 * Cleanup.
			 * Discard every edge connecting two vertices in the same old tree
			 * and all but a minimum-cost edge connecting each pair of old
			 * trees.
			 */
				
			/* 
			 * first radix-sort pass: sort the edges lexicographically on the
			 * numbers of trees containing their sources
			 */
			WeightedEdge[] a = new WeightedEdge[oldEdges.size()];
			a = oldEdges.toArray(a);
			
			// count bits to be sorted
			int k = Integer.SIZE - Integer.numberOfLeadingZeros(n);
			
			radixSortSources(a, 0, a.length - 1, k);
			
			/*
			 * second radix-sort pass: sort edges whose sources are within
			 * the same tree by the numbers of trees containing their targets
			 */
			int l;
			int r = 0;
			int t;
			
			while (r < a.length) {
				l = r;
				t = tree(a[r].getSource()).number;
				
				// look for the first edge with its source in another tree
				while (r < a.length && tree(a[r].getSource()).number == t) {
					r++;
				}
				
				radixSortTargets(a, l, r - 1, k);
			}
			
			// scan the sorted edge list, saving only the appropriate edges
			ArrayList<WeightedEdge> newEdges = new ArrayList<WeightedEdge>();
			
			int currentSource = tree(a[0].getSource()).number;
			int currentTarget = tree(a[0].getTarget()).number;
			int currentWeight = a[0].getWeight();
			int currentMinimumEdgeIndex = 0;
			
			// iterate all edges
			for (int i = 1; i < a.length; i++) {
				// check if the current edge originates from the next tree
				if (tree(a[i].getSource()).number != currentSource) {
					// if the minimum edge connects two trees, save it
					if (tree(a[currentMinimumEdgeIndex].getSource()).number !=
						tree(a[currentMinimumEdgeIndex].getTarget()).number) {
						
						newEdges.add(a[currentMinimumEdgeIndex]);
					}
					
					/*
					 * start looking for the minimum edge from the new source
					 * tree
					 */
					currentSource = tree(a[i].getSource()).number;
					currentTarget = tree(a[i].getTarget()).number;
					currentWeight = a[i].getWeight();
					currentMinimumEdgeIndex = i;
				}
				// check if the current edge targets the next tree
				else if (tree(a[i].getTarget()).number != currentTarget) {
					// if the minimum edge connects two trees, save it
					if (tree(a[currentMinimumEdgeIndex].getSource()).number !=
						tree(a[currentMinimumEdgeIndex].getTarget()).number) {
						
						newEdges.add(a[currentMinimumEdgeIndex]);
					}
					
					// start looking for the minimum edge to the new target tree
					currentTarget = tree(a[i].getTarget()).number;
					currentWeight = a[i].getWeight();
					currentMinimumEdgeIndex = i;
				}
				/*
				 * check if the weight of the current edge is less than the one
				 * of the edges before
				 */
				else {
					if (a[i].getWeight() < currentWeight) {
						currentWeight = a[i].getWeight();
						currentMinimumEdgeIndex = i;
					}
				}
			}
			
			// process the last edge individually: does it connect other trees?
			if ((tree(a[a.length - 1].getSource()).number !=
				 tree(a[a.length - 2].getSource()).number) ||
				(tree(a[a.length - 1].getTarget()).number !=
				 tree(a[a.length - 2].getTarget()).number)) {
				
				// if it connects two trees, save it
				if (tree(a[a.length - 1].getSource()).number !=
					tree(a[a.length - 1].getTarget()).number) {
					
					newEdges.add(a[a.length - 1]);
				}
			}
			
			/* 
			 * construct a list for each old tree T of the edges with one
			 * endpoint in T (each edge will appear in two such lists)
			 */
			for (Tree tree : oldTrees) {
				tree.edges.clear();
			}
			
			for (WeightedEdge e : newEdges) {
				/* 
				 * we only need to add every edge once, because we are modeling
				 * undirected graphs by adding every edge {v,w} twice:
				 * (v,w) and (w,v)
				 */
				tree(e.getSource()).edges.add(e);
			}
			

			// get the number of trees before the pass
			t = oldTrees.size();
			k = (int)Math.pow(2, 2 * m / t);
			
			// give every old tree a key of INFINITY und unmark it
			LinkedList<Tree> unmarkedOldTrees = new LinkedList<Tree>();
			
			for (Tree tree : oldTrees) {
				tree.key = Double.POSITIVE_INFINITY;
				tree.marked = false;
				
				unmarkedOldTrees.add(tree);
			}
			
			// create an empty heap
			FibonacciHeap<Tree> fh = new FibonacciHeap<Tree>();
			FibonacciHeapItem<Tree>[] fhItems = new FibonacciHeapItem[t];
			
			// prepare a list holding the trees for the next pass
			ArrayList<Tree> newTrees = new ArrayList<Tree>();
			
			// repeat tree-growing step until there are no unmarked old trees
			while (!unmarkedOldTrees.isEmpty()) {
				// Grow a New Tree.
				
				// select any unmarked old tree T0
				Tree T0 = unmarkedOldTrees.removeFirst();
				
				while (T0.marked && !unmarkedOldTrees.isEmpty()) {
					T0 = unmarkedOldTrees.removeFirst();
				}
				
				if (!T0.marked) {
					newTrees.add(T0);
					
					// insert it as an item into the heap with a key of zero
					fh.insert(T0, 0);
					T0.key = 0;
	
					do {
						// Connect to Starting Tree.
						
						// delete an old T of minimum key from the heap
						Tree T = fh.deleteMin().getItem();
						
						// set key(T) = NEGATIVE_INFINITY
						T.key = Double.NEGATIVE_INFINITY;
						
						// if T != T0, add e(T) to the forest
						if (T != T0) {
							mst.addEdge(new WeightedEdge
									(T.e.getSource(),
									 T.e.getTarget(),
									 T.e.getWeight()));
							mst.addEdge(new WeightedEdge
									(T.e.getTarget(),
									 T.e.getSource(),
									 T.e.getWeight()));
							
							// all vertices of T belong to T0 now
							T0.vertices.addAll(T.vertices);
						}
						
						/*
						 * if T is marked, stop growing the current tree and
						 * finish the growth step as described below
						 */
						if (T.marked) {
							break;
						}
						
						// otherwise mark T
						T.marked = true;
						
						/*
						 *  for each edge (v,w) with v in T and 
						 *  c(v,w) < key(tree(w)): set e(tree(w)) = (v,w)
						 */
						for (WeightedEdge edge : T.edges) {
							Tree treeW = tree(edge.getTarget());
							int cVW = edge.getWeight();
							
							if (cVW < treeW.key) {
								treeW.e = edge;
								
								/* 
								 * if key(tree(w)) = INFINITY, insert tree(w) in
								 * the heap with a redefined key of c(v,w)
								 */
								if (Double.isInfinite(treeW.key)) {
									/*
									 * check using Double.isInfinite is enough,
									 * because NEGATIVE_INFINITY would never be
									 * greater than cVW
									 */
									treeW.key = (double)cVW;
									fhItems[treeW.number] =
										fh.insert(treeW, cVW);
								} else {
									// decrease the key of tree(w) to c(v,w)
									treeW.key = (double)cVW;
									fh.decreaseKeyTo
										(fhItems[treeW.number], cVW);
								}
							}
						}
					} while (!fh.isEmpty() && fh.getSize() <= k);
					
					// finish growing step: empty the heap...
					fh.clear();
					
					/*
					 * ...and set key(T) = INFINITY for every old tree T with
					 * finite key
					 */
					for (Tree tree : oldTrees) {
						if (!Double.isInfinite(tree.key)) {
							tree.key = Double.POSITIVE_INFINITY;
						}
					}
				}
			}
			
			// complete the pass
			return pass(newTrees, newEdges);
		}
	}
	
	/**
	 * Returns the old tree containing the passed vertex.
	 * 
	 * @param v
	 * 		the vertex v to access the old tree of
	 * @return
	 * 		the old tree containing the passed vertex
	 */
	private Tree tree(int v) {
		return containingTree.get(v);
	}
	
	/**
	 * Sorts the passed array of edges according to the number of the trees
	 * containing their source vertices.
	 * 
	 * @param a
	 * 		the array of edges to be sorted
	 * @param l
	 * 		the first index of the interval to be sorted
	 * @param r
	 * 		the last index of the interval to be sorted
	 * @param i
	 * 		the bit to look at while sorting
	 */
	private void radixSortSources(WeightedEdge[] a, int l, int r, int i) {
		// check for termination
		if ((i >= 0) && (l < r)) {
			int L = l - 1;
			int R = r + 1;
			
			// look for the first edge to be swapped from the left side
			do {
				L++;
			} while (L <= r && getBit(tree(a[L].getSource()).number, i) == 0);
			
			// look for the first edge to be swapped from the right side
			do {
				R--;
			} while (R >= l && getBit(tree(a[R].getSource()).number, i) == 1);
			
			while (L < R) {
				// swap both edges
				swap(a, L, R);
				
				// look for the next edge to be swapped from the left side
				do {
					L++;
				} while (getBit(tree(a[L].getSource()).number, i) == 0);
				
				// look for the next edge to be swapped from the right side
				do {
					R--;
				} while (getBit(tree(a[R].getSource()).number, i) == 1);
			}
			
			/*
			 * recursively sort the left and right parts of the initial
			 * interval, looking at the next bit
			 */
			radixSortSources(a, l, R, i - 1);
			radixSortSources(a, L, r, i - 1);
		}
	}

	/**
	 * Sorts the passed array of edges according to the number of the trees
	 * containing their target vertices.
	 * 
	 * @param a
	 * 		the array of edges to be sorted
	 * @param l
	 * 		the first index of the interval to be sorted
	 * @param r
	 * 		the last index of the interval to be sorted
	 * @param i
	 * 		the bit to look at while sorting
	 */
	private void radixSortTargets(WeightedEdge[] a, int l, int r, int i) {
		// check for termination
		if ((i >= 0) && (l < r)) {
			int L = l - 1;
			int R = r + 1;
			
			// look for the first edge to be swapped from the left side
			do {
				L++;
			} while (L <= r && getBit(tree(a[L].getTarget()).number, i) == 0);
			
			// look for the first edge to be swapped from the right side
			do {
				R--;
			} while (R >= l && getBit(tree(a[R].getTarget()).number, i) == 1);
			
			while (L < R) {
				// swap both edges
				swap(a, L, R);
				
				// look for the next edge to be swapped from the left side
				do {
					L++;
				} while (getBit(tree(a[L].getTarget()).number, i) == 0);
				
				// look for the next edge to be swapped from the right side
				do {
					R--;
				} while (getBit(tree(a[R].getTarget()).number, i) == 1);
			}
			
			/*
			 * recursively sort the left and right parts of the initial
			 * interval, looking at the next bit
			 */
			radixSortTargets(a, l, R, i - 1);
			radixSortTargets(a, L, r, i - 1);
		}
	}
	
	/**
	 * Returns the <code>k</code>th bit of <code>i</code>, i.e. a zero or a one.
	 *  
	 * @param i
	 * 		the number the get the <code>k</code>th bit of
	 * @param k
	 * 		the bit to get
	 * @return
	 * 		the <code>k</code>th bit of <code>i</code>
	 */
	private int getBit(int i, int k) {
		return ((i >> k) & 1);
	}
	
	/**
	 * Swaps the two entries with the specified indices in the passed array.
	 * 
	 * @param <T>
	 * 		the type of the elements to be swapped
	 * @param a
	 * 		the array to swap the elements of
	 * @param i
	 * 		the index of the first element to swap
	 * @param j
	 * 		the index of the second element to swap
	 */
	private <T> void swap(T[] a, int i, int j) {
		T temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
	
	
	/**
	 * A tree of the forest that finally will become the minimum spanning tree
	 * of the input graph.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 07/31/09
	 */
	private static class Tree {
		/**
		 * The unique number of this tree.
		 */
		private int number;
		
		/**
		 * The vertices of this tree.
		 */
		private ArrayList<Integer> vertices;
		
		/**
		 * The edges with one endpoint in this tree.
		 */
		private ArrayList<WeightedEdge> edges;
		
		/**
		 * The key of this tree in the heap.
		 */
		private double key;
		
		/**
		 * Whether this tree is marked, or not.
		 */
		private boolean marked;
		
		/**
		 * The edge that will added to the forest as soon as this tree is
		 * removed from the heap.
		 */
		private WeightedEdge e;
		
		
		/**
		 * Constructs a new tree containing the vertex with the passed index
		 * only. The tree initially has no number, edges or key and is not
		 * marked.
		 * 
		 * @param v
		 * 		the initial vertex of the new tree
		 */
		public Tree(int v) {
			vertices = new ArrayList<Integer>();
			edges = new ArrayList<WeightedEdge>();
			
			vertices.add(v);
		}
		
		
		/**
		 * Returns the number of this tree.
		 * 
		 * @return
		 * 		the number of this tree.
		 */
		public String toString() {
			return String.valueOf(number);
		}
	}
}
