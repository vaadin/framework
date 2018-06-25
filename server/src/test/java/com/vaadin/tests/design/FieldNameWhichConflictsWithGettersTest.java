package com.vaadin.tests.design;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
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
        Assert.assertNotNull(v.caption);
        Assert.assertNotNull(v.description);
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
        String str = baos.toString("UTF-8");

        Document doc = Jsoup.parse(str);
        Element body = doc.body();
        Element captionElement = body.getElementById("caption");
        Assert.assertNotNull(captionElement);
        Assert.assertEquals("vaadin-label", captionElement.tagName());

        Element descriptionElement = captionElement.nextElementSibling();
        Assert.assertNotNull(descriptionElement);
        Assert.assertEquals("vaadin-text-field", descriptionElement.tagName());
        Assert.assertEquals("description", descriptionElement.attr("_id"));

    }
}
