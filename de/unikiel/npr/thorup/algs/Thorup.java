package de.unikiel.npr.thorup.algs;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import de.unikiel.npr.thorup.ds.SplitFindminStructure;
import de.unikiel.npr.thorup.ds.SplitFindminStructureElement;
import de.unikiel.npr.thorup.ds.UnionFindNode;
import de.unikiel.npr.thorup.ds.UnionFindStructure;
import de.unikiel.npr.thorup.ds.graph.WeightedEdge;
import de.unikiel.npr.thorup.ds.graph.WeightedGraph;

/**
 * An implementation of <i>Thorup</i>'s single-source shortest paths
 * algorithm.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class Thorup {
	/**
	 * The graph <i>G</i> this instance of <i>Thorup</i>'s algorithm computes
	 * the shortest paths of.
	 */
	private WeightedGraph<? extends WeightedEdge> g;
	
	/**
	 * The number of vertices of the graph <i>G</i> this instance of
	 * <i>Thorup</i>'s algorithm computes the shortest paths of.
	 */
	private int n;
	
	/**
	 * The set <i>S</i> of visited vertices of this instance of <i>Thorup</i>'s
	 * algorithm. A vertex <i>v</i> is element of the set <i>S</i> if
	 * <code>s[v]</code> is <code>true</code>.
	 */
	private boolean[] s;
	
	/**
	 * The msb-minimum spanning tree <i>M</i> used to compute the component tree
	 * <i>T</i> of <i>G</i>.
	 */
	private WeightedGraph<? extends WeightedEdge> m;
	
	/**
	 * The component tree <i>T</i> this instance of <i>Thorup</i>'s algorithm
	 * runs on.
	 */
	private ComponentTree t;
	
	/**
	 * The unvisited data structure <i>U</i> this instance of <i>Thorup</i>'s 
	 * algorithm uses.
	 */
	private UnvisitedDataStructure u;
	
	/**
	 * The index of the source vertex to compute all shortest paths to.
	 */
	private int source;

	
	/**
	 * Prepares this instance of <i>Thorup</i>'s algorithm for computing the
	 * shortest paths in the passed graph <i>G</i> by computing an <i>msb</i>-
	 * minimum spanning tree <i>M</i> of <i>G</i>.<br>
	 * <br>
	 * Use {@link #constructOtherDataStructures(UnionFindStructure,
	 * SplitFindminStructure)} for computing the other required data structures
	 * before calling {@link #findShortestPaths(int)} for computing the shortest
	 * paths to the specified source vertex.
	 * 
	 * @see #constructOtherDataStructures(UnionFindStructure,
	 * 		SplitFindminStructure)
	 * @param graph
	 * 		the graph this instance of <i>Thorup</i>'s algorithm will computes
	 * 		the	shortest paths of
	 * @param a
	 * 		the algorithm to use for computing the <i>msb</i>-minimum spanning
	 * 		tree
	 */
	public void constructMinimumSpanningTree
		(WeightedGraph<? extends WeightedEdge> graph, MSTAlgorithm a) {
		
		g = graph;
		n = g.getNumberOfVertices();
		m = a.findSolution(g);
	}
	
	/**
	 * Prepares this instance of <i>Thorup</i>'s algorithm for computing the
	 * shortest paths in the passed graph <i>G</i> by:
	 * 
	 * <ol>
	 * 		<li>
	 * 			Constructing the component tree <i>T</i> using <i>M</i>,
	 * 		</li>
	 * 		<li>
	 * 			Initializing the bucket structure <i>B</i> of <i>T</i>, and
	 * 		</li>
	 * 		<li>
	 * 			Preparing the unvisited data structure <i>U</i> using <i>T</i>
	 * 		</li>
	 * </ol>
	 * 
	 * Use {@link #findShortestPaths(int)} for computing the shortest paths to
	 * the specified source vertex after all data structures have been properly
	 * initialized.
	 * 
	 * @see #findShortestPaths(int)
	 * @param uf
	 * 		the union-find structure to use for computing the component tree
	 * 		<i>T</i>
	 * @param sf
	 * 		the split-find structure to use for the unvisited data structure
	 * 		<i>U</i>
	 */
	@SuppressWarnings("unchecked")
	public void constructOtherDataStructures(UnionFindStructure uf,
			SplitFindminStructure<Integer> sf) {
		
		s = new boolean[n];
		t = constructT(uf);
		u = new UnvisitedDataStructure(n, t, sf);
	}
	
	/**
	 * Prepares this instance of <i>Thorup</i>'s algorithm for another query on
	 * the same graph by:
	 * 
	 * <ol>
	 * 		<li>
	 * 			Resetting the set <i>S</i> of visited vertices,
	 * 		</li>
	 * 		<li>
	 * 			Marking every component as unvisited, and
	 * 		</li>
	 * 		<li>
	 * 			Re-initializing the unvisited data structure <i>U</i>
	 * 		</li>
	 * </ol>
	 * 
	 * @param sf
	 * 		the new split-find structure to use for the unvisited data structure
	 * 		<i>U</i>
	 */
	@SuppressWarnings("unchecked")
	public void cleanUpBetweenQueries(SplitFindminStructure<Integer> sf) {
		// reset S
		s = new boolean[n];
		
		// mark every component as unvisited
		deepCleanUpNodes(t.root);
		
		// re-initialize U
		u.containers = new SplitFindminStructureElement[n];

		for (int i = 0; i < n; i++) {
			u.containers[i] = sf.add(i, Double.POSITIVE_INFINITY);
		}

		sf.initialize();
	}
	
	/**
	 * Iterates the passed weighted graph, computing the paths from the passed
	 * source vertex to all others.
	 * 
	 * @param source
	 * 		the index of the source vertex
	 * @return
	 * 		the distances of all vertices of the checked graph from the source
	 * 		vertex
	 * @throws IllegalArgumentException
	 * 		if <code>u</code> is not a vertex of <code>g</code>
	 */
	public int[] findShortestPaths(int source) {
		// check the passed source vertex
		if (source < 0 || source >= n) {
			throw new IllegalArgumentException(source +
					" is no valid source vertex.");
		}
		
		// B.1.
		this.source = source;
		s[source] = true;
		
		for (WeightedEdge edge : g.getIncidentEdges(source)) {
			u.decreaseD(edge.getTarget(), edge.getWeight());
		}
		
		// B.3.
		visit(t.root);
		
		// B.4.
		int[] d = new int[n];
		
		for (int i = 0; i < n; i++) {
			d[i] = u.getD(i);
		}
		
		// B.2.
		d[source] = 0;
		
		return d;
	}
	
	
	/**
	 * Returns the index of the most significant bit of the passed integer
	 * value.
	 * 
	 * @param i
	 * 		the integer value to compute the index of the most significant bit
	 * 		of
	 * @return
	 * 		the index of the most significant bit of <code>i</code>
	 */
	public static int msb(int i) {
		return 31 - Integer.numberOfLeadingZeros(i);
	}
	
	
	/**
	 * Constructing the component tree <i>T</i> using <i>M</i> and the passed
	 * union-find structure (Algorithm G).
	 * 
	 * @param uf
	 * 		the union-find structure to use for computing <i>T</i>
	 * @return
	 * 		the component tree of <i>G</i>
	 */
	@SuppressWarnings("unchecked")
	private ComponentTree constructT(UnionFindStructure uf) {
		// prepare union and find
		UnionFindNode<Integer>[] ufNodes = new UnionFindNode[n];
		
		for (int v = 0; v < n; v++) {
			ufNodes[v] = uf.makeSet(v);
		}
		
		//  sort edges of m
		WeightedEdge[] eis = bucketSortMSTEdges();
		
		int[] c = new int[n];
		int[] s = new int[n];
		ComponentTree t = new ComponentTree(n);
		
		int[] newC = new int[n];
		boolean[] representsInternalNode = new boolean[n];
		
		// G.1.
		for (int v = 0; v < n; v++) {
			c[v] = v;
			s[v] = 0;
		}
		
		// G.2.
		int comp = 0;
		LinkedHashSet<Integer> x = new LinkedHashSet<Integer>();
		
		// G.3.
		for (int i = 0; i < eis.length - 1; i++) {
			// G.3.1.
			WeightedEdge ei = eis[i];
			
			// G.3.2.
			x.add((Integer)uf.find(ufNodes[ei.getSource()]).getItem());
			x.add((Integer)uf.find(ufNodes[ei.getTarget()]).getItem());
			
			// G.3.3.
			int newS =
				s[(Integer)uf.find(ufNodes[ei.getSource()]).getItem()] +
				s[(Integer)uf.find(ufNodes[ei.getTarget()]).getItem()] +
				ei.getWeight();
			
			// G.3.4.
			uf.union(ufNodes[ei.getSource()], ufNodes[ei.getTarget()]);
			
			// G.3.5.
			s[(Integer)uf.find(ufNodes[ei.getSource()]).getItem()] = newS;
			
			// G.3.6.
			if (msb(ei.getWeight()) < msb(eis[i + 1].getWeight())) {
				/* 
				 * G.3.6.1.:
				 * newX are the canonical elements of the new components of T
				 */
				LinkedHashSet<Integer> newX = new LinkedHashSet<Integer>();
				
				for (Integer v : x) {
					newX.add((Integer)uf.find(ufNodes[v]).getItem());
				}
				
				// G.3.6.2.
				for (Integer v : newX) {
					comp++;
					newC[v] = comp;
				}
				
				// G.3.6.3.
				for (Integer v : x) {
					if (!representsInternalNode[v]) {
						t.setParentOfLeaf(c[v],
								newC[(Integer)uf.find(ufNodes[v]).getItem()]);
					} else {
						t.setParentOfInternalNode(c[v],
								newC[(Integer)uf.find(ufNodes[v]).getItem()]);
					}
				}
				
				// G.3.6.4
				for (Integer v : newX) {
					c[v] = newC[v];
					representsInternalNode[v] = true;
					t.setDelta(c[v], (int)Math.ceil(s[v] /
							Math.pow(2, msb(ei.getWeight()))));
					t.setI(c[v], msb(ei.getWeight()) + 1);
				}
				
				// G.3.6.5
				x.clear();
			}
		}
		
		{
			int i = eis.length - 1;
			
			// G.3.1.
			WeightedEdge ei = eis[i];
			
			// G.3.2.
			x.add((Integer)uf.find(ufNodes[ei.getSource()]).getItem());
			x.add((Integer)uf.find(ufNodes[ei.getTarget()]).getItem());
			
			// G.3.4.
			uf.union(ufNodes[ei.getSource()], ufNodes[ei.getTarget()]);
			
			// G.3.6.
			if (msb(ei.getWeight()) < msb(Integer.MAX_VALUE)) {
				/* 
				 * G.3.6.1.:
				 * newX are the canonical elements of the new components of T
				 */
				LinkedHashSet<Integer> newX = new LinkedHashSet<Integer>();
				
				for (Integer v : x) {
					newX.add((Integer)uf.find(ufNodes[v]).getItem());
				}
				
				// G.3.6.2.
				for (Integer v : newX) {
					comp++;
					newC[v] = comp;
				}
				
				// G.3.6.3.
				for (Integer v : x) {
					if (!representsInternalNode[v]) {
						t.setParentOfLeaf(c[v],
								newC[(Integer)uf.find(ufNodes[v]).getItem()]);
					} else {
						t.setParentOfInternalNode(c[v],
								newC[(Integer)uf.find(ufNodes[v]).getItem()]);
					}
				}
				
				// G.3.6.4
				for (Integer v : newX) {
					c[v] = newC[v];
					representsInternalNode[v] = true;
					t.setDelta(c[v], (int)Math.ceil(s[v] /
							Math.pow(2, msb(ei.getWeight()))));
					t.setI(c[v], msb(ei.getWeight()) + 1);
				}
				
				// G.3.6.5
				x.clear();
			}
		}
		
		return t;
	}
	
	/**
	 * Sorts the edges of <i>G</i> according to the most significant
	 * bits of their weights in <i>O(m)</i>.
	 * 
	 * @return
	 * 		an array containing the ordered edges of <i>G</i>
	 */
	@SuppressWarnings("unchecked")
	private WeightedEdge[] bucketSortMSTEdges() {
		// initialize buckets
		LinkedList<WeightedEdge>[] buckets =
			new LinkedList[msb(WeightedEdge.MAXIMUM_EDGE_WEIGHT)];
		
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new LinkedList<WeightedEdge>();
		}
		
		// bucket edges
		for (int v = 0; v < m.getNumberOfVertices(); v++) {
			for (WeightedEdge e : m.getIncidentEdges(v)) {
				if (e.getSource() < e.getTarget()) {
					buckets[msb(e.getWeight())].add(e);
				}
			}
		}
		
		// create sorted edge array
		WeightedEdge[] a = new WeightedEdge[m.getNumberOfEdges() / 2];
		int j = 0;
		
		for (int i = 0; i < buckets.length; i++) {
			for (WeightedEdge e : buckets[i]) {
				a[j] = e;
				j++;
			}
		}
		
		return a;
	}
	
	
	/**
	 * Assumes that the passed component has just been visited for the first
	 * time. Buckets all children of the passed component and initializes the
	 * bucket indizes (Algorithm D).
	 * 
	 * @param v
	 * 		the component to expand
	 */
	private void expand(ComponentTree.TreeNode v) {
		// initiate ix0([v]_i) and ix8([v]_i)
		v.ix0 = u.getMinDviMinus(v) >> (v.i - 1);
		v.ix8 = v.ix0 + v.delta;
		
		// bucket the children of [v_i]
		v.initializeBuckets();
		u.deleteRoot(v);
		for (ComponentTree.TreeNode wh : v.children) {
			int min = u.getMinDviMinus(wh);
			
			if (min != -1) {
				if (!(wh.children.isEmpty() && wh.index == source)) {
					v.bucket(wh, min >> (v.i - 1));
				} else {
					ComponentTree.TreeNode current = v;
					
					while (current != null) {
						current.numberOfUnvisitedVertices--;
						current = current.parent;
					}
				}
			}			
		}
		
		v.visited = true;
	}
	
	/**
	 * Assumes that all ancestors of the passed component are expanded. Visits
	 * the passed minimal singleton component, and restores the bucketing of
	 * unvisited children of visited components (Algorithm E).
	 *  
	 * @param v
	 * 		the vertex to visit
	 */
	private void visit(int v) {
		if (v != source) {
			// mark v as visited
			s[v] = true;
			
			// iterate all neighbors
			for (WeightedEdge edge : g.getIncidentEdges(v)) {
				/*
				 * check if we have to decrease the D-value of the current
				 * neighbor
				 */
				int newDValue = u.getD(v) + edge.getWeight();
				
				if (newDValue > 0 && newDValue < u.getD(edge.getTarget())) {
					ComponentTree.TreeNode wh =
						u.getUnvisitedRootOf(t, edge.getTarget());
					ComponentTree.TreeNode wi = wh.parent;
					
					int oldValue = u.getMinDviMinus(wh) >> (wi.i - 1);
					u.decreaseD(edge.getTarget(), newDValue);
					int newValue = u.getMinDviMinus(wh) >> (wi.i - 1);
					
					if (oldValue == -1 || newValue < oldValue) {
						wh.moveToBucket(wi, u.getMinDviMinus(wh) >> (wi.i - 1));
					}
				}
			}
		}
	}
	
	/**
	 * Assumes that the passed component is minimal. Visits all unvisited
	 * vertices <i>w</i> of the passed component with d(w) >> j - 1 equal to the
	 * call time value of the minimum over the super distances of all unvisited
	 * vertices of the passed component, shifted right by j - 1 bits, where
	 * j is the level of the parent of the passed component.
	 * 
	 * @param vi
	 * 		the minimal component to visit
	 */
	private void visit(ComponentTree.TreeNode vi) {
		ComponentTree.TreeNode vj = vi.parent;
		int j;
		
		if (vj == null) {
			j = 32;
		} else {
			j = vj.i;
		}
		
		// F.1.
		if (vi.i == 0) {
			// F.1.1.
			visit(vi.index);
			
			ComponentTree.TreeNode current = vi.parent;
			while (current != null) {
				current.numberOfUnvisitedVertices--;
				current = current.parent;
			}
			
			// F.1.2.
			vi.removeFromParentBucket();
			
			// F.1.3.
			return;
		}
		
		// F.2.
		if (!vi.visited) {
			expand(vi);
			vi.ix = vi.ix0;
		}
		
		// F.3.
		int oldShiftedIx = vi.ix >> (j - vi.i);
			
		while (vi.numberOfUnvisitedVertices > 0 &&
			   vi.ix >> (j - vi.i) == oldShiftedIx) {
			
			// F.3.1.
			while (!vi.getBucket(vi.ix).isEmpty()) {
				// F.3.1.1.
				ComponentTree.TreeNode wh = vi.getBucket(vi.ix).getFirst();
				
				// F.3.1.2.
				visit(wh);
			}
			
			// F.3.2.
			vi.ix++;
		}
		
		// F.4.
		if (vi.numberOfUnvisitedVertices > 0) {
			vi.moveToBucket(vj, vi.ix >> (j - vi.i));
		} else {
			// F.5.
			if (vi.parent != null) {
				vi.removeFromParentBucket();
			}
		}
	}
	
	/**
	 * Marks the passed component and all of its children as unvisited.
	 * 
	 * @param node
	 * 		the component to mark as unvisited
	 */
	private void deepCleanUpNodes(ComponentTree.TreeNode node) {
		node.numberOfUnvisitedVertices = node.numberOfUnvisitedVerticesInitial;
		node.visited = false;
		
		for (ComponentTree.TreeNode child : node.children) {
			deepCleanUpNodes(child);
		}
	}

	/**
	 * A component tree of a weighted, undirected graph with positive integer
	 * edge weights.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 09/17/09
	 */
	private static class ComponentTree {
		/**
		 * The leafs of this component tree.
		 */
		private TreeNode[] leafs;
		
		/**
		 * The internal nodes of this component tree.
		 */
		private TreeNode[] internalNodes;
		
		/**
		 * The root of this component tree.
		 */
		private TreeNode root;
		
		/**
		 * Constructs a new component tree for a graph with <code>n</code>
		 * vertices. Use {@link #setParentOfLeaf(int, int)} and
		 * {@link #setParentOfInternalNode(int, int)} in order to add
		 * parent-child relationships to the constructed tree.
		 * 
		 * @see #setParentOfLeaf(int, int)
		 * @see #setParentOfInternalNode(int, int)
		 * @param n
		 * 		the number of vertices to construct the component tree of
		 */
		public ComponentTree(int n) {
			leafs = new TreeNode[n];
			
			for (int i = 0; i < n; i++) {
				leafs[i] = new TreeNode(i);
			}
			
			internalNodes = new TreeNode[n];
		}
		
		/**
		 * Sets the number of buckets <code>delta</code> of the internal node
		 * with the passed index.
		 * 
		 * @param internalNode
		 * 		the index of the internal node to set the number of buckets of
		 * @param delta
		 * 		the new number of buckets of the internal node with the passed
		 * 		index 
		 */
		public void setDelta(int internalNode, int delta) {
			internalNodes[internalNode].delta = delta;
		}

		/**
		 * Sets the level in the component hierarchy of the internal node with
		 * the passed index.
		 * 
		 * @param internalNode
		 * 		the index of the internal node to set the level of
		 * @param i
		 * 		the new level of the internal node with the passed index
		 */
		public void setI(int internalNode, int i) {
			internalNodes[internalNode].i = i;
		}
		
		/**
		 * Adds a parent-child relationship between the leaf with the passed
		 * index and the new parent with the passed index within this tree,
		 * updating the tree root if necessary.
		 * 
		 * @param leaf
		 * 		the index of the leaf that will be the child
		 * @param parent
		 * 		the index of the component that will be the parent
		 */
		public void setParentOfLeaf(int leaf, int parent) {
			if (internalNodes[parent] == null) {
				internalNodes[parent] = new TreeNode(parent);
				root = internalNodes[parent];
			}
			
			leafs[leaf].setParent(internalNodes[parent]);
			internalNodes[parent].numberOfUnvisitedVertices++;
			internalNodes[parent].numberOfUnvisitedVerticesInitial++;
		}
		
		/**
		 * Adds a parent-child relationship between the internal node with the
		 * passed index and the new parent with the passed index within this
		 * tree, updating the tree root if necessary.
		 * 
		 * @param internalNode
		 * 		the index of the internal node that will be the child
		 * @param parent
		 * 		the index of the component that will be the parent
		 */
		public void setParentOfInternalNode(int internalNode, int parent) {
			if (internalNodes[parent] == null) {
				internalNodes[parent] = new TreeNode(parent);
				root = internalNodes[parent];
			}
			
			internalNodes[internalNode].setParent(internalNodes[parent]);
			internalNodes[parent].numberOfUnvisitedVertices +=
				internalNodes[internalNode].numberOfUnvisitedVertices;
			internalNodes[parent].numberOfUnvisitedVerticesInitial +=
				internalNodes[internalNode].numberOfUnvisitedVerticesInitial;
		}
		
		/**
		 * A node of a component tree of a weighted, undirected graph with
		 * positive integer edge weights.
		 * 
		 * @author
		 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
		 * @version
		 * 		1.0, 09/17/09
		 */
		private static class TreeNode {
			/**
			 * The parent of this node.
			 */
			private TreeNode parent;
			
			/**
			 * The list of children of this node.
			 */
			private LinkedList<TreeNode> children;
			
			/**
			 * The index of this tree node.
			 */
			private int index;
			
			/**
			 * The buckets of this tree node.
			 */
			private LinkedList<TreeNode>[] buckets;
			
			/**
			 * The number of buckets of this tree node.
			 */
			private int delta;
			
			/**
			 * The lowest bucket index of this tree node.
			 */
			private int ix0;
			
			/**
			 * The highest bucket index of this tree node.
			 */
			private int ix8;
			
			/**
			 * The level in the component hierarchy of this tree node.
			 */
			private int i;
			
			/**
			 * Whether this component has already been visited, or not.
			 */
			private boolean visited;
			
			/**
			 * The offset required to index into the array of buckets of this
			 * tree node.
			 */
			private int bucketIndexOffset;
			
			/**
			 * The bucket this tree node is in.
			 */
			private LinkedList<TreeNode> containingBucket;
			
			/**
			 * The index of the next bucket to visit children of.
			 */
			private int ix;
			
			/**
			 * The maximum index of an unvisited vertex in this component. 
			 */
			private int lastUIndex;
			
			/**
			 * The number of unvisited vertices of this component.
			 */
			private int numberOfUnvisitedVertices;
			
			/**
			 * The initial number of unvisited vertices of this component.
			 * Required for a fast clean-up between repetitive queries.
			 */
			private int numberOfUnvisitedVerticesInitial;
			
			
			/**
			 * Constructs a new component tree node with the specified index.
			 * 
			 * @param index
			 * 		the index of the new component tree node
			 */
			public TreeNode(int index) {
				this.index = index;
				children = new LinkedList<TreeNode>();
			}
			
			
			/**
			 * Removes this node from its containing bucket.
			 */
			public void removeFromParentBucket() {
				containingBucket.remove(this);
			}

			/**
			 * Moves this node to the bucket with the specified index of the
			 * passed other node.
			 * 
			 * @param wi
			 * 		the node which owns the bucket to insert this one into
			 * @param index
			 * 		the index of the bucket to insert this node into
			 */
			public void moveToBucket(TreeNode wi, int index) {
				if (containingBucket != null) {
					containingBucket.remove(this);
				}
				wi.bucket(this, index);
			}

			/**
			 * Inserts the passed tree node into the bucket with the specified
			 * index, if the latter is <i>relevant</i>.
			 * 
			 * @param wh
			 * 		the node to bucket
			 * @param index
			 * 		the index of the bucket to insert the passed into
			 */
			public void bucket(TreeNode wh, int index) {
				if (index - bucketIndexOffset < buckets.length) {
					buckets[index - bucketIndexOffset].add(wh);
					wh.containingBucket = buckets[index - bucketIndexOffset];
				}
			}

			/**
			 * Returns the bucket with the specified index of this node.
			 * 
			 * @param index
			 * 		the index of the bucket to get
			 * @return
			 * 		the bucket with the specified index of this node
			 */
			public LinkedList<TreeNode> getBucket(int index) {
				return buckets[index - bucketIndexOffset];
			}
			
			/**
			 * Makes this node a child of the passed one.
			 * 
			 * @param parent
			 * 		the new parent of this node
			 */
			public void setParent(TreeNode parent) {
				this.parent = parent;
				parent.children.add(this);
			}
			
			/**
			 * Initializes all buckets of this node.
			 */
			@SuppressWarnings("unchecked")
			public void initializeBuckets() {
				bucketIndexOffset = ix0;
				buckets = new LinkedList[ix8 - ix0 + 1];
				
				for (int b = 0; b <= (ix8 - ix0); b++) {
					buckets[b] = new LinkedList<TreeNode>();
				}
			}
		}
	}
	
	/**
	 * An unvisited data structure used by <i>Thorup</i>'s algorithm for
	 * maintaining the chaning set of roots of a component tree.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 09/17/09
	 */
	private static class UnvisitedDataStructure {
		/**
		 * Maps the indices of vertices to the indices of their corresponding
		 * containers of the split-findmin structure.
		 */
		private int[] indexOfVertex;
		
		/**
		 * The containers of the split-findmin structure representing the
		 * vertices.
		 */
		private SplitFindminStructureElement<Integer>[] containers;

		
		/**
		 * Constructs a new unvisited data structure for a graph with the
		 * specified number of vertices, using the passed corresponding
		 * component tree and the passed split-findmin structure.
		 * 
		 * @param n
		 * 		the number of vertices of the graph the new unvisited data
		 * 		structure will belong to
		 * @param t
		 * 		the component tree belonging to the graph
		 * @param sf
		 * 		an empty split-findmin structure to be used by the new
		 * 		unvisited data structure
		 */
		@SuppressWarnings("unchecked")
		public UnvisitedDataStructure(int n, ComponentTree t,
				SplitFindminStructure<Integer> sf) {
			indexOfVertex = new int[n];

			initializeMapping(t.root, 0);

			containers = new SplitFindminStructureElement[n];

			for (int i = 0; i < n; i++) {
				containers[i] = sf.add(i, Double.POSITIVE_INFINITY);
			}

			sf.initialize();
		}
		
		
		/**
		 * Gets the minimum among all super-distances D of the passed node and
		 * all of its unvisited children, if it is less than
		 * {@link Double#POSITIVE_INFINITY}, and <code>-1</code> otherwise.
		 * 
		 * @param v
		 * 		the node to get the minimum super-distance of
		 * @return
		 * 		minimum among all super-distances D of the passed node and
		 * 		all of its unvisited children
		 */
		public int getMinDviMinus(ComponentTree.TreeNode v) {
			double cost = containers[v.lastUIndex].getListCost();
			
			return Double.isInfinite(cost) ? -1 : (int)cost;
		}

		/**
		 * Decreases the super-distance D of the vertex with the passed index to
		 * the specified new value.
		 * 
		 * @param v
		 * 		the index of the vertex to decrease the super-distance D of
		 * @param newDValue
		 * 		the new lower super-distance D
		 */
		public void decreaseD(int v, int newDValue) {
			containers[indexOfVertex[v]].decreaseCost(newDValue);
		}

		/**
		 * Gets the super-distance D of the vertex with the passed index.
		 * 
		 * @param v
		 * 		the index of the vertex to get the super-distance D of
		 * @return
		 * 		the super-distance D of the vertex with the passed index
		 */
		public int getD(int v) {
			return (int)containers[indexOfVertex[v]].getCost();
		}

		/**
		 * Gets the unvisited root of subtree of the leaf with the passed index
		 * in the unvisited part of the passed component tree.
		 * 
		 * @param t
		 * 		the component tree to check the unvisited part of
		 * @param w
		 * 		the index of the leaf to get the unvisited root of
		 * @return
		 * 		the unvisited root of subtree of the leaf with the passed index
		 */
		public ComponentTree.TreeNode getUnvisitedRootOf(ComponentTree t,
				int w) {
			ComponentTree.TreeNode current = t.leafs[w];
			while (!current.parent.visited) {
				current = current.parent;
			}
			
			return current;
		}

		/**
		 * Delete the passed root of the unvisited part of the component tree,
		 * turning all its children into new roots of this structure.
		 * 
		 * @param v
		 * 		the root to delete
		 */
		public void deleteRoot(ComponentTree.TreeNode v) {
			// turn the children of v into roots in this structure
			for (ComponentTree.TreeNode child : v.children) {
				if (child != v.children.getLast()) {
					containers[child.lastUIndex].split();
				}
			}
		}
		
		
		/**
		 * Recursively initializes the mapping of the indices of the passed
		 * vertex and all of its children to the indices of their corresponding
		 * containers of the split-findmin structure.
		 * 
		 * @param node
		 * 		the node to map
		 * @param index
		 * 		the index to map the node to
		 * @return
		 * 		the index of the next node to map
		 */
		private int initializeMapping(ComponentTree.TreeNode node, int index) {
			if (node.children.isEmpty()) {
				indexOfVertex[node.index] = index;

				node.lastUIndex = index;
				
				return index + 1;
			} else {
				int nextIndex = index;
				
				for (ComponentTree.TreeNode child : node.children) {
					nextIndex = initializeMapping(child, nextIndex);
				}
				
				node.lastUIndex = nextIndex - 1;
				
				return nextIndex;
			}
		}
	}
}
