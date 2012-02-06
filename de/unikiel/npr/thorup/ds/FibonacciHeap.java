package de.unikiel.npr.thorup.ds;

import java.util.NoSuchElementException;

/**
 * An implementation of a Fibonacci heap (abbreviated F-heap) by 
 * <i>Michael L. Fredman</i> and <i>Robert Endre Tarjan</i> which represents a
 * very fast priority queue.<br>
 * <br>
 * Provides insertion, finding the minimum, melding and decreasing keys in
 * constant amortized time, and deleting from an n-item heap in <i>O(log n)</i>
 * amortized time.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 07/12/09
 * @param <T>
 * 		the type of the items held by this Fibonacci heap
 */
public class FibonacciHeap<T> implements
	PriorityQueue<T, FibonacciHeap.FibonacciHeapItem<T>> {
	
	/**
	 * The root containing the item with the minumum key in this heap.
	 */
	private TreeNode<T> minimumNode;
	
	/**
	 * The number of elements of this heap.
	 */
	private int size;
	
	
	/**
	 * Constructs a new, empty Fibonacci heap.
	 */
	public FibonacciHeap() {}
	
	
	/**
	 * Checks whether this heap is empty, or not.
	 * 
	 * @return
	 * 		<code>true</code>, if this heap is empty, and <code>false</code>
	 * 		otherwise
	 */
	public boolean isEmpty() {
		return (minimumNode == null);
	}
	
	/**
	 * Clears this Fibonacci heap, removing all items.
	 */
	public void clear() {
		minimumNode = null;
		size = 0;
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
	public FibonacciHeapItem<T> insert(T item, double key) {
		// contruct a new container for the passed item
		FibonacciHeapItem<T> newItem = new FibonacciHeapItem<T>(item, key);
		
		// create a new heap consisting of one node containing the passed item
		FibonacciHeap<T> newHeap = new FibonacciHeap<T>();
		newHeap.minimumNode = new TreeNode<T>(newItem);
		newHeap.size = 1;
		
		// meld this heap and the new one
		meld(newHeap);
		
		return newItem;
	}
	
	/**
	 * Returns the item with the minimum key in this heap.
	 * 
	 * @return
	 * 		the item with the minimum key in this heap
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public FibonacciHeapItem<T> findMin() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("This heap is empty.");
		}
		
		return minimumNode.item;
	}
	
	/**
	 * Deletes the item with the minimum key in this heap and returns it.
	 * 
	 * @return
	 * 		the item with the minimum key in this heap
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public FibonacciHeapItem<T> deleteMin() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("This heap is empty.");
		}
		
		/*
		 * remove the minimum node x from this heap and concatenate the list of
		 * children of x with the list of roots of this heap other than x
		 */
		TreeNode<T> x = minimumNode;
		FibonacciHeapItem<T> minimumItem = x.item;
		
		size--;
		
		// remember some node to start with the linking step later on
		TreeNode<T> someListNode;
		
		if (x.rightSibling == x) {
			if (x.someChild == null) {
				// the heap consists of only the root node
				minimumNode = null;
				
				return minimumItem;
			} else {
				// root has no siblings - apply linking step to list of children
				someListNode = x.someChild;
			}
		} else {
			if (x.someChild == null) {
				// root has no children - apply linking step to list of siblings
				x.leftSibling.rightSibling = x.rightSibling;
				x.rightSibling.leftSibling = x.leftSibling;
				
				someListNode = x.leftSibling;
			} else {
				// concatenate children and siblings and apply linking step
				x.leftSibling.rightSibling = x.someChild;
				x.someChild.leftSibling.rightSibling = x.rightSibling;
				x.rightSibling.leftSibling = x.someChild.leftSibling;
				x.someChild.leftSibling = x.leftSibling;

				someListNode = x.someChild;
			}
		}

		
		// Linking Step.
		/*
		 * create an hashtable indexed by rank, from one up to the maximum
		 * possible rank, each entry pointing to a tree root
		 */
		java.util.Hashtable<Integer, TreeNode<T>> rankIndexedRoots = new
			java.util.Hashtable<Integer, TreeNode<T>>();
		
		// insert the roots one-by-one into the appropriate table positions
		TreeNode<T> nextOldRoot = someListNode;
		
		do {
			TreeNode<T> toBeInserted = nextOldRoot;
			nextOldRoot = nextOldRoot.rightSibling;
			
			while (toBeInserted != null) {
				/*
				 * if the position is already occupied, perform a linking
				 * step and attempt to insert the root of the new tree into
				 * the next higher position
				 */
				TreeNode<T> other = rankIndexedRoots.get(toBeInserted.rank);
				
				if (other != null) {
					rankIndexedRoots.remove(toBeInserted.rank);
					toBeInserted = toBeInserted.link(other);
				} else {
					rankIndexedRoots.put(toBeInserted.rank, toBeInserted);
					toBeInserted = null;
				}
			}
		} while (nextOldRoot != someListNode);

		/*
		 * form a list of the remaining roots, in the process finding a root
		 * containing an item of minimum key to serve as the minimum node of the
		 * modified heap
		 */
		java.util.Enumeration<TreeNode<T>> newRoots =
			rankIndexedRoots.elements();
		
		// start with the first new root
		TreeNode<T> firstNewRoot = newRoots.nextElement();
		minimumNode = firstNewRoot;
		minimumNode.parent = null;
		
		TreeNode<T> previousNewRoot = null;
		TreeNode<T> currentNewRoot = firstNewRoot;
		
		while (newRoots.hasMoreElements()) {
			// get the next new root
			previousNewRoot = currentNewRoot;
			currentNewRoot = newRoots.nextElement();
			
			// update pointers
			previousNewRoot.rightSibling = currentNewRoot;
			currentNewRoot.leftSibling = previousNewRoot;
			currentNewRoot.parent = null;
			
			// check for new minimum node
			if (currentNewRoot.item.key < minimumNode.item.key) {
				minimumNode = currentNewRoot;
			}
		}
	
		currentNewRoot.rightSibling = firstNewRoot;
		firstNewRoot.leftSibling = currentNewRoot;
		
		
		return minimumItem;
	}
	
	/**
	 * Takes the union of the passed heap and this one. Assumes that both heaps
	 * are item-disjoint.<br>
	 * <br>
	 * <i>This operation destroys the passed heap.</i>
	 * 
	 * @param other
	 * 		the other heap to take the union of
	 */
	public void meld(FibonacciHeap<T> other) {
		// if the other heap is empty, there is nothing to do
		if (!other.isEmpty()) {
			if (isEmpty()) {
				// if this heap is empty, return the other heap
				minimumNode = other.minimumNode;
			} else {
				// combine the root lists of both heaps into a single list
				minimumNode.rightSibling.leftSibling =
					other.minimumNode.leftSibling;
				other.minimumNode.leftSibling.rightSibling =
					minimumNode.rightSibling;
				
				minimumNode.rightSibling = other.minimumNode;
				other.minimumNode.leftSibling = minimumNode;
				
				// set the minimum node of the resulting heap
				if (minimumNode.item.key > other.minimumNode.item.key) {
					minimumNode = other.minimumNode;
				}
			}
			
			size += other.size;
		}
	}
	
	/**
	 * Decreases the key of the specified item in this heap by subtracting
	 * the passed non-negative real number <code>delta</code>.
	 * 
	 * @param item
	 * 		the item to decrease the key of
	 * @param delta
	 * 		the non-negative real number to be subtracted from the item's key
	 * @throws IllegalArgumentException
	 * 		if <code>delta</code> is negative
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public void decreaseKey(FibonacciHeapItem<T> item, double delta)
		throws IllegalArgumentException, NoSuchElementException {
		
		if (delta < 0) {
			throw new IllegalArgumentException("delta has to be non-negative.");
		}
		
		if (isEmpty()) {
			throw new NoSuchElementException("This heap is empty.");
		}
		
		// subtract delta from the key of the passed item
		item.key -= delta;
		
		// cut the edge joining the containing node x to its parent p
		TreeNode<T> x = item.containingNode;
		
		if (x.parent != null) {
			/*
			 * if x is not a root, remove it from the list of children of
			 * its parent, decrease its parent's rank, and add it to the list
			 * of roots of this heap in order to preserve the heap order
			 */
			x.cutEdgeToParent(true, this);
		}
		
		// redefine the minimum node of this heap, if necessary
		if (item.key < minimumNode.item.key) {
			minimumNode = x;
		}
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
	 * 		if the resulting key would be greater than the current one
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public void decreaseKeyTo(FibonacciHeapItem<T> item, double newKey) {
		decreaseKey(item, item.key - newKey);
	}
	
	/**
	 * Deletes the specified item from this heap.
	 * 
	 * @param item
	 * 		the item to be deleted
	 * @throws NoSuchElementException
	 * 		if this heap is empty
	 */
	public void delete(FibonacciHeapItem<T> item) 
		throws NoSuchElementException {
		
		if (isEmpty()) {
			throw new NoSuchElementException("This heap is empty.");
		}
		
		// cut the edge joining the containing node x to its parent p
		TreeNode<T> x = item.containingNode;
		
		if (x.parent == null) {
			// x is originally a root - just remove it from the list of roots
			if (x == minimumNode) {
				deleteMin();
				return;
			} else {
				x.leftSibling.rightSibling = x.rightSibling;
				x.rightSibling.leftSibling = x.leftSibling;
			}
		} else {
			/*
			 * as x is not a root, remove it from the list of children of
			 * its parent and decrease its parent's rank
			 */
			x.cutEdgeToParent(false, this);
			
			/*
			 * form a new list of roots by concatenating the list of children of
			 * x with the original list of roots
			 */
			if (x.someChild != null) {
				minimumNode.rightSibling.leftSibling = x.someChild.leftSibling;
				x.someChild.leftSibling.rightSibling = minimumNode.rightSibling;
				
				minimumNode.rightSibling = x.someChild;
				x.someChild.leftSibling = minimumNode;
			}
		}
		
		size--;
	}
	
	/**
	 * Returns the number of elements of this heap.
	 * 
	 * @return
	 * 		the number of elements of this heap
	 */
	public int getSize() {
		return size;
	}
	
	
	/**
	 * A container for an item that can be inserted into a Fibonacci heap.
	 * Provides a pointer to its heap position and a key for comparing it to
	 * other heap items for order.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 07/12/09
	 * @param <T>
	 * 		the type of the item held by this container
	 */
	public static class FibonacciHeapItem<T> implements PriorityQueueItem<T> {
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
		 * The heap node which contains the item.
		 */
		private TreeNode<T> containingNode;
		
		
		/**
		 * Constructs a new container for the passed item that can be inserted
		 * into a Fibonacci heap with the specified key for comparing it to
		 * other heap items for order.
		 *  
		 * @param item
		 * 		the item held by the new container
		 * @param key
		 * 		the key of the item which is used for comparing it to other
		 * 		heap items for order
		 */
		private FibonacciHeapItem(T item, double key) {
			this.item = item;
			this.key = key;
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
		
		/**
		 * Returns the <code>String</code> representation of the item held by
		 * this container.
		 * 
		 * @return
		 * 		a <code>String</code> representation of the item held by
		 * 		this container
		 */
		public String toString() {
			return item.toString();
		}
	}

	
	/**
	 * A node of an heap-ordered tree. Contains an item with a key which allows
	 * comparing it to other heap items for order. Provides pointers to its
	 * parent node, to its left and right siblings, and to one of its children.
	 * Can be marked in order to decide whether to make a cascading cut after
	 * the edge to this node's parent has been cut, or not.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 07/12/09
	 * @param <T>
	 * 		the type of the item held by this node
	 */
	private static class TreeNode<T> {
		/**
		 * The container holding this node's item and its key.
		 */
		private FibonacciHeapItem<T> item;
		
		/**
		 * The parent of this node.
		 */
		private TreeNode<T> parent;
		
		/**
		 * The left sibling of this node.
		 */
		private TreeNode<T> leftSibling;
		
		/**
		 * One of the children of this node.
		 */
		private TreeNode<T> someChild;
		
		/**
		 * The right sibling of this node.
		 */
		private TreeNode<T> rightSibling;
		
		/**
		 * The number of children of this node.
		 */
		private int rank;
		
		/**
		 * Whether to perform a cascading cut after the edge to this node's
		 * parent has been cut, or not.
		 */
		private boolean marked;
		
		
		/**
		 * Constructs a new heap-ordered tree node holding the passed item.
		 * The node initially has no siblings.
		 * 
		 * @param item
		 * 		the container holding this node's item and its key
		 */
		public TreeNode(FibonacciHeapItem<T> item) {
			this.item = item;
			item.containingNode = this;
			
			leftSibling = this;
			rightSibling = this;
		}
		
		
		/**
		 * Adds to passed heap-ordered tree node to the list of this node's
		 * children, increasing the rank of this node.
		 * 
		 * @param node
		 * 		the new child of this node
		 */
		public void addChild(TreeNode<T> node) {
			// update the rank of this node
			rank++;
			
			// set the parent of the new node
			node.parent = this;
			
			if (someChild == null) {
				// new node is the only child (has no siblings)
				node.leftSibling = node;
				node.rightSibling = node;
			} else {
				// append new node to the right
				node.leftSibling = someChild;
				node.rightSibling = someChild.rightSibling;
				node.rightSibling.leftSibling = node;
				someChild.rightSibling = node;
			}
			
			someChild = node;
		}
		
		/**
		 * Cuts the edge to this node's parent, decreasing the rank of its
		 * parent. Performs a <i>cascading cut</i> if necessary.
		 * 
		 * @param addToRootList
		 * 		whether this node should be added to the list of roots of its
		 * 		heap, or not
		 * @param heap
		 * 		the heap whose root list this node is added to, if
		 * 		<code>addToRootList</code> is set to true
		 */
		public void cutEdgeToParent(boolean addToRootList,
				FibonacciHeap<T> heap) {
			// remove this node from the list of children of its parent
			if (leftSibling != this) {

				leftSibling.rightSibling = rightSibling;
				rightSibling.leftSibling = leftSibling;
				
				parent.someChild = leftSibling;
			} else {
				parent.someChild = null;
			}
			
			// decrease the rank of this node's parent
			parent.rank--;
			
			if (parent.parent != null) {
				// parent is not a root
				
				if (!parent.marked) {
					// mark it if it is unmarked
					parent.marked = true;
				} else {
					// cut the edge to its parent if it is marked
					parent.cutEdgeToParent(true, heap);
				}
			}
			
			parent = null;
			
			if (addToRootList) {
				// add this node to the list of roots of this heap
				heap.minimumNode.rightSibling.leftSibling = this;
				rightSibling = heap.minimumNode.rightSibling;
				
				heap.minimumNode.rightSibling = this;
				leftSibling = heap.minimumNode;
			}
		}
		
		/**
		 * Combines the heap-ordered tree represented by the passed root node
		 * with the tree represented by this one. Assumes that both trees are
		 * item-disjoint.
		 * 
		 * @param otherTreeRoot
		 * 		the root of the other tree to combine with this one
		 * @return
		 * 		the root of the resulting heap-ordered tree
		 */
		public TreeNode<T> link(TreeNode<T> otherTreeRoot) {
			if (item.key < otherTreeRoot.item.key) {
				addChild(otherTreeRoot);
				otherTreeRoot.marked = false;
				return this;
			} else {
				otherTreeRoot.addChild(this);
				marked = false;
				return otherTreeRoot;
			}
		}
	}
}
