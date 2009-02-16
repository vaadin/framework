package com.itmill.toolkit.demo.tutorial.addressbook.data;

public class SearchFilter {

	private final String term;
	private final Object propertyId;
	private String searchName;

	public SearchFilter(Object propertyId, String searchTerm, String name) {
		this.propertyId = propertyId;
		this.term = searchTerm;
		this.searchName = name;
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @return the propertyId
	 */
	public Object getPropertyId() {
		return propertyId;
	}

	/**
	 * @return the name of the search
	 */
	public String getSearchName() {
		return searchName;
	}

	@Override
	public String toString() {
		return getSearchName();
	}

}
