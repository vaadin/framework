/*
 * Copyright (c) 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: LexicalUnitImpl.java,v 1.3 2000/02/15 02:08:19 plehegar Exp $
 */
package com.vaadin.sass.parser;

import java.io.Serializable;

import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.util.ColorUtil;

/**
 * @version $Revision: 1.3 $
 * @author Philippe Le Hegaret
 * 
 * @modified Sebastian Nyholm @ Vaadin Ltd
 */
public class LexicalUnitImpl implements LexicalUnit, SCSSLexicalUnit,
        Serializable {
    private static final long serialVersionUID = -6649833716809789399L;

    LexicalUnitImpl prev;
    LexicalUnitImpl next;

    short type;
    int line;
    int column;

    int i;
    float f;
    short dimension;
    String sdimension;
    String s;
    String fname;
    LexicalUnitImpl params;

    LexicalUnitImpl(short type, int line, int column, LexicalUnitImpl p) {
        if (p != null) {
            prev = p;
            p.next = this;
        }
        this.line = line;
        this.column = column - 1;
        this.type = type;
    }

    LexicalUnitImpl(int line, int column, LexicalUnitImpl previous, int i) {
        this(SAC_INTEGER, line, column, previous);
        this.i = i;
    }

    LexicalUnitImpl(int line, int column, LexicalUnitImpl previous,
            short dimension, String sdimension, float f) {
        this(dimension, line, column, previous);
        this.f = f;
        this.dimension = dimension;
        this.sdimension = sdimension;
    }

    LexicalUnitImpl(int line, int column, LexicalUnitImpl previous, short type,
            String s) {
        this(type, line, column, previous);
        this.s = s;
    }

    LexicalUnitImpl(short type, int line, int column, LexicalUnitImpl previous,
            String fname, LexicalUnitImpl params) {
        this(type, line, column, previous);
        this.fname = fname;
        this.params = params;
    }

    public int getLineNumber() {
        return line;
    }

    public int getColumnNumber() {
        return column;
    }

    @Override
    public short getLexicalUnitType() {
        return type;
    }

    public void setLexicalUnitType(short type) {
        this.type = type;
    }

    public void getLexicalUnitType(short type) {
        this.type = type;
    }

    @Override
    public LexicalUnitImpl getNextLexicalUnit() {
        return next;
    }

    public void setNextLexicalUnit(LexicalUnitImpl n) {
        next = n;
    }

    @Override
    public LexicalUnitImpl getPreviousLexicalUnit() {
        return prev;
    }

    public void setPrevLexicalUnit(LexicalUnitImpl n) {
        prev = n;
    }

    @Override
    public int getIntegerValue() {
        return i;
    }

    void setIntegerValue(int i) {
        this.i = i;
    }

    @Override
    public float getFloatValue() {
        return f;
    }

    public void setFloatValue(float f) {
        this.f = f;
    }

    @Override
    public String getDimensionUnitText() {
        switch (type) {
        case SAC_PERCENTAGE:
            return "%";
        case SAC_EM:
            return "em";
        case SAC_EX:
            return "ex";
        case SAC_PIXEL:
            return "px";
        case SAC_CENTIMETER:
            return "cm";
        case SAC_MILLIMETER:
            return "mm";
        case SAC_INCH:
            return "in";
        case SAC_POINT:
            return "pt";
        case SAC_PICA:
            return "pc";
        case SAC_DEGREE:
            return "deg";
        case SAC_RADIAN:
            return "rad";
        case SAC_GRADIAN:
            return "grad";
        case SAC_MILLISECOND:
            return "ms";
        case SAC_SECOND:
            return "s";
        case SAC_HERTZ:
            return "Hz";
        case SAC_KILOHERTZ:
            return "kHz";
        case SAC_DIMENSION:
            return sdimension;
        default:
            throw new IllegalStateException("invalid dimension " + type);
        }
    }

    @Override
    public String getStringValue() {
        return s;
    }

    public void setStringValue(String str) {
        s = str;
    }

    @Override
    public String getFunctionName() {
        return fname;
    }

    @Override
    public LexicalUnitImpl getParameters() {
        return params;
    }

    @Override
    public LexicalUnitImpl getSubValues() {
        return params;
    }

    @Override
    public String toString() {
        short type = getLexicalUnitType();
        String text = null;
        switch (type) {
        case SCSS_VARIABLE:
            text = "$" + s;
            break;
        case LexicalUnit.SAC_OPERATOR_COMMA:
            text = ",";
            break;
        case LexicalUnit.SAC_OPERATOR_PLUS:
            text = "+";
            break;
        case LexicalUnit.SAC_OPERATOR_MINUS:
            text = "-";
            break;
        case LexicalUnit.SAC_OPERATOR_MULTIPLY:
            text = "*";
            break;
        case LexicalUnit.SAC_OPERATOR_SLASH:
            text = "/";
            break;
        case LexicalUnit.SAC_OPERATOR_MOD:
            text = "%";
            break;
        case LexicalUnit.SAC_OPERATOR_EXP:
            text = "^";
            break;
        case LexicalUnit.SAC_OPERATOR_LT:
            text = "<";
            break;
        case LexicalUnit.SAC_OPERATOR_GT:
            text = ">";
            break;
        case LexicalUnit.SAC_OPERATOR_LE:
            text = "<=";
            break;
        case LexicalUnit.SAC_OPERATOR_GE:
            text = "=>";
            break;
        case LexicalUnit.SAC_OPERATOR_TILDE:
            text = "~";
            break;
        case LexicalUnit.SAC_INHERIT:
            text = "inherit";
            break;
        case LexicalUnit.SAC_INTEGER:
            text = Integer.toString(getIntegerValue(), 10);
            break;
        case LexicalUnit.SAC_REAL:
            text = getFloatValue() + "";
            break;
        case LexicalUnit.SAC_EM:
        case LexicalUnit.SAC_EX:
        case LexicalUnit.SAC_PIXEL:
        case LexicalUnit.SAC_INCH:
        case LexicalUnit.SAC_CENTIMETER:
        case LexicalUnit.SAC_MILLIMETER:
        case LexicalUnit.SAC_POINT:
        case LexicalUnit.SAC_PICA:
        case LexicalUnit.SAC_PERCENTAGE:
        case LexicalUnit.SAC_DEGREE:
        case LexicalUnit.SAC_GRADIAN:
        case LexicalUnit.SAC_RADIAN:
        case LexicalUnit.SAC_MILLISECOND:
        case LexicalUnit.SAC_SECOND:
        case LexicalUnit.SAC_HERTZ:
        case LexicalUnit.SAC_KILOHERTZ:
        case LexicalUnit.SAC_DIMENSION:
            float f = getFloatValue();
            int i = (int) f;
            if ((i) == f) {
                text = i + getDimensionUnitText();
            } else {
                text = f + getDimensionUnitText();
            }
            break;
        case LexicalUnit.SAC_URI:
            text = "url(" + getStringValue() + ")";
            break;
        case LexicalUnit.SAC_RGBCOLOR:
        case LexicalUnit.SAC_COUNTER_FUNCTION:
        case LexicalUnit.SAC_COUNTERS_FUNCTION:
        case LexicalUnit.SAC_RECT_FUNCTION:
        case LexicalUnit.SAC_FUNCTION:
            String funcName = getFunctionName();
            LexicalUnitImpl firstParam = getParameters();
            if ("round".equals(funcName)) {
                firstParam
                        .setFloatValue(Math.round(firstParam.getFloatValue()));
                text = firstParam.toString();
            } else if ("ceil".equals(funcName)) {
                firstParam.setFloatValue((float) Math.ceil(firstParam
                        .getFloatValue()));
                text = firstParam.toString();
            } else if ("floor".equals(funcName)) {
                firstParam.setFloatValue((float) Math.floor(firstParam
                        .getFloatValue()));
                text = firstParam.toString();
            } else if ("abs".equals(funcName)) {
                firstParam.setFloatValue(Math.abs(firstParam.getFloatValue()));
                text = firstParam.toString();
            } else if ("darken".equals(funcName)) {
                LexicalUnitImpl dark = ColorUtil.darken(this);
                text = dark.toString();
            } else if ("lighten".equals(funcName)) {
                text = ColorUtil.lighten(this).toString();
            } else {
                text = getFunctionName() + "(" + getParameters() + ")";
            }
            break;
        case LexicalUnit.SAC_IDENT:
            text = getStringValue();
            break;
        case LexicalUnit.SAC_STRING_VALUE:
            // @@SEEME. not exact
            text = "\"" + getStringValue() + "\"";
            break;
        case LexicalUnit.SAC_ATTR:
            text = "attr(" + getStringValue() + ")";
            break;
        case LexicalUnit.SAC_UNICODERANGE:
            text = "@@TODO";
            break;
        case LexicalUnit.SAC_SUB_EXPRESSION:
            text = getSubValues().toString();
            break;
        default:
            text = "@unknown";
            break;
        }
        if (getNextLexicalUnit() != null) {
            if (getNextLexicalUnit().getLexicalUnitType() == SAC_OPERATOR_COMMA) {
                return text + getNextLexicalUnit();
            }
            return text + ' ' + getNextLexicalUnit();
        } else {
            return text;
        }
    }

    @Override
    public LexicalUnitImpl divide(LexicalUnitImpl denominator) {
        setFloatValue(getFloatValue() / denominator.getIntegerValue());
        return this;
    }

    @Override
    public LexicalUnitImpl add(LexicalUnitImpl another) {
        setFloatValue(getFloatValue() + another.getFloatValue());
        return this;
    }

    @Override
    public LexicalUnitImpl minus(LexicalUnitImpl another) {
        setFloatValue(getFloatValue() - another.getFloatValue());
        return this;
    }

    @Override
    public LexicalUnitImpl multiply(LexicalUnitImpl another) {
        setFloatValue(getFloatValue() * another.getIntegerValue());
        return this;
    }

    public void replaceValue(LexicalUnitImpl another) {
        type = another.getLexicalUnitType();
        i = another.getIntegerValue();
        f = another.getFloatValue();
        s = another.getStringValue();
        fname = another.getFunctionName();
        prev = another.getPreviousLexicalUnit();
        dimension = another.getDimension();
        sdimension = another.getSdimension();
        params = another.getParameters();

        LexicalUnitImpl finalNextInAnother = another;
        while (finalNextInAnother.getNextLexicalUnit() != null) {
            finalNextInAnother = finalNextInAnother.getNextLexicalUnit();
        }

        finalNextInAnother.setNextLexicalUnit(next);
        next = another.next;
    }

    public void setParameters(LexicalUnitImpl params) {
        this.params = params;
    }

    public short getDimension() {
        return dimension;
    }

    public String getSdimension() {
        return sdimension;
    }

    // here some useful function for creation
    public static LexicalUnitImpl createVariable(int line, int column,
            LexicalUnitImpl previous, String name) {
        return new LexicalUnitImpl(line, column, previous, SCSS_VARIABLE, name);
    }

    public static LexicalUnitImpl createNumber(int line, int column,
            LexicalUnitImpl previous, float v) {
        int i = (int) v;
        if (v == i) {
            return new LexicalUnitImpl(line, column, previous, i);
        } else {
            return new LexicalUnitImpl(line, column, previous, SAC_REAL, "", v);
        }
    }

    public static LexicalUnitImpl createInteger(int line, int column,
            LexicalUnitImpl previous, int i) {
        return new LexicalUnitImpl(line, column, previous, i);
    }

    public static LexicalUnitImpl createPercentage(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_PERCENTAGE,
                null, v);
    }

    static LexicalUnitImpl createEMS(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_EM, null, v);
    }

    static LexicalUnitImpl createEXS(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_EX, null, v);
    }

    public static LexicalUnitImpl createPixel(float p) {
        return new LexicalUnitImpl(0, 0, null, SAC_PIXEL, null, p);
    }

    static LexicalUnitImpl createPX(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_PIXEL, null, v);
    }

    static LexicalUnitImpl createCM(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_CENTIMETER,
                null, v);
    }

    static LexicalUnitImpl createMM(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_MILLIMETER,
                null, v);
    }

    static LexicalUnitImpl createIN(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_INCH, null, v);
    }

    static LexicalUnitImpl createPT(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_POINT, null, v);
    }

    static LexicalUnitImpl createPC(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_PICA, null, v);
    }

    static LexicalUnitImpl createDEG(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_DEGREE, null, v);
    }

    static LexicalUnitImpl createRAD(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_RADIAN, null, v);
    }

    static LexicalUnitImpl createGRAD(int line, int column,
            LexicalUnitImpl previous, float v) {
        return new LexicalUnitImpl(line, column, previous, SAC_GRADIAN, null, v);
    }

    static LexicalUnitImpl createMS(int line, int column,
            LexicalUnitImpl previous, float v) {
        if (v < 0) {
            throw new ParseException("Time values may not be negative");
        }
        return new LexicalUnitImpl(line, column, previous, SAC_MILLISECOND,
                null, v);
    }

    static LexicalUnitImpl createS(int line, int column,
            LexicalUnitImpl previous, float v) {
        if (v < 0) {
            throw new ParseException("Time values may not be negative");
        }
        return new LexicalUnitImpl(line, column, previous, SAC_SECOND, null, v);
    }

    static LexicalUnitImpl createHZ(int line, int column,
            LexicalUnitImpl previous, float v) {
        if (v < 0) {
            throw new ParseException("Frequency values may not be negative");
        }
        return new LexicalUnitImpl(line, column, previous, SAC_HERTZ, null, v);
    }

    static LexicalUnitImpl createKHZ(int line, int column,
            LexicalUnitImpl previous, float v) {
        if (v < 0) {
            throw new ParseException("Frequency values may not be negative");
        }
        return new LexicalUnitImpl(line, column, previous, SAC_KILOHERTZ, null,
                v);
    }

    static LexicalUnitImpl createDimen(int line, int column,
            LexicalUnitImpl previous, float v, String s) {
        return new LexicalUnitImpl(line, column, previous, SAC_DIMENSION, s, v);
    }

    static LexicalUnitImpl createInherit(int line, int column,
            LexicalUnitImpl previous) {
        return new LexicalUnitImpl(line, column, previous, SAC_INHERIT,
                "inherit");
    }

    public static LexicalUnitImpl createIdent(int line, int column,
            LexicalUnitImpl previous, String s) {
        return new LexicalUnitImpl(line, column, previous, SAC_IDENT, s);
    }

    public static LexicalUnitImpl createString(String s) {
        return new LexicalUnitImpl(0, 0, null, SAC_STRING_VALUE, s);
    }

    static LexicalUnitImpl createString(int line, int column,
            LexicalUnitImpl previous, String s) {
        return new LexicalUnitImpl(line, column, previous, SAC_STRING_VALUE, s);
    }

    static LexicalUnitImpl createURL(int line, int column,
            LexicalUnitImpl previous, String s) {
        return new LexicalUnitImpl(line, column, previous, SAC_URI, s);
    }

    static LexicalUnitImpl createAttr(int line, int column,
            LexicalUnitImpl previous, String s) {
        return new LexicalUnitImpl(line, column, previous, SAC_ATTR, s);
    }

    static LexicalUnitImpl createCounter(int line, int column,
            LexicalUnitImpl previous, LexicalUnit params) {
        return new LexicalUnitImpl(SAC_COUNTER_FUNCTION, line, column,
                previous, "counter", (LexicalUnitImpl) params);
    }

    public static LexicalUnitImpl createCounters(int line, int column,
            LexicalUnitImpl previous, LexicalUnit params) {
        return new LexicalUnitImpl(SAC_COUNTERS_FUNCTION, line, column,
                previous, "counters", (LexicalUnitImpl) params);
    }

    public static LexicalUnitImpl createRGBColor(int line, int column,
            LexicalUnitImpl previous, LexicalUnit params) {
        return new LexicalUnitImpl(SAC_RGBCOLOR, line, column, previous, "rgb",
                (LexicalUnitImpl) params);
    }

    public static LexicalUnitImpl createRect(int line, int column,
            LexicalUnitImpl previous, LexicalUnit params) {
        return new LexicalUnitImpl(SAC_RECT_FUNCTION, line, column, previous,
                "rect", (LexicalUnitImpl) params);
    }

    public static LexicalUnitImpl createFunction(int line, int column,
            LexicalUnitImpl previous, String fname, LexicalUnit params) {
        return new LexicalUnitImpl(SAC_FUNCTION, line, column, previous, fname,
                (LexicalUnitImpl) params);
    }

    public static LexicalUnitImpl createUnicodeRange(int line, int column,
            LexicalUnit previous, LexicalUnit params) {
        // @@ return new LexicalUnitImpl(line, column, previous, null,
        // SAC_UNICODERANGE, params);
        return null;
    }

    public static LexicalUnitImpl createComma(int line, int column,
            LexicalUnitImpl previous) {
        return new LexicalUnitImpl(SAC_OPERATOR_COMMA, line, column, previous);
    }

    public static LexicalUnitImpl createSlash(int line, int column,
            LexicalUnitImpl previous) {
        return new LexicalUnitImpl(SAC_OPERATOR_SLASH, line, column, previous);
    }

    @Override
    public LexicalUnitImpl clone() {
        LexicalUnitImpl cloned = new LexicalUnitImpl(type, line, column,
                (LexicalUnitImpl) prev);
        cloned.replaceValue(this);
        return cloned;
    }

    /**
     * Tries to return the value for this {@link LexicalUnitImpl} without
     * considering any related units.
     * 
     * @return
     */
    public Object getValue() {
        if (s != null) {
            return s;
        } else if (i != -1) {
            return i;
        } else if (f != -1) {
            return f;
        } else
            return null;
    }

    public void setFunctionName(String functionName) {
        fname = functionName;
    }

    public static LexicalUnitImpl createIdent(String s) {
        return new LexicalUnitImpl(0, 0, null, SAC_IDENT, s);
    }
}
