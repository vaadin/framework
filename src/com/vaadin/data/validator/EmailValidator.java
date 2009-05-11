package com.vaadin.data.validator;

/**
 * String validator for e-mail addresses. The e-mail address syntax is not
 * complete according to RFC 822 but handles the vast majority of valid e-mail
 * addresses correctly.
 * 
 * See {@link com.vaadin.data.validator.AbstractStringValidator} for
 * more information.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.4
 */
@SuppressWarnings("serial")
public class EmailValidator extends RegexpValidator {

    /**
     * Creates a validator for checking that a string is a syntactically valid
     * e-mail address.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     */
    public EmailValidator(String errorMessage) {
        super(
                "^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$",
                true, errorMessage);
    }

}
