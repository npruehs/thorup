package de.unikiel.npr.thorup.ds;

/**
 * An implementation of <i>Robert Endre Tarjan</i>'s union-find structure, using
 * union with size and find with path compression.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the items managed by this union-find structure
 */
public class UnionFindStructureTarjan<T> implements
	UnionFindStructure<T, UnionFindStructureTarjan.UnionFindNodeTarjan<T>> {
	
	/**
	 * Adds a new singleton set containing the passed item.
	 * 
	 * @param item
	 * 		the item to add to the universe of elements maintained by this
	 * 		union-find structure
	 * @return
	 * 		the container holding the passed item
	 */
	public UnionFindNodeTarjan<T> makeSet(T item) {
		return new UnionFindNodeTarjan<T>(item);
	}
	
	/**
	 * Returns the canonical element of the set containing <code>v</code>.
	 * The question "Are u and v in the same set?" can be reduced to
	 * <code>find(u) == find(v)</code>.
	 * 
	 * @param v
	 * 		the item to get the canonical element of
	 * @return
	 * 		the canonical elements of the set containing <code>v</code>
	 */
	public UnionFindNodeTarjan<T> find(UnionFindNodeTarjan<T> v) {
		UnionFindNodeTarjan<T> root = v;
		
		while (root.parent != null) {
			root = root.parent;
		}

		// path compression
		UnionFindNodeTarjan<T> current = v;
		UnionFindNodeTarjan<T> next = null;
		
		while (current != root) {
			next = current.parent;
			current.parent = root;
			current = next;
		}
		
		return root;
	}
	
	/**
	 * Merges the sets containing the canonical elements <code>u</code> and
	 * <code>v</code>.
	 * 
	 * @param u
	 * 		an element in the first set to merge
	 * @param v
	 * 		an element in the second set to merge
	 */
	public void union(UnionFindNodeTarjan<T> u, UnionFindNodeTarjan<T> v) {
		UnionFindNodeTarjan<T> rootU = find(u);
		UnionFindNodeTarjan<T> rootV = find(v);

		if (u != v) {
			// union with size
			
			/* 
			 * make the root of the smaller tree a child of the root of the
			 * larger one
			 */
			if (rootU.subtreeSize < rootV.subtreeSize) {
				rootU.parent = rootV;
				rootV.subtreeSize += rootU.subtreeSize;
			} else {
				rootV.parent = rootU;
				rootU.subtreeSize += rootV.subtreeSize;
			}
		}
	}
	
	/**
	 * A node of <i>Robert Endre Tarjan</i>'s union-find structure.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 09/17/09
	 * @param <T>
	 * 		the type of the item held by this union-find node
	 */
	public static class UnionFindNodeTarjan<T> implements UnionFindNode<T> {
		/**
		 * The parent of this node.
		 */
		private UnionFindNodeTarjan<T> parent;
		
		/**
		 * The item held by this union-find node.
		 */
		private T item;
		
		/**
		 * The size of the subtree of this union-find node.
		 */
		private int subtreeSize;
		
		
		/**
		 * Constructs a new union-find node containing the passed item. The new
		 * node has no parent and a subtree size of 1.
		 * 
		 * @param item
		 * 		the item held by the new node
		 */
		private UnionFindNodeTarjan(T item) {
			this.item = item;
			subtreeSize = 1;
		}
		
		/**
		 * Returns the item held by this union-find node.
		 * 
		 * @return
		 * 		the item held by this union-find node
		 */
		public T getItem() {
			return item;
		}
	}
}
