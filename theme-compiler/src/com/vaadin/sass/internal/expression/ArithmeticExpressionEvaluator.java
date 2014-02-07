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

import static com.vaadin.sass.internal.parser.SCSSLexicalUnit.SCSS_VARIABLE;

import java.util.Stack;

import com.vaadin.sass.internal.expression.exception.ArithmeticException;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.parser.SCSSLexicalUnit;

public class ArithmeticExpressionEvaluator {
    private static ArithmeticExpressionEvaluator instance;

    public static ArithmeticExpressionEvaluator get() {
        if (instance == null) {
            instance = new ArithmeticExpressionEvaluator();
        }
        return instance;
    }

    private void createNewOperand(BinaryOperator operator,
            Stack<Object> operands) {
        Object rightOperand = operands.pop();
        operands.push(new BinaryExpression(operands.pop(), operator,
                rightOperand));
    }

    public boolean containsArithmeticalOperator(LexicalUnitImpl term) {
        LexicalUnitImpl current = term;
        while (current != null) {
            for (BinaryOperator operator : BinaryOperator.values()) {
                /*
                 * '/' is treated as an arithmetical operator when one of its
                 * operands is Variable, or there is another binary operator.
                 * Otherwise, '/' is treated as a CSS operator.
                 */
                if (current.getLexicalUnitType() == operator.type) {
                    if (current.getLexicalUnitType() != BinaryOperator.DIV.type) {
                        return true;
                    } else {
                        if (current.getPreviousLexicalUnit()
                                .getLexicalUnitType() == SCSS_VARIABLE
                                || current.getNextLexicalUnit()
                                        .getLexicalUnitType() == SCSS_VARIABLE) {
                            return true;
                        }
                    }
                }
            }
            current = current.getNextLexicalUnit();
        }
        return false;
    }

    private Object createExpression(LexicalUnitImpl term) {
        LexicalUnitImpl current = term;
        boolean afterOperand = false;
        Stack<Object> operands = new Stack<Object>();
        Stack<Object> operators = new Stack<Object>();
        inputTermLoop: while (current != null) {
            if (afterOperand) {
                if (current.getLexicalUnitType() == SCSSLexicalUnit.SCSS_OPERATOR_RIGHT_PAREN) {
                    Object operator = null;
                    while (!operators.isEmpty()
                            && ((operator = operators.pop()) != Parentheses.LEFT)) {
                        createNewOperand((BinaryOperator) operator, operands);
                    }
                    current = current.getNextLexicalUnit();
                    continue;
                }
                afterOperand = false;
                for (BinaryOperator operator : BinaryOperator.values()) {
                    if (current.getLexicalUnitType() == operator.type) {
                        while (!operators.isEmpty()
                                && (operators.peek() != Parentheses.LEFT)
                                && (((BinaryOperator) operators.peek()).precedence >= operator.precedence)) {
                            createNewOperand((BinaryOperator) operators.pop(),
                                    operands);
                        }
                        operators.push(operator);

                        current = current.getNextLexicalUnit();
                        continue inputTermLoop;
                    }
                }
                throw new ArithmeticException("Illegal arithmetic expression",
                        term);
            }
            if (current.getLexicalUnitType() == SCSSLexicalUnit.SCSS_OPERATOR_LEFT_PAREN) {
                operators.push(Parentheses.LEFT);
                current = current.getNextLexicalUnit();
                continue;
            }
            afterOperand = true;

            operands.push(current);
            current = current.getNextLexicalUnit();
        }

        while (!operators.isEmpty()) {
            Object operator = operators.pop();
            if (operator == Parentheses.LEFT) {
                throw new ArithmeticException("Unexpected \"(\" found", term);
            }
            createNewOperand((BinaryOperator) operator, operands);
        }
        Object expression = operands.pop();
        if (!operands.isEmpty()) {
            LexicalUnitImpl operand = (LexicalUnitImpl) operands.peek();
            throw new ArithmeticException("Unexpected operand "
                    + operand.toString() + " found", term);
        }
        return expression;
    }

    public LexicalUnitImpl evaluate(LexicalUnitImpl term) {
        Object result = ArithmeticExpressionEvaluator.get().createExpression(
                term);
        if (result instanceof BinaryExpression) {
            return ((BinaryExpression) result).eval();
        }
        return term;
    }
}
