package de.unikiel.npr.thorup.ds;

import java.util.Hashtable;

/**
 * A table that allows accessing the values of <i>Ackermann</i>'s function
 * <i>A(i, j)</i> that are <i>n</i> or less for a given <i>n</i>.
 * 
 * @author
 * 		<a href="mailto:npr@informatik.uni-kiel.de">Nick Pr&uuml;hs</a>
 * @version
 * 		1.0, 09/17/09
 */
public class AckermannTable {
	/**
	 * The table containing the values of <i>Ackermann</i>'s function.
	 */
	private Hashtable<Integer, Hashtable<Integer, Integer>> table;

	
	/**
	 * Constructs a new table, computing all values of <i>Ackermann</i>'s
	 * function <i>A(i, j)</i> that are <i>n</i> or less.
	 * 
	 * @param n
	 * 		the maximum value of the new table
	 */
	public AckermannTable(int n) {
		// construct new table
		table = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		
		// set first value
		int i = 1;
		int j = 2;
		setValue(1, 1, 2);
		
		while (true) {
			int newValue = -1;
			
			// compute next entry
			if (i == 1) {
				newValue = getValue(i, j - 1) * 2;
			} else {
				newValue = getValue(i - 1, getValue(i, j - 1));
			}
			
			if (newValue > n || newValue == -1) {
				if (j == 1) {
					// no single entry in this row - return
					return;
				} else {
					// go to the next row
					i++;
					j = 1;
				}
			} else {
				// save the computed value
				setValue(i, j, newValue);
				j++;
			}
		}
	}
	
	/**
	 * Returns the value of <i>Ackermann</i>'s function <i>A(i, j)</i>, if it
	 * is <i>n</i> or less, and <code>-1</code> otherwise.
	 * 
	 * @param i
	 * 		the first parameter for <i>Ackermann</i>'s function
	 * @param j
	 * 		the second parameter for <i>Ackermann</i>'s function
	 * @return
	 * 		<i>A(i, j)</i>
	 */
	public int getValue(int i, int j) {
		if (j == 0) {
			return 2;
		} else {
			if (table.containsKey(i)) {
				Hashtable<Integer, Integer> rowI = table.get(i);
				
				if (rowI.containsKey(j)) {
					return rowI.get(j);
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		}
	}
	
	/**
	 * Returns the inverse value of <i>Ackermann</i>'s function <i>A(m, n)</i>.
	 * 
	 * @param m
	 * 		the first parameter for the inverse <i>Ackermann</i>'s function
	 * @param n
	 * 		the second parameter for the inverse <i>Ackermann</i>'s function
	 * @return
	 * 		the inverse of <i>A(m, n)</i>
	 */
	public int getInverse(int m, int n) {
		 if (n >= 4) {
				int j = 0;
				
				while (2 * getValue(m, j) <= n && getValue(m, j) != -1) {
					j++;
				}
				
				return j - 1;
		} else if (m >= n) {
				int i = 1;
				
				while (getValue(i, (int)Math.floor(m / n)) != -1) {
					i++;
				}
				
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Adds the passed value of <i>Ackermann</i>'s function <i>A(i, j)</i> to
	 * this table.
	 * 
	 * @param i
	 * 		the first parameter for <i>Ackermann</i>'s function
	 * @param j
	 * 		the second parameter for <i>Ackermann</i>'s function
	 * @param value
	 * 		<i>A(i, j)</i>
	 */
	private void setValue(int i, int j, int value) {
		if (!table.containsKey(i)) {
			table.put(i, new Hashtable<Integer, Integer>());
		}
		
		Hashtable<Integer, Integer> rowI = table.get(i);
		rowI.put(j, value);
	}
}
