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
package com.vaadin.data.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String validator comparing the string against a Java regular expression. Both
 * complete matches and substring matches are supported.
 * 
 * <p>
 * For the Java regular expression syntax, see
 * {@link java.util.regex.Pattern#sum}
 * </p>
 * <p>
 * See {@link com.vaadin.data.validator.AbstractStringValidator} for more
 * information.
 * </p>
 * <p>
 * An empty string or a null is always accepted - use the required flag on
 * fields or a separate validator (or override {@link #isValidValue(String)}) to
 * fail on empty values.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 5.4
 */
@SuppressWarnings("serial")
public class RegexpValidator extends AbstractStringValidator {

    private Pattern pattern;
    private boolean complete;
    private transient Matcher matcher = null;

    /**
     * Creates a validator for checking that the regular expression matches the
     * complete string to validate.
     * 
     * @param regexp
     *            a Java regular expression
     * @param errorMessage
     *            the message to display in case the value does not validate.
     */
    public RegexpValidator(String regexp, String errorMessage) {
        this(regexp, true, errorMessage);
    }

    /**
     * Creates a validator for checking that the regular expression matches the
     * string to validate.
     * 
     * @param regexp
     *            a Java regular expression
     * @param complete
     *            true to use check for a complete match, false to look for a
     *            matching substring
     * @param errorMessage
     *            the message to display in case the value does not validate.
     */
    public RegexpValidator(String regexp, boolean complete, String errorMessage) {
        super(errorMessage);
        pattern = Pattern.compile(regexp);
        this.complete = complete;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.validator.AbstractValidator#isValidValue(java.lang.Object
     * )
     */
    @Override
    protected boolean isValidValue(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        if (complete) {
            return getMatcher(value).matches();
        } else {
            return getMatcher(value).find();
        }
    }

    /**
     * Get a new or reused matcher for the pattern
     * 
     * @param value
     *            the string to find matches in
     * @return Matcher for the string
     */
    private Matcher getMatcher(String value) {
        if (matcher == null) {
            matcher = pattern.matcher(value);
        } else {
            matcher.reset(value);
        }
        return matcher;
    }

}
