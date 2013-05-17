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

import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.internal.parser.LexicalUnitImpl;

public enum BinaryOperator {
    ADD(LexicalUnit.SAC_OPERATOR_PLUS, 1) {
        @Override
        public LexicalUnitImpl eval(LexicalUnitImpl leftValue,
                LexicalUnitImpl rightValue) {
            return leftValue.add(rightValue);
        }
    },
    MINUS(LexicalUnit.SAC_OPERATOR_MINUS, 1) {
        @Override
        public LexicalUnitImpl eval(LexicalUnitImpl leftValue,
                LexicalUnitImpl rightValue) {
            return leftValue.minus(rightValue);
        }
    },
    MUL(LexicalUnit.SAC_OPERATOR_MULTIPLY, 2) {
        @Override
        public LexicalUnitImpl eval(LexicalUnitImpl leftValue,
                LexicalUnitImpl rightValue) {
            return leftValue.multiply(rightValue);
        }
    },
    DIV(LexicalUnit.SAC_OPERATOR_SLASH, 2) {
        @Override
        public LexicalUnitImpl eval(LexicalUnitImpl leftValue,
                LexicalUnitImpl rightValue) {
            return leftValue.divide(rightValue);
        }
    },
    MOD(LexicalUnit.SAC_OPERATOR_MOD, 2) {
        @Override
        public LexicalUnitImpl eval(LexicalUnitImpl leftValue,
                LexicalUnitImpl rightValue) {
            return leftValue.modulo(rightValue);
        }
    };

    public final short type;
    public final int precedence;

    BinaryOperator(short type, int precedence) {
        this.type = type;
        this.precedence = precedence;
    }

    public abstract LexicalUnitImpl eval(LexicalUnitImpl leftValue,
            LexicalUnitImpl rightValue);
}
