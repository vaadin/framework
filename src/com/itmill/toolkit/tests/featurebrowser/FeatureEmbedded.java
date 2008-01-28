/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.OrderedLayout;

public class FeatureEmbedded extends Feature {

    public FeatureEmbedded() {
        super();
    }

    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final ClassResource flashResource = new ClassResource(
                "itmill_spin.swf", getApplication());
        final Embedded emb = new Embedded("Embedded Caption", flashResource);
        emb.setType(Embedded.TYPE_OBJECT);
        emb.setMimeType("application/x-shockwave-flash");
        emb.setWidth(250);
        emb.setHeight(100);
        l.addComponent(emb);

        // Properties
        propertyPanel = new PropertyPanel(emb);
        final Form ap = propertyPanel.createBeanPropertySet(new String[] {
                "type", "source", "width", "height", "widthUnits",
                "heightUnits", "codebase", "codetype", "archive", "mimeType",
                "standby", "classId" });
        ap.replaceWithSelect("type", new Object[] {
                new Integer(Embedded.TYPE_IMAGE),
                new Integer(Embedded.TYPE_OBJECT) }, new Object[] { "Image",
                "Object" });
        final Object[] units = new Object[Embedded.UNIT_SYMBOLS.length];
        final Object[] symbols = new Object[Embedded.UNIT_SYMBOLS.length];
        for (int i = 0; i < units.length; i++) {
            units[i] = new Integer(i);
            symbols[i] = Embedded.UNIT_SYMBOLS[i];
        }
        ap.replaceWithSelect("heightUnits", units, symbols);
        ap.replaceWithSelect("widthUnits", units, symbols);
        ap.replaceWithSelect("source", new Object[] { flashResource },
                new Object[] { "itmill_spin.swf" });
        propertyPanel.addProperties("Embedded Properties", ap);
        propertyPanel.getField("standby").setDescription(
                "The text to display while loading the object.");
        propertyPanel.getField("codebase").setDescription(
                "root-path used to access resources with relative paths.");
        propertyPanel.getField("codetype").setDescription(
                "MIME-type of the code.");
        propertyPanel
                .getField("classId")
                .setDescription(
                        "Unique object id. This can be used for example to identify windows components.");

        setJavadocURL("ui/Embedded.html");

        return l;
    }

    protected String getExampleSrc() {
        return "// Load image from jpg-file, that is in the same package with the application\n"
                + "Embedded e = new Embedded(\"Image title\",\n"
                + "   new ClassResource(\"image.jpg\", getApplication()));";
    }

    protected String getDescriptionXHTML() {
        return "The embedding feature allows for adding images, multimedia and other non-specified "
                + "content to your application. "
                + "The feature has provisions for embedding both applets and Active X controls. "
                + "Actual support for embedded media types is left to the terminal.";
    }

    protected String getImage() {
        return "icon_demo.png";
    }

    protected String getTitle() {
        return "Embedded";
    }

}
