package com.itmill.toolkit.demo.tutorial.addressbook.validators;

import com.itmill.toolkit.data.Validator;

public class EmailValidator implements Validator {

	public boolean isValid(Object value) {
		if (value == null || !(value instanceof String)) {
			return false;
		}

		return ((String) value).matches(".+@.+\\..+");
	}

	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(
					"Email must contain '@' and have full domain.");
		}
	}

}
