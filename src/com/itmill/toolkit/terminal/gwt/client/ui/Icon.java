/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;

public class Icon extends UIObject {
    private final ApplicationConnection client;
    private String myUri;

    public Icon(ApplicationConnection client) {
        setElement(DOM.createImg());
        DOM.setElementProperty(getElement(), "alt", "icon");
        setStyleName("i-icon");
        this.client = client;
        BrowserInfo b = BrowserInfo.get();
        if (b.isIE6()) {
            addPngFix(getElement(), client.getThemeUri()
                    + "/../default/common/img/blank.gif");
        }
    }

    public Icon(ApplicationConnection client, String uidlUri) {
        this(client);
        setUri(uidlUri);
    }

    public void setUri(String uidlUri) {
        if (!uidlUri.equals(myUri)) {
            String uri = client.translateToolkitUri(uidlUri);
            DOM.setElementProperty(getElement(), "src", uri);
            myUri = uidlUri;
        }
    }

    private native static void addPngFix(Element el, String blankImageUrl)
    /*-{
        el.attachEvent("onload", function() {
            var src = el.src;
            if (src.indexOf(".png")<1) return;
            var w = el.width||16; 
            var h = el.height||16;
            el.src =blankImageUrl;
            el.style.height = h+"px";
            el.style.width = w+"px";
            el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+src+"', sizingMethod='crop');";  
        },false);
    }-*/;

}
