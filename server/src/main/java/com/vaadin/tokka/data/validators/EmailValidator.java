/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tokka.data.validators;

/**
 * A string validator for e-mail addresses. The e-mail address syntax is not
 * complete according to RFC 822 but handles the vast majority of valid e-mail
 * addresses correctly.
 * 
 * @author Vaadin Ltd.
 * @since
 */
@SuppressWarnings("serial")
public class EmailValidator extends RegexpValidator {

    private static final String PATTERN = "^"
            + "([a-zA-Z0-9_\\.\\-+])+" // local
            + "@"
            + "[a-zA-Z0-9-.]+" // domain
            + "\\."
            + "[a-zA-Z0-9-]{2,}" // tld
            + "$";

    /**
     * Creates a validator for checking that a string is a syntactically valid
     * e-mail address.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     */
    public EmailValidator(String errorMessage) {
        super(errorMessage, PATTERN, true);
    }
}
