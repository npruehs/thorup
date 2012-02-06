package de.unikiel.npr.thorup.ds;

/**
 * A container for an item with a real key that can be inserted into a priority
 * queue.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 07/12/09
 * @param <T>
 * 		the type of the item held by the container
 */
public interface PriorityQueueItem<T> {
	/**
	 * Returns the item held by this container.
	 * 
	 * @return
	 * 		the item held by this container
	 */
	public T getItem();

	/**
	 * Returns the key of the item which is used for comparing it to other
	 * heap items for order.
	 * 
	 * @return
	 * 		the key of the item which is used for comparing it to other
	 * 		heap items for order
	 */
	public double getKey();
}
