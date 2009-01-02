/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Table;

public class FeatureParameters extends Feature implements URIHandler,
        ParameterHandler {

    private final Label context = new Label();

    private final Label relative = new Label();

    private final Table params = new Table();

    public FeatureParameters() {
        super();
        params.addContainerProperty("Values", String.class, "");
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Label info = new Label("To test this feature, try to "
                + "add some get parameters to URL. For example if you have "
                + "the feature browser installed in your local host, try url: ");
        info.setCaption("Usage info");
        l.addComponent(info);
        try {
            final URL u1 = new URL(getApplication().getURL(),
                    "test/uri?test=1&test=2");
            final URL u2 = new URL(getApplication().getURL(),
                    "foo/bar?mary=john&count=3");

            l.addComponent(new Link(u1.toString(), new ExternalResource(u1)));
            l.addComponent(new Label("Or this: "));
            l.addComponent(new Link(u2.toString(), new ExternalResource(u2)));
        } catch (final Exception e) {
            System.out.println("Couldn't get hostname for this machine: "
                    + e.toString());
            e.printStackTrace();
        }

        // URI
        final Panel p1 = new Panel("URI Handler");
        context.setCaption("Last URI handler context");
        p1.addComponent(context);
        relative.setCaption("Last relative URI");
        p1.addComponent(relative);
        l.addComponent(p1);

        // Parameters
        final Panel p2 = new Panel("Parameter Handler");
        params.setCaption("Last parameters");
        params.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
        params.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        p2.addComponent(params);
        l.addComponent(p2);

        // Properties
        propertyPanel = new PropertyPanel(p1);
        final Form ap = propertyPanel.createBeanPropertySet(new String[] {
                "width", "height" });
        final Select themes = (Select) propertyPanel.getField("style");
        themes.addItem("light").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("light");
        themes.addItem("strong").getItemProperty(
                themes.getItemCaptionPropertyId()).setValue("strong");
        propertyPanel.addProperties("Panel Properties", ap);

        setJavadocURL("ui/Panel.html");

        return l;
    }

    @Override
    protected String getDescriptionXHTML() {
        return "This is a demonstration of how URL parameters can be recieved and handled."
                + "Parameters and URL:s can be received trough the windows by registering "
                + "URIHandler and ParameterHandler classes window.";
    }

    @Override
    protected String getImage() {
        return "parameters.jpg";
    }

    @Override
    protected String getTitle() {
        return "Parameters";
    }

    /**
     * Add URI and parametes handlers to window.
     * 
     * @see com.itmill.toolkit.ui.Component#attach()
     */
    @Override
    public void attach() {
        super.attach();
        getWindow().addURIHandler(this);
        getWindow().addParameterHandler(this);
    }

    /**
     * Remove all handlers from window
     * 
     * @see com.itmill.toolkit.ui.Component#detach()
     */
    @Override
    public void detach() {
        super.detach();
        getWindow().removeURIHandler(this);
        getWindow().removeParameterHandler(this);
    }

    /**
     * Update URI
     * 
     * @see com.itmill.toolkit.terminal.URIHandler#handleURI(URL, String)
     */
    public DownloadStream handleURI(URL context, String relativeUri) {
        this.context.setValue(context.toString());
        relative.setValue(relativeUri);
        return null;
    }

    /**
     * Update parameters table
     * 
     * @see com.itmill.toolkit.terminal.ParameterHandler#handleParameters(Map)
     */
    public void handleParameters(Map parameters) {
        params.removeAllItems();
        for (final Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            final String name = (String) i.next();
            final String[] values = (String[]) parameters.get(name);
            String v = "";
            for (int j = 0; j < values.length; j++) {
                if (v.length() > 0) {
                    v += ", ";
                }
                v += "'" + values[j] + "'";
            }
            params.addItem(new Object[] { v }, name);
        }
    }
}
