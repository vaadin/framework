/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.dd;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.UI;

public class SpacebarPanner extends AbstractExtension {

    private static final long serialVersionUID = -7712258690917457123L;

    public static SpacebarPanner wrap(UI ui) {
        SpacebarPanner panner = new SpacebarPanner();
        panner.extend(ui);
        return panner;
    }

    public void interruptNext() {
        getState().enabled = !getState().enabled;
    }

}
