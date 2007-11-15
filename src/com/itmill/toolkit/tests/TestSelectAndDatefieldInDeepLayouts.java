package com.itmill.toolkit.tests;

import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

import com.itmill.toolkit.ui.AbstractLayout;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;

/**
 * This test has a somewhat deep layout within one page. At the bottom, Select
 * and Datefield render their popups incorrectly. Popus tend to be "left behind"
 * from the actual components. When the page is even bigger or longer, the
 * popups are eventually rendered outside the visual parts of the page.
 * 
 * @author Ville Ingman
 * 
 */
public class TestSelectAndDatefieldInDeepLayouts extends CustomComponent {

	public TestSelectAndDatefieldInDeepLayouts() {
		OrderedLayout root = (OrderedLayout) getOrderedLayout();
		setCompositionRoot(root);

		root.addComponent(getSelect());
		root.addComponent(getDateField());
		root.addComponent(getSelect());
		root.addComponent(getDateField());

		Panel p1 = (Panel) getPanel();
		root.addComponent(p1);

		p1.addComponent(getSelect());
		p1.addComponent(getDateField());
		p1.addComponent(getSelect());
		p1.addComponent(getDateField());

		OrderedLayout l1 = (OrderedLayout) getOrderedLayout();
		p1.addComponent(l1);

		l1.addComponent(getSelect());
		l1.addComponent(getDateField());
		l1.addComponent(getSelect());
		l1.addComponent(getDateField());

		Panel p2 = (Panel) getPanel();
		l1.addComponent(p2);

		p2.addComponent(getSelect());
		p2.addComponent(getDateField());
		p2.addComponent(getSelect());
		p2.addComponent(getDateField());

	}

	AbstractLayout getOrderedLayout() {
		OrderedLayout l = new OrderedLayout();
		l.setCaption(getCaption("orderedlayout"));
		return l;
	}

	AbstractLayout getPanel() {
		Panel panel = new Panel();
		panel.setCaption(getCaption("panel"));
		return panel;
	}

	Component getSelect() {
		return new Select(getCaption("select"), getSelectOptions());
	}

	Component getDateField() {
		return new DateField(getCaption("datefield"));
	}

	private Collection getSelectOptions() {
		Collection opts = new Vector(3);
		opts.add(getCaption("opt 1"));
		opts.add(getCaption("opt 2"));
		opts.add(getCaption("opt 3"));
		return opts;
	}

	private String getCaption(String string) {
		return string + " " + UUID.randomUUID().toString().substring(0, 5);
	}

}
