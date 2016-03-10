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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Validator;

/**
 * The <code>CompositeValidator</code> allows you to chain (compose) many
 * validators to validate one field. The contained validators may be required to
 * all validate the value to validate or it may be enough that one contained
 * validator validates the value. This behaviour is controlled by the modes
 * <code>AND</code> and <code>OR</code>.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CompositeValidator implements Validator {

    public enum CombinationMode {
        /**
         * The validators are combined with <code>AND</code> clause: validity of
         * the composite implies validity of the all validators it is composed
         * of must be valid.
         */
        AND,
        /**
         * The validators are combined with <code>OR</code> clause: validity of
         * the composite implies that some of validators it is composed of must
         * be valid.
         */
        OR;
    }

    /**
     * @deprecated As of 7.0, use {@link CombinationMode#AND} instead    
     */
    @Deprecated
    public static final CombinationMode MODE_AND = CombinationMode.AND;
    /**
     * @deprecated As of 7.0, use {@link CombinationMode#OR} instead    
     */
    @Deprecated
    public static final CombinationMode MODE_OR = CombinationMode.OR;

    private String errorMessage;

    /**
     * Operation mode.
     */
    private CombinationMode mode = CombinationMode.AND;

    /**
     * List of contained validators.
     */
    private final List<Validator> validators = new LinkedList<Validator>();

    /**
     * Construct a composite validator in <code>AND</code> mode without error
     * message.
     */
    public CompositeValidator() {
        this(CombinationMode.AND, "");
    }

    /**
     * Constructs a composite validator in given mode.
     * 
     * @param mode
     * @param errorMessage
     */
    public CompositeValidator(CombinationMode mode, String errorMessage) {
        setErrorMessage(errorMessage);
        setMode(mode);
    }

    /**
     * Validates the given value.
     * <p>
     * The value is valid, if:
     * <ul>
     * <li><code>MODE_AND</code>: All of the sub-validators are valid
     * <li><code>MODE_OR</code>: Any of the sub-validators are valid
     * </ul>
     * 
     * If the value is invalid, validation error is thrown. If the error message
     * is set (non-null), it is used. If the error message has not been set, the
     * first error occurred is thrown.
     * </p>
     * 
     * @param value
     *            the value to check.
     * @throws Validator.InvalidValueException
     *             if the value is not valid.
     */
    @Override
    public void validate(Object value) throws Validator.InvalidValueException {
        switch (mode) {
        case AND:
            for (Validator validator : validators) {
                validator.validate(value);
            }
            return;

        case OR:
            Validator.InvalidValueException first = null;
            for (Validator v : validators) {
                try {
                    v.validate(value);
                    return;
                } catch (final Validator.InvalidValueException e) {
                    if (first == null) {
                        first = e;
                    }
                }
            }
            if (first == null) {
                return;
            }
            final String em = getErrorMessage();
            if (em != null) {
                throw new Validator.InvalidValueException(em);
            } else {
                throw first;
            }
        }
    }

    /**
     * Gets the mode of the validator.
     * 
     * @return Operation mode of the validator: {@link CombinationMode#AND} or
     *         {@link CombinationMode#OR}.
     */
    public final CombinationMode getMode() {
        return mode;
    }

    /**
     * Sets the mode of the validator. The valid modes are:
     * <ul>
     * <li>{@link CombinationMode#AND} (default)
     * <li>{@link CombinationMode#OR}
     * </ul>
     * 
     * @param mode
     *            the mode to set.
     */
    public void setMode(CombinationMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException(
                    "The validator can't be set to null");
        }
        this.mode = mode;
    }

    /**
     * Gets the error message for the composite validator. If the error message
     * is null, original error messages of the sub-validators are used instead.
     */
    public String getErrorMessage() {
        if (errorMessage != null) {
            return errorMessage;
        }

        // TODO Return composite error message

        return null;
    }

    /**
     * Adds validator to the interface.
     * 
     * @param validator
     *            the Validator object which performs validation checks on this
     *            set of data field values.
     */
    public void addValidator(Validator validator) {
        if (validator == null) {
            return;
        }
        validators.add(validator);
    }

    /**
     * Removes a validator from the composite.
     * 
     * @param validator
     *            the Validator object which performs validation checks on this
     *            set of data field values.
     */
    public void removeValidator(Validator validator) {
        validators.remove(validator);
    }

    /**
     * Gets sub-validators by class.
     * 
     * <p>
     * If the component contains directly or recursively (it contains another
     * composite containing the validator) validators compatible with given type
     * they are returned. This only applies to <code>AND</code> mode composite
     * validators.
     * </p>
     * 
     * <p>
     * If the validator is in <code>OR</code> mode or does not contain any
     * validators of given type null is returned.
     * </p>
     * 
     * @param validatorType
     *            The type of validators to return
     * 
     * @return Collection<Validator> of validators compatible with given type
     *         that must apply or null if none found.
     */
    public Collection<Validator> getSubValidators(Class validatorType) {
        if (mode != CombinationMode.AND) {
            return null;
        }

        final HashSet<Validator> found = new HashSet<Validator>();
        for (Validator v : validators) {
            if (validatorType.isAssignableFrom(v.getClass())) {
                found.add(v);
            }
            if (v instanceof CompositeValidator
                    && ((CompositeValidator) v).getMode() == MODE_AND) {
                final Collection<Validator> c = ((CompositeValidator) v)
                        .getSubValidators(validatorType);
                if (c != null) {
                    found.addAll(c);
                }
            }
        }

        return found.isEmpty() ? null : found;
    }

    /**
     * Sets the message to be included in the exception in case the value does
     * not validate. The exception message is typically shown to the end user.
     * 
     * @param errorMessage
     *            the error message.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
