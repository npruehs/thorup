package de.unikiel.npr.thorup.ds;

/**
 * A structure which supports two types of operations for manipulating a
 * family of disjoint sets which partition a universe of n elements:
 * 
 * <ol>
 * 		<li><code>find(v)</code> computes the name of the (unique) set
 * 			containing the element v</li>
 * 		<li><code>union(v, w)</code> combines the sets containing the elements v
 * 			and w into a new set</li>
 * </ol>
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the items managed by this union-find structure
 * @param <U>
 * 		the type of the containers holding the items of this union-find
 * 		structure
 */
public interface UnionFindStructure<T, U extends UnionFindNode<T>> {
	/**
	 * Adds a new singleton set containing the passed item.
	 * 
	 * @param item
	 * 		the item to add to the universe of elements maintained by this
	 * 		union-find structure
	 * @return
	 * 		the container holding the passed item
	 */
	U makeSet(T item);
	
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
	U find(U v);
	
	/**
	 * Merges the sets containing the canonical elements <code>u</code> and
	 * <code>v</code>.
	 * 
	 * @param u
	 * 		an element in the first set to merge
	 * @param v
	 * 		an element in the second set to merge
	 */
	void union(U u, U v);
}
