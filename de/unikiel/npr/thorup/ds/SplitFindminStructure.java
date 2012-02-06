package de.unikiel.npr.thorup.ds;

/**
 * A structure which maintains a universe of elements with real costs
 * partitioned into lists. The cost of each list is the smallest cost
 * of an element of that list. The elements of this structure allow two types
 * of operations:
 * 
 * <ol>
 * 		<li><code>decreaseCost(newCost)</code> decreases the cost of an
 * 			element and updates the minimum of the list containing that element,
 * 			if necessary</li>
 * 		<li><code>split()</code> replaces the list containing an element
 * 			by the list of all elements up to and including that element,
 * 			and the list of all remaining elements. The costs of the two new
 * 			lists are set to their proper values.</li>
 * </ol>
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the elements held by this split-findmin structure
 */
public interface SplitFindminStructure<T> {
	/**
	 * Adds the passed item with the specified cost to this split-findmin
	 * structure.
	 * 
	 * @param item
	 * 		the item to add
	 * @param cost
	 * 		the cost of the item to add
	 * @return
	 * 		the container holding the passed item
	 */
	SplitFindminStructureElement<T> add(T item, double cost);
	
	/**
	 * Initializes this split-findmin structure, preparing it for
	 * <code>decreaseCost(newCost)</code> and <code>split()</code> calls on its
	 * elements.
	 */
	void initialize();
}
