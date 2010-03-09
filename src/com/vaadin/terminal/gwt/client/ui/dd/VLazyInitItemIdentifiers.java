/*
@ITMillApache2LicenseForJavaFiles@
 */
/**
 * 
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.HashSet;

import com.vaadin.terminal.gwt.client.UIDL;

/**
 * 
 */
final public class VLazyInitItemIdentifiers extends VAcceptCriterion {
    private boolean loaded = false;
    private HashSet<String> hashSet;
    private VDragEvent lastDragEvent;

    @Override
    public void accept(final VDragEvent drag, UIDL configuration,
            final VAcceptCallback callback) {
        if (lastDragEvent == null || lastDragEvent != drag) {
            loaded = false;
            lastDragEvent = drag;
        }
        if (loaded) {
            Object object = drag.getDropDetails().get("itemIdOver");
            if (hashSet.contains(object)) {
                callback.accepted(drag);
            }
        } else {

            VDragEventServerCallback acceptCallback = new VDragEventServerCallback() {

                public void handleResponse(boolean accepted, UIDL response) {
                    hashSet = new HashSet<String>();
                    String[] stringArrayAttribute = response
                            .getStringArrayAttribute("allowedIds");
                    for (int i = 0; i < stringArrayAttribute.length; i++) {
                        hashSet.add(stringArrayAttribute[i]);
                    }
                    loaded = true;
                    if (accepted) {
                        callback.accepted(drag);
                    }
                }
            };

            VDragAndDropManager.get().visitServer(acceptCallback);
        }

    }

    @Override
    public boolean needsServerSideCheck(VDragEvent drag, UIDL criterioUIDL) {
        return loaded;
    }

    @Override
    public boolean validates(VDragEvent drag, UIDL configuration) {
        return false; // not used is this implementation
    }
}