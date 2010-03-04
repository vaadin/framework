package com.vaadin.tests.util;

import com.vaadin.ui.Window;

public class TestUtils {

    /**
     * Crossbrowser hack to dynamically add css current window. Can be used to
     * keep tests css in source files.
     * 
     * @param cssString
     */
    public static void injectCSS(Window w, String cssString) {
        String script = "if ('\\v'=='v') /* ie only */ {\n"
                + "        document.createStyleSheet().cssText = '"
                + cssString
                + "';\n"
                + "    } else {var tag = document.createElement('style'); tag.type = 'text/css';"
                + " document.getElementsByTagName('head')[0].appendChild(tag);tag[ (typeof "
                + "document.body.style.WebkitAppearance=='string') /* webkit only */ ? 'innerText' "
                + ": 'innerHTML'] = '" + cssString + "';}";

        w.executeJavaScript(script);
    }

}
