/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.magi;

import java.text.MessageFormat;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;

/* Finnish Social Security Number input field that validates the value. */
public class SSNField extends CustomComponent implements
        Property.ValueChangeListener {
    OrderedLayout layout = new FormLayout();
    // new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL); //;new FormLayout();
    TextField myfield;
    Label myerror;

    /** Validator for Finnish Social Security Number. */
    class SSNValidator implements Validator {

        /** The isValid() is simply a wrapper for the validate() method. */
        public boolean isValid(Object value) {
            try {
                validate(value);
            } catch (final InvalidValueException e) {
                return false;
            }
            return true;
        }

        /** Validate the given SSN. */
        public void validate(Object value) throws InvalidValueException {
            final String ssn = (String) value;
            if (ssn.length() == 0)
            	return;

            if (ssn.length() != 11)
                throw new InvalidValueException("Invalid SSN length");

            final String numbers = ssn.substring(0, 6) + ssn.substring(7, 10);
            final int checksum = new Integer(numbers).intValue() % 31;
            if (!ssn.substring(10).equals(
                    "0123456789ABCDEFHJKLMNPRSTUVWXY".substring(checksum,
                            checksum + 1))) {
                throw new InvalidValueException("Invalid SSN checksum");
            }
        }
    }

    SSNField() {
        setCompositionRoot(layout);
        layout.setOrientation(FormLayout.ORIENTATION_VERTICAL);

        /* Create the text field for the SSN. */
        myfield = new TextField("Social Security Number");
        myfield.setColumns(11);

        /* Create and set the validator object for the field. */
        final SSNValidator ssnvalidator = new SSNValidator();
        myfield.addValidator(ssnvalidator);

        /* ValueChageEvent will be generated immediately when the component
           loses focus. */
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
        } catch (final Validator.InvalidValueException e) {
            /* Report the error message to the user. */
            myerror.setValue(e.getMessage());
        }
    }
}
