package com.vaadin.v7.tests.components.grid;

import java.util.Date;
import java.util.Locale;

import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionMode;
import com.vaadin.v7.ui.renderers.ButtonRenderer;
import com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.v7.ui.renderers.DateRenderer;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import com.vaadin.v7.ui.renderers.ImageRenderer;
import com.vaadin.v7.ui.renderers.NumberRenderer;
import com.vaadin.v7.ui.renderers.ProgressBarRenderer;
import com.vaadin.v7.ui.renderers.TextRenderer;

@SuppressWarnings("all")
public class NullRenderers extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        IndexedContainer container = new IndexedContainer();

        container.addContainerProperty(TextRenderer.class, String.class, null);
        container.addContainerProperty(HtmlRenderer.class, String.class, null);
        container.addContainerProperty(DateRenderer.class, Date.class, null);
        container.addContainerProperty(NumberRenderer.class, Number.class,
                null);

        container.addContainerProperty(ProgressBarRenderer.class, Double.class,
                null);
        container.addContainerProperty(ButtonRenderer.class, String.class,
                null);
        container.addContainerProperty(ImageRenderer.class, Resource.class,
                null);

        container.addItem();

        final Grid gridDefaults = new Grid(container);

        gridDefaults.setId("test-grid-defaults");
        gridDefaults.setSelectionMode(SelectionMode.NONE);
        gridDefaults.setWidth("100%");

        gridDefaults.getColumn(TextRenderer.class)
                .setRenderer(new TextRenderer("-- No Text --"));
        gridDefaults.getColumn(HtmlRenderer.class)
                .setRenderer(new HtmlRenderer("-- No Jokes --"));
        gridDefaults.getColumn(DateRenderer.class).setRenderer(
                new DateRenderer("%s", Locale.getDefault(), "-- Never --"));
        gridDefaults.getColumn(NumberRenderer.class).setRenderer(
                new NumberRenderer("%s", Locale.getDefault(), "-- Nothing --"));

        gridDefaults.getColumn(ProgressBarRenderer.class)
                .setRenderer(new ProgressBarRenderer());

        gridDefaults.getColumn(ButtonRenderer.class)
                .setRenderer(new ButtonRenderer(new RendererClickListener() {
                    @Override
                    public void click(RendererClickEvent event) {
                    }
                }, "-- No Control --"));

        gridDefaults.getColumn(ImageRenderer.class)
                .setRenderer(new ImageRenderer(new RendererClickListener() {

                    @Override
                    public void click(RendererClickEvent event) {
                    }
                }));

        addComponent(gridDefaults);

        final Grid gridNoDefaults = new Grid(container);

        gridNoDefaults.setId("test-grid");
        gridNoDefaults.setSelectionMode(SelectionMode.NONE);
        gridNoDefaults.setWidth("100%");

        gridNoDefaults.getColumn(TextRenderer.class)
                .setRenderer(new TextRenderer());
        gridNoDefaults.getColumn(HtmlRenderer.class)
                .setRenderer(new HtmlRenderer());
        gridNoDefaults.getColumn(DateRenderer.class)
                .setRenderer(new DateRenderer());
        gridNoDefaults.getColumn(NumberRenderer.class)
                .setRenderer(new NumberRenderer());

        gridNoDefaults.getColumn(ProgressBarRenderer.class)
                .setRenderer(new ProgressBarRenderer());

        gridNoDefaults.getColumn(ButtonRenderer.class)
                .setRenderer(new ButtonRenderer(new RendererClickListener() {
                    @Override
                    public void click(RendererClickEvent event) {
                    }
                }));

        gridNoDefaults.getColumn(ImageRenderer.class)
                .setRenderer(new ImageRenderer(new RendererClickListener() {

                    @Override
                    public void click(RendererClickEvent event) {
                    }
                }));

        addComponent(gridNoDefaults);
    }

    @Override
    protected String getTestDescription() {
        return "Tests the functionality of widget-based renderers";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13334);
    }
}
