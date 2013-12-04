/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.sass.internal.expression;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.internal.expression.exception.ArithmeticException;
import com.vaadin.sass.internal.expression.exception.IncompatibleUnitsException;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;

public class ArithmeticExpressionEvaluatorTest {
    private ArithmeticExpressionEvaluator evaluator = new ArithmeticExpressionEvaluator();

    @Test
    public void testPrecedenceSameAsAppearOrder() {
        // 2 * 3 - 4 = 2
        LexicalUnitImpl operand2 = LexicalUnitImpl.createInteger(0, 0, null, 2);
        LexicalUnitImpl operatorMultiply = LexicalUnitImpl.createMultiply(0, 0,
                operand2);
        LexicalUnitImpl operand3 = LexicalUnitImpl.createInteger(0, 0,
                operatorMultiply, 3);
        LexicalUnitImpl operatorMinus = LexicalUnitImpl.createMinus(0, 0,
                operand3);
        LexicalUnitImpl operand4 = LexicalUnitImpl.createInteger(0, 0,
                operatorMinus, 4);
        LexicalUnitImpl result = evaluator.evaluate(operand2);
        Assert.assertEquals(2, result.getIntegerValue());
    }

    @Test
    public void testPrecedenceDifferFromAppearOrder() {
        // 2 - 3 * 4 = -10
        LexicalUnitImpl operand2 = LexicalUnitImpl.createInteger(0, 0, null, 2);
        LexicalUnitImpl operatorMinus = LexicalUnitImpl.createMinus(0, 0,
                operand2);
        LexicalUnitImpl operand3 = LexicalUnitImpl.createInteger(0, 0,
                operatorMinus, 3);
        LexicalUnitImpl operatorMultiply = LexicalUnitImpl.createMultiply(0, 0,
                operand3);
        LexicalUnitImpl operand4 = LexicalUnitImpl.createInteger(0, 0,
                operatorMultiply, 4);
        LexicalUnitImpl result = evaluator.evaluate(operand2);
        Assert.assertEquals(-10, result.getIntegerValue());
    }

    @Test(expected = IncompatibleUnitsException.class)
    public void testIncompatibleUnit() {
        // 2cm - 3px
        LexicalUnitImpl operand2 = LexicalUnitImpl.createCM(0, 0, null, 2);
        LexicalUnitImpl operatorMinus = LexicalUnitImpl.createMinus(0, 0,
                operand2);
        LexicalUnitImpl operand3 = LexicalUnitImpl.createPX(0, 0,
                operatorMinus, 3);
        evaluator.evaluate(operand2);
    }

    @Test
    public void testMultiplyWithUnitInfirstOperand() {
        // 2cm * 3 = 6cm
        LexicalUnitImpl operand2cm = LexicalUnitImpl.createCM(0, 0, null, 2);
        LexicalUnitImpl operatorMultiply = LexicalUnitImpl.createMultiply(0, 0,
                operand2cm);
        LexicalUnitImpl operand3 = LexicalUnitImpl.createInteger(0, 0,
                operatorMultiply, 3);
        LexicalUnitImpl result = evaluator.evaluate(operand2cm);
        Assert.assertEquals(6, result.getIntegerValue());
        Assert.assertEquals(LexicalUnit.SAC_CENTIMETER,
                result.getLexicalUnitType());
    }

    @Test
    public void testMultiplyWithUnitInSecondOperand() {
        // 2 * 3cm = 6cm
        LexicalUnitImpl operand2 = LexicalUnitImpl.createInteger(0, 0, null, 2);
        LexicalUnitImpl operatorMultiply = LexicalUnitImpl.createMultiply(0, 0,
                operand2);
        LexicalUnitImpl operand3cm = LexicalUnitImpl.createCM(0, 0,
                operatorMultiply, 3);
        LexicalUnitImpl result = evaluator.evaluate(operand2);
        Assert.assertEquals(6, result.getIntegerValue());
        Assert.assertEquals(LexicalUnit.SAC_CENTIMETER,
                result.getLexicalUnitType());
    }

    @Test
    public void testDivideWithSameUnit() {
        // 4cm / 2cm = 2
        LexicalUnitImpl operand4cm = LexicalUnitImpl.createCM(0, 0, null, 4);
        LexicalUnitImpl operatorDivide = LexicalUnitImpl.createSlash(0, 0,
                operand4cm);
        LexicalUnitImpl operand2cm = LexicalUnitImpl.createCM(0, 0,
                operatorDivide, 2);
        LexicalUnitImpl result = evaluator.evaluate(operand4cm);
        Assert.assertEquals(2, result.getIntegerValue());
        Assert.assertEquals(LexicalUnit.SAC_REAL, result.getLexicalUnitType());
    }

    @Test
    public void testDivideDenominatorWithoutUnit() {
        // 4cm / 2 = 2cm
        LexicalUnitImpl operand4cm = LexicalUnitImpl.createCM(0, 0, null, 4);
        LexicalUnitImpl operatorDivide = LexicalUnitImpl.createSlash(0, 0,
                operand4cm);
        LexicalUnitImpl operand2 = LexicalUnitImpl.createInteger(0, 0,
                operatorDivide, 2);
        LexicalUnitImpl result = evaluator.evaluate(operand4cm);
        Assert.assertEquals(2, result.getIntegerValue());
        Assert.assertEquals(LexicalUnit.SAC_CENTIMETER,
                result.getLexicalUnitType());
    }

    @Test(expected = ArithmeticException.class)
    public void testNonExistingSignal() {
        LexicalUnitImpl operand2Integer = LexicalUnitImpl.createInteger(2, 3,
                null, 2);
        LexicalUnitImpl operatorComma = LexicalUnitImpl.createComma(2, 3,
                operand2Integer);
        LexicalUnitImpl operand3Integer = LexicalUnitImpl.createInteger(2, 3,
                operatorComma, 3);
        LexicalUnitImpl result = evaluator.evaluate(operand2Integer);
    }
}
