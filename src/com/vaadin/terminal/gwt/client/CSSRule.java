/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Utility class for fetching CSS properties from DOM StyleSheets JS object.
 */
public class CSSRule {

    private final String selector;
    private JavaScriptObject rules = null;

    /**
     * 
     * @param selector
     *            the CSS selector to search for in the stylesheets
     * @param deep
     *            should the search follow any @import statements?
     */
    public CSSRule(final String selector, final boolean deep) {
        this.selector = selector;
        fetchRule(selector, deep);
    }

    // TODO how to find the right LINK-element? We should probably give the
    // stylesheet a name.
    private native void fetchRule(final String selector, final boolean deep) /*-{
        var sheets = $doc.styleSheets;
        for(var i = 0; i < sheets.length; i++) {
        var sheet = sheets[i];
        if(sheet.href && sheet.href.indexOf("VAADIN/themes")>-1) {
        this.@com.vaadin.terminal.gwt.client.CSSRule::rules = @com.vaadin.terminal.gwt.client.CSSRule::searchForRule(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Z)(sheet, selector, deep);
        return;
        }
        }
        this.@com.vaadin.terminal.gwt.client.CSSRule::rules = [];
    }-*/;

    /*
     * Loops through all current style rules and collects all matching to
     * 'rules' array. The array is reverse ordered (last one found is first).
     */
    private static native JavaScriptObject searchForRule(
            final JavaScriptObject sheet, final String selector,
            final boolean deep) /*-{
        if(!$doc.styleSheets)
        return null;

        selector = selector.toLowerCase();

        var allMatches = [];

        // IE handles imported sheet differently
        if(deep && sheet.imports && sheet.imports.length > 0) {
        for(var i=0; i < sheet.imports.length; i++) {
        var imports = @com.vaadin.terminal.gwt.client.CSSRule::searchForRule(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Z)(sheet.imports[i], selector, deep);
        allMatches.concat(imports);
        }
        }

        var theRules = new Array();
        if (sheet.cssRules)
        theRules = sheet.cssRules
        else if (sheet.rules)
        theRules = sheet.rules

        var j = theRules.length;
        for(var i=0; i<j; i++) {
        var r = theRules[i];
        if(r.type == 1 || sheet.imports) {
        var selectors = r.selectorText.toLowerCase().split(",");
        var n = selectors.length;
        for(var m=0; m<n; m++) {
        if(selectors[m].replace(/^\s+|\s+$/g, "") == selector) {
        allMatches.unshift(r);
        break; // No need to loop other selectors for this rule
        }
        }
        } else if(deep && r.type == 3) {
        // Search @import stylesheet
        var imports = @com.vaadin.terminal.gwt.client.CSSRule::searchForRule(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Z)(r.styleSheet, selector, deep);
        allMatches = allMatches.concat(imports);
        }
        }

        return allMatches;
    }-*/;

    /**
     * Returns a specific property value from this CSS rule.
     * 
     * @param propertyName
     *            camelCase CSS property name
     * @return the value of the property as a String
     */
    public native String getPropertyValue(final String propertyName) /*-{
        var j = this.@com.vaadin.terminal.gwt.client.CSSRule::rules.length;
        for(var i=0; i<j; i++) {
        var value = this.@com.vaadin.terminal.gwt.client.CSSRule::rules[i].style[propertyName];
        if(value)
        return value;
        }
        return null;
    }-*/;

    public String getSelector() {
        return selector;
    }

}
