package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;

/**
 * Shows a few variations of Labels, including the effects of XHTML- and
 * pre-formatted mode.
 * 
 * @author IT Mill Ltd.
 */
public class LabelsExample extends CustomComponent {

    private static final String xhtml = "This text has <b>HTML</b> formatting.<br/>"
            + "A plain <i>Label</i> will show the markup, while a <u>XHTML-mode</u>"
            + " <i>Label</i> will show the formatted text.";

    private static final String pre = "This text has linebreaks.\n\n"
            + "They will show up in a preformatted Label,\n"
            + "but not in a \"plain\" Label.\n\n"
            + "       This is an indented row. \n       Same indentation here.";

    public LabelsExample() {

        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        GridLayout g = new GridLayout(2, 4);
        main.addComponent(g);

        // plain w/o caption
        Panel p = new Panel("Plain");
        p.setStyleName(Panel.STYLE_LIGHT);
        Label l = new Label("A plain label without caption.");
        p.addComponent(l);
        g.addComponent(p);
        // plain w/ caption
        p = new Panel("Plain w/ caption + tooltip");
        p.setStyleName(Panel.STYLE_LIGHT);
        l = new Label("A plain label with caption.");
        l.setCaption("Label caption");
        l.setDescription("This is a description (tooltip) for the label.");
        p.addComponent(l);
        g.addComponent(p);
        // plain w/ xhtml
        p = new Panel("Plain w/ XHTML content");
        p.setStyleName(Panel.STYLE_LIGHT);
        l = new Label(xhtml);
        p.addComponent(l);
        g.addComponent(p);
        // xhtml w/ xhtml
        p = new Panel("XHTML-mode w/ XHTML content");
        p.setStyleName(Panel.STYLE_LIGHT);
        l = new Label(xhtml);
        l.setContentMode(Label.CONTENT_XHTML);
        p.addComponent(l);
        g.addComponent(p);
        // plain w/ preformatted
        p = new Panel("Plain w/ preformatted content");
        p.setStyleName(Panel.STYLE_LIGHT);
        l = new Label(pre);
        p.addComponent(l);
        g.addComponent(p);
        // preformatted w/ preformatted
        p = new Panel("Preformatted-mode w/ preformatted content");
        p.setStyleName(Panel.STYLE_LIGHT);
        l = new Label(pre);
        l.setContentMode(Label.CONTENT_PREFORMATTED);
        p.addComponent(l);
        g.addComponent(p);

    }
}
