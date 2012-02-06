package de.unikiel.npr.thorup.ds;

import java.util.NoSuchElementException;

/**
 * The interface of a priority queue which allows inserting items with real
 * keys, decreasing their keys, and finding or removing the minimum item.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the items held by this priority queue
 * @param <U>
 * 		the type of the containers holding the items of this priority queue
 */
public interface PriorityQueue<T, U extends PriorityQueueItem<T>> {
	/**
	 * Checks whether this heap is empty, or not.
	 * 
	 * @return
	 * 		<code>true</code>, if this heap is empty, and <code>false</code>
	 * 		otherwise
	 */
	boolean isEmpty();
	
	/**
	 * Inserts the passed item with the specified key into this heap.
	 * 
	 * @param item
	 * 		the item to insert
	 * @param key
	 * 		the key of the item to insert
	 * @return
	 * 		the container that holds the passed item
	 */
	public U insert(T item, double key);
	
	/**
	 * Returns the item with the minimum key in this heap.
	 * 
	 * @return
	 * 		the item with the minimum key in this heap
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public U findMin() throws NoSuchElementException;
	
	/**
	 * Deletes the item with the minimum key in this heap and returns it.
	 * 
	 * @return
	 * 		the item with the minimum key in this heap
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public U deleteMin() throws NoSuchElementException;
	
	/**
	 * Decreases the key of the specified item in this heap to the passed
	 * non-negative real number.
	 * 
	 * @param item
	 * 		the item to decrease the key of
	 * @param newKey
	 * 		the item's new key
	 * @throws IllegalArgumentException
	 * 		if the resulting key would be greater than the current one
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public void decreaseKeyTo(U item, double newKey)
		throws IllegalArgumentException, NoSuchElementException;
}
