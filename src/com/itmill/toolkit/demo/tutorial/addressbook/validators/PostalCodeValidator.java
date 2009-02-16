package com.itmill.toolkit.demo.tutorial.addressbook.validators;

import com.itmill.toolkit.data.Validator;

public class PostalCodeValidator implements Validator {

	public boolean isValid(Object value) {
		if (value == null || !(value instanceof String)) {
			return false;
		}

		return ((String) value).matches("[1-9][0-9]{4}");
	}

	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException(
					"Postal code must be a five digit number and cannot start with a zero.");
		}
	}

}