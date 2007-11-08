package com.itmill.toolkit.tests;

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

/**
 * 
 * @author IT Mill Ltd.
 */
public class TestDateField extends CustomComponent {

	OrderedLayout main = new OrderedLayout();

	DateField df;

	public TestDateField() {
		setCompositionRoot(main);
		createNewView();
	}

	public void createNewView() {
		main.removeAllComponents();
		main.addComponent(new Label("DateField"));

		df = new DateField();
		main.addComponent(df);

		ErrorMessage errorMsg = new UserError("User error " + df);
		df.setCaption("DateField caption " + df);
		df.setDescription("DateField description " + df);
		df.setComponentError(errorMsg);
		df.setImmediate(true);
		// FIXME: bug #1138 this makes datefield to render with unknown component (UIDL tree debug)
		df.addStyleName("thisShouldBeHarmless");
	}

	public void attach() {
		ClassResource res = new ClassResource("m.gif", super.getApplication());
		df.setIcon(res);
		super.attach();
	}

}
