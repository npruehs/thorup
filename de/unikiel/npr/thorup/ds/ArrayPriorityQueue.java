package de.unikiel.npr.thorup.ds;

import java.util.NoSuchElementException;

/**
 * An implementation of a priority queue using an array to store the values.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the items held by this priority queue
 */
public class ArrayPriorityQueue<T> implements
	PriorityQueue<T, ArrayPriorityQueue.ArrayPriorityQueueItem<T>> {
	
	/**
	 * The values held by this this priority queue.
	 */
	private ArrayPriorityQueueItem<T>[] a;
	
	/**
	 * The number of elements of this priority queue.
	 */
	private int numberOfElements;
	
	/**
	 * The item with minimum key in this priority queue.
	 */
	private ArrayPriorityQueueItem<T> minItem;
	
	
	/**
	 * Constructs a new, empty array priority queue.
	 * 
	 * @param n
	 * 		the maximum number of elements of the new priority queue
	 * throws IllegalArgumentException
	 * 		if <code>n</code> is less than 0
	 */
	@SuppressWarnings("unchecked")
	public ArrayPriorityQueue(int n) {
		// check the passed natural number
		if (n < 0) {
			String errorMessage = "n must be greater than or equal to 0.";
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		// initialize the value array
		a = new ArrayPriorityQueueItem[n];
		
		// initially the queue is empty
		numberOfElements = 0;
	}
	
	
	/**
	 * Returns <code>true</code>, if this priority queue is empty, and
	 * <code>false</code> otherwise, in <i>O(1)</i>.
	 * 
	 * @return
	 * 		<code>true</code>, if this priority queue is empty, and<br>
	 * 		<code>false</code>, otherwise
	 */
	public boolean isEmpty() {
		return (numberOfElements == 0);
	}
	
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
	public ArrayPriorityQueueItem<T> insert(T item, double key) {
		ArrayPriorityQueueItem<T> newItem =
			new ArrayPriorityQueueItem<T>(item, key, numberOfElements);
		
		if (minItem == null || key < minItem.key) {
			minItem = newItem;
		}
		
		a[numberOfElements++] = newItem;
		return newItem;
	}
	
	/**
	 * Decreases the key of the specified item in this heap to the passed
	 * non-negative real number.
	 * 
	 * @param item
	 * 		the item to decrease the key of
	 * @param newKey
	 * 		the item's new key
	 * @throws IllegalArgumentException
	 * 		if the passed item is <code>null</code>
	 * @throws IllegalArgumentException
	 * 		if <code>neyKey</code> is less then 0
	 */
	public void decreaseKeyTo(ArrayPriorityQueueItem<T> item, double newKey)
		throws IllegalArgumentException {
		
		if (item == null) {
			String errorMessage = "The passed item mustn't be null.";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (newKey < 0) {
			throw new IllegalArgumentException("c must be greater than 0.");
		}
		
		item.key = Math.min(item.key, newKey);
		
		if (minItem == null || newKey < minItem.key) {
			minItem = item;
		}
	}

	/**
	 * Returns the item with the minimum key in this heap.
	 * 
	 * @return
	 * 		the item with the minimum key in this heap
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public ArrayPriorityQueueItem<T> findMin() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("This queue is empty.");
		}
		
		return minItem;
	}

	/**
	 * Deletes the item with the minimum key in this heap and returns it.
	 * 
	 * @return
	 * 		the item with the minimum key in this heap
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public ArrayPriorityQueueItem<T> deleteMin() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("This queue is empty.");
		}
		
		ArrayPriorityQueueItem<T> oldMin = minItem;
		
		// remove it from the queue
		a[minItem.index] = a[numberOfElements - 1];
		a[minItem.index].index = minItem.index;
		a[numberOfElements - 1] = null;
		numberOfElements--;
		
		// compute the new minimum
		minItem = null;
		
		for (int i = 0; i < numberOfElements; i++) {
			if (minItem == null || a[i].key < minItem.key) {
				minItem = a[i];
			}
		}
		
		// return the index of the previous minimum
		return oldMin;
	}
	
	
	/**
	 * A container for an item that can be inserted into an array priority
	 * queue. Provides a pointer to its heap position and a key for comparing it
	 * to other heap items for order.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 09/17/09
	 * @param <T>
	 * 		the type of the item held by this container
	 */
	public static class ArrayPriorityQueueItem<T> implements
		PriorityQueueItem<T> {
		
		/**
		 * The item held by this container.
		 */
		private T item;
		
		/**
		 * The key of the item which is used for comparing it to other heap 
		 * items for order.
		 */
		private double key;
		
		/**
		 * The array index of this item.
		 */
		private int index;
		
		
		/**
		 * Constructs a new container for the passed item that can be inserted
		 * into an array priority queue with the specified key for comparing it
		 * to other heap items for order.
		 *  
		 * @param item
		 * 		the item held by the new container
		 * @param key
		 * 		the key of the item which is used for comparing it to other
		 * 		heap items for order
		 * @param index
		 * 		the array index of the new item
		 */
		private ArrayPriorityQueueItem(T item, double key, int index) {
			this.item = item;
			this.key = key;
			this.index = index;
		}
		
		
		/**
		 * Returns the item held by this container.
		 * 
		 * @return
		 * 		the item held by this container
		 */
		public T getItem() {
			return item;
		}

		/**
		 * Returns the key of the item which is used for comparing it to other
		 * heap items for order.
		 * 
		 * @return
		 * 		the key of the item which is used for comparing it to other
		 * 		heap items for order
		 */
		public double getKey() {
			return key;
		}
	}
}
