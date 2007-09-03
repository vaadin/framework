package com.itmill.toolkit.ui.select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.Select;

public class ContainsFilter implements OptionFilter {
	private Select s;

	private ArrayList filteredItemsBuffer;

	public ContainsFilter(Select s) {
		this.s = s;
	}

	public List filter(String filterstring, int pageLength, int page) {
		// prefix MUST be in lowercase
		if (filterstring == null || "".equals(filterstring)) {
			this.filteredItemsBuffer = new ArrayList(s.getItemIds());
			return this.filteredItemsBuffer;

		} else if (s.getContainerDataSource() != null) {
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

				if (test.toLowerCase().indexOf(filterstring) != -1) {
					this.filteredItemsBuffer.add(id);
				}
			}
		}
		return this.filteredItemsBuffer;
	}

	public int getMatchCount() {
		return filteredItemsBuffer.size();
	}
}
