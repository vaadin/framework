package com.itmill.toolkit.tests;

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class TestCaptionWrapper extends CustomComponent {

	OrderedLayout main = new OrderedLayout();
	Label label = new Label("iconLabel");

	public TestCaptionWrapper() {
		setCompositionRoot(main);
	}

	public void attach() {
		super.attach();
		createNewView();
	}

	public void createNewView() {
		main.removeAllComponents();

		// Add resource for label (icon)
		ClassResource res = new ClassResource("m.gif", this.getApplication());
		label.setIcon(res);

		// Add error message for label
		ErrorMessage errorMsg = new UserError("User error");
		label.setComponentError(errorMsg);

		// Set other common properties for label
		label.setDescription("iconLabel description");
		label.setCaption("iconLabel caption");
		label.setValue("iconLabel value");

		main.addComponent(label);
	}

}
