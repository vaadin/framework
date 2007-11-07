package com.itmill.toolkit.tests.magi;
import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.data.*;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import java.text.*;

/* Finnish Social Security Number input field that validates the value. */
public class SSNField extends CustomComponent implements Property.ValueChangeListener {
	OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
	TextField myfield;
	Label myerror;

	/** Validator for Finnish Social Security Number. */
	class SSNValidator implements Validator {

		/** The isValid() is simply a wrapper for the validate() method. */
		public boolean isValid(Object value) {
			try {
				validate(value);
			} catch (InvalidValueException e) {
				return false;
			}
			return true;
		}

		/** Validate the given SSN. */
		public void validate(Object value) throws InvalidValueException {
			String ssn = (String) value;
			if (ssn.length() != 11)
				throw new InvalidValueException("Invalid SSN length");
			
			String numbers = ssn.substring(0,6) + ssn.substring(7,10);
			int checksum = new Integer(numbers) % 31;
			if (!ssn.substring(10).equals("0123456789ABCDEFHJKLMNPRSTUVWXY".substring(checksum,checksum+1)))
				throw new InvalidValueException("Invalid SSN checksum");
		}
	}
	
	SSNField() {
		setCompositionRoot(layout);
		layout.setStyle("form");

		/* Create the text field for the SSN. */
		myfield = new TextField("Social Security Number");
		myfield.setColumns(11);
		myfield.setFormat(new MessageFormat("{0,number,##}"));
		
		/* Create and set the validator object for the field. */
		SSNValidator ssnvalidator = new SSNValidator ();
		myfield.addValidator(ssnvalidator);
		
		/* ValueChageEvent will be generated immediately when the component loses focus. */
		myfield.setImmediate(true);
		
		/* Listen for ValueChangeEvent events. */
		myfield.addListener(this);

		layout.addComponent(myfield);

		/* The field will have an error label, normally invisible. */
		myerror = new Label();
		layout.addComponent(myerror);
	}

	public void valueChange(ValueChangeEvent event) {
		try {
			/* Validate the field value. */
			myfield.validate();
			
			/* The value was correct. */
			myerror.setValue("Ok");
			myfield.setStyle("");
		} catch (Validator.InvalidValueException e) {
			/* Report the error message to the user. */
			myerror.setValue(e.getMessage());
			
			/* The CSS defines that text field with the "error" class will be colored red. */
			myfield.setStyle("error");
		}		
	}
}
