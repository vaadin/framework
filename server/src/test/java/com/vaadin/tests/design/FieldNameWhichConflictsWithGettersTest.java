package com.vaadin.tests.design;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

public class FieldNameWhichConflictsWithGettersTest {

    @DesignRoot("MyVerticalLayout.html")
    public static class MyVerticalLayout extends VerticalLayout {
        private Label caption;
        private TextField description;

        public MyVerticalLayout() {
            Design.read(this);
        }
    }

    @Test
    public void readWithConflictingFields() {
        MyVerticalLayout v = new MyVerticalLayout();
        assertNotNull(v.caption);
        assertNotNull(v.description);
    }

    @Test
    public void writeWithConflictingFields() throws IOException {
        VerticalLayout v = new VerticalLayout();
        Label l = new Label();
        l.setId("caption");
        TextField tf = new TextField();
        v.addComponents(l, tf);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DesignContext context = new DesignContext();
        context.setComponentLocalId(tf, "description");
        context.setRootComponent(v);

        Design.write(context, baos);
        String str = baos.toString(UTF_8.name());

        Document doc = Jsoup.parse(str);
        Element body = doc.body();
        Element captionElement = body.getElementById("caption");
        assertNotNull(captionElement);
        assertEquals("vaadin-label", captionElement.tagName());

        Element descriptionElement = captionElement.nextElementSibling();
        assertNotNull(descriptionElement);
        assertEquals("vaadin-text-field", descriptionElement.tagName());
        assertEquals("description", descriptionElement.attr("_id"));

    }
}
