/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.coverflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class CoverflowApplication extends com.itmill.toolkit.Application {

    Coverflow covers = new Coverflow();

    public void init() {

        setMainWindow(new Window("Coverflow", createMainLayout()));

        setTheme("coverflow");

        addSlidesToCoverflow();
    }

    private VerticalLayout createMainLayout() {

        // Initialize coverflow component
        covers.setHeight("150px");
        covers.setWidth("100%");
        covers.setBackgroundColor(0, 0, 0, 100, 100, 100);

        // Initialize visible slide viewer
        Panel slidePanel = new Panel();
        slidePanel.setStyleName(Panel.STYLE_LIGHT);
        slidePanel.setSizeFull();
        final Embedded visibleSlide = new Embedded();
        visibleSlide.setHeight("480px");
        slidePanel.addComponent(visibleSlide);
        ((VerticalLayout) slidePanel.getLayout()).setComponentAlignment(
                visibleSlide, "center");

        // Listen to coverflow changes as change slides when needed
        covers.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                visibleSlide.setSource((Resource) covers.getValue());
            }
        });

        // Show sources button
        Button showSrc = new Button("Show source", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Window srcWindow = new Window("Source code");
                srcWindow.setWidth("700px");
                srcWindow.setHeight("500px");
                Label l = new Label(getSourceCodeForThisClass(),
                        Label.CONTENT_XHTML);
                srcWindow.addComponent(l);
                getMainWindow().addWindow(srcWindow);
            }
        });
        showSrc.setStyleName(Button.STYLE_LINK);
        // Initialize main layout
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(showSrc);
        layout.setComponentAlignment(showSrc, Alignment.TOP_RIGHT);
        layout.addComponent(slidePanel);
        layout.addComponent(covers);
        layout.setExpandRatio(slidePanel, 1);
        layout.setSizeFull();

        return layout;
    }

    private String getSourceCodeForThisClass() {
        String code = "Could not find source-file";
        try {
            InputStream is = this.getClass().getResource(
                    "CoverflowApplication.html").openStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuffer buf = new StringBuffer();
            String line;
            while ((line = r.readLine()) != null) {
                buf.append(line);
            }
            code = buf.toString();
        } catch (IOException ignored) {
        }
        return code;
    }

    private void addSlidesToCoverflow() {
        for (int i = 0; i < 20; i++) {
            String head = "images/";
            String tail = "slideshow-example.0" + ((i < 10) ? "0" : "") + i
                    + ".jpg";
            ThemeResource slide = new ThemeResource(head + tail);
            covers.addItem(slide);
            covers.setItemIcon(slide,
                    new ThemeResource(head + "thumbs/" + tail));
        }
    }
}
