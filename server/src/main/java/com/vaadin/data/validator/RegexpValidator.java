/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;

/**
 * A string validator comparing the string against a Java regular expression.
 * Both complete matches and substring matches are supported.
 * <p>
 * For the Java regular expression syntax, see {@link java.util.regex.Pattern}.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
@SuppressWarnings("serial")
public class RegexpValidator extends AbstractValidator<String> {

    private Pattern pattern;
    private boolean complete;
    private transient Matcher matcher = null;

    /**
     * Creates a validator for checking that the regular expression matches the
     * complete string to validate.
     *
     * @param errorMessage
     *            the message to display in case the value does not validate.
     * @param regexp
     *            a Java regular expression
     */
    public RegexpValidator(String errorMessage, String regexp) {
        this(errorMessage, regexp, true);
    }

    /**
     * Creates a validator for checking that the regular expression matches the
     * string to validate.
     *
     * @param errorMessage
     *            the message to display in case the value does not validate.
     * @param regexp
     *            a Java regular expression
     * @param complete
     *            true to use check for a complete match, false to look for a
     *            matching substring
     *
     */
    public RegexpValidator(String errorMessage, String regexp,
            boolean complete) {
        super(errorMessage);
        pattern = Pattern.compile(regexp);
        this.complete = complete;
    }

    @Override
    public ValidationResult apply(String value, ValueContext context) {
        return toResult(value, isValid(value));
    }

    @Override
    public String toString() {
        return "RegexpValidator[" + pattern + "]";
    }

    /**
     * Returns whether the given string matches the regular expression.
     *
     * @param value
     *            the string to match
     * @return true if the string matched, false otherwise
     */
    protected boolean isValid(String value) {
        if (value == null) {
            return true;
        }
        if (complete) {
            return getMatcher(value).matches();
        } else {
            return getMatcher(value).find();
        }
    }

    /**
     * Returns a new or reused matcher for the pattern.
     *
     * @param value
     *            the string to find matches in
     * @return a matcher for the string
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
