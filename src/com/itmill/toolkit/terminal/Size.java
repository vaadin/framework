package com.itmill.toolkit.terminal;

import com.itmill.toolkit.ui.Component;

public class Size {

    private Component component;
    private int width;
    private int height;
    private int widthUnit;
    private int heightUnit;

    /**
     * Unit code representing pixels.
     */
    public static final int UNITS_PIXELS = 0;

    /**
     * Unit code representing points (1/72nd of an inch).
     */
    public static final int UNITS_POINTS = 1;

    /**
     * Unit code representing picas (12 points).
     */
    public static final int UNITS_PICAS = 2;

    /**
     * Unit code representing the font-size of the relevant font.
     */
    public static final int UNITS_EM = 3;

    /**
     * Unit code representing the x-height of the relevant font.
     */
    public static final int UNITS_EX = 4;

    /**
     * Unit code representing millimeters.
     */
    public static final int UNITS_MM = 5;

    /**
     * Unit code representing centimeters.
     */
    public static final int UNITS_CM = 6;

    /**
     * Unit code representing inches.
     */
    public static final int UNITS_INCH = 7;

    /**
     * Unit code representing in percentage of the containing element defined by
     * terminal.
     */
    public static final int UNITS_PERCENTAGE = 8;

    /**
     * Unit code representing in rows of text. This unit is only applicable to
     * some components can it's meaning is specified by component
     * implementation.
     */
    public static final int UNITS_ROWS = 9;

    /**
     * Textual representations of units symbols. Supported units and their
     * symbols are:
     * <ul>
     * <li><code>UNITS_PIXELS</code>: "px"</li>
     * <li><code>UNITS_POINTS</code>: "pt"</li>
     * <li><code>UNITS_PICAS</code>: "pc"</li>
     * <li><code>UNITS_EM</code>: "em"</li>
     * <li><code>UNITS_EX</code>: "ex"</li>
     * <li><code>UNITS_MM</code>: "mm"</li>
     * <li><code>UNITS_CM</code>. "cm"</li>
     * <li><code>UNITS_INCH</code>: "in"</li>
     * <li><code>UNITS_PERCENTAGE</code>: "%"</li>
     * <li><code>UNITS_ROWS</code>: "rows"</li>
     * </ul>
     * These can be used like <code>Size.UNIT_SYMBOLS[UNITS_PIXELS]</code>.
     */
    public static final String[] UNIT_SYMBOLS = { "px", "pt", "pc", "em", "ex",
            "mm", "cm", "in", "%", "rows" };

    public Size(Component c) {
        component = c;
        width = -1;
        height = -1;
        widthUnit = UNITS_PIXELS;
        heightUnit = UNITS_PIXELS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#getHeight()
     */
    public int getHeight() {
        return height;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#getHeightUnits()
     */
    public int getHeightUnits() {
        return heightUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#getWidth()
     */
    public int getWidth() {
        return width;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#getWidthUnits()
     */
    public int getWidthUnits() {
        return widthUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#setHeight(int)
     */
    public void setHeight(int height) {
        this.height = height;
        component.requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#setHeightUnits(int)
     */
    public void setHeightUnits(int unit) {
        heightUnit = unit;
        component.requestRepaint();
    }

    public void setHeight(int height, int unit) {
        setHeight(height);
        setHeightUnits(unit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#setSizeFull()
     */
    public void setSizeFull() {
        height = 100;
        width = 100;
        heightUnit = UNITS_PERCENTAGE;
        widthUnit = UNITS_PERCENTAGE;
        component.requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#setSizeUndefined()
     */
    public void setSizeUndefined() {
        height = -1;
        width = -1;
        heightUnit = UNITS_PIXELS;
        widthUnit = UNITS_PIXELS;
        component.requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#setWidth(int)
     */
    public void setWidth(int width) {
        this.width = width;
        component.requestRepaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.Sizeable#setWidthUnits(int)
     */
    public void setWidthUnits(int unit) {
        widthUnit = unit;
        component.requestRepaint();
    }

    public void setWidth(int width, int unit) {
        setWidth(width);
        setWidthUnits(unit);
    }

    public void paint(PaintTarget target) throws PaintException {
        if (getHeight() >= 0) {
            target.addAttribute("height", "" + getHeight()
                    + UNIT_SYMBOLS[getHeightUnits()]);
        }
        if (getWidth() >= 0) {
            target.addAttribute("width", "" + getWidth()
                    + UNIT_SYMBOLS[getWidthUnits()]);
        }
    }

}