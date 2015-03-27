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
package com.vaadin.tests.widgetset.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.util.ResizeTerrorizer;

@Connect(ResizeTerrorizer.ResizeTerrorizerControl.class)
public class ResizeTerrorizerControlConnector extends
        AbstractComponentConnector implements PostLayoutListener {

    public static class ResizeTerorrizerState extends AbstractComponentState {
        public Connector target;
        public int defaultWidthOffset = 200;
    }

    public class ResizeTerrorizerControlPanel extends FlowPanel {
        private Label results = new Label("Results");
        private IntegerBox startWidth = new IntegerBox();
        private IntegerBox endWidth = new IntegerBox();
        private final Button terrorizeButton = new Button("Terrorize",
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        terrorize(startWidth.getValue(), endWidth.getValue(),
                                1000);
                    }
                });

        public ResizeTerrorizerControlPanel() {
            add(new Label("Start width"));
            add(startWidth);

            add(new Label("End width"));
            add(endWidth);

            add(terrorizeButton);
            add(results);

            startWidth.getElement().setId("terror-start-width");
            endWidth.getElement().setId("terror-end-width");
            terrorizeButton.getElement().setId("terror-button");
            results.getElement().setId("terror-results");
        }

        private void showResults(String results) {
            Integer temp = startWidth.getValue();
            startWidth.setValue(endWidth.getValue());
            endWidth.setValue(temp);

            this.results.setText(results);
        }
    }

    private void terrorize(final double startWidth, final double endWidth,
            final double duration) {
        final AbstractComponentConnector target = getTarget();

        final AnimationScheduler scheduler = AnimationScheduler.get();
        AnimationCallback callback = new AnimationCallback() {
            double startTime = -1;
            int frameCount = 0;

            @Override
            public void execute(double timestamp) {
                frameCount++;

                boolean done = false;
                if (startTime == -1) {
                    startTime = timestamp;
                }

                double time = timestamp - startTime;
                if (time > duration) {
                    time = duration;
                    done = true;
                }

                double progress = time / duration;
                double widthToSet = startWidth + (endWidth - startWidth)
                        * progress;

                // TODO Optionally inform LayoutManager as well
                target.getWidget().setWidth(widthToSet + "px");
                if (target.getWidget() instanceof RequiresResize) {
                    ((RequiresResize) target.getWidget()).onResize();
                }

                if (!done) {
                    scheduler.requestAnimationFrame(this);
                } else {
                    double fps = Math.round(frameCount / (duration / 1000));
                    String results = frameCount + " frames, " + fps + " fps";

                    getWidget().showResults(results);
                }
            }
        };
        scheduler.requestAnimationFrame(callback);
    }

    private AbstractComponentConnector getTarget() {
        return (AbstractComponentConnector) getState().target;
    }

    @Override
    public ResizeTerorrizerState getState() {
        return (ResizeTerorrizerState) super.getState();
    }

    @Override
    public ResizeTerrorizerControlPanel getWidget() {
        return (ResizeTerrorizerControlPanel) super.getWidget();
    }

    @Override
    protected ResizeTerrorizerControlPanel createWidget() {
        return new ResizeTerrorizerControlPanel();
    }

    @Override
    public void postLayout() {
        if (getWidget().startWidth.getValue() == null) {
            int width = getTarget().getWidget().getElement().getOffsetWidth();
            getWidget().startWidth.setValue(width);
            getWidget().endWidth
                    .setValue(width + getState().defaultWidthOffset);
        }
    }

}
