package com.itmill.toolkit.tests;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TestForWindowOpen extends CustomComponent {

	public TestForWindowOpen() {

		OrderedLayout main = new OrderedLayout();
		setCompositionRoot(main);

		main.addComponent(new Button("Open in this window",
				new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						ExternalResource r = new ExternalResource(
								"http://www.google.com");
						getApplication().getMainWindow().open(r);

					}

				}));

		main.addComponent(new Button("Open in target \"asd\"",
				new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						ExternalResource r = new ExternalResource(
								"http://www.google.com");
						getApplication().getMainWindow().open(r, "asd");

					}

				}));

		main.addComponent(new Button("Open in target \"foo\"",
				new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						ExternalResource r = new ExternalResource(
								"http://www.google.com");
						getApplication().getMainWindow().open(r, "foo");

					}

				}));

	}

}
