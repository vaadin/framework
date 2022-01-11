/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4);
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
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
 */
public class VHtml5File extends JavaScriptObject {

    protected VHtml5File() {
    }

    public native final String getName()
    /*-{
        return this.name;
     }-*/;

    public native final String getType()
    /*-{
        return this.type;
     }-*/;

    /*
     * Browser implementations support files >2GB dropped and report the value
     * as long. Due to JSNI limitations this value needs to be sent as double
     * and then cast back to a long value.
     * www.gwtproject.org/doc/latest/DevGuideCodingBasicsJSNI.html#important
     */
    public native final double getSize()
    /*-{
        return this.size ? this.size : 0;
    }-*/;

}
