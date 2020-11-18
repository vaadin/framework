/*
 * Copyright 2000-2020 Vaadin Ltd.
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
package com.vaadin.tests.components.progressindicator;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.themes.Runo;

@Theme(Runo.THEME_NAME)
public class ProgressBarStaticRuno extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.addStyleName(Runo.PROGRESSBAR_STATIC);
        addComponent(progressBar);
    }
}
