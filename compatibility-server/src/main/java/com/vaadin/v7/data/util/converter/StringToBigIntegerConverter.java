/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.data.util.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A converter that converts from {@link String} to {@link BigInteger} and back.
 * Uses the given locale and a {@link NumberFormat} instance for formatting and
 * parsing.
 * <p>
 * Leading and trailing white spaces are ignored when converting from a String.
 * </p>
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
@Deprecated
public class StringToBigIntegerConverter
        extends AbstractStringToNumberConverter<BigInteger> {

    @Override
    protected NumberFormat getFormat(Locale locale) {
        NumberFormat numberFormat = super.getFormat(locale);
        if (numberFormat instanceof DecimalFormat) {
            ((DecimalFormat) numberFormat).setParseBigDecimal(true);
        }

        return numberFormat;
    }

    @Override
    public BigInteger convertToModel(String value,
            Class<? extends BigInteger> targetType, Locale locale)
            throws ConversionException {

        BigDecimal bigDecimalValue = (BigDecimal) convertToNumber(value,
                BigDecimal.class, locale);

        return (bigDecimalValue != null) ? bigDecimalValue.toBigInteger()
                : null;
    }

    @Override
    public Class<BigInteger> getModelType() {
        return BigInteger.class;
    }
}
