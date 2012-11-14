package com.vaadin.tests.components.uitest.components;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.Reindeer;

public class LayoutsCssTest extends GridLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public LayoutsCssTest(TestSampler parent) {
        this.parent = parent;
        setSpacing(true);
        setColumns(4);
        setWidth("100%");

        VerticalLayout vl = new VerticalLayout();
        vl.setCaption("VerticalLayout");
        vl.setMargin(true);
        vl.setSpacing(true);
        vl.setComponentError(new UserError("A error message..."));
        vl.addComponent(new Label("Some content"));
        vl.setId("layout" + debugIdCounter++);
        addComponent(vl);

        CssLayout css = new CssLayout();
        css.setCaption("CssLayout");
        css.addComponent(new Label("Some content"));
        css.setId("layout" + debugIdCounter++);
        addComponent(css);

        AbsoluteLayout abs = new AbsoluteLayout();
        abs.setCaption("Abs layout");
        abs.addComponent(new Label("Some content"));
        abs.setComponentError(new UserError("A error message..."));
        abs.setId("layout" + debugIdCounter++);

        addComponent(abs);

        GridLayout gl = new GridLayout();
        gl.setMargin(true);
        gl.setSpacing(true);
        gl.setCaption("GridLayout");
        gl.setComponentError(new UserError("A error message..."));
        gl.addComponent(new Label("Some content"));
        gl.setId("layout" + debugIdCounter++);

        addComponent(gl);

        VerticalSplitPanel vert = new VerticalSplitPanel();
        vert.setCaption("VertSplitPan");
        vert.setFirstComponent(new Label("Some content 1"));
        vert.setSecondComponent(new Label("Some content 2"));
        vert.setComponentError(new UserError("A error message..."));
        vert.setSplitPosition(50);
        vert.setEnabled(false);
        vert.setHeight("50px");
        vert.setId("layout" + debugIdCounter++);

        addComponent(vert);

        HorizontalSplitPanel horiz = new HorizontalSplitPanel();
        horiz.setSplitPosition(50);
        horiz.setFirstComponent(new Label("Some content 1"));
        horiz.setSecondComponent(new Label("Some content 2"));
        horiz.setIcon(new ThemeResource(parent.ICON_URL));
        horiz.setCaption("HorizSplitPan");
        horiz.setId("layout" + debugIdCounter++);

        addComponent(horiz);

        VerticalSplitPanel smallSplitPanel = new VerticalSplitPanel();
        smallSplitPanel.setCaption("SmallVertSplitPan");
        smallSplitPanel.setFirstComponent(new Label("Some content 1"));
        smallSplitPanel.setSecondComponent(new Label("Some content 2"));
        smallSplitPanel.setComponentError(new UserError("A error message..."));
        smallSplitPanel.setSplitPosition(50);
        smallSplitPanel.addStyleName(Reindeer.SPLITPANEL_SMALL);
        smallSplitPanel.setEnabled(false);
        smallSplitPanel.setHeight("50px");
        smallSplitPanel.setId("layout" + debugIdCounter++);
        addComponent(smallSplitPanel);

        String customLayoutSrc = "<html><div location='pos1' class='customclass'> </div></html>";

        CustomLayout custom;
        try {
            custom = new CustomLayout(new ByteArrayInputStream(
                    customLayoutSrc.getBytes()));
            custom.addComponent(new Label("Some content"), "pos1");
            custom.setComponentError(new UserError("A error mesasge..."));
            custom.setCaption("CustomLayout");
            custom.setId("layout" + debugIdCounter++);

            addComponent(custom);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Panel defPanel = createPanelWith("A default panel", null);
        addComponent(defPanel);

        Panel light = createPanelWith("A light panel", Reindeer.PANEL_LIGHT);
        addComponent(light);

        Panel borderless = createPanelWith("A borderless panel",
                ChameleonTheme.PANEL_BORDERLESS);
        addComponent(borderless);

        Panel bubbling = createPanelWith("A Bubbling panel",
                ChameleonTheme.PANEL_BUBBLE);
        addComponent(bubbling);
    }

    /**
     * Helper to create panels for different theme variants...
     */
    private Panel createPanelWith(String caption, String styleName) {
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel(caption, panelLayout);
        panelLayout.addComponent(new Label("Some content"));
        panel.setIcon(new ThemeResource(parent.ICON_URL));
        panel.setComponentError(new UserError("A error message..."));
        panel.setId("layout" + debugIdCounter++);

        if (styleName != null) {
            panel.addStyleName(styleName);
        }

        return panel;
    }

    @Override
    public void addComponent(Component component) {
        parent.registerComponent(component);
        super.addComponent(component);

    }
}
