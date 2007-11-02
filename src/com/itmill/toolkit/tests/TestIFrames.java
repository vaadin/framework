package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class TestIFrames extends CustomComponent {

	OrderedLayout main = new OrderedLayout();

	public TestIFrames() {
		setCompositionRoot(main);
		createNewView();
	}

	public void createNewView() {
		main.removeAllComponents();
		main.addComponent(createEmbedded("../Reservr/"));
		main.addComponent(createEmbedded("../colorpicker"));
		// main.addComponent(createEmbedded("../TestForNativeWindowing"));
		main
				.addComponent(createEmbedded("http://toolkit.itmill.com/demo/FeaturesApplication"));
		main
				.addComponent(createEmbedded("http://toolkit.itmill.com/demo/TableDemo"));
	}

	private Label createEmbedded(String URL) {
		int width = 600;
		int height = 250;
		String iFrame = "<iframe height=\"" + height + "\" width=\"" + width
				+ "\" src=\"" + URL + "\" />";
		return new Label(iFrame, Label.CONTENT_XHTML);
	}

}
