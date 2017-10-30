package com.vaadin.tests.components.window;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.WindowModeChangeEvent;
import com.vaadin.ui.Window.WindowModeChangeListener;

public class WindowMaximizeRestoreTest extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Button addButton = new Button("Add new Window");
        addButton.addClickListener(event -> {
            addWindow(createNewWindow());
            addWindowAgain.setValue(null);
        });
        addComponent(addButton);

        addWindowAgain = new ComboBox<>("Add Window Again");
        addWindowAgain
                .setItemCaptionGenerator(window -> window.getData().toString());
        addWindowAgain.addValueChangeListener(event -> {
            Object value = event.getValue();
            if (value != null && value instanceof Window) {
                UI.getCurrent().addWindow((Window) value);
                windowList.remove(value);
                addWindowAgain.setItems(windowList);
            }
        });
        addComponent(addWindowAgain);

        addWindow(createNewWindow());
    }

    private int windowCount = 0;
    private ComboBox<Window> addWindowAgain;
    private List<Window> windowList = new ArrayList<>();

    private Window createNewWindow() {
        final Window w = new Window("Window " + (++windowCount));
        final VerticalLayout content = new VerticalLayout();
        w.setContent(content);
        w.setData(windowCount);
        w.setWidth("200px");
        w.setHeight("300px");
        w.setPositionX(200);
        w.setPositionY(200);
        final NativeButton maximize = new NativeButton("Maximize");
        maximize.addClickListener(event -> {
            if (w.getWindowMode() == WindowMode.MAXIMIZED) {
                w.setWindowMode(WindowMode.NORMAL);
                maximize.setCaption("Maximize");
            } else {
                w.setWindowMode(WindowMode.MAXIMIZED);
                maximize.setCaption("Restore");
            }
        });
        ((ComponentContainer) w.getContent()).addComponent(maximize);

        w.addWindowModeChangeListener(new WindowModeChangeListener() {

            @Override
            public void windowModeChanged(WindowModeChangeEvent event) {
                WindowMode state = (event.getWindow().getWindowMode());
                if (state == WindowMode.NORMAL) {
                    w.setCaption("Window " + w.getData() + " Normal");
                    maximize.setCaption("Maximize");
                } else if (state == WindowMode.MAXIMIZED) {
                    w.setCaption("Window " + w.getData() + " Maximized");
                    maximize.setCaption("Restore");
                }
            }
        });
        final CheckBox resizeable = new CheckBox("Resizeable");
        resizeable.setValue(w.isResizable());
        resizeable.addValueChangeListener(
                event -> w.setResizable(resizeable.getValue()));
        ((ComponentContainer) w.getContent()).addComponent(resizeable);
        final CheckBox closeable = new CheckBox("Closeable");
        closeable.setValue(w.isClosable());
        closeable.addValueChangeListener(
                event -> w.setClosable(closeable.getValue()));
        ((ComponentContainer) w.getContent()).addComponent(closeable);
        NativeButton contentFull = new NativeButton("Set Content Size Full",
                event -> w.getContent().setSizeFull());
        contentFull.setWidth("100%");
        ((ComponentContainer) w.getContent()).addComponent(contentFull);

        NativeButton center = new NativeButton("Center");
        center.addClickListener(event -> w.center());
        ((ComponentContainer) w.getContent()).addComponent(center);

        w.addCloseListener(e -> {
            windowList.add(w);
            addWindowAgain.setItems(windowList);
        });

        return w;
    }

    @Override
    protected Integer getTicketNumber() {
        return 3400;
    }

    @Override
    protected String getTestDescription() {
        return "Tests the default maximize & restore funtionality. Max. makes window 100%*100% and pos(0, 0), and restore returns it to the values that are set in windows state.";
    }
}
