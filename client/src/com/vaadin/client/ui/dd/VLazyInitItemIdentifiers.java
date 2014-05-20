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
/**
 * 
 */
package com.vaadin.client.ui.dd;

import java.util.HashSet;

import com.vaadin.client.UIDL;
import com.vaadin.shared.ui.dd.AcceptCriterion;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

/**
 * 
 */
public class VLazyInitItemIdentifiers extends VAcceptCriterion {
    private boolean loaded = false;
    private HashSet<String> hashSet;
    private VDragEvent lastDragEvent;

    @AcceptCriterion(Table.TableDropCriterion.class)
    final public static class VTableLazyInitItemIdentifiers extends
            VLazyInitItemIdentifiers {
        // all logic in superclass
    }

    @AcceptCriterion(Tree.TreeDropCriterion.class)
    final public static class VTreeLazyInitItemIdentifiers extends
            VLazyInitItemIdentifiers {
        // all logic in superclass
    }

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

                @Override
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
    protected boolean accept(VDragEvent drag, UIDL configuration) {
        return false; // not used is this implementation
    }
}
