/*
 * Copyright 2000-2021 Vaadin Ltd.
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

package com.vaadin.tests.components.media;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.PreloadMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Video;

public class Media extends TestBase {

    @Override
    protected void setup() {
        final Video v = new Video("video");
        v.setSources(
                new ExternalResource(
                        "http://techslides.com/demos/sample-videos/small.ogv"));
        v.setWidth("560px");
        v.setHeight("320px");
        addComponent(v);
        addComponent(new Button("Play video", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                v.play();
            }

        }));
        addComponent(new Button("Pause video", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                v.pause();
            }

        }));
        final CheckBox loop = new CheckBox("Loop");
        loop.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                v.setLoop(loop.booleanValue());
            }
        });
        addComponent(loop);
        v.setPreload(PreloadMode.METADATA);

        final Audio a = new Audio("audio");
        a.setSources(
                new ExternalResource(
                        "http://www.sample-videos.com/audio/mp3/crowd-cheering.mp3"));
        addComponent(a);

        addComponent(new Button("Play audio", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                a.play();
            }

        }));
        addComponent(new Button("Pause audio", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                a.pause();
            }

        }));
    }

    @Override
    protected String getDescription() {
        return "Video and audio files should play using the HTML5 elements. "
                + "(Movie is from http://techslides.com/sample-webm-ogg-and-mp4-video-files-for-html5)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6909;
    }

}
