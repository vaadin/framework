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

package com.vaadin.sass.internal.util;

import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.internal.parser.LexicalUnitImpl;

public class ColorUtil {
    public static LexicalUnitImpl hexColorToHsl(LexicalUnitImpl hexColor) {
        String s = hexColor.getStringValue().substring(1);
        int r = 0, g = 0, b = 0;
        if (s.length() == 3) {
            String sh = s.substring(0, 1);
            r = Integer.parseInt(sh + sh, 16);
            sh = s.substring(1, 2);
            g = Integer.parseInt(sh + sh, 16);
            sh = s.substring(2, 3);
            b = Integer.parseInt(sh + sh, 16);
        } else if (s.length() == 6) {
            r = Integer.parseInt(s.substring(0, 2), 16);
            g = Integer.parseInt(s.substring(2, 4), 16);
            b = Integer.parseInt(s.substring(4, 6), 16);
        }
        int hsl[] = calculateHsl(r, g, b);

        LexicalUnitImpl hslParams = createHslParameters(hsl[0], hsl[1], hsl[2],
                hexColor.getLineNumber(), hexColor.getColumnNumber(),
                hexColor.getPreviousLexicalUnit());

        return LexicalUnitImpl.createFunction(hexColor.getLineNumber(),
                hexColor.getColumnNumber(), hexColor.getPreviousLexicalUnit(),
                "hsl", hslParams);
    }

    public static LexicalUnitImpl hslToHexColor(LexicalUnitImpl hsl, int lengh) {
        int[] rgb = calculateRgb(hsl);
        StringBuilder builder = new StringBuilder("#");
        for (int i = 0; i < 3; i++) {
            String color = Integer.toHexString(rgb[i]);
            if (lengh == 6) {
                if (color.length() == 1) {
                    color = "0" + color;
                }
            }
            if (lengh == 3) {
                color = color.substring(0, 1);
            }
            builder.append(color);
        }
        return LexicalUnitImpl.createIdent(hsl.getLineNumber(),
                hsl.getColumnNumber(), hsl.getPreviousLexicalUnit(),
                builder.toString());
    }

    private static int[] calculateRgb(LexicalUnitImpl hsl) {
        LexicalUnitImpl hslParam = hsl.getParameters();
        LexicalUnitImpl hue = null;
        LexicalUnitImpl saturation = null;
        LexicalUnitImpl lightness = null;
        int i = 0;
        while (i < 5) {
            switch (i) {
            case 0:
                hue = hslParam;
                break;
            case 2:
                saturation = hslParam;
                break;
            case 4:
                lightness = hslParam;
                break;
            case 1:
            case 3:
                break;
            }
            hslParam = hslParam.getNextLexicalUnit();
            i++;
        }
        float h = ((hue.getIntegerValue() % 360) + 360) % 360 / 360f;
        float s = saturation.getFloatValue() / 100;
        float l = lightness.getFloatValue() / 100;
        float m2, m1;
        int[] rgb = new int[3];
        m2 = l <= 0.5 ? l * (s + 1) : l + s - l * s;
        m1 = l * 2 - m2;
        rgb[0] = Math.round(hueToRgb(m1, m2, h + 1f / 3) * 255);
        rgb[1] = Math.round(hueToRgb(m1, m2, h) * 255);
        rgb[2] = Math.round(hueToRgb(m1, m2, h - 1f / 3) * 255);
        return rgb;
    }

    public static LexicalUnitImpl rgbToHsl(LexicalUnitImpl rgb) {
        LexicalUnitImpl rgbParam = rgb.getParameters();
        LexicalUnitImpl red = null;
        LexicalUnitImpl green = null;
        LexicalUnitImpl blue = null;
        int i = 0;
        while (i < 5) {
            switch (i) {
            case 0:
                red = rgbParam;
                break;
            case 2:
                green = rgbParam;
                break;
            case 4:
                blue = rgbParam;
                break;
            case 1:
            case 3:
                break;
            }
            rgbParam = rgbParam.getNextLexicalUnit();
            i++;
        }

        int hsl[] = calculateHsl(red.getIntegerValue(),
                green.getIntegerValue(), blue.getIntegerValue());

        rgbParam = rgb.getParameters();

        LexicalUnitImpl hslParams = createHslParameters(hsl[0], hsl[1], hsl[2],
                rgbParam.getLineNumber(), rgbParam.getColumnNumber(),
                rgbParam.getPreviousLexicalUnit());

        return LexicalUnitImpl.createFunction(rgb.getLineNumber(),
                rgb.getColumnNumber(), rgb.getPreviousLexicalUnit(), "hsl",
                hslParams);
    }

    private static int[] calculateHsl(int red, int green, int blue) {
        int[] hsl = new int[3];

        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float d = max - min;

        float h = 0f, s = 0f, l = 0f;

        if (max == min) {
            h = 0;
        }
        if (max == r) {
            h = 60 * (g - b) / d;
        } else if (max == g) {
            h = 60 * (b - r) / d + 120;
        } else if (max == b) {
            h = 60 * (r - g) / d + 240;
        }

        l = (max + min) / 2f;

        if (max == min) {
            s = 0;
        } else if (l < 0.5) {
            s = d / (2 * l);
        } else {
            s = d / (2 - 2 * l);
        }

        hsl[0] = Math.round(h % 360);
        hsl[1] = Math.round(s * 100);
        hsl[2] = Math.round(l * 100);

        return hsl;
    }

    public static LexicalUnitImpl hslToRgb(LexicalUnitImpl hsl) {
        int[] rgb = calculateRgb(hsl);
        LexicalUnitImpl hslParam = hsl.getParameters();
        LexicalUnitImpl rgbParams = createRgbParameters(rgb[0], rgb[1], rgb[2],
                hslParam.getLineNumber(), hslParam.getColumnNumber(),
                hslParam.getPreviousLexicalUnit());

        return LexicalUnitImpl.createFunction(hsl.getLineNumber(),
                hsl.getColumnNumber(), hsl.getPreviousLexicalUnit(), "rgb",
                rgbParams);
    }

    private static float hueToRgb(float m1, float m2, float h) {
        if (h < 0) {
            h = h + 1;
        }
        if (h > 1) {
            h = h - 1;
        }
        if (h * 6 < 1) {
            return m1 + (m2 - m1) * h * 6;
        }
        if (h * 2 < 1) {
            return m2;
        }
        if (h * 3 < 2) {
            return m1 + (m2 - m1) * (2f / 3 - h) * 6;
        }
        return m1;
    }

    private static LexicalUnitImpl createRgbParameters(int r, int g, int b,
            int ln, int cn, LexicalUnitImpl prev) {
        LexicalUnitImpl red = LexicalUnitImpl.createInteger(ln, cn, prev, r);
        LexicalUnitImpl firstComma = LexicalUnitImpl.createComma(ln, cn, red);
        LexicalUnitImpl green = LexicalUnitImpl.createInteger(ln, cn,
                firstComma, g);
        LexicalUnitImpl secondComma = LexicalUnitImpl
                .createComma(ln, cn, green);
        LexicalUnitImpl.createInteger(ln, cn, secondComma, b);
        return red;
    }

    private static LexicalUnitImpl createHslParameters(int h, int s, int l,
            int ln, int cn, LexicalUnitImpl prev) {
        LexicalUnitImpl hue = LexicalUnitImpl.createInteger(ln, cn, prev, h);
        LexicalUnitImpl firstComma = LexicalUnitImpl.createComma(ln, cn, hue);
        LexicalUnitImpl saturation = LexicalUnitImpl.createPercentage(ln, cn,
                firstComma, s);
        LexicalUnitImpl secondComma = LexicalUnitImpl.createComma(ln, cn,
                saturation);
        LexicalUnitImpl.createPercentage(ln, cn, secondComma, l);
        return hue;
    }

    public static LexicalUnitImpl darken(LexicalUnitImpl darkenFunc) {
        LexicalUnitImpl color = darkenFunc.getParameters();
        float amount = getAmountValue(color);
        LexicalUnitImpl pre = darkenFunc.getPreviousLexicalUnit();

        return adjust(color, amount, ColorOperation.Darken, pre);
    }

    private static LexicalUnitImpl adjust(LexicalUnitImpl color,
            float amountByPercent, ColorOperation op, LexicalUnitImpl pre) {
        if (color.getLexicalUnitType() == LexicalUnit.SAC_FUNCTION) {
            LexicalUnit funcParam = color.getParameters();
            if ("hsl".equals(color.getFunctionName())) {
                LexicalUnit lightness = funcParam;
                for (int index = 0; index < 4; index++) {
                    lightness = lightness.getNextLexicalUnit();
                }
                float newValue = 0f;
                if (op == ColorOperation.Darken) {
                    newValue = lightness.getFloatValue() - amountByPercent;
                    newValue = newValue < 0 ? 0 : newValue;
                } else if (op == ColorOperation.Lighten) {
                    newValue = lightness.getFloatValue() + amountByPercent;
                    newValue = newValue > 100 ? 100 : newValue;
                }
                ((LexicalUnitImpl) lightness).setFloatValue(newValue);
                return LexicalUnitImpl.createFunction(color.getLineNumber(),
                        color.getColumnNumber(), pre, color.getFunctionName(),
                        funcParam);
            }

        } else if (color.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
            if (color.getStringValue().startsWith("#")) {
                return hslToHexColor(
                        adjust(hexColorToHsl(color), amountByPercent, op, pre),
                        color.getStringValue().substring(1).length());
            }
        } else if (color.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR) {
            LexicalUnitImpl hsl = rgbToHsl(color);
            LexicalUnitImpl hslAfterDarken = adjust(hsl, amountByPercent, op,
                    pre);
            return hslToRgb(hslAfterDarken);
        }
        return color;
    }

    public static LexicalUnitImpl lighten(LexicalUnitImpl lightenFunc) {
        LexicalUnitImpl color = lightenFunc.getParameters();
        float amount = getAmountValue(color);
        LexicalUnitImpl pre = lightenFunc.getPreviousLexicalUnit();

        return adjust(color, amount, ColorOperation.Lighten, pre);
    }

    private static float getAmountValue(LexicalUnitImpl color) {
        LexicalUnit next = color.getNextLexicalUnit();
        float amount = 10f;
        if (next != null && next.getNextLexicalUnit() != null) {
            next = next.getNextLexicalUnit();
            amount = next.getFloatValue();
        }
        return amount;
    }

    enum ColorOperation {
        Darken, Lighten
    }
}
