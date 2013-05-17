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

import com.vaadin.sass.internal.parser.LexicalUnitImpl;

public class BinaryExpression {
    public Object leftOperand = null;
    public BinaryOperator operator = null;
    public Object rightOperand = null;

    public BinaryExpression(Object leftOperand, BinaryOperator operator,
            Object rightOperand) {
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    public LexicalUnitImpl eval() {
        LexicalUnitImpl leftValue = (leftOperand instanceof BinaryExpression) ? ((BinaryExpression) leftOperand)
                .eval() : (LexicalUnitImpl) leftOperand;
        LexicalUnitImpl rightValue = (rightOperand instanceof BinaryExpression) ? ((BinaryExpression) rightOperand)
                .eval() : (LexicalUnitImpl) rightOperand;
        return operator.eval(leftValue, rightValue);
    }

    @Override
    public String toString() {
        return "(" + leftOperand + " " + operator.type + " " + rightOperand
                + ")";
    }
}
