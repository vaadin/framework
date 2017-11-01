package com.vaadin.tests.components.splitpanel;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class SplitPanelWithMinimumAndMaximum extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabs = new TabSheet();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        verticalLayout.setCaption("Horizontal splits");

        HorizontalSplitPanel percentagePositionWithPercentageLimitsHorizontal = new HorizontalSplitPanel();
        percentagePositionWithPercentageLimitsHorizontal.setMinSplitPosition(10,
                Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsHorizontal.setMaxSplitPosition(80,
                Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsHorizontal
                .setFirstComponent(new Label("Min 10 % - 50 % position"));
        percentagePositionWithPercentageLimitsHorizontal
                .setSecondComponent(new Label("Max 80 %"));
        percentagePositionWithPercentageLimitsHorizontal.setSplitPosition(50,
                Sizeable.UNITS_PERCENTAGE);
        verticalLayout
                .addComponent(percentagePositionWithPercentageLimitsHorizontal);

        HorizontalSplitPanel pixelPositionWithPercentageLimitsHorizontal = new HorizontalSplitPanel();
        pixelPositionWithPercentageLimitsHorizontal.setMinSplitPosition(10,
                Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsHorizontal.setMaxSplitPosition(80,
                Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsHorizontal
                .setFirstComponent(new Label("Min 10 % - 400 px position"));
        pixelPositionWithPercentageLimitsHorizontal
                .setSecondComponent(new Label("Max 80 %"));
        pixelPositionWithPercentageLimitsHorizontal.setSplitPosition(400,
                Sizeable.UNITS_PIXELS);
        verticalLayout
                .addComponent(pixelPositionWithPercentageLimitsHorizontal);

        HorizontalSplitPanel pixelPositionWithPixelLimitsHorizontal = new HorizontalSplitPanel();
        pixelPositionWithPixelLimitsHorizontal.setMinSplitPosition(100,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsHorizontal.setMaxSplitPosition(550,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsHorizontal
                .setFirstComponent(new Label("Min 100 px - 400 px position"));
        pixelPositionWithPixelLimitsHorizontal
                .setSecondComponent(new Label("Max 550 px"));
        pixelPositionWithPixelLimitsHorizontal.setSplitPosition(400,
                Sizeable.UNITS_PIXELS);
        verticalLayout.addComponent(pixelPositionWithPixelLimitsHorizontal);

        HorizontalSplitPanel percentagePositionWithPixelLimitsHorizontal = new HorizontalSplitPanel();
        percentagePositionWithPixelLimitsHorizontal.setMinSplitPosition(100,
                Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsHorizontal.setMaxSplitPosition(550,
                Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsHorizontal
                .setFirstComponent(new Label("Min 100 px - 30 % position"));
        percentagePositionWithPixelLimitsHorizontal
                .setSecondComponent(new Label("Max 550 px"));
        percentagePositionWithPixelLimitsHorizontal.setSplitPosition(30,
                Sizeable.UNITS_PERCENTAGE);
        verticalLayout
                .addComponent(percentagePositionWithPixelLimitsHorizontal);

        HorizontalSplitPanel percentagePositionWithPercentageLimitsHorizontalResersed = new HorizontalSplitPanel();
        percentagePositionWithPercentageLimitsHorizontalResersed
                .setMinSplitPosition(10, Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsHorizontalResersed
                .setMaxSplitPosition(80, Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsHorizontalResersed
                .setFirstComponent(
                        new Label("Max 80 % - Reversed 50 % position"));
        percentagePositionWithPercentageLimitsHorizontalResersed
                .setSecondComponent(new Label("Min 10 %"));
        percentagePositionWithPercentageLimitsHorizontalResersed
                .setSplitPosition(50, Sizeable.UNITS_PERCENTAGE, true);
        verticalLayout.addComponent(
                percentagePositionWithPercentageLimitsHorizontalResersed);

        HorizontalSplitPanel pixelPositionWithPercentageLimitsHorizontalResersed = new HorizontalSplitPanel();
        pixelPositionWithPercentageLimitsHorizontalResersed
                .setMinSplitPosition(10, Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsHorizontalResersed
                .setMaxSplitPosition(80, Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsHorizontalResersed.setFirstComponent(
                new Label("Max 80 % - Reversed 400 px position"));
        pixelPositionWithPercentageLimitsHorizontalResersed
                .setSecondComponent(new Label("Min 10 %"));
        pixelPositionWithPercentageLimitsHorizontalResersed
                .setSplitPosition(400, Sizeable.UNITS_PIXELS, true);
        verticalLayout.addComponent(
                pixelPositionWithPercentageLimitsHorizontalResersed);

        HorizontalSplitPanel pixelPositionWithPixelLimitsHorizontalResersed = new HorizontalSplitPanel();
        pixelPositionWithPixelLimitsHorizontalResersed.setMinSplitPosition(100,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsHorizontalResersed.setMaxSplitPosition(550,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsHorizontalResersed.setFirstComponent(
                new Label("Max 550 px - Reversed 400 px position"));
        pixelPositionWithPixelLimitsHorizontalResersed
                .setSecondComponent(new Label("Min 100 px"));
        pixelPositionWithPixelLimitsHorizontalResersed.setSplitPosition(400,
                Sizeable.UNITS_PIXELS, true);
        verticalLayout
                .addComponent(pixelPositionWithPixelLimitsHorizontalResersed);

        HorizontalSplitPanel percentagePositionWithPixelLimitsHorizontalResersed = new HorizontalSplitPanel();
        percentagePositionWithPixelLimitsHorizontalResersed
                .setMinSplitPosition(100, Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsHorizontalResersed
                .setMaxSplitPosition(550, Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsHorizontalResersed.setFirstComponent(
                new Label("Max 550 px - Reversed 30 % position"));
        percentagePositionWithPixelLimitsHorizontalResersed
                .setSecondComponent(new Label("Min 100 px"));
        percentagePositionWithPixelLimitsHorizontalResersed.setSplitPosition(30,
                Sizeable.UNITS_PERCENTAGE, true);
        verticalLayout.addComponent(
                percentagePositionWithPixelLimitsHorizontalResersed);

        verticalLayout.setSizeFull();
        // a hack for a Chrome 55+ issue (extra scrollbars) that is only seen on
        // the testing cluster
        Page.getCurrent().getStyles().add(
                ".v-tabsheet-tabsheetpanel .v-scrollable { overflow: hidden }");
        tabs.addComponent(verticalLayout);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(false);
        horizontalLayout.setCaption("Vertical splits");

        VerticalSplitPanel percentagePositionWithPercentageLimitsVertical = new VerticalSplitPanel();
        percentagePositionWithPercentageLimitsVertical.setMinSplitPosition(10,
                Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsVertical.setMaxSplitPosition(80,
                Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsVertical
                .setFirstComponent(new Label("Min 10 % - 50 % position"));
        percentagePositionWithPercentageLimitsVertical
                .setSecondComponent(new Label("Max 80 %"));
        percentagePositionWithPercentageLimitsVertical.setSplitPosition(50,
                Sizeable.UNITS_PERCENTAGE);
        horizontalLayout
                .addComponent(percentagePositionWithPercentageLimitsVertical);

        VerticalSplitPanel pixelPositionWithPercentageLimitsVertical = new VerticalSplitPanel();
        pixelPositionWithPercentageLimitsVertical.setMinSplitPosition(10,
                Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsVertical.setMaxSplitPosition(80,
                Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsVertical
                .setFirstComponent(new Label("Min 10 % - 400 px position"));
        pixelPositionWithPercentageLimitsVertical
                .setSecondComponent(new Label("Max 80 %"));
        pixelPositionWithPercentageLimitsVertical.setSplitPosition(400,
                Sizeable.UNITS_PIXELS);
        horizontalLayout
                .addComponent(pixelPositionWithPercentageLimitsVertical);

        VerticalSplitPanel pixelPositionWithPixelLimitsVertical = new VerticalSplitPanel();
        pixelPositionWithPixelLimitsVertical.setMinSplitPosition(100,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsVertical.setMaxSplitPosition(450,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsVertical
                .setFirstComponent(new Label("Min 100 px - 400 px position"));
        pixelPositionWithPixelLimitsVertical
                .setSecondComponent(new Label("Max 450 px"));
        pixelPositionWithPixelLimitsVertical.setSplitPosition(400,
                Sizeable.UNITS_PIXELS);
        horizontalLayout.addComponent(pixelPositionWithPixelLimitsVertical);

        VerticalSplitPanel percentagePositionWithPixelLimitsVertical = new VerticalSplitPanel();
        percentagePositionWithPixelLimitsVertical.setMinSplitPosition(100,
                Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsVertical.setMaxSplitPosition(450,
                Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsVertical
                .setFirstComponent(new Label("Min 100 px - 30 % position"));
        percentagePositionWithPixelLimitsVertical
                .setSecondComponent(new Label("Max 450 px"));
        percentagePositionWithPixelLimitsVertical.setSplitPosition(30,
                Sizeable.UNITS_PERCENTAGE);
        horizontalLayout
                .addComponent(percentagePositionWithPixelLimitsVertical);

        VerticalSplitPanel percentagePositionWithPercentageLimitsVerticalReversed = new VerticalSplitPanel();
        percentagePositionWithPercentageLimitsVerticalReversed
                .setMinSplitPosition(10, Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsVerticalReversed
                .setMaxSplitPosition(80, Sizeable.UNITS_PERCENTAGE);
        percentagePositionWithPercentageLimitsVerticalReversed
                .setFirstComponent(
                        new Label("Max 80 % - Reversed 50 % position"));
        percentagePositionWithPercentageLimitsVerticalReversed
                .setSecondComponent(new Label("Min 10 %"));
        percentagePositionWithPercentageLimitsVerticalReversed
                .setSplitPosition(50, Sizeable.UNITS_PERCENTAGE, true);
        horizontalLayout.addComponent(
                percentagePositionWithPercentageLimitsVerticalReversed);

        VerticalSplitPanel pixelPositionWithPercentageLimitsVerticalReversed = new VerticalSplitPanel();
        pixelPositionWithPercentageLimitsVerticalReversed
                .setMinSplitPosition(10, Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsVerticalReversed
                .setMaxSplitPosition(80, Sizeable.UNITS_PERCENTAGE);
        pixelPositionWithPercentageLimitsVerticalReversed.setFirstComponent(
                new Label("Max 80 % - Reversed 400 px position"));
        pixelPositionWithPercentageLimitsVerticalReversed
                .setSecondComponent(new Label("Min 10 %"));
        pixelPositionWithPercentageLimitsVerticalReversed.setSplitPosition(400,
                Sizeable.UNITS_PIXELS, true);
        horizontalLayout.addComponent(
                pixelPositionWithPercentageLimitsVerticalReversed);

        VerticalSplitPanel pixelPositionWithPixelLimitsVerticalReversed = new VerticalSplitPanel();
        pixelPositionWithPixelLimitsVerticalReversed.setMinSplitPosition(100,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsVerticalReversed.setMaxSplitPosition(400,
                Sizeable.UNITS_PIXELS);
        pixelPositionWithPixelLimitsVerticalReversed.setFirstComponent(
                new Label("Max 400 px - Reversed 300 px position"));
        pixelPositionWithPixelLimitsVerticalReversed
                .setSecondComponent(new Label("Min 100 px"));
        pixelPositionWithPixelLimitsVerticalReversed.setSplitPosition(300,
                Sizeable.UNITS_PIXELS, true);
        horizontalLayout
                .addComponent(pixelPositionWithPixelLimitsVerticalReversed);

        VerticalSplitPanel percentagePositionWithPixelLimitsVerticalReversed = new VerticalSplitPanel();
        percentagePositionWithPixelLimitsVerticalReversed
                .setMinSplitPosition(100, Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsVerticalReversed
                .setMaxSplitPosition(400, Sizeable.UNITS_PIXELS);
        percentagePositionWithPixelLimitsVerticalReversed.setFirstComponent(
                new Label("Max 400 px - Reversed 30 % position"));
        percentagePositionWithPixelLimitsVerticalReversed
                .setSecondComponent(new Label("Min 100 px"));
        percentagePositionWithPixelLimitsVerticalReversed.setSplitPosition(30,
                Sizeable.UNITS_PERCENTAGE, true);
        horizontalLayout.addComponent(
                percentagePositionWithPixelLimitsVerticalReversed);

        tabs.addComponent(horizontalLayout);
        horizontalLayout.setSizeFull();

        final VerticalLayout togglableSplitPanelLayout = new VerticalLayout();
        togglableSplitPanelLayout.setSpacing(false);
        togglableSplitPanelLayout.setMargin(false);
        togglableSplitPanelLayout.setCaption("Togglable minimum/maximum");

        final HorizontalSplitPanel togglableSplitPanel = new HorizontalSplitPanel();
        togglableSplitPanel.setMinSplitPosition(10, Sizeable.UNITS_PERCENTAGE);
        togglableSplitPanel.setMaxSplitPosition(80, Sizeable.UNITS_PERCENTAGE);
        togglableSplitPanel
                .setFirstComponent(new Label("Min 10 % - 50 % position"));
        togglableSplitPanel.setSecondComponent(new Label("Max 80 %"));
        togglableSplitPanel.setSplitPosition(50, Sizeable.UNITS_PERCENTAGE);
        togglableSplitPanel.setHeight("250px");
        togglableSplitPanelLayout.addComponent(togglableSplitPanel);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(false);

        Button disableMinimum = new Button("Disable min limit",
                event -> togglableSplitPanel.setMinSplitPosition(0,
                        Sizeable.UNITS_PERCENTAGE));
        Button enableMinimum = new Button("Enable min limit",
                event -> togglableSplitPanel.setMinSplitPosition(10,
                        Sizeable.UNITS_PERCENTAGE));
        Button disableMaximum = new Button("Disable max limit",
                event -> togglableSplitPanel.setMaxSplitPosition(100,
                        Sizeable.UNITS_PERCENTAGE));
        Button enableMaximum = new Button("Enable max limit",
                event -> togglableSplitPanel.setMaxSplitPosition(80,
                        Sizeable.UNITS_PERCENTAGE));
        buttonLayout.addComponent(disableMinimum);
        buttonLayout.addComponent(enableMinimum);
        buttonLayout.addComponent(disableMaximum);
        buttonLayout.addComponent(enableMaximum);

        togglableSplitPanelLayout.addComponent(buttonLayout);
        tabs.addComponent(togglableSplitPanelLayout);

        addComponent(tabs);
        tabs.setHeight("550px");
        tabs.setWidth("600px");
        getLayout().setSizeFull();
    }

    @Override
    protected String getTestDescription() {
        return "SplitPanel could have setMaxSplitPosition and setMinSplitPosition methods as a way to set maximum and minimum limits for the split position. This is not a very critical feature but could be useful in some situations.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1744;
    }
}
