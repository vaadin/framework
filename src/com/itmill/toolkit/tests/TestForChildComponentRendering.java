package com.itmill.toolkit.tests;

import java.util.ArrayList;
import java.util.Iterator;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;

/**
 * 
 * This Component contains some simple test to see that component updates its
 * contents propertly.
 * 
 * @author IT Mill Ltd.
 */
public class TestForChildComponentRendering extends CustomComponent {

	private OrderedLayout main;

	public TestForChildComponentRendering() {

		main = new OrderedLayout();
		setCompositionRoot(main);
		createNewView();
	}

	public void createNewView() {
		main.removeAllComponents();
		main.addComponent(new Label("SDFGFHFHGJGFDSDSSSGFDD"));

		Link l = new Link();
		l.setCaption("Siirry ITMILLIIN");
		l.setResource(new ExternalResource("http://www.itmill.com/"));
		l.setTargetHeight(200);
		l.setTargetWidth(500);
		l.setTargetBorder(Link.TARGET_BORDER_MINIMAL);
		main.addComponent(l);

		Select se = new Select();
		se.setCaption("VALITSET TÄSTÄ");
		se.addItem("valinta1");
		se.addItem("Valinta 2");

		Button b = new Button("refresh view", this, "createNewView");
		main.addComponent(b);

		b = new Button("reorder view", this, "randomReorder");
		main.addComponent(b);

		b = new Button("remove randomly one component", this,
				"removeRandomComponent");
		main.addComponent(b);

	}

	public void randomReorder() {
		Iterator it = main.getComponentIterator();
		ArrayList components = new ArrayList();
		while (it.hasNext())
			components.add(it.next());

		OrderedLayout v = main;
		v.removeAllComponents();

		for (int i = components.size(); i > 0; i--) {
			int index = (int) (Math.random() * i);
			v.addComponent((Component) components.get(index));
			components.remove(index);
		}
	}

	public void removeRandomComponent() {
		Iterator it = main.getComponentIterator();
		ArrayList components = new ArrayList();
		while (it.hasNext())
			components.add(it.next());
		int size = components.size();
		int index = (int) (Math.random() * size);
		main.removeComponent((Component) components.get(index));

	}

}
