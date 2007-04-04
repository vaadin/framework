package com.itmill.toolkit.demo;

import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.ui.*;

public class ObjectPropertyDemo extends com.itmill.toolkit.Application {

	private TextField floatTextField = null;
	
	private Label floatLabel = new Label();

	private Float floatObject = new Float(1.0f);

	private ObjectProperty floatObjectProperty = null;

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

		// Textfield that uses ObjectProperty
		floatObjectProperty = new ObjectProperty(floatObject);
		floatTextField = new TextField("floatTextField (uses ObjectProperty)", floatObjectProperty);
		floatObjectProperty.setReadOnly(false);
		// needed because of bug in variable change handling?
		// change textfield value and unfocus it to change textfields value
		// succesfully
		floatTextField.setImmediate(true);
		floatTextField.setInvalidCommitted(true);
		
		floatLabel.setCaption("floatObject value");

		main.addComponent(floatTextField);
		main.addComponent(storeButton);
		main.addComponent(commitButton);
		main.addComponent(discardButton);
		main.addComponent(floatLabel);
	}

	public void storeButtonClickedEvent() {
		printValues();
	}

	public void commitButtonClickedEvent() {
		floatTextField.commit();
		printValues();
	}

	public void discardButtonClickedEvent() {
		floatTextField.discard();
		printValues();
	}

	private void printValues() {
		System.out.println("textField.getValue()=" + floatTextField.getValue());
		System.out.println("floatValue=" + floatObject);
		floatLabel.setValue(floatObject);
	}

}
