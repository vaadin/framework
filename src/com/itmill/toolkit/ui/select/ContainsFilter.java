package com.itmill.toolkit.ui.select;

import java.util.ArrayList;
import java.util.Iterator;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.Select;

public class ContainsFilter implements OptionFilter {
	private Select s;
	
	private ArrayList filteredItemsBuffer;

	public ContainsFilter(Select s) {
		this.s = s;
	}


	public ArrayList filter(String filterstring) {
		// prefix MUST be in lowercase
		if ("".equals(filterstring)) {
			this.filteredItemsBuffer = new ArrayList(s.getItemIds());
			return this.filteredItemsBuffer;

		} else if (s.getContainerDataSource() != null) { 
			// all items will be iterated and tested.
			// SLOW when there are lot of items.
			this.filteredItemsBuffer = new ArrayList();
			for (Iterator iter = s.getItemIds().iterator(); iter
					.hasNext();) {
				Object id = iter.next();

				Item item = s.getItem(id);
				String test = "";
				if (s.getItemCaptionMode() == Select.ITEM_CAPTION_MODE_PROPERTY)
					test = item.getItemProperty(s.getItemCaptionPropertyId())
							.getValue().toString().trim();
				else
					test = String.valueOf(id);

				if (test.toLowerCase().contains(filterstring)) {
					this.filteredItemsBuffer.add(id);
				}
			}
		}
		return this.filteredItemsBuffer;		
	}
}
