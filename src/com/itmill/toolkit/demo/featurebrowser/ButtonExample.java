/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * Shows a few variations of Buttons and Links.
 * 
 * @author IT Mill Ltd.
 */
public class ButtonExample extends CustomComponent implements
        Button.ClickListener {

    public ButtonExample() {

        final OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        final OrderedLayout horiz = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(horiz);
        final Panel basic = new Panel("Basic buttons");
        basic.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(basic);

        final Panel bells = new Panel("w/ bells & whistles");
        bells.setStyleName(Panel.STYLE_LIGHT);
        horiz.addComponent(bells);

        Button b = new Button("Basic button");
        b.addListener(this);
        basic.addComponent(b);

        b = new Button("Button w/ icon + tooltip");
        b.addListener(this);
        b.setIcon(new ThemeResource("icons/ok.png"));
        b.setDescription("This button does nothing, fast");
        bells.addComponent(b);

        b = new CheckBox("CheckBox - a switch-button");
        b.setImmediate(true); // checkboxes are not immediate by default
        b.addListener(this);
        basic.addComponent(b);

        b = new CheckBox("CheckBox w/ icon + tooltip");
        b.setImmediate(true); // checkboxes are not immediate by default
        b.addListener(this);
        b.setIcon(new ThemeResource("icons/ok.png"));
        b.setDescription("This is a CheckBox");
        bells.addComponent(b);

        b = new Button("Link-style button");
        b.addListener(this);
        b.setStyleName(Button.STYLE_LINK);
        basic.addComponent(b);

        b = new Button("Link button w/ icon + tooltip");
        b.addListener(this);
        b.setStyleName(Button.STYLE_LINK);
        b.setIcon(new ThemeResource("icons/ok.png"));
        b.setDescription("Link-style, icon+tootip, no caption");
        bells.addComponent(b);

        b = new Button();
        b.addListener(this);
        b.setStyleName(Button.STYLE_LINK);
        b.setIcon(new ThemeResource("icons/ok.png"));
        b.setDescription("Link-style, icon+tootip, no caption");
        basic.addComponent(b);

        final Panel links = new Panel("Links");
        links.setStyleName(Panel.STYLE_LIGHT);
        main.addComponent(links);
        final Label desc = new Label(
                "The main difference between a Link and"
                        + " a link-styled Button is that the Link works client-"
                        + " side, whereas the Button works server side.<br/> This means"
                        + " that the Button triggers some event on the server,"
                        + " while the Link is a normal web-link. <br/><br/>Note that for"
                        + " opening new windows, the Link might be a safer "
                        + " choice, since popup-blockers might interfer with "
                        + " server-initiated window opening.");
        desc.setContentMode(Label.CONTENT_XHTML);
        links.addComponent(desc);
        Link l = new Link("IT Mill home", new ExternalResource(
                "http://www.itmill.com"));
        l.setDescription("Link without target name, opens in this window");
        links.addComponent(l);

        l = new Link("IT Mill home (new window)", new ExternalResource(
                "http://www.itmill.com"));
        l.setTargetName("_blank");
        l.setDescription("Link with target name, opens in new window");
        links.addComponent(l);

        l = new Link("IT Mill home (new window, less decor)",
                new ExternalResource("http://www.itmill.com"));
        l.setTargetName("_blank");
        l.setTargetBorder(Link.TARGET_BORDER_MINIMAL);
        l.setTargetName("_blank");
        l
                .setDescription("Link with target name and BORDER_MINIMAL, opens in new window with less decor");
        links.addComponent(l);

        l = new Link("IT Mill home (new 200x200 window, no decor, icon)",
                new ExternalResource("http://www.itmill.com"), "_blank", 200,
                200, Link.TARGET_BORDER_NONE);
        l.setTargetName("_blank");
        l
                .setDescription("Link with target name and BORDER_NONE, opens in new window with no decor");
        l.setIcon(new ThemeResource("icons/ok.png"));
        links.addComponent(l);

    }

    public void buttonClick(ClickEvent event) {
        final Button b = event.getButton();
        getWindow().showNotification(
                "Clicked"
                        + (b instanceof CheckBox ? ", value: "
                                + event.getButton().getValue() : ""));

    }

}
