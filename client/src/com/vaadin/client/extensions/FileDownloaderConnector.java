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

package com.vaadin.client.extensions;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.server.FileDownloader;
import com.vaadin.shared.ui.Connect;

@Connect(FileDownloader.class)
public class FileDownloaderConnector extends AbstractExtensionConnector
        implements ClickHandler {

    private IFrameElement iframe;

    @Override
    protected void extend(ServerConnector target) {
        final Widget downloadWidget = ((ComponentConnector) target).getWidget();

        downloadWidget.addDomHandler(this, ClickEvent.getType());
    }

    @Override
    public void onClick(ClickEvent event) {
        final String url = getResourceUrl("dl");
        if (url != null && !url.isEmpty()) {
            if (iframe != null) {
                // make sure it is not on dom tree already, might start
                // multiple downloads at once
                iframe.removeFromParent();
            }
            iframe = Document.get().createIFrameElement();

            Style style = iframe.getStyle();
            style.setVisibility(Visibility.HIDDEN);
            style.setHeight(0, Unit.PX);
            style.setWidth(0, Unit.PX);

            iframe.setFrameBorder(0);
            iframe.setTabIndex(-1);
            iframe.setSrc(url);
            RootPanel.getBodyElement().appendChild(iframe);
        }
    }

    @Override
    public void setParent(ServerConnector parent) {
        super.setParent(parent);
        if (parent == null && iframe != null) {
            iframe.removeFromParent();
        }
    }

}
