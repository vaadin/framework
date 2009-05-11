/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class FeatureWindow extends Feature implements Window.CloseListener {

    private final Button addButton = new Button("Add window", this, "addWin");

    private final Button removeButton = new Button("Remove window", this,
            "delWin");

    private Window demoWindow;

    private Form windowProperties;

    public FeatureWindow() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout layoutRoot = new OrderedLayout();
        final OrderedLayout layoutUpper = new OrderedLayout();
        final OrderedLayout layoutLower = new OrderedLayout();

        demoWindow = new Window("Feature Test Window");
        demoWindow.addListener(this);
        demoWindow.setWidth("400px");
        demoWindow.setHeight("200px");
        demoWindow.setTheme("default");

        layoutUpper.addComponent(addButton);
        layoutUpper.addComponent(removeButton);

        updateWinStatus();

        // Properties
        propertyPanel = new PropertyPanel(demoWindow);
        windowProperties = propertyPanel.createBeanPropertySet(new String[] {
                "width", "height", "name", "theme", "border", "scrollable", });
        windowProperties.replaceWithSelect("border", new Object[] {
                new Integer(Window.BORDER_DEFAULT),
                new Integer(Window.BORDER_NONE),
                new Integer(Window.BORDER_MINIMAL) }, new Object[] { "Default",
                "None", "Minimal" });
        // Disabled, not applicable for default theme
        windowProperties.getField("border").setEnabled(false);
        windowProperties.getField("scrollable").setEnabled(false);

        propertyPanel.addProperties("Window Properties", windowProperties);
        windowProperties.getField("width").setDescription(
                "Minimum width is 100");
        windowProperties.getField("height").setDescription(
                "Minimum height is 100");

        setJavadocURL("ui/Window.html");

        layoutRoot.addComponent(layoutUpper);
        layoutRoot.addComponent(layoutLower);
        return layoutRoot;
    }

    @Override
    protected String getExampleSrc() {
        return "Window win = new Window();\n"
                + "getApplication().addWindow(win);\n";

    }

    @Override
    protected String getDescriptionXHTML() {
        return "The window support in IT Mill Toolkit allows for opening and closing windows, "
                + "refreshing one window from another (for asynchronous terminals), "
                + "resizing windows and scrolling window content. "
                + "There are also a number of preset window border styles defined by "
                + "this feature.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "Window";
    }

    public void addWin() {

        propertyPanel.commit();

        getApplication().getMainWindow().addWindow(demoWindow);

        demoWindow.removeAllComponents();

        demoWindow
                .addComponent(new Label(
                        "<br /><br />This is a new window created by "
                                + "<em>Add window</em>"
                                + " button's event.<br /><br />You may simply"
                                + " close this window or select "
                                + "<em>Remove window</em> from the Feature Browser window.",
                        Label.CONTENT_XHTML));
        // prevent user to change window name tag (after window is
        // created)
        windowProperties.getField("name").setEnabled(false);
        windowProperties.getField("name").setReadOnly(true);
        demoWindow.setVisible(true);
        updateWinStatus();
    }

    public void delWin() {
        getApplication().getMainWindow().removeWindow(demoWindow);
        // allow user to change window name tag (before window is
        // created)
        windowProperties.getField("name").setEnabled(true);
        windowProperties.getField("name").setReadOnly(false);
        updateWinStatus();
    }

    private void updateWinStatus() {
        if (demoWindow.getApplication() == null) {
            addButton.setEnabled(true);
            removeButton.setEnabled(false);
        } else {
            addButton.setEnabled(false);
            removeButton.setEnabled(true);
        }
    }

    public void windowClose(CloseEvent e) {
        delWin();
    }
}
