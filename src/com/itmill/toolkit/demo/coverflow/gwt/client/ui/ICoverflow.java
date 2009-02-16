/* 
@ITMillApache2LicenseForJavaFiles@
 */
 
package com.itmill.toolkit.demo.coverflow.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICoverflow extends Composite implements Paintable {
    private String uidlId;
    protected ApplicationConnection client;
    private ArrayList coverList = new ArrayList();

    private Object _selected;
    private boolean flashInited = false;
    private HTML flash;
    private boolean scrollbarVisibility = true;
    private String backgroundGradientStart;
    private String backgroundGradientEnd;
    private boolean colorChanged = false;
    private boolean sbVisibilityChanged = false;
    private HashMap keyMap = new HashMap();

    /**
     * Constructor
     */
    public ICoverflow() {
        flash = new HTML();

        initWidget(flash);
    }

    /**
     * This method accepts parses the uidl sent by the server
     * 
     * @param UIDL
     *            uidl
     * @param ApplicationConnection
     *            client
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Store variables
        uidlId = uidl.getId();
        this.client = client;
        String tempColor;

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Has the scrollbar's visibility status changed?
        if (uidl.hasAttribute("scrollbarVisibility")) {
            boolean tempVis = uidl.getBooleanAttribute("scrollbarVisibility");
            if (scrollbarVisibility != tempVis) {
                scrollbarVisibility = tempVis;
                sbVisibilityChanged = true;
            }
        }

        // Has the start gradient changed?
        if (uidl.hasAttribute("backgroundGradientStart")) {
            tempColor = uidl.getStringAttribute("backgroundGradientStart")
                    .toString();
            if (tempColor != backgroundGradientStart) {
                backgroundGradientStart = tempColor;
                colorChanged = true;
            }
        }

        // Has the end gradient changed?
        if (uidl.hasAttribute("backgroundGradientEnd")) {
            tempColor = uidl.getStringAttribute("backgroundGradientEnd")
                    .toString();
            if (tempColor != backgroundGradientEnd) {
                backgroundGradientEnd = tempColor;
                colorChanged = true;
            }
        }

        final UIDL images = uidl.getChildUIDL(0);

        // Check which covers should be removed. This array list contains all
        // current
        // covers. We remove from this list all covers which are sent with the
        // repainted
        // uidl. All remaining covers in this list should be "old" ones and are
        // should
        // be deleted.

        ArrayList newList = new ArrayList();

        // Iterate through all option elements
        for (final Iterator i = images.getChildIterator(); i.hasNext();) {
            final UIDL imgUidl = (UIDL) i.next();

            // Make sure all required attributes exist
            if (imgUidl.hasAttribute("caption") && imgUidl.hasAttribute("key")
                    && imgUidl.hasAttribute("icon")) {
                HashMap set = new HashMap();

                // Update the key map
                keyMap.put(imgUidl.getStringAttribute("caption"), imgUidl
                        .getStringAttribute("key"));

                // Get information

                set.put("icon", client.translateToolkitUri(imgUidl
                        .getStringAttribute("icon")));
                set.put("caption", imgUidl.getStringAttribute("caption"));

                newList.add(set);

                // Is the current cover selected?
                if (imgUidl.hasAttribute("selected")) {
                    _selected = imgUidl.getStringAttribute("caption");
                }
            }
        }

        // Deleted items
        ArrayList intersectList = new ArrayList();
        intersectList.addAll(coverList);
        intersectList.removeAll(newList);

        if (flashInited) {
            for (int i = 0; i < intersectList.size(); i++) {
                HashMap cover = (HashMap) intersectList.get(i);
                removeCover(uidlId, cover.get("caption").toString());
            }
        }

        // Added items
        intersectList = new ArrayList();
        intersectList.addAll(newList);
        intersectList.removeAll(coverList);

        if (flashInited) {
            for (int i = 0; i < intersectList.size(); i++) {
                HashMap cover = (HashMap) intersectList.get(i);
                addCover(uidlId, cover.get("caption").toString(), cover.get(
                        "icon").toString());
            }
        }

        coverList = newList;

        // Has the flash been initialized?
        if (!flashInited) {
            colorChanged = false;
            setFlash();
            initializeMethods(uidlId);
        }

        // Inform flash of the selected cover
        if (_selected != null && flashInited) {
            selectCover(uidlId, _selected.toString());
        }

        if (colorChanged && flashInited) {
            setBackgroundColor(uidlId, backgroundGradientStart,
                    backgroundGradientEnd);
            colorChanged = false;
        }

        if (sbVisibilityChanged && flashInited) {
            toggleScrollbarVisibility(uidlId, scrollbarVisibility);
            sbVisibilityChanged = false;
        }

    }

    /**
     * Inform the server which cover is selected
     * 
     * @param String
     *            coverKey
     */
    public void setCover(String coverId) {
        if (uidlId == null || client == null) {
            return;
        }

        client.updateVariable(uidlId, "selected", new String[] { keyMap.get(
                coverId).toString() }, true);
    }

    /**
     * Initialize the native javascript functions needed for the flash <-> GWT
     * communication
     * 
     * @param String
     *            id
     */
    public native void initializeMethods(String id) /*-{
          var app = this;
          
          if($wnd.itmill.coverflow == null)
          	var coverflow = [];
          else
          	var coverflow = $wnd.itmill.coverflow;
          	
          coverflow['getCovers_' + id] = function() {                
             	app.@com.itmill.toolkit.demo.coverflow.gwt.client.ui.ICoverflow::getCovers()();
          };   
          
         	coverflow['setCurrent_' + id] = function(selected) {
             	app.@com.itmill.toolkit.demo.coverflow.gwt.client.ui.ICoverflow::setCover(Ljava/lang/String;)(selected);
          };
          
          $wnd.itmill.coverflow = coverflow;
      }-*/;

    /**
     * This function sends all covers to the flash. We cannot do this directly
     * in the updateFromUIDL method, because we cannot be sure if the flash has
     * been loaded into the browser. The flash will call for this method when
     * it's ready.
     */
    public void getCovers() {
        // Loop through all stored coves
        for (int i = 0; i < coverList.size(); i++) {
            HashMap set = (HashMap) coverList.get(i);

            try {
                // Add the cover
                addCover(uidlId, set.get("caption").toString(), set.get("icon")
                        .toString());
            } catch (Exception e) {
                // Do not add covers lacking obligatory data
            }
        }
        // The flash calls for this method, therefore we can be sure that the
        // flash has been loaded
        // into the browser.
        flashInited = true;

        // Set selected cover
        if (_selected != null) {
            selectCover(uidlId, _selected.toString());
        }
    }

    /**
     * This function is a native javascript function which adds covers to the
     * actual flash. This method works as a bridge between GWT and flash.
     * 
     * @param id
     * @param key
     * @param caption
     * @param icon
     */
    public native void addCover(String id, String caption, String icon) /*-{   
          try {
          	    $doc['fxcoverflow' + id].addCover(caption.toString(), icon.toString());
              }
              catch(e) {
                  $wnd.alert(e.message);
              }   
               	
          }-*/;

    /**
     * This function tells the flash which cover should be selected.
     * 
     * @param id
     * @param key
     */
    public native void selectCover(String id, String key) /*-{    
       	$doc["fxcoverflow" + id].selectCover(key.toString());
       }-*/;

    public native void setBackgroundColor(String id, String startGradient,
            String endGradient) /*-{    	
          	$doc["fxcoverflow" + id].setBackgroundColor("0x" + startGradient.toString(), "0x" + endGradient.toString());
      }-*/;

    public native void toggleScrollbarVisibility(String id, boolean visibility) /*-{    	
       	$doc["fxcoverflow" + id].toggleScrollbarVisibility(visibility);
       }-*/;

    public native void removeCover(String id, String key) /*-{    	
       	$doc["fxcoverflow" + id].removeCover(key);
       }-*/;

    /**
     * Set the HTML coding of the flash movie. This isn't done until the
     * updateFromUIDL method is called for the first time. The reason is that we
     * use an id from the UIDL to uniquely identify all instances of this
     * widget.
     */
    private void setFlash() {
        String html = "<object id=\"fxcoverflow"
                + uidlId
                + "\" classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" width=\"100%\""
                + " height=\"100%\" codebase=\"http://fpdownload.adobe.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0\">"
                + "<param name=\"movie\" value=\""
                + GWT.getModuleBaseURL()
                + "coverflowflash.swf\">"
                + "<param name=\"quality\" value=\"high\">"
                + "<param name=\"flashVars\" value=\"pid="
                + uidlId
                + "&sbVis="
                + scrollbarVisibility
                + "&bgS=0x"
                + backgroundGradientStart
                + "&bgE=0x"
                + backgroundGradientEnd
                + "\" />"
                + "<embed name=\"fxcoverflow"
                + uidlId
                + "\" flashVars=\"pid="
                + uidlId
                + "&sbVis="
                + scrollbarVisibility
                + "&bgS=0x"
                + backgroundGradientStart
                + "&bgE=0x"
                + backgroundGradientEnd
                + "\" src=\""
                + GWT.getModuleBaseURL()
                + "coverflowflash.swf\" width=\"100%\" height=\"100%\" "
                + "quality=\"high\" "
                + "pluginspage=\"http://www.adobe.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash\">"
                + "</embed>" + "</object>";
        flash.setHTML(html);
    }
}