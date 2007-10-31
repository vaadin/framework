package com.itmill.toolkit.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TwinColSelect;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestForMultipleStyleNames extends CustomComponent implements
		ValueChangeListener {

	private final OrderedLayout main = new OrderedLayout();

	private Label l;

	private final TwinColSelect s = new TwinColSelect();

	private ArrayList styleNames2;

	public TestForMultipleStyleNames() {
		setCompositionRoot(this.main);
		createNewView();
	}

	public void createNewView() {
		this.main.removeAllComponents();
		this.main.addComponent(new Label(
				"TK5 supports multiple stylenames for components."));

		this.styleNames2 = new ArrayList();

		this.styleNames2.add("red");
		this.styleNames2.add("bold");
		this.styleNames2.add("italic");

		this.s.setContainerDataSource(new IndexedContainer(this.styleNames2));
		this.s.addListener(this);
		this.s.setImmediate(true);
		this.main.addComponent(this.s);

		this.l = new Label("Test labele");
		this.main.addComponent(this.l);

	}

	public void valueChange(ValueChangeEvent event) {

		String currentStyle = this.l.getStyle();
		String[] tmp = currentStyle.split(" ");
		ArrayList curStyles = new ArrayList();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i] != "") {
				curStyles.add(tmp[i]);
			}
		}

		Collection styles = (Collection) this.s.getValue();

		for (Iterator iterator = styles.iterator(); iterator.hasNext();) {
			String styleName = (String) iterator.next();
			if (curStyles.contains(styleName)) {
				// already added
				curStyles.remove(styleName);
			} else {
				this.l.addStyleName(styleName);
			}
		}
		for (Iterator iterator2 = curStyles.iterator(); iterator2.hasNext();) {
			String object = (String) iterator2.next();
			this.l.removeStyleName(object);
		}
	}

}
