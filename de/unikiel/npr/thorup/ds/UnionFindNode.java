package de.unikiel.npr.thorup.ds;

/**
 * A node of a union-find structure.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the item held by this union-find node
 */
public interface UnionFindNode<T> {
	/**
	 * Returns the item held by this union-find node.
	 * 
	 * @return
	 * 		the item held by this union-find node
	 */
	T getItem();
}
