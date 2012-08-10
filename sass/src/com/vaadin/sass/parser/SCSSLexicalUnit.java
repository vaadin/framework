package com.vaadin.sass.parser;

import org.w3c.css.sac.LexicalUnit;

public interface SCSSLexicalUnit extends LexicalUnit {
    static final short SCSS_VARIABLE = 100;

    LexicalUnitImpl divide(LexicalUnitImpl denominator);

    LexicalUnitImpl add(LexicalUnitImpl another);

    LexicalUnitImpl minus(LexicalUnitImpl another);

    LexicalUnitImpl multiply(LexicalUnitImpl another);

}
