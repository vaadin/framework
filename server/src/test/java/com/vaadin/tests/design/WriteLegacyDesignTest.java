package com.vaadin.tests.design;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.Constants;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.util.CurrentInstance;

/**
 * Parse and write a legacy design (using the "v-" prefix).
 */
public class WriteLegacyDesignTest {

    // The context is used for accessing the created component hierarchy.
    private DesignContext ctx;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        properties.put(Constants.SERVLET_PARAMETER_LEGACY_DESIGN_PREFIX,
                "true");
        final DeploymentConfiguration configuration = new DefaultDeploymentConfiguration(
                WriteLegacyDesignTest.class, properties);

        VaadinService service = new VaadinServletService(null, configuration);

        CurrentInstance.set(VaadinService.class, service);

        ctx = Design.read(
                getClass().getResourceAsStream("testFile-legacy.html"), null);
    }

    @After
    public void tearDown() {
        CurrentInstance.set(VaadinService.class, null);
    }

    private ByteArrayOutputStream serializeDesign(DesignContext context)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Design.write(context, out);

        return out;
    }

    @Test
    public void designIsSerializedWithCorrectPrefixesAndPackageNames()
            throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);

        Document doc = Jsoup.parse(out.toString("UTF-8"));
        for (Node child : doc.body().childNodes()) {
            checkNode(child);
        }
    }

    private void checkNode(Node node) {
        if (node instanceof Element) {
            assertTrue("Wrong design element prefix",
                    node.nodeName().startsWith("v-"));
            for (Node child : node.childNodes()) {
                checkNode(child);
            }
        }
    }

}
