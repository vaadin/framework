package com.itmill.toolkit.ui.select;

import java.util.List;

public interface OptionFilter {
	/**
	 * 
	 * @param filterstring
	 *            string to use in filtering
	 * @return List of filtered item id's
	 */
	public List filter(String filterstring, int pageLength, int page);

	/**
	 * Returns total matches in last filtering process
	 * 
	 * @return
	 */
	public int getMatchCount();
}
