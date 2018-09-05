package com.vaadin.tests.widgetset.client;

import java.util.Arrays;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;
import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.util.ResizeTerrorizer;

@Connect(ResizeTerrorizer.ResizeTerrorizerControl.class)
public class ResizeTerrorizerControlConnector extends AbstractComponentConnector
        implements PostLayoutListener {

    public static class ResizeTerorrizerState extends AbstractComponentState {
        public Connector target;
        public int defaultWidthOffset = 200;
        public int defaultHeightOffset = 200;
        @DelegateToWidget
        public boolean useUriFragments = true;
    }

    public class ResizeTerrorizerControlPanel extends FlowPanel {
        private Label results = new Label("Results");

        private IntegerBox startWidth = new IntegerBox();
        private IntegerBox endWidth = new IntegerBox();

        private IntegerBox startHeight = new IntegerBox();
        private IntegerBox endHeight = new IntegerBox();

        private final Button terrorizeButton = new Button("Terrorize",
                (ClickHandler) event -> terrorize(startWidth.getValue(),
                        endWidth.getValue(), startHeight.getValue(),
                        endHeight.getValue(), 1000));

        private HandlerRegistration historyHandlerRegistration;

        public ResizeTerrorizerControlPanel() {
            add(new Label("Start width"));
            add(startWidth);

            add(new Label("End width"));
            add(endWidth);

            add(new Label("Start height"));
            add(startHeight);

            add(new Label("End height"));
            add(endHeight);

            add(terrorizeButton);
            add(results);

            startWidth.getElement().setId("terror-start-width");
            endWidth.getElement().setId("terror-end-width");
            terrorizeButton.getElement().setId("terror-button");
            results.getElement().setId("terror-results");

            // Emulate button click from enter on any of the text boxes
            for (IntegerBox box : Arrays.asList(startWidth, endWidth,
                    startHeight, endHeight)) {
                box.addKeyUpHandler(event -> {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                        terrorizeButton.click();
                    }
                });
            }
        }

        private void showResults(String results) {
            Integer temp = startWidth.getValue();
            startWidth.setValue(endWidth.getValue());
            endWidth.setValue(temp);

            temp = startHeight.getValue();
            startHeight.setValue(endHeight.getValue());
            endHeight.setValue(temp);

            this.results.setText(results);

            if (historyHandlerRegistration != null) {
                upateHistoryToken();
            }
        }

        public void setUseUriFragments(boolean useUriFragments) {
            if (useUriFragments && historyHandlerRegistration == null) {
                // First read current state
                updateFromHistoryToken(History.getToken());

                // Then add history change listener
                historyHandlerRegistration = History.addValueChangeHandler(
                        event -> updateFromHistoryToken(event.getValue()));
            } else if (!useUriFragments && historyHandlerRegistration != null) {
                historyHandlerRegistration.removeHandler();
                historyHandlerRegistration = null;
            }
        }

        private void updateFromHistoryToken(String token) {
            if (token == null || token.isEmpty()) {
                return;
            }
            String[] parts = token.split(",");
            if (parts.length != 4) {
                results.setText("Unsupported history token format: " + token);
                return;
            }

            try {
                startWidth.setValue(new Integer(parts[0]));
                endWidth.setValue(new Integer(parts[1]));
                startHeight.setValue(new Integer(parts[2]));
                endHeight.setValue(new Integer(parts[3]));
            } catch (NumberFormatException e) {
                results.setText("Number format problem: " + e.getMessage());
            }
        }

        private void upateHistoryToken() {
            String token = startWidth.getValue().intValue() + ","
                    + endWidth.getValue().intValue() + ","
                    + startHeight.getValue().intValue() + ","
                    + endHeight.getValue().intValue();

            History.newItem(token, false);
        }
    }

    private void terrorize(final double startWidth, final double endWidth,
            final double startHeight, final double endHeight,
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

                double widthToSet = startWidth
                        + (endWidth - startWidth) * progress;

                double heightToSet = startHeight
                        + (endHeight - startHeight) * progress;

                if (widthToSet != startWidth) {
                    target.getWidget().setWidth(widthToSet + "px");
                }
                if (heightToSet != startHeight) {
                    target.getWidget().setHeight(heightToSet + "px");
                }

                // TODO Optionally inform LayoutManager as well
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
        ResizeTerrorizerControlPanel panel = getWidget();
        if (panel.startWidth.getValue() == null) {
            int width = getTarget().getWidget().getElement().getOffsetWidth();
            panel.startWidth.setValue(width);
            panel.endWidth.setValue(width + getState().defaultWidthOffset);
        }

        if (panel.startHeight.getValue() == null) {
            int height = getTarget().getWidget().getElement().getOffsetHeight();
            panel.startHeight.setValue(height);
            panel.endHeight.setValue(height + getState().defaultHeightOffset);
        }
    }

}
