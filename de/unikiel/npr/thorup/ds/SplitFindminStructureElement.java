package de.unikiel.npr.thorup.ds;

/**
 * An element of a split-findmin structure.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the item held by this element
 */
public interface SplitFindminStructureElement<T> {
	/**
	 * Decreases the cost of this element and updates the minimum of the list
	 * containing it, if necessary.
	 * 
	 * @param newCost
	 * 		the new cost of this element
	 * @return
	 * 		the list containing this element
	 */
	SplitFindminStructure<T> decreaseCost(double newCost);
	
	/**
	 * Replaces the list containing this element by the list of all elements up
	 * to and including it, and the list of all remaining elements. The costs of
	 * the two new lists are set to their proper values.
	 * 
	 * @return
	 * 		the (second) list of all remaining elements
	 */
	SplitFindminStructure<T> split();
	
	/**
	 * Returns the cost of this element.
	 * 
	 * @return
	 * 		the cost of this element
	 */
	double getCost();
	
	/**
	 * Returns the cost of the list containing this element.
	 * 
	 * @return
	 * 		the cost of the list containing this element
	 */
	double getListCost();
}
