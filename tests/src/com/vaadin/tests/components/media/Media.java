package com.vaadin.tests.components.media;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Video;

public class Media extends TestBase {

    @Override
    protected void setup() {
        final Video v = new Video("video");
        v.setSources(
                new ExternalResource(
                        "http://jonatan.virtuallypreinstalled.com/media/big_buck_bunny.mp4"),
                new ExternalResource(
                        "http://jonatan.virtuallypreinstalled.com/media/big_buck_bunny.ogv"));
        v.setWidth("640px");
        v.setHeight("360px");
        addComponent(v);
        addComponent(new Button("Play video", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                v.play();
            }

        }));
        addComponent(new Button("Pause video", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                v.pause();
            }

        }));

        final Audio a = new Audio("audio");
        a.setSources(
                new ExternalResource(
                        "http://jonatan.virtuallypreinstalled.com/media/audio.mp3"),
                new ExternalResource(
                        "http://jonatan.virtuallypreinstalled.com/media/audio.ogg"));
        addComponent(a);

        addComponent(new Button("Play audio", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                a.play();
            }

        }));
        addComponent(new Button("Pause audio", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                a.pause();
            }

        }));
    }

    @Override
    protected String getDescription() {
        return "Video and audio files should play using the HTML5 elements. "
                + "(Movie is (c) copyright 2008, Blender Foundation / www.bigbuckbunny.org)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6909;
    }

}
