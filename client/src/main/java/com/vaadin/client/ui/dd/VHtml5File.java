/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.client.ui.dd;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wrapper for html5 File object.
 *
 * @author Vaadin Ltd
 * @deprecated Since 8.1, will be replaced by FileDropTargetExtensionConnector
 *             and FileDropTargetExtension,
 *             https://github.com/vaadin/framework/issues/8891
 */
@Deprecated
public class VHtml5File extends JavaScriptObject {

    protected VHtml5File() {
    }

    public final native String getName()
    /*-{
        return this.name;
     }-*/;

    public final native String getType()
    /*-{
        return this.type;
     }-*/;

    /*
     * Browser implementations support files >2GB dropped and report the value
     * as long. Due to JSNI limitations this value needs to be sent as double
     * and then cast back to a long value.
     * www.gwtproject.org/doc/latest/DevGuideCodingBasicsJSNI.html#important
     */
    public final native double getSize()
    /*-{
        return this.size ? this.size : 0;
    }-*/;

}
