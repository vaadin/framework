package com.itmill.toolkit.ui.select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.Select;

public class StartsWithFilter implements OptionFilter {
	private Select s;

	public StartsWithFilter(Select s) {
		this.s = s;
	}

	ArrayList filteredItemsBuffer;

	private String prevFilter;

	public List filter(String filterstring, int pageLength, int page) {
		if(filterstring == null) {
			filterstring = "";
		}
		if(this.prevFilter != filterstring || filteredItemsBuffer == null) {
			if ("".equals(filterstring)) {
				this.filteredItemsBuffer = new ArrayList(s.getItemIds());
			} else if (s.getContainerDataSource() != null) {
				// prefix MUST be in lowercase
				filterstring = filterstring.toLowerCase();

				// all items will be iterated and tested.
				// SLOW when there are lot of items.
				this.filteredItemsBuffer = new ArrayList();
				for (Iterator iter = s.getItemIds().iterator(); iter.hasNext();) {
					Object id = iter.next();

					Item item = s.getItem(id);
					String test = "";
					if (s.getItemCaptionMode() == Select.ITEM_CAPTION_MODE_PROPERTY)
						test = item.getItemProperty(s.getItemCaptionPropertyId())
								.getValue().toString().trim();
					else
						test = String.valueOf(id);

					if (test.toLowerCase().startsWith(filterstring)) {
						this.filteredItemsBuffer.add(id);
					}
				}
			}
		}
		
		prevFilter = filterstring;
		
		if(filteredItemsBuffer.size() > pageLength) {
			int first = page*pageLength;
			int last = first + pageLength;
			if(filteredItemsBuffer.size() < last) {
				last = filteredItemsBuffer.size();
			}
			return filteredItemsBuffer.subList(first, last);
		} else {
			return filteredItemsBuffer;
		}
	}

	public int getMatchCount() {
		return filteredItemsBuffer.size();
	}
}
