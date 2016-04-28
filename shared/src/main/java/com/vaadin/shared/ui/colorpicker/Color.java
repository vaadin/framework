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
package com.vaadin.shared.ui.colorpicker;

import java.io.Serializable;

/**
 * Default implementation for color.
 * 
 * @since 7.0.0
 */
public class Color implements Serializable {

    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color CYAN = new Color(0, 255, 255);

    private int red;
    private int green;
    private int blue;
    private int alpha;

    private String OUTOFRANGE = "Value must be within the range [0-255]. Was: ";

    /**
     * Creates a color that has the specified red, green, blue, and alpha values
     * within the range [0 - 255].
     * 
     * @throws IllegalArgumentException
     *             if <code>red</code>, <code>green</code>, <code>blue</code> or
     *             <code>alpha</code> fall outside of the inclusive range from 0
     *             to 255
     * @param red
     *            the red value
     * @param green
     *            the green value
     * @param blue
     *            the blue value
     * @param alpha
     *            the alpha value
     */
    public Color(int red, int green, int blue, int alpha) {
        checkRange(red, green, blue, alpha);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Creates a color that has the specified red, green, and blue values within
     * the range [0 - 255]. Alpha gets the default value of 255.
     * 
     * @throws IllegalArgumentException
     *             if <code>red</code>, <code>green</code> or <code>blue</code>
     *             fall outside of the inclusive range from 0 to 255
     * @param red
     *            the red value
     * @param green
     *            the green value
     * @param blue
     *            the blue value
     */
    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    /**
     * Creates a color based on an RGB value.
     * 
     * @throws IllegalArgumentException
     *             if converted values of <code>red</code>, <code>green</code>,
     *             <code>blue</code> or <code>alpha</code> fall outside of the
     *             inclusive range from 0 to 255
     * 
     * @param rgb
     *            the RGB value
     */
    public Color(int rgb) {
        int value = 0xff000000 | rgb;
        int red = (value >> 16) & 0xFF;
        int green = (value >> 8) & 0xFF;
        int blue = (value >> 0) & 0xFF;
        int alpha = (value >> 24) & 0xff;

        checkRange(red, green, blue, alpha);

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Checks that all values are within the acceptable range of [0, 255].
     * 
     * @throws IllegalArgumentException
     *             if any of the values fall outside of the range
     * 
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    private void checkRange(int red, int green, int blue, int alpha) {
        if (!withinRange(red) || !withinRange(green) || !withinRange(blue)
                || !withinRange(alpha)) {

            String errorMessage = "All values must fall within range [0-255]. (red: "
                    + red
                    + ", green: "
                    + green
                    + ", blue: "
                    + blue
                    + ", alpha: " + alpha + ")";
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Checks whether the value is within the acceptable range of [0, 255].
     * 
     * @param value
     * @return true if the value falls within the range, false otherwise
     */
    private boolean withinRange(int value) {
        if (value < 0 || value > 255) {
            return false;
        }
        return true;
    }

    /**
     * Returns the red value of the color.
     * 
     */
    public int getRed() {
        return red;
    }

    /**
     * Sets the red value of the color. Value must be within the range [0, 255].
     * 
     * @param red
     *            new red value
     */
    public void setRed(int red) {
        if (withinRange(red)) {
            this.red = red;
        } else {
            throw new IllegalArgumentException(OUTOFRANGE + red);
        }
    }

    /**
     * Returns the green value of the color.
     * 
     */
    public int getGreen() {
        return green;
    }

    /**
     * Sets the green value of the color. Value must be within the range [0,
     * 255].
     * 
     * @param green
     *            new green value
     */
    public void setGreen(int green) {
        if (withinRange(green)) {
            this.green = green;
        } else {
            throw new IllegalArgumentException(OUTOFRANGE + green);
        }
    }

    /**
     * Returns the blue value of the color.
     * 
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Sets the blue value of the color. Value must be within the range [0,
     * 255].
     * 
     * @param blue
     *            new blue value
     */
    public void setBlue(int blue) {
        if (withinRange(blue)) {
            this.blue = blue;
        } else {
            throw new IllegalArgumentException(OUTOFRANGE + blue);
        }
    }

    /**
     * Returns the alpha value of the color.
     * 
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Sets the alpha value of the color. Value must be within the range [0,
     * 255].
     * 
     * @param alpha
     *            new alpha value
     */
    public void setAlpha(int alpha) {
        if (withinRange(alpha)) {
            this.alpha = alpha;
        } else {
            throw new IllegalArgumentException(OUTOFRANGE + alpha);
        }
    }

    /**
     * Returns CSS representation of the Color, e.g. #000000.
     */
    public String getCSS() {
        String redString = Integer.toHexString(red);
        redString = redString.length() < 2 ? "0" + redString : redString;

        String greenString = Integer.toHexString(green);
        greenString = greenString.length() < 2 ? "0" + greenString
                : greenString;

        String blueString = Integer.toHexString(blue);
        blueString = blueString.length() < 2 ? "0" + blueString : blueString;

        return "#" + redString + greenString + blueString;
    }

    /**
     * Returns RGB value of the color.
     */
    public int getRGB() {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16)
                | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
    }

    /**
     * Returns converted HSV components of the color.
     * 
     */
    public float[] getHSV() {
        float[] hsv = new float[3];

        int maxColor = (red > green) ? red : green;
        if (blue > maxColor) {
            maxColor = blue;
        }
        int minColor = (red < green) ? red : green;
        if (blue < minColor) {
            minColor = blue;
        }

        float value = maxColor / 255.0f;

        float saturation = 0;
        if (maxColor != 0) {
            saturation = ((float) (maxColor - minColor)) / ((float) maxColor);
        }

        float hue = 0;
        if (saturation != 0) {
            float redF = ((float) (maxColor - red))
                    / ((float) (maxColor - minColor));
            float greenF = ((float) (maxColor - green))
                    / ((float) (maxColor - minColor));
            float blueF = ((float) (maxColor - blue))
                    / ((float) (maxColor - minColor));

            if (red == maxColor) {
                hue = blueF - greenF;
            } else if (green == maxColor) {
                hue = 2.0f + redF - blueF;
            } else {
                hue = 4.0f + greenF - redF;
            }

            hue = hue / 6.0f;
            if (hue < 0) {
                hue = hue + 1.0f;
            }
        }

        hsv[0] = hue;
        hsv[1] = saturation;
        hsv[2] = value;
        return hsv;
    }

    @Override
    public int hashCode() {
        return getRGB();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Color && ((Color) obj).getRGB() == getRGB();
    }

    /**
     * <p>
     * Converts HSV's hue, saturation and value into an RGB value.
     * <p>
     * The <code>saturation</code> and <code>value</code> components should be
     * floating-point values within the range [0.0-1.0].
     * <p>
     * 
     * @param hue
     *            the hue of the color
     * @param saturation
     *            the saturation of the color
     * @param value
     *            the value of the color
     * @return the RGB value of corresponding color
     */
    public static int HSVtoRGB(float hue, float saturation, float value) {
        int red = 0;
        int green = 0;
        int blue = 0;

        if (saturation == 0) {
            red = green = blue = (int) (value * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = value * (1.0f - saturation);
            float q = value * (1.0f - saturation * f);
            float t = value * (1.0f - (saturation * (1.0f - f)));

            switch ((int) h) {
            case 0:
                red = (int) (value * 255.0f + 0.5f);
                green = (int) (t * 255.0f + 0.5f);
                blue = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                red = (int) (q * 255.0f + 0.5f);
                green = (int) (value * 255.0f + 0.5f);
                blue = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                red = (int) (p * 255.0f + 0.5f);
                green = (int) (value * 255.0f + 0.5f);
                blue = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                red = (int) (p * 255.0f + 0.5f);
                green = (int) (q * 255.0f + 0.5f);
                blue = (int) (value * 255.0f + 0.5f);
                break;
            case 4:
                red = (int) (t * 255.0f + 0.5f);
                green = (int) (p * 255.0f + 0.5f);
                blue = (int) (value * 255.0f + 0.5f);
                break;
            case 5:
                red = (int) (value * 255.0f + 0.5f);
                green = (int) (p * 255.0f + 0.5f);
                blue = (int) (q * 255.0f + 0.5f);
                break;
            }
        }

        return 0xff000000 | (red << 16) | (green << 8) | (blue << 0);
    }

    /**
     * <p>
     * Converts HSL's hue, saturation and lightness into an RGB value.
     * 
     * @param hue
     *            the hue of the color. The unit of the value is degrees and
     *            should be between 0-360.
     * @param saturation
     *            the saturation of the color. The unit of the value is
     *            percentages and should be between 0-100;
     * @param lightness
     *            the lightness of the color. The unit of the value is
     *            percentages and should be between 0-100;
     * 
     * @return the RGB value of corresponding color
     */
    public static int HSLtoRGB(int hue, int saturation, int lightness) {
        int red = 0;
        int green = 0;
        int blue = 0;

        float hueRatio = hue / 360f;
        float saturationRatio = saturation / 100f;
        float lightnessRatio = lightness / 100f;

        if (saturationRatio == 0) {
            red = green = blue = (int) (lightnessRatio * 255.0f + 0.5f);
        } else {
            float p = lightnessRatio < 0.5f ? lightnessRatio
                    * (1f + saturationRatio) : lightnessRatio + saturationRatio
                    - lightnessRatio * saturationRatio;
            float q = 2 * lightnessRatio - p;

            red = hslComponentToRgbComponent(p, q, hueRatio + (1f / 3f));
            green = hslComponentToRgbComponent(p, q, hueRatio);
            blue = hslComponentToRgbComponent(p, q, hueRatio - (1f / 3f));
        }
        return 0xff000000 | (red << 16) | (green << 8) | (blue << 0);
    }

    private static int hslComponentToRgbComponent(float p, float q, float ratio) {
        if (ratio < 0) {
            ratio += 1;
        } else if (ratio > 1) {
            ratio -= 1;
        }

        if (6 * ratio < 1f) {
            return (int) ((q + (p - q) * 6f * ratio) * 255f + 0.5f);
        } else if (2f * ratio < 1f) {
            return (int) (p * 255f + 0.5f);
        } else if (3f * ratio < 2f) {
            return (int) ((q + (p - q) * ((2f / 3f) - ratio) * 6f) * 255f + 0.5f);
        }

        return (int) (q * 255f + 0.5f);
    }
}
