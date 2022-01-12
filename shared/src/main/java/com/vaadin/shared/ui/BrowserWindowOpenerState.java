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

package com.vaadin.shared.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.AbstractComponentState;

public class BrowserWindowOpenerState extends AbstractComponentState {
    public static final String locationResource = "url";

    public String target = "_blank";

    public String features;

    public String uriFragment;

    public Map<String, String> parameters = new HashMap<String, String>();

}
