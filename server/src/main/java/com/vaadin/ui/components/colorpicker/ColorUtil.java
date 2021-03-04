/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.ui.components.colorpicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.shared.ui.colorpicker.Color;

/**
 * Utility class for matching and parsing {@link Color} objects from
 * {@code String} input.
 *
 * Description of supported formats see
 * http://www.w3schools.com/cssref/css_colors_legal.asp
 *
 * @since 8.4
 */
public class ColorUtil {
    private ColorUtil() {
    }

    /**
     * Parses {@link Color} from any of the following {@link String} inputs:
     * <br>
     * - RGB hex (e.g. "#FFAA00"), {@link #HEX_PATTERN}<br>
     * - RGB "function" (e.g. "rgb(128,0,255)"), {@link #RGB_PATTERN}<br>
     * - RGBA "function" (e.g. "rgba(50,50,50,0.2)"), {@link #RGBA_PATTERN}<br>
     * - HSL "function" (e.g. "hsl(50,50,50)"), {@link #HSL_PATTERN}<br>
     * - HSLA "function" (e.g. "hsl(50,50,50,0.2)"), {@link #HSLA_PATTERN}
     * <p>
     * Parsing is case-insensitive.
     *
     * @param input
     *            String input
     * @return {@link Color} parsed from input
     * @throws NumberFormatException
     *             Input does not match any recognized pattern
     */
    public static Color stringToColor(String input) {
        Matcher m = HEX_PATTERN.matcher(input);
        if (m.matches()) {
            return getHexPatternColor(m);
        }
        m = RGB_PATTERN.matcher(input);
        if (m.matches()) {
            return getRGBPatternColor(m);
        }
        m = RGBA_PATTERN.matcher(input);
        if (m.matches()) {
            return getRGBAPatternColor(m);
        }
        m = HSL_PATTERN.matcher(input);
        if (m.matches()) {
            return getHSLPatternColor(m);
        }
        m = HSLA_PATTERN.matcher(input);
        if (m.matches()) {
            return getHSLAPatternColor(m);
        }

        throw new NumberFormatException("Parsing color from input failed.");
    }

    /**
     * Parses {@link Color} from matched hexadecimal {@link Matcher}.
     *
     * @param matcher
     *            {@link Matcher} matching hexadecimal pattern with named regex
     *            groups {@code red}, {@code green}, and {@code blue}
     * @return {@link Color} parsed from {@link Matcher}
     */
    public static Color getHexPatternColor(Matcher matcher) {
        int red = Integer.parseInt(matcher.group("red"), 16);
        int green = Integer.parseInt(matcher.group("green"), 16);
        int blue = Integer.parseInt(matcher.group("blue"), 16);
        return new Color(red, green, blue);
    }

    /**
     * Parses {@link Color} from matched RGB {@link Matcher}.
     *
     * @param matcher
     *            {@link Matcher} matching RGB pattern with named regex groups
     *            {@code red}, {@code green}, and {@code blue}
     * @return {@link Color} parsed from {@link Matcher}
     */
    public static Color getRGBPatternColor(Matcher matcher) {
        int red = Integer.parseInt(matcher.group("red"));
        int green = Integer.parseInt(matcher.group("green"));
        int blue = Integer.parseInt(matcher.group("blue"));
        return new Color(red, green, blue);
    }

    /**
     * Parses {@link Color} from matched RGBA {@link Matcher}.
     *
     * @param matcher
     *            {@link Matcher} matching RGBA pattern with named regex groups
     *            {@code red}, {@code green}, {@code blue}, and {@code alpha}
     * @return {@link Color} parsed from {@link Matcher}
     */
    public static Color getRGBAPatternColor(Matcher matcher) {
        Color c = getRGBPatternColor(matcher);
        c.setAlpha((int) (Double.parseDouble(matcher.group("alpha")) * 255d));
        return c;
    }

    /**
     * Parses {@link Color} from matched HSL {@link Matcher}.
     *
     * @param matcher
     *            {@link Matcher} matching HSL pattern with named regex groups
     *            {@code hue}, {@code saturation}, and {@code light}
     * @return {@link Color} parsed from {@link Matcher}
     */
    public static Color getHSLPatternColor(Matcher matcher) {
        int hue = Integer.parseInt(matcher.group("hue"));
        int saturation = Integer.parseInt(matcher.group("saturation"));
        int light = Integer.parseInt(matcher.group("light"));
        int rgb = Color.HSLtoRGB(hue, saturation, light);
        return new Color(rgb);
    }

    /**
     * Parses {@link Color} from matched HSLA {@link Matcher}.
     *
     * @param matcher
     *            {@link Matcher} matching HSLA pattern with named regex groups
     *            {@code hue}, {@code saturation}, {@code light}, and
     *            {@code alpha}
     * @return {@link Color} parsed from {@link Matcher}
     */
    public static Color getHSLAPatternColor(Matcher matcher) {
        Color c = getHSLPatternColor(matcher);
        c.setAlpha((int) (Double.parseDouble(matcher.group("alpha")) * 255d));
        return c;
    }

    /**
     * Case-insensitive {@link Pattern} with regular expression matching the
     * default hexadecimal color presentation pattern:<br>
     * '#' followed by six <code>[\da-fA-F]</code> characters.
     * <p>
     * Pattern contains named groups <code>red</code>, <code>green</code>, and
     * <code>blue</code>, which represent the individual values.
     */
    public static final Pattern HEX_PATTERN = Pattern.compile(
            "(?i)^#\\s*(?<red>[\\da-f]{2})(?<green>[\\da-f]{2})(?<blue>[\\da-f]{2}"
                    + ")\\s*$");
    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * RGB color presentation patterns:<br>
     * 'rgb' followed by three [0-255] number values. Values can be separated
     * with either comma or whitespace.
     * <p>
     * Pattern contains named groups <code>red</code>, <code>green</code>, and
     * <code>blue</code>, which represent the individual values.
     */
    public static final Pattern RGB_PATTERN = Pattern.compile(
            "(?i)^rgb\\(\\s*(?<red>[01]?\\d{1,2}|2[0-4]\\d|25[0-5])(?:\\s*[,+|\\"
                    + "s+]\\s*)(?<green>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\s*[,"
                    + "+|\\s+]\\s*)(?<blue>[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s*\\"
                    + ")$");
    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * RGBA presentation patterns:<br>
     * 'rgba' followed by three [0-255] values and one [0.0-1.0] value. Values
     * can be separated with either comma or whitespace. The only accepted
     * decimal marker is point ('.').
     * <p>
     * Pattern contains named groups <code>red</code>, <code>green</code>,
     * <code>blue</code>, and <code>alpha</code>, which represent the individual
     * values.
     */
    public static final Pattern RGBA_PATTERN = Pattern.compile(
            "(?i)^rgba\\(\\s*(?<red>[01]?\\d{1,2}|2[0-4]\\d|25[0-5])(?:\\s*[,+|"
                    + "\\s+]\\s*)(?<green>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\s"
                    + "*[,+|\\s+]\\s*)(?<blue>[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?"
                    + ":\\s*[,+|\\s+]\\s*)(?<alpha>0(?:\\.\\d{1,2})?|0?(?:\\.\\"
                    + "d{1,2})|1(?:\\.0{1,2})?)\\s*\\)$");

    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * HSL presentation patterns:<br>
     * 'hsl' followed by one [0-360] value and two [0-100] percentage value.
     * Values can be separated with either comma or whitespace. The percent sign
     * ('%') is optional.
     * <p>
     * Pattern contains named groups <code>hue</code>,<code>saturation</code>,
     * and <code>light</code>, which represent the individual values.
     */
    public static final Pattern HSL_PATTERN = Pattern.compile(
            "(?i)hsl\\(\\s*(?<hue>[12]?\\d{1,2}|3[0-5]\\d|360)(?:\\s*[,+|\\s+]"
                    + "\\s*)(?<saturation>\\d{1,2}|100)(?:\\s*%?\\s*[,+|\\s+]\\"
                    + "s*)(?<light>\\d{1,2}|100)(?:\\s*%?\\s*)\\)$");

    /**
     * Case-insensitive {@link Pattern} with regular expression matching common
     * HSLA presentation patterns:<br>
     * 'hsla' followed by one [0-360] value, two [0-100] percentage values, and
     * one [0.0-1.0] value. Values can be separated with either comma or
     * whitespace. The percent sign ('%') is optional. The only accepted decimal
     * marker is point ('.').
     * <p>
     * Pattern contains named groups <code>hue</code>,<code>saturation</code>,
     * <code>light</code>, and <code>alpha</code>, which represent the
     * individual values.
     */
    public static final Pattern HSLA_PATTERN = Pattern.compile(
            "(?i)hsla\\(\\s*(?<hue>[12]?\\d{1,2}|3[0-5]\\d|360)(?:\\s*[,+|\\s+"
                    + "]\\s*)(?<saturation>\\d{1,2}|100)(?:\\s*%?\\s*[,+|\\s+]\\s*"
                    + ")(?<light>\\d{1,2}|100)(?:\\s*%?[,+|\\s+]\\s*)(?<alpha>"
                    + "0(?:\\.\\d{1,2})?|0?(?:\\.\\d{1,2})|1(?:\\.0{1,2})?)"
                    + "\\s*\\)$");
}
