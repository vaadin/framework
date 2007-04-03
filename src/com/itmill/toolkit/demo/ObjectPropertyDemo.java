package com.itmill.toolkit.demo;

import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.ui.*;

public class ObjectPropertyDemo extends com.itmill.toolkit.Application {

	private TextField textField = null;

	private Float floatValue = new Float(1.0f);

	private ObjectProperty ob = null;

	private Button storeButton = new Button("Set", this,
			"storeButtonClickedEvent");

	private Button commitButton = new Button("Commit", this,
			"commitButtonClickedEvent");

	private Button discardButton = new Button("Commit", this,
			"discardButtonClickedEvent");

	public void init() {

		Window main = new Window("Test window");
		setMainWindow(main);

		setTheme("corporate");

		// Textfield
		ob = new ObjectProperty(floatValue);
		textField = new TextField("Textfield that uses ObjectProperty", ob);
		ob.setReadOnly(false);
		textField.setImmediate(true);
		textField.setInvalidCommitted(true);

		main.addComponent(textField);
		main.addComponent(storeButton);
		main.addComponent(commitButton);
		main.addComponent(discardButton);
	}

	public void storeButtonClickedEvent() {
		printValues();
	}

	public void commitButtonClickedEvent() {
		textField.commit();
		printValues();
	}

	public void discardButtonClickedEvent() {
		textField.discard();
		printValues();
	}
	
	private void printValues() {
		System.out.println("textField.getValue()=" + textField.getValue());
		System.out.println("floatValue=" + floatValue);
	}

}
