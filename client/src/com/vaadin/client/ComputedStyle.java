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
package com.vaadin.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class ComputedStyle {

    protected final JavaScriptObject computedStyle;
    private final Element elem;

    /**
     * Gets this element's computed style object which can be used to gather
     * information about the current state of the rendered node.
     * <p>
     * Note that this method is expensive. Wherever possible, reuse the returned
     * object.
     * 
     * @param elem
     *            the element
     * @return the computed style
     */
    public ComputedStyle(Element elem) {
        computedStyle = getComputedStyle(elem);
        this.elem = elem;
    }

    private static native JavaScriptObject getComputedStyle(Element elem)
    /*-{
      if(elem.nodeType != 1) {
          return {};
      }
      
      if($wnd.document.defaultView && $wnd.document.defaultView.getComputedStyle) {
          return $wnd.document.defaultView.getComputedStyle(elem, null);
      }
      
      if(elem.currentStyle) {
          return elem.currentStyle;
      }
    }-*/;

    /**
     * 
     * @param name
     *            name of the CSS property in camelCase
     * @return the value of the property, normalized for across browsers (each
     *         browser returns pixel values whenever possible).
     */
    public final native String getProperty(String name)
    /*-{
        var cs = this.@com.vaadin.client.ComputedStyle::computedStyle;
        var elem = this.@com.vaadin.client.ComputedStyle::elem;
        
        // Border values need to be checked separately. The width might have a 
        // meaningful value even if the border style is "none". In that case the 
        // value should be 0.
        if(name.indexOf("border") > -1 && name.indexOf("Width") > -1) {
            var borderStyleProp = name.substring(0,name.length-5) + "Style";
            if(cs.getPropertyValue)
                var borderStyle = cs.getPropertyValue(borderStyleProp);
            else // IE
                var borderStyle = cs[borderStyleProp];
            if(borderStyle == "none")
                return "0px";
        }

        if(cs.getPropertyValue) {
        
            // Convert name to dashed format
            name = name.replace(/([A-Z])/g, "-$1").toLowerCase();
            var ret = cs.getPropertyValue(name);
            
        } else {
        
            var ret = cs[name];
            var style = elem.style;

            // From the awesome hack by Dean Edwards
            // http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291

            // If we're not dealing with a regular pixel number
            // but a number that has a weird ending, we need to convert it to pixels
                if ( !/^\d+(px)?$/i.test( ret ) && /^\d/.test( ret ) ) {
                    // Remember the original values
                    var left = style.left, rsLeft = elem.runtimeStyle.left;
    
                    // Put in the new values to get a computed value out
                    elem.runtimeStyle.left = cs.left;
                    style.left = ret || 0;
                    ret = style.pixelLeft + "px";
    
                    // Revert the changed values
                    style.left = left;
                    elem.runtimeStyle.left = rsLeft;
                }
            
        }
        
        // Normalize margin values. This is not totally valid, but in most cases 
        // it is what the user wants to know.
        if(name.indexOf("margin") > -1 && ret == "auto") {
            return "0px";
        }
        
        // Some browsers return undefined width and height values as "auto", so
        // we need to retrieve those ourselves.
        if (name == "width" && ret == "auto") {
            ret = elem.clientWidth + "px";
        } else if (name == "height" && ret == "auto") {
            ret = elem.clientHeight + "px";
        }

        return ret;
        
    }-*/;

    public final int getIntProperty(String name) {
        Profiler.enter("ComputedStyle.getIntProperty");
        String value = getProperty(name);
        int result = parseIntNative(value);
        Profiler.leave("ComputedStyle.getIntProperty");
        return result;
    }

    /**
     * Get current margin values from the DOM. The array order is the default
     * CSS order: top, right, bottom, left.
     */
    public final int[] getMargin() {
        int[] margin = { 0, 0, 0, 0 };
        margin[0] = getIntProperty("marginTop");
        margin[1] = getIntProperty("marginRight");
        margin[2] = getIntProperty("marginBottom");
        margin[3] = getIntProperty("marginLeft");
        return margin;
    }

    /**
     * Get current padding values from the DOM. The array order is the default
     * CSS order: top, right, bottom, left.
     */
    public final int[] getPadding() {
        int[] padding = { 0, 0, 0, 0 };
        padding[0] = getIntProperty("paddingTop");
        padding[1] = getIntProperty("paddingRight");
        padding[2] = getIntProperty("paddingBottom");
        padding[3] = getIntProperty("paddingLeft");
        return padding;
    }

    /**
     * Get current border values from the DOM. The array order is the default
     * CSS order: top, right, bottom, left.
     */
    public final int[] getBorder() {
        int[] border = { 0, 0, 0, 0 };
        border[0] = getIntProperty("borderTopWidth");
        border[1] = getIntProperty("borderRightWidth");
        border[2] = getIntProperty("borderBottomWidth");
        border[3] = getIntProperty("borderLeftWidth");
        return border;
    }

    /**
     * Takes a String value e.g. "12px" and parses that to Integer 12.
     * 
     * @param String
     *            a value starting with a number
     * @return Integer the value from the string before any non-numeric
     *         characters. If the value cannot be parsed to a number, returns
     *         <code>null</code>.
     * 
     * @deprecated Since 7.1.4, the method {@link #parseIntNative(String)} is
     *             used internally and this method does not belong in the public
     *             API of {@link ComputedStyle}. {@link #parseInt(String)} might
     *             be removed or moved to a utility class in future versions.
     */
    @Deprecated
    public static native Integer parseInt(final String value)
    /*-{
        var number = parseInt(value, 10);
        if (isNaN(number))
            return null;
        else
            // $entry not needed as function is not exported
            return @java.lang.Integer::valueOf(I)(number);
    }-*/;

    /**
     * Takes a String value e.g. "12px" and parses that to int 12.
     * 
     * <p>
     * This method returns 0 for <code>NaN</code>.
     * 
     * @param String
     *            a value starting with a number
     * @return int the value from the string before any non-numeric characters.
     *         If the value cannot be parsed to a number, returns 0.
     */
    private static native int parseIntNative(final String value)
    /*-{
        var number = parseInt(value, 10);
        if (isNaN(number))
            return 0;
        else
            return number;
    }-*/;

}
