package de.unikiel.npr.thorup.ds;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of <i>Harold N. Gabow</i>'s split-findmin structure, using
 * superelements and sublists.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 * @param <T>
 * 		the type of the items managed by this split-findmin structure
 */
public class SplitFindminStructureGabow<T> implements SplitFindminStructure<T> {
	/**
	 * The index of this list. Every sublist has an index that equals the index
	 * of this list reduces by one.
	 */
	private int i;
	
	/**
	 * The smallest cost of an element of this list.
	 */
	private double cost;
	
	/**
	 * The elements of this list.
	 */
	private MyList<Element<T>> elements;
	
	/**
	 * The left-overs of this list.
	 */
	private MyList<Element<T>> singletonElements;
	
	/**
	 * The singleton superelements of this list.
	 */
	private MyList<Superelement<T>> singletonSuperelements;
	
	/**
	 * The sublists of this list.
	 */
	private MyList<SplitFindminStructureGabow<Superelement<T>>> sublists;
	
	/**
	 * The list containing this sublist, if this is a sublist, and
	 * <code>null</code> otherwise. 
	 */
	@SuppressWarnings("unchecked")
	private SplitFindminStructureGabow containingList;
	
	/**
	 * The table of values of <i>Ackermann</i>'s function <i>A(i, j)</i> that
	 * are <i>n</i> or less, where <i>n</i> is the number of elements of the
	 * original split-findmin structure. 
	 */
	private AckermannTable ackermann;
	
	/**
	 * The container holding this sublist, if this is a sublist, and
	 * <code>null</code> otherwise.
	 */
	private MyList.Container<SplitFindminStructureGabow<T>>
		containingContainerSublists;
	
	/**
	 * Constructs a new split-findmin structure for processing a universe of
	 * <i>n</i> elements. If the number of <i>decreasecosts m</i> is known in
	 * advance, choose {@link #SplitFindminStructureGabow(int, int)} instead.
	 * 
	 * @param n
	 * 		the number of elements to be held by the new structure
	 */
	public SplitFindminStructureGabow(int n) {
		this(n, n);
	}
	
	/**
	 * Constructs a new split-findmin structure for processing a universe of
	 * <i>n</i> elements with <i>m decreasecosts</i>.
	 * 
	 * @param n
	 * 		the number of elements to be held by the new structure
	 * @param m
	 * 		the number of <i>decreasecosts</i> to be supported
	 */
	public SplitFindminStructureGabow(int n, int m) {
		this();

		ackermann = new AckermannTable(n);
		i = ackermann.getInverse(m, n);
	}
	
	/**
	 * Constructs a new list with the passed table of values of
	 * <i>Ackermann</i>'s function and the specified index.
	 * 
	 * @param ackermann
	 * 		the table of values of <i>Ackermann</i>'s function to be used by
	 * 		the new list
	 * @param i
	 * 		the index of the new list
	 */
	private SplitFindminStructureGabow(AckermannTable ackermann, int i) {
		this();
		
		this.ackermann = ackermann;
		this.i = i;
	}
	
	/**
	 * Constructs a new, empty split-findmin structure.
	 */
	private SplitFindminStructureGabow() {
		elements = new MyList<Element<T>>();
		singletonElements = new MyList<Element<T>>();
		singletonSuperelements = new MyList<Superelement<T>>();
		sublists = new MyList<SplitFindminStructureGabow<Superelement<T>>>();
	}
	
	
	/**
	 * Returns <code>true</code>, if this list is a sublist, and
	 * <code>false</code> otherwise.
	 * 
	 * @return
	 * 		<code>true</code>, if this list is a sublist, and<br>
	 * 		<code>false</code> otherwise
	 */
	public boolean isSublist() {
		return containingList != null;
	}
	
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
	public Element<T> add(T item, double cost) {
		Element<T> e = new Element<T>(item, cost);
		MyList.Container<Element<T>> container = elements.add(e);
		e.containingContainer = container;
		return e;
	}
	
	/**
	 * Initializes this split-findmin structure, preparing it for
	 * <code>decreaseCost(newCost)</code> and <code>split()</code> calls on its
	 * elements.
	 */
	public void initialize() {
		initializeHead();
	}
	
	/**
	 * Scans the elements of this list right-to-left, partitioning them into
	 * superelements, sublists and singletons. Initializes all sublists the
	 * same way then.
	 */
	private void initializeHead() {
		// scan list right-to-left
		MyList.Container<Element<T>> current = elements.lastContainer;
		
		// compute c(L) and the size of this list
		cost = Double.POSITIVE_INFINITY;
		int size = 0;
		
		while (current != elements.leftSentinel) {
			size++;
			cost = Math.min(cost, current.item.cost);
			current = current.predecessor;
		}
		
		// partition this list into superelements, sublists and singletons
		current = elements.lastContainer;
		int processedElements = 0;
		int superelementsInCurrentSublist = 0;
		Superelement<T> mostRecentSuperelement = null;
		Superelement<T> currentSuperelement = null;
		SplitFindminStructureGabow<Superelement<T>> currentLevelSublist =
			new SplitFindminStructureGabow<Superelement<T>>(ackermann, i - 1);
		
		// check whether there are enough elements remaining for a superelement
		while (size - processedElements > 3) {
			// compute the level of the next superelement
			int level = ackermann.getInverse(i, size - processedElements);
			
			// construct a new superelement
			currentSuperelement = new Superelement<T>(level);
			currentSuperelement.cost = Double.POSITIVE_INFINITY;
			
			// compute the number of elements of the next superelement
			int numberOfElements = 2 * ackermann.getValue(i, level);

			// add the elements to the current superelement
			currentSuperelement.last = current.item;
			for (int k = 0; k < numberOfElements; k++) {
				// set e(x)
				current.item.superelement = currentSuperelement;
				
				// update c(e(x))
				currentSuperelement.cost =
					Math.min(currentSuperelement.cost, current.item.cost);
				
				current = current.predecessor;
			}
			currentSuperelement.first = current.successor.item;

			
			if (mostRecentSuperelement != null && mostRecentSuperelement.level
					!= level) {
				
				// now we have to add or reject our constructed sublist
				if (superelementsInCurrentSublist > 1) {
					MyList.Container<SplitFindminStructureGabow
						<Superelement<T>>> container =
							sublists.addFirst(currentLevelSublist);
					currentLevelSublist.containingContainerSublists = container;
					
					currentLevelSublist.containingList = this;
				} else {
					// most recent superelement is a singleton
					MyList.Container<Superelement<T>> container =
						singletonSuperelements.addFirst(mostRecentSuperelement);
					mostRecentSuperelement.
						containingContainerSingletonSuperelements = container;
					
					mostRecentSuperelement.containingList = this;
					mostRecentSuperelement.elementInSublist = null;
					mostRecentSuperelement.containingSublist = null;
				}
				
				// construct a new sublist - we might need it later
				currentLevelSublist =
					new SplitFindminStructureGabow<Superelement<T>>
						(ackermann, i - 1);
				superelementsInCurrentSublist = 0;
			}
			
			// add the current superelement to the current sublist
			Element<Superelement<T>> e = currentLevelSublist.addFirst
				(currentSuperelement, currentSuperelement.cost);
			currentSuperelement.elementInSublist = e;
			currentSuperelement.containingSublist = currentLevelSublist;
			superelementsInCurrentSublist++;
			
			// prepare next iteration
			processedElements += numberOfElements;
			mostRecentSuperelement = currentSuperelement;
		}
		
		// process the last sublist individually, if necessary
		if (superelementsInCurrentSublist > 1) {
			MyList.Container<SplitFindminStructureGabow<Superelement<T>>>
				container = sublists.addFirst(currentLevelSublist);
			currentLevelSublist.containingContainerSublists = container;
			
			currentLevelSublist.containingList = this;
		} else {
			if (mostRecentSuperelement != null) {
				// most recent superelement is a singleton
				MyList.Container<Superelement<T>> container =
					singletonSuperelements.addFirst(mostRecentSuperelement);
				mostRecentSuperelement.
					containingContainerSingletonSuperelements = container;
				
				mostRecentSuperelement.containingList = this;
				mostRecentSuperelement.elementInSublist = null;
				mostRecentSuperelement.containingSublist = null;
			}
		}

		// process leftovers
		while (current != elements.leftSentinel) {
			MyList.Container<Element<T>> container =
				singletonElements.addFirst(current.item);
			current.item.containingContainerSingletonElements = container; 
			current.item.containingList = this;
			current = current.predecessor;
		}
		
		// call A_{i-1} to do initialize-head on each sublist
		for (SplitFindminStructureGabow<Superelement<T>> sublist : sublists) {
			sublist.initializeHead();
		}
		
	}

	/**
	 * Scans the elements of this list left-to-right, partitioning them into
	 * superelements, sublists and singletons. Initializes all sublists the
	 * same way then.
	 */
	private void initializeTail() {
		// scan list left-to-right
		MyList.Container<Element<T>> current = elements.leftSentinel.successor;
		
		// compute c(L) and the size of this list
		cost = Double.POSITIVE_INFINITY;
		int size = 0;
		
		while (current != null) {
			size++;
			cost = Math.min(cost, current.item.cost);
			current = current.successor;
		}
		
		// partition this list into superelements, sublists and singletons
		current = elements.leftSentinel.successor;
		int processedElements = 0;
		int superelementsInCurrentSublist = 0;
		Superelement<T> mostRecentSuperelement = null;
		Superelement<T> currentSuperelement = null;
		SplitFindminStructureGabow<Superelement<T>> currentLevelSublist =
			new SplitFindminStructureGabow<Superelement<T>>(ackermann, i - 1);
		
		// check whether there are enough elements remaining for a superelement
		while (size - processedElements > 3) {
			// compute the level of the next superelement
			int level = ackermann.getInverse(i, size - processedElements);
			
			// construct a new superelement
			currentSuperelement = new Superelement<T>(level);
			currentSuperelement.cost = Double.POSITIVE_INFINITY;
			
			// compute the number of elements of the next superelement
			int numberOfElements = 2 * ackermann.getValue(i, level);

			// add the elements to the current superelement
			currentSuperelement.first = current.item;
			for (int k = 0; k < numberOfElements; k++) {
				// set e(x)
				current.item.superelement = currentSuperelement;
				
				// update c(e(x))
				currentSuperelement.cost =
					Math.min(currentSuperelement.cost, current.item.cost);
				
				current = current.successor;
			}
			currentSuperelement.last = current.predecessor.item;

			
			if (mostRecentSuperelement != null && mostRecentSuperelement.level
					!= level) {
				
				// now we have to add or reject our constructed sublist
				if (superelementsInCurrentSublist > 1) {
					MyList.Container<SplitFindminStructureGabow
						<Superelement<T>>> container =
							sublists.add(currentLevelSublist);
					currentLevelSublist.containingContainerSublists = container;
					
					currentLevelSublist.containingList = this;
				} else {
					// most recent superelement is a singleton
					MyList.Container<Superelement<T>> container =
						singletonSuperelements.add(mostRecentSuperelement);
					mostRecentSuperelement.
						containingContainerSingletonSuperelements = container;
					
					mostRecentSuperelement.containingList = this;
					mostRecentSuperelement.elementInSublist = null;
					mostRecentSuperelement.containingSublist = null;
				}
				
				// construct a new sublist - we might need it later
				currentLevelSublist = new SplitFindminStructureGabow
					<Superelement<T>>(ackermann, i - 1);
				superelementsInCurrentSublist = 0;
			}
			
			// add the current superelement to the current sublist
			Element<Superelement<T>> e = currentLevelSublist.add
				(currentSuperelement, currentSuperelement.cost);
			currentSuperelement.elementInSublist = e;
			currentSuperelement.containingSublist = currentLevelSublist;
			superelementsInCurrentSublist++;
			
			// prepare next iteration
			processedElements += numberOfElements;
			mostRecentSuperelement = currentSuperelement;
		}
		
		// process the last sublist individually, if necessary
		if (superelementsInCurrentSublist > 1) {
			MyList.Container<SplitFindminStructureGabow<Superelement<T>>>
				container = sublists.add(currentLevelSublist);
			currentLevelSublist.containingContainerSublists = container;
			
			currentLevelSublist.containingList = this;
		} else {
			if (mostRecentSuperelement != null) {
				// most recent superelement is a singleton
				MyList.Container<Superelement<T>> container =
					singletonSuperelements.add(mostRecentSuperelement);
				mostRecentSuperelement.
					containingContainerSingletonSuperelements = container;
				
				mostRecentSuperelement.containingList = this;
				mostRecentSuperelement.elementInSublist = null;
				mostRecentSuperelement.containingSublist = null;
			}
		}

		// process leftovers
		while (current != null) {
			MyList.Container<Element<T>> container =
				singletonElements.add(current.item);
			current.item.containingContainerSingletonElements = container; 
			current.item.containingList = this;
			current = current.successor;
		}
		
		// call A_{i-1} to do initialize-tail on each sublist
		for (SplitFindminStructureGabow<Superelement<T>> sublist : sublists) {
			sublist.initializeTail();
		}
	}

	/**
	 * Scans the elements of this list right-to-left from <code>first</code> to
	 * <code>last</code>, partitioning them into superelements, sublists and
	 * singletons, which are inserted into the three passed lists. Initializes
	 * all sublists the same way then.
	 * 
	 * @param first
	 * 		the first element to scan 
	 * @param last
	 * 		the last element to scan
	 * @param newSingletonElements
	 * 		the list to insert the new singleton elements into
	 * @param newSingletonSuperelements 
	 * 		the list to insert the new singleton superelements into
	 * @param newSublists
	 * 		the list to insert the new sublists into
	 */
	private void initializeHead
			(MyList.Container<Element<T>> first,
			MyList.Container<Element<T>> last,
			MyList<Element<T>> newSingletonElements,
			MyList<Superelement<T>> newSingletonSuperelements,
			MyList<SplitFindminStructureGabow<Superelement<T>>> newSublists) {
		
		// scan list right-to-left
		MyList.Container<Element<T>> current = last;
		
		// compute the size of this list
		int size = 0;
		
		while (current != first.predecessor) {
			size++;
			current = current.predecessor;
		}
		
		// partition this list into superelements, sublists and singletons
		current = last;
		int processedElements = 0;
		int superelementsInCurrentSublist = 0;
		Superelement<T> mostRecentSuperelement = null;
		Superelement<T> currentSuperelement = null;
		SplitFindminStructureGabow<Superelement<T>> currentLevelSublist =
			new SplitFindminStructureGabow<Superelement<T>>(ackermann, i - 1);
		
		// check whether there are enough elements remaining for a superelement
		while (size - processedElements > 3) {
			// compute the level of the next superelement
			int level = ackermann.getInverse(i, size - processedElements);
			
			// construct a new superelement
			currentSuperelement = new Superelement<T>(level);
			currentSuperelement.cost = Double.POSITIVE_INFINITY;
			
			// compute the number of elements of the next superelement
			int numberOfElements = 2 * ackermann.getValue(i, level);

			// add the elements to the current superelement
			currentSuperelement.last = current.item;
			for (int k = 0; k < numberOfElements; k++) {
				// set e(x)
				current.item.superelement = currentSuperelement;
				
				// update c(e(x))
				currentSuperelement.cost =
					Math.min(currentSuperelement.cost, current.item.cost);
				
				current = current.predecessor;
			}
			currentSuperelement.first = current.successor.item;

			
			if (mostRecentSuperelement != null && mostRecentSuperelement.level
					!= level) {
				
				// now we have to add or reject our constructed sublist
				if (superelementsInCurrentSublist > 1) {
					MyList.Container<SplitFindminStructureGabow
						<Superelement<T>>> container =
							newSublists.addFirst(currentLevelSublist);
					currentLevelSublist.containingContainerSublists = container;
					
					currentLevelSublist.containingList = this;
				} else {
					// most recent superelement is a singleton
					MyList.Container<Superelement<T>> container =
						newSingletonSuperelements.addFirst
							(mostRecentSuperelement);
					mostRecentSuperelement.
						containingContainerSingletonSuperelements = container;
					
					mostRecentSuperelement.containingList = this;
					mostRecentSuperelement.elementInSublist = null;
					mostRecentSuperelement.containingSublist = null;
				}
				
				// construct a new sublist - we might need it later
				currentLevelSublist =
					new SplitFindminStructureGabow<Superelement<T>>
						(ackermann, i - 1);
				superelementsInCurrentSublist = 0;
			}
			
			// add the current superelement to the current sublist
			Element<Superelement<T>> e = currentLevelSublist.addFirst
				(currentSuperelement, currentSuperelement.cost);
			currentSuperelement.elementInSublist = e;
			currentSuperelement.containingSublist = currentLevelSublist;
			superelementsInCurrentSublist++;
			
			// prepare next iteration
			processedElements += numberOfElements;
			mostRecentSuperelement = currentSuperelement;
		}
		
		// process the last sublist individually, if necessary
		if (superelementsInCurrentSublist > 1) {
			MyList.Container<SplitFindminStructureGabow<Superelement<T>>>
				container = newSublists.addFirst(currentLevelSublist);
			currentLevelSublist.containingContainerSublists = container;
			
			currentLevelSublist.containingList = this;
		} else {
			// most recent superelement is a singleton
			if (mostRecentSuperelement != null) {
				MyList.Container<Superelement<T>> container =
					newSingletonSuperelements.addFirst(mostRecentSuperelement);
				mostRecentSuperelement.
					containingContainerSingletonSuperelements = container;
				
				mostRecentSuperelement.containingList = this;
				mostRecentSuperelement.elementInSublist = null;
				mostRecentSuperelement.containingSublist = null;
			}
		}

		// process leftovers
		while (current != first.predecessor) {
			MyList.Container<Element<T>> container =
				newSingletonElements.addFirst(current.item);
			current.item.containingContainerSingletonElements = container; 
			current.item.containingList = this;
			current.item.superelement = null;
			current = current.predecessor;
		}
		
		// call A_{i-1} to do initialize-head on each sublist
		for (SplitFindminStructureGabow<Superelement<T>> sublist : newSublists)
		{
			sublist.initializeHead();
		}
	}
	
	/**
	 * Scans the elements of this list left-to-right from <code>first</code> to
	 * <code>last</code>, partitioning them into superelements, sublists and
	 * singletons, which are inserted into the three passed lists. Initializes
	 * all sublists the same way then.
	 * 
	 * @param first
	 * 		the first element to scan 
	 * @param last
	 * 		the last element to scan
	 * @param newSingletonElements
	 * 		the list to insert the new singleton elements into
	 * @param newSingletonSuperelements 
	 * 		the list to insert the new singleton superelements into
	 * @param newSublists
	 * 		the list to insert the new sublists into
	 */
	private void initializeTail
			(MyList.Container<Element<T>> first,
			MyList.Container<Element<T>> last,
			MyList<Element<T>> newSingletonElements,
			MyList<Superelement<T>> newSingletonSuperelements,
			MyList<SplitFindminStructureGabow<Superelement<T>>> newSublists) {
			
			// scan list left-to-right
			MyList.Container<Element<T>> current = first;
			
			// compute the size of this list
			int size = 0;
			
			while (current != last.successor) {
				size++;
				current = current.successor;
			}
			
			// partition this list into superelements, sublists and singletons
			current = first;
			int processedElements = 0;
			int superelementsInCurrentSublist = 0;
			Superelement<T> mostRecentSuperelement = null;
			Superelement<T> currentSuperelement = null;
			SplitFindminStructureGabow<Superelement<T>> currentLevelSublist =
				new SplitFindminStructureGabow<Superelement<T>>
					(ackermann, i - 1);
			
			/*
			 * check whether there are enough elements remaining for a
			 * superelement
			 */
			while (size - processedElements > 3) {
				// compute the level of the next superelement
				int level = ackermann.getInverse(i, size - processedElements);
				
				// construct a new superelement
				currentSuperelement = new Superelement<T>(level);
				currentSuperelement.cost = Double.POSITIVE_INFINITY;
				
				// compute the number of elements of the next superelement
				int numberOfElements = 2 * ackermann.getValue(i, level);

				// add the elements to the current superelement
				currentSuperelement.first = current.item;
				for (int k = 0; k < numberOfElements; k++) {
					// set e(x)
					current.item.superelement = currentSuperelement;
					
					// update c(e(x))
					currentSuperelement.cost =
						Math.min(currentSuperelement.cost, current.item.cost);
					
					current = current.successor;
				}
				currentSuperelement.last = current.predecessor.item;

				
				if (mostRecentSuperelement != null &&
					mostRecentSuperelement.level != level) {
					// now we have to add or reject our constructed sublist
					if (superelementsInCurrentSublist > 1) {
						MyList.Container<SplitFindminStructureGabow
							<Superelement<T>>> container =
								newSublists.add(currentLevelSublist);
						currentLevelSublist.containingContainerSublists =
							container;
						
						currentLevelSublist.containingList = this;
					} else {
						// most recent superelement is a singleton
						MyList.Container<Superelement<T>> container =
							newSingletonSuperelements.add
								(mostRecentSuperelement);
						mostRecentSuperelement.
							containingContainerSingletonSuperelements =
								container;
						
						mostRecentSuperelement.containingList = this;
						mostRecentSuperelement.elementInSublist = null;
						mostRecentSuperelement.containingSublist = null;
					}
					
					// construct a new sublist - we might need it later
					currentLevelSublist = new SplitFindminStructureGabow
						<Superelement<T>>(ackermann, i - 1);
					superelementsInCurrentSublist = 0;
				}
				
				// add the current superelement to the current sublist
				Element<Superelement<T>> e = currentLevelSublist.add
					(currentSuperelement, currentSuperelement.cost);
				currentSuperelement.elementInSublist = e;
				currentSuperelement.containingSublist = currentLevelSublist;
				superelementsInCurrentSublist++;
				
				// prepare next iteration
				processedElements += numberOfElements;
				mostRecentSuperelement = currentSuperelement;
			}
			
			// process the last sublist individually, if necessary
			if (superelementsInCurrentSublist > 1) {
				MyList.Container<SplitFindminStructureGabow<Superelement<T>>>
					container = newSublists.add(currentLevelSublist);
				currentLevelSublist.containingContainerSublists = container;
				
				currentLevelSublist.containingList = this;
			} else {
				if (mostRecentSuperelement != null) {
					// most recent superelement is a singleton
					MyList.Container<Superelement<T>> container =
						newSingletonSuperelements.add(mostRecentSuperelement);
					mostRecentSuperelement.
						containingContainerSingletonSuperelements = container;
					
					mostRecentSuperelement.containingList = this;
					mostRecentSuperelement.elementInSublist = null;
					mostRecentSuperelement.containingSublist = null;
				}
			}

			// process leftovers
			while (current != last.successor) {
				MyList.Container<Element<T>> container =
					newSingletonElements.add(current.item);
				current.item.containingContainerSingletonElements = container; 
				current.item.containingList = this;
				current.item.superelement = null;
				current = current.successor;
			}
			
			// call A_{i-1} to do initialize-tail on each sublist
			for (SplitFindminStructureGabow<Superelement<T>> sublist :
					newSublists) {
				
				sublist.initializeTail();
			}
	}
	
	/**
	 * Adds the passed item with the specified cost to the front of this
	 * split-findmin structure.
	 * 
	 * @param item
	 * 		the item to add
	 * @param cost
	 * 		the cost of the item to add
	 * @return
	 * 		the container holding the passed item
	 */
	private Element<T> addFirst(T item, double cost) {
		Element<T> e = new Element<T>(item, cost);
		MyList.Container<Element<T>> container = elements.addFirst(e);
		e.containingContainer = container;
		return e;
	}

	/**
	 * Returns the smallest cost of an element of this list, if this is no
	 * sublist, and the smallest cost of an element of the list containing this
	 * sublist, otherwise.
	 * 
	 * @return
	 * 		the cost of this list
	 */
	private double getCost() {
		if (containingList == null) {
			return cost;
		} else {
			return containingList.getCost();
		}
	}

	/**
	 * An element of <i>Harold N. Gabow</i>'s split-findmin structure.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 09/17/09
	 * @param <T>
	 * 		the type of the item held by this element
	 */
	public static class Element<T> implements SplitFindminStructureElement<T> {
		/**
		 * The cost of this element.
		 */
		private double cost;
		
		/**
		 * The superelement that contains this element, if any, and
		 * <code>null</code> otherwise.
		 */
		private Superelement<T> superelement;
		
		/**
		 * The list containing this element, if this element is a left-over,
		 * and <code>null</code> otherwise.
		 */
		private SplitFindminStructureGabow<T> containingList;
		
		/**
		 * The container holding this element in the list of elements of the
		 * list containing this element.
		 */
		private MyList.Container<Element<T>> containingContainer;
		
		/**
		 * The container holding this element in the list of left-overs of the
		 * list containing this element.
		 */
		private MyList.Container<Element<T>>
			containingContainerSingletonElements;
		
		/**
		 * The item held by this element.
		 */
		private T item;
		
		
		/**
		 * Constructs a new element holding the passed item with the specified
		 * cost.
		 *  
		 * @param item
		 * 		the item held by the new element
		 * @param cost
		 * 		the cost of the new element
		 */
		public Element(T item, double cost) {
			this.item = item;
			this.cost = cost;
		}
		
		
		/**
		 * Returns <code>true</code>, if this element is a singleton, and
		 * <code>false</code> otherwise.
		 * 
		 * @return
		 * 		<code>true</code>, if this element is a singleton, and<br>
		 * 		<code>false</code> otherwise
		 */
		public boolean isSingleton() {
			return containingList != null ||
					(superelement != null && superelement.isSingleton());
		}
		
		/**
		 * Decreases the cost of this element and updates the minimum of the
		 * list containing it, if necessary.
		 * 
		 * @param newCost
		 * 		the new cost of this element
		 * @return
		 * 		the list containing this element
		 */
		@SuppressWarnings("unchecked")
		public SplitFindminStructureGabow<T> decreaseCost(double newCost) {
			if (isSingleton()) {
				// update c(x)
				cost = Math.min(cost, newCost);
				
				if (superelement != null) {
					/* x is contained by a singleton superelement */
					
					// update c(e(x))
					superelement.cost = Math.min(superelement.cost, newCost);
					
					// update c(L(x))
					superelement.containingList.cost =
						Math.min(superelement.containingList.cost, newCost);
					
					// return L(x)
					return superelement.containingList;
				} else {
					/* x is a left-over */
					
					// update c(L(x))
					containingList.cost =
						Math.min(containingList.cost, newCost);
					
					// return L(x)
					return containingList;
				}
			} else {
				/* x is contained by a superelement in a sublist */
				
				// call A_{i-1} to do decreasecost(e(x), d)
				SplitFindminStructureGabow<Superelement<T>> sublist =
					superelement.elementInSublist.decreaseCost(newCost);
				
				// update c(e(x))
				superelement.cost = Math.min(superelement.cost, newCost);
				
				// update c(x)
				cost = Math.min(cost, newCost);
				
				// find L(x)
				SplitFindminStructureGabow list = sublist.containingList;
				
				// update c(L(x))
				list.cost = Math.min(list.cost, newCost);
				
				// return L(x)
				return list;
			}
		}
		
		/**
		 * Replaces the list containing this element by the list of all elements
		 * up to and including it, and the list of all remaining elements. The
		 * costs of* the two new lists are set to their proper values.
		 * 
		 * @return
		 * 		the (second) list of all remaining elements
		 */
		@SuppressWarnings("unchecked")
		public SplitFindminStructureGabow<T> split() {
			//System.err.println(item);
			SplitFindminStructureGabow<T> l1 = null;
			SplitFindminStructureGabow<T> l2 = null;
			
			if (isSingleton()) {
				if (superelement == null) {
					/* this element is a left-over */
					l1 = containingList;
					
					// initialize the new list L2
					l2 = new SplitFindminStructureGabow<T>(l1.ackermann, l1.i);
					l2.singletonElements = l1.singletonElements.cutAfter
						(containingContainerSingletonElements);
					
					/*
					 * look for the last singleton superelement in front of this
					 * singleton
					 */
					MyList.Container<Element<T>> current =
						containingContainer.predecessor;
					
					while (current != l1.elements.leftSentinel) {
						Superelement<T> se = current.item.superelement;
						
						if (se != null && se.isSingleton()) {
							l2.singletonSuperelements =
								l1.singletonSuperelements.cutAfter
								(se.containingContainerSingletonSuperelements);
							break;
						}
						
						current = current.predecessor;
					}
					
					if (current == l1.elements.leftSentinel) {
						l2.singletonSuperelements = l1.singletonSuperelements;
						l1.singletonSuperelements =
							new MyList<Superelement<T>>();
					}
					
					// look for the last sublist in front of this singleton
					current = containingContainer.predecessor;
					
					while (current != l1.elements.leftSentinel) {
						Superelement<T> se = current.item.superelement;
						
						if (se != null && !se.isSingleton()) {
							l2.sublists = l1.sublists.cutAfter
								(se.containingSublist.
										containingContainerSublists);
							break;
						}
						
						current = current.predecessor;
					}
					
					if (current == l1.elements.leftSentinel) {
						l2.sublists = l1.sublists;
						l1.sublists = new MyList<SplitFindminStructureGabow
							<Superelement<T>>>();
					}
				} else {
					/* this element is contained by a singleton superelement */
					l1 = superelement.containingList;
					
					// initialize the new list L2
					l2 = new SplitFindminStructureGabow<T>(l1.ackermann, l1.i);
					
					if (this == superelement.last) {
						// look for the last singleton in front of this one
						MyList.Container<Element<T>> current =
							containingContainer.predecessor;
						
						while (current != l1.elements.leftSentinel) {
							Element<T> e = current.item;
							
							if (e.isSingleton() && e.superelement == null) {
								l2.singletonElements = l1.singletonElements.
									cutAfter(e.
										containingContainerSingletonElements);
								break;
							}
							
							current = current.predecessor;
						}
						
						if (current == l1.elements.leftSentinel) {
							l2.singletonElements = l1.singletonElements;
							l1.singletonElements = new MyList<Element<T>>();
						}
						
						l2.singletonSuperelements =
							l1.singletonSuperelements.cutAfter
								(superelement.
									containingContainerSingletonSuperelements);
						
						// look for the last sublist in front of this singleton
						current = containingContainer.predecessor;
						
						while (current != l1.elements.leftSentinel) {
							Superelement<T> se = current.item.superelement;
							
							if (se != null && !se.isSingleton()) {
								l2.sublists = l1.sublists.cutAfter
									(se.containingSublist.
										containingContainerSublists);
								break;
							}
							
							current = current.predecessor;
						}
						
						if (current == l1.elements.leftSentinel) {
							l2.sublists = l1.sublists;
							l1.sublists = new MyList<SplitFindminStructureGabow
								<Superelement<T>>>();
						}
					} else {
						// look for the position to insert the new left-overs at
						MyList.Container<Element<T>> lastSingletonElement =
							null;
						MyList.Container<Element<T>> current =
							containingContainer.predecessor;
						
						while (current != l1.elements.leftSentinel) {
							Element<T> e = current.item;
							
							if (e.isSingleton() && e.superelement == null) {
								lastSingletonElement =
									e.containingContainerSingletonElements;
								break;
							}
							
							current = current.predecessor;
						}

						if (current == l1.elements.leftSentinel) {
							lastSingletonElement =
								l1.singletonElements.leftSentinel;
						}
						
						/*
						 * look for the position to insert the new singleton
						 * superelements at
						 */
						MyList.Container<Superelement<T>>
							lastSingletonSuperelement =
								superelement.
								containingContainerSingletonSuperelements;
						
						// look for the position to insert the new sublists at
						MyList.Container<SplitFindminStructureGabow
							<Superelement<T>>> lastSublist = null;
						current = containingContainer.predecessor;
						
						while (current != l1.elements.leftSentinel) {
							Superelement<T> se = current.item.superelement;
							
							if (se != null && !se.isSingleton()) {
								lastSublist = se.containingSublist.
									containingContainerSublists;
								break;
							}
							
							current = current.predecessor;
						}
						
						if (current == l1.elements.leftSentinel) {
							lastSublist = l1.sublists.leftSentinel;
						}
						
						/*
						 * remove e(x) from the list of singleton superelements
						 * of this list
						 */
						lastSingletonSuperelement =
							l1.singletonSuperelements.remove
							(lastSingletonSuperelement);

						/*
						 *  prepare three new lists for doing initialize-head on
						 *  the elements of e(x) up to and including x
						 */
						MyList<Element<T>> newSingletonElements =
							new MyList<Element<T>>();
						MyList<Superelement<T>> newSingletonSuperelements =
							new MyList<Superelement<T>>();
						MyList<SplitFindminStructureGabow<Superelement<T>>>
							newSublists =
								new MyList<SplitFindminStructureGabow
									<Superelement<T>>>();
						
						// remember the old superelement of this element
						Superelement<T> oldSuperelement = superelement;
						
						/*
						 * do initialize-head on the elements of e(x) up to and
						 * including x
						 */
						l1.initializeHead
							(superelement.first.containingContainer,
							 containingContainer,
							 newSingletonElements,
							 newSingletonSuperelements,
							 newSublists);
						
						// concat the three lists
						lastSingletonElement =
							l1.singletonElements.insertListAfter
								(lastSingletonElement, newSingletonElements);
						lastSingletonSuperelement =
							l1.singletonSuperelements.insertListAfter
								(lastSingletonSuperelement,
								 newSingletonSuperelements);
						lastSublist =
							l1.sublists.insertListAfter
								(lastSublist, newSublists);
						
						// cut the three lists of L1
						l2.singletonElements =
							l1.singletonElements.cutAfter(lastSingletonElement);
						l2.singletonSuperelements =
							l1.singletonSuperelements.cutAfter
								(lastSingletonSuperelement);
						l2.sublists = l1.sublists.cutAfter(lastSublist);
						
						/*
						 * prepare three new lists for doing initialize-tail on
						 * the remaining elements
						 */
						newSingletonElements = new MyList<Element<T>>();
						newSingletonSuperelements =
							new MyList<Superelement<T>>();
						newSublists = new MyList<SplitFindminStructureGabow
							<Superelement<T>>>();

						// do initialize-tail on the remaining elements
						l1.initializeTail
							(containingContainer.successor,
							 oldSuperelement.last.containingContainer,
							 newSingletonElements,
							 newSingletonSuperelements,
							 newSublists);
						
						// concat the three lists
						newSingletonElements.concat(l2.singletonElements);
						newSingletonSuperelements.concat
							(l2.singletonSuperelements);
						newSublists.concat(l2.sublists);
						
						l2.singletonElements = newSingletonElements;
						l2.singletonSuperelements = newSingletonSuperelements;
						l2.sublists = newSublists;
					}
				}
			} else {
				/* this element is contained by superelement in a sublist */
				l1 = superelement.containingSublist.containingList;
				
				// initialize the new list L2
				l2 = new SplitFindminStructureGabow<T>(l1.ackermann, l1.i);
				
				/*
				 * get the sublist-container of the sublist containing this
				 * element
				 */
				MyList.Container<SplitFindminStructureGabow<Superelement<T>>>
					containerToInsertAfter =
						superelement.containingSublist.
						containingContainerSublists;
				
				// call A_{i-1} to do split(e(x))
				SplitFindminStructureGabow<Superelement<T>> subl2 = null;
				SplitFindminStructureGabow<Superelement<T>> subl3 =
					superelement.elementInSublist.split();
				
				// update the pointers of the superelements of subl3 to subl3
				for (Element<Superelement<T>> e : subl3.elements) {
					Superelement<T> se = e.item;
					se.containingSublist = subl3;
				}
				
				// and then another split to make {e(x)} into a list
				if (superelement.elementInSublist.containingContainer.
						predecessor.item != null) {
					
					subl2 = superelement.elementInSublist.containingContainer.
						predecessor.item.split();
					
					/*
					 * update the pointers of the superelements of subl2 to
					 * subl2
					 */
					for (Element<Superelement<T>> e : subl2.elements) {
						Superelement<T> se = e.item;
						se.containingSublist = subl2;
					}
				}
				
				/* 
				 * inserted the (probably two) new sublist(s) into the list of
				 * sublists
				 */
				if (subl2 != null) {
					containerToInsertAfter = l1.sublists.insertAfter
						(containerToInsertAfter, subl2);
					subl2.containingContainerSublists = containerToInsertAfter;
					subl2.containingList = l1;
				}
				
				containerToInsertAfter = l1.sublists.insertAfter
					(containerToInsertAfter, subl3);
				subl3.containingContainerSublists = containerToInsertAfter;
				subl3.containingList = l1;
				
				if (this == superelement.last) {
					// look for the last singleton in front of this one
					MyList.Container<Element<T>> current =
						containingContainer.predecessor;
					
					while (current != l1.elements.leftSentinel) {
						Element<T> e = current.item;
						
						if (e.isSingleton() && e.superelement == null) {
							l2.singletonElements =
								l1.singletonElements.cutAfter
									(e.containingContainerSingletonElements);
							break;
						}
						
						current = current.predecessor;
					}
					
					if (current == l1.elements.leftSentinel) {
						l2.singletonElements = l1.singletonElements;
						l1.singletonElements = new MyList<Element<T>>();
					}
					
					/*
					 * look for the last singleton superelement in front of this
					 * singleton
					 */
					current = containingContainer.predecessor;
					
					while (current != l1.elements.leftSentinel) {
						Superelement<T> se = current.item.superelement;
						
						if (se != null && se.isSingleton()) {
							l2.singletonSuperelements =
								l1.singletonSuperelements.cutAfter
								(se.containingContainerSingletonSuperelements);
							break;
						}
						
						current = current.predecessor;
					}
					
					if (current == l1.elements.leftSentinel) {
						l2.singletonSuperelements = l1.singletonSuperelements;
						l1.singletonSuperelements =
							new MyList<Superelement<T>>();
					}
					
					// cut the list of sublists of this list in front of {e(x)}
					if (subl2 != null) {
						l2.sublists = l1.sublists.cutAfter
							(subl2.containingContainerSublists);
					} else {
						l2.sublists = l1.sublists.cutAfter
							(superelement.containingSublist.
								containingContainerSublists);
					}
				} else {
					// look for the position to insert the new left-overs at
					MyList.Container<Element<T>> lastSingletonElement = null;
					MyList.Container<Element<T>> current =
						containingContainer.predecessor;
					
					while (current != l1.elements.leftSentinel) {
						Element<T> e = current.item;
						
						if (e.isSingleton() && e.superelement == null) {
							lastSingletonElement =
								e.containingContainerSingletonElements;
							break;
						}
						
						current = current.predecessor;
					}
					
					if (current == l1.elements.leftSentinel) {
						lastSingletonElement = l1.singletonElements.
							leftSentinel;
					}
					
					/*
					 * look for the position to insert the new singleton
					 * superelements at
					 */
					MyList.Container<Superelement<T>>
						lastSingletonSuperelement = null;
					current = containingContainer.predecessor;
					
					while (current != l1.elements.leftSentinel) {
						Superelement<T> se = current.item.superelement;
						
						if (se != null && se.isSingleton()) {
							lastSingletonSuperelement =
								se.containingContainerSingletonSuperelements;
							break;
						}
						
						current = current.predecessor;
					}
					
					if (current == l1.elements.leftSentinel) {
						lastSingletonSuperelement =
							l1.singletonSuperelements.leftSentinel;
					}
					
					// look for the position to insert the new sublists at
					MyList.Container<SplitFindminStructureGabow
						<Superelement<T>>> lastSublist =
							superelement.containingSublist.
								containingContainerSublists.predecessor;
					
					/*
					 * prepare three new lists for doing initialize-head on the
					 * elements of e(x) up to and including x
					 */
					MyList<Element<T>> newSingletonElements =
						new MyList<Element<T>>();
					MyList<Superelement<T>> newSingletonSuperelements =
						new MyList<Superelement<T>>();
					MyList<SplitFindminStructureGabow<Superelement<T>>>
						newSublists =
							new MyList<SplitFindminStructureGabow
								<Superelement<T>>>();
					
					// remember the old superelement of this element
					Superelement<T> oldSuperelement = superelement;
					
					/*
					 * do initialize-head on the elements of e(x) up to and
					 * including x
					 */
					l1.initializeHead
						(superelement.first.containingContainer,
						 containingContainer,
						 newSingletonElements,
						 newSingletonSuperelements,
						 newSublists);
					
					// concat the three lists
					lastSingletonElement = l1.singletonElements.insertListAfter
						(lastSingletonElement, newSingletonElements);
					lastSingletonSuperelement =
						l1.singletonSuperelements.insertListAfter
							(lastSingletonSuperelement,
							 newSingletonSuperelements);
					lastSublist = l1.sublists.insertListAfter
						(lastSublist, newSublists);
					
					// cut the three lists of L1
					l2.singletonElements = l1.singletonElements.cutAfter
						(lastSingletonElement);
					l2.singletonSuperelements =
						l1.singletonSuperelements.cutAfter
							(lastSingletonSuperelement);
					l2.sublists = l1.sublists.cutAfter(lastSublist);
					
					// remove the list {e(x)} from the list of sublists of l2
					l2.sublists = l2.sublists.cutAfter
						(l2.sublists.leftSentinel.successor);
					
					/*
					 * prepare three new lists for doing initialize-tail on the
					 * remaining elements
					 */
					newSingletonElements = new MyList<Element<T>>();
					newSingletonSuperelements = new MyList<Superelement<T>>();
					newSublists = new MyList<SplitFindminStructureGabow
						<Superelement<T>>>();

					// do initialize-tail on the remaining elements
					l1.initializeTail
						(containingContainer.successor,
						 oldSuperelement.last.containingContainer,
						 newSingletonElements,
						 newSingletonSuperelements,
						 newSublists);
					
					// concat the three lists
					newSingletonElements.concat(l2.singletonElements);
					newSingletonSuperelements.concat(l2.singletonSuperelements);
					newSublists.concat(l2.sublists);
					
					l2.singletonElements = newSingletonElements;
					l2.singletonSuperelements = newSingletonSuperelements;
					l2.sublists = newSublists;
				}
			}
			
			// cut the list of elements of this list after this element
			l2.elements = l1.elements.cutAfter(containingContainer);
			
			// set the list containing L2, if it is a sublist
			l2.containingList = l1.containingList;
			
			// prepare computing the new costs of L1 and L2
			l1.cost = Double.POSITIVE_INFINITY;
			l2.cost = Double.POSITIVE_INFINITY;
			
			// compute the new cost of L1
			for (Element<T> e : l1.singletonElements) {
				l1.cost = Math.min(l1.cost, e.cost);
			}
			
			for (Superelement<T> se : l1.singletonSuperelements) {
				l1.cost = Math.min(l1.cost, se.cost);
			}
			
			for (SplitFindminStructureGabow<Superelement<T>> sublist :
				l1.sublists) {
				
				l1.cost = Math.min(l1.cost, sublist.cost);
			}
			
			// iterate all of the three lists of L2 and set all pointers to L2
			for (Element<T> e : l2.singletonElements) {
				e.containingList = l2;
				l2.cost = Math.min(l2.cost, e.cost);
			}
			
			for (Superelement<T> se : l2.singletonSuperelements) {
				se.containingList = l2;
				l2.cost = Math.min(l2.cost, se.cost);
			}
			
			for (SplitFindminStructureGabow<Superelement<T>> sublist :
				l2.sublists) {
				
				deepSetPointers(sublist, l2);
				l2.cost = Math.min(l2.cost, sublist.cost);
			}
			
			return l2;
		}
		
		/**
		 * Returns the cost of this element.
		 * 
		 * @return
		 * 		the cost of this element
		 */
		public double getCost() {
			return cost;
		}
		
		/**
		 * Returns the cost of the list containing this element.
		 * 
		 * @return
		 * 		the cost of the list containing this element
		 */
		public double getListCost() {
			if (isSingleton()) {
				if (containingList != null) {
					return containingList.cost;
				} else {
					return superelement.cost;
				}
			} else {
				return superelement.containingSublist.getCost();
			}
		}

		/**
		 * Sets the containing list of the passed <code>sublist</code> to
		 * <code>containingList</code>. After that, properly updates the
		 * pointers of all sublists contained by <code>sublist</code> the same
		 * way.
		 * 
		 * @param sublist
		 * 		the sublist contained by <code>containingList</code>
		 * @param containingList
		 * 		the list containing <code>sublist</code>
		 */
		@SuppressWarnings("unchecked")
		private void deepSetPointers
			(SplitFindminStructureGabow<Superelement<T>> sublist,
			 SplitFindminStructureGabow containingList) {
			
			sublist.containingList = containingList;
			
			for (SplitFindminStructureGabow subsublist : sublist.sublists) {
				deepSetPointers(subsublist, sublist);
			}
		}
	}
	
	/**
	 * A superelemtn of <i>Harold N. Gabow</i>'s split-findmin structure.
	 * 
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 09/17/09
	 * @param <T>
	 * 		the type of the item held by this superelement
	 */
	public static class Superelement<T> {
		/**
		 * The level of this superelement.
		 */
		private int level;
		
		/**
		 * The first element contained by this superelement.
		 */
		private Element<T> first;
		
		/**
		 * The last element contained by this superelement.
		 */
		private Element<T> last;
		
		/**
		 * The smallest cost of an element of this superelement.
		 */
		private double cost;
		
		/**
		 * The list containing this superelement, if this superelement is a
		 * singleton, and <code>null</code> otherwise.
		 */
		private SplitFindminStructureGabow<T> containingList;

		/**
		 * The container holding this superelement in the list of singleton
		 * superelements of the list containing this superelement.
		 */
		private MyList.Container<Superelement<T>>
			containingContainerSingletonSuperelements;
		
		/**
		 * The sublist element holding this superelement, if this superelement
		 * is no singleton.
		 */
		private Element<Superelement<T>> elementInSublist;
		
		/**
		 * The sublist holding this superelement, if this superelement is no
		 * singleton.
		 */
		private SplitFindminStructureGabow<Superelement<T>> containingSublist;
		
		
		/**
		 * Constructs a new superelement of the specified level.
		 * 
		 * @param level
		 * 		the level of the new superelement
		 */
		public Superelement(int level) {
			this.level = level;
		}
		
		
		/**
		 * Returns <code>true</code>, if this superelement is a singleton, and
		 * <code>false</code> otherwise.
		 * 
		 * @return
		 * 		<code>true</code>, if this superelement is a singleton, and<br>
		 * 		<code>false</code> otherwise
		 */
		public boolean isSingleton() {
			return containingList != null;
		}
	}
	
	
	/**
	 * An implementation of a doubly-linked list which allows cutting this list
	 * after a specified element, or concatenating two lists, in constant time.
	 *  
	 * @author
	 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
	 * @version
	 * 		1.0, 09/17/09
	 * @param <E>
	 * 		the type of the elements held by this list
	 */
	private static class MyList<E> implements Iterable<E> {
		/**
		 * The predecessor of the container holding the first element of this
		 * list.
		 */
		private Container<E> leftSentinel;
		
		/**
		 * The container holding the last element of this list.
		 */
		private Container<E> lastContainer;
		
		
		/**
		 * Constructs a new, empty list.
		 */
		public MyList() {
			leftSentinel = new Container<E>(null, null, null);
			lastContainer = leftSentinel;
		}
		
		/**
		 * Constructs a new list the passed first and last elements.
		 *  
		 * @param first
		 * 		the first element of the new list
		 * @param last
		 * 		the last element of the new list
		 */
		public MyList(Container<E> first, Container<E> last) {
			leftSentinel = new Container<E>(null, null, first);
			first.predecessor = leftSentinel;
			
			lastContainer = last;
		}
		
		
		/**
		 * Returns <code>true</code>, if this list is empty, and
		 * <code>false</code> otherwise.
		 * 
		 * @return
		 * 		<code>true</code>, if this list is empty, and<br>
		 * 		<code>false</code>, otherwise
		 */
		public boolean isEmpty() {
			return leftSentinel == lastContainer;
		}
		
		/**
		 * Adds the passed item to this list.
		 * 
		 * @param item
		 * 		the item to add
		 * @return
		 * 		the container holding the passed item
		 */
		public Container<E> add(E item) {
			lastContainer.insertAfter(item);
			lastContainer = lastContainer.successor;
			return lastContainer;
		}

		/**
		 * Adds the passed item to the front of list.
		 * 
		 * @param item
		 * 		the item to add
		 * @return
		 * 		the container holding the passed item
		 */
		public Container<E> addFirst(E item) {
			leftSentinel.insertAfter(item);
			
			
			if (leftSentinel == lastContainer) {
				lastContainer = leftSentinel.successor;
			}
			
			return leftSentinel.successor;
		}
		
		/**
		 * Removes the specified container from this list in constant time.
		 *  
		 * @param container
		 * 		the container to remove
		 * @return
		 * 		the predecessor of the removed container
		 */
		public Container<E> remove(Container<E> container) {
			if (container == lastContainer) {
				lastContainer = lastContainer.predecessor;
			}

			return container.remove();
		}

		/**
		 * Inserts the passed item after the specified container in constant
		 * time.
		 * 
		 * @param containerToInsertAfter
		 * 		the container the new item is to be inserted after
		 * @param item
		 * 		the item to insert
		 * @return
		 * 		the container holding the inserted item
		 */
		public Container<E> insertAfter(Container<E> containerToInsertAfter,
				E item) {
			
			containerToInsertAfter.insertAfter(item);
			
			if (containerToInsertAfter == lastContainer) {
				lastContainer = containerToInsertAfter.successor;
			}
			
			return containerToInsertAfter.successor;
		}

		/**
		 * Cuts this list after the specified container in constant time.
		 * 
		 * @param container
		 * 		the container to cut this list after
		 * @return
		 * 		the (second) list containing all elements after the specified
		 * 		container
		 */
		public MyList<E> cutAfter(Container<E> container) {
			if (container == lastContainer) {
				return new MyList<E>();
			} else {
				MyList<E> newList = new MyList<E>
					(container.successor, lastContainer);
				container.successor = null;
				lastContainer = container;
				return newList;
			}
		}
		
		/**
		 * Inserts the passed list into this one after the specified position.
		 * 
		 * @param position
		 * 		the position to insert the passed list after
		 * @param newElements
		 * 		the list to insert
		 * @return
		 * 		the last container of the inserted list
		 */
		public Container<E> insertListAfter(Container<E> position,
				MyList<E> newElements) {
			
			if (newElements.isEmpty()) {
				return position;
			} else {
				if (position.successor != null) {
					position.successor.predecessor = newElements.lastContainer;
					newElements.lastContainer.successor = position.successor;
				}
				
				position.successor = newElements.leftSentinel.successor;
				newElements.leftSentinel.successor.predecessor = position;
				
				if (position == lastContainer) {
					lastContainer = newElements.lastContainer;
				}
				
				return newElements.lastContainer;
			}
		}
		
		/**
		 * Concatenates this list and the passed one in constant time.
		 * 
		 * @param newElements
		 * 		the list to concatenate with this one
		 */
		public void concat(MyList<E> newElements) {
			if (!newElements.isEmpty()) {
				lastContainer.successor = newElements.leftSentinel.successor;
				newElements.leftSentinel.successor.predecessor = lastContainer;
				
				lastContainer = newElements.lastContainer;
			}
		}
		
		
		/**
		 * Returns an iterator over this list.
		 */
		@Override
		public Iterator<E> iterator() {
			return new Iterator<E>() {
				private Container<E> current = leftSentinel;
				
				@Override
				public boolean hasNext() {
					return current.successor != null;
				}

				@Override
				public E next() {
					if (current.successor == null) {
						throw new NoSuchElementException();
					}
					
					E item = current.successor.item;
					current = current.successor;
					return item;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}				
			};
		}
		
		/**
		 * A container holding an item in a doubly-linked list.
		 * 
		 * @author
		 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
		 * @version
		 * 		1.0, 09/17/09
		 * @param <E>
		 * 		the type of the elements held by this list
		 */
		private static class Container<E> {
			/**
			 * The item held by this container.
			 */
			private E item;
			
			/**
			 * The successor of this container.
			 */
			private Container<E> successor;
			
			/**
			 * The predecessor of this container.
			 */
			private Container<E> predecessor;
			
			
			/**
			 * Constructs a new container for the passed item with the specified
			 * predecessor and successor.
			 * 
			 * @param item
			 * 		the item to construct the new container for
			 * @param predecessor
			 * 		the predecessor of the new container
			 * @param successor
			 * 		the successor of the new container
			 */
			public Container(E item, Container<E> predecessor,
					Container<E> successor) {
				
				this.item = item;
				this.predecessor = predecessor;
				this.successor = successor;
			}
			
			
			/**
			 * Inserts a new container with the passed item after this one.
			 * 
			 * @param item
			 * 		the item to insert
			 * @return
			 * 		the new container
			 */
			public Container<E> insertAfter(E item) {
				Container<E> newContainer =
					new Container<E>(item, this, successor);
				
				if (successor != null) {
					successor.predecessor = newContainer;
				}
				
				successor = newContainer;
				return newContainer;
			}
			
			/**
			 * Removes this container from its containing list, updating the
			 * pointers of its predecessor and successor.
			 * 
			 * @return
			 * 		the predecessor of this container
			 */
			public Container<E> remove() {
				predecessor.successor = successor;
				
				if (successor != null) {
					successor.predecessor = predecessor;
				}
				
				return predecessor;
			}
		}
	}
}
