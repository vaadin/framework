package com.vaadin.tests.resources;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class NonExistingFileResource extends TestBase {

    @Override
    protected void setup() {
        Button existing = createButton("WEB-INF/web.xml");
        Button nonExisting = createButton("WEB-INF/web2.xml");
        addComponent(existing);
        addComponent(nonExisting);

    }

    private Button createButton(final String filename) {
        Button b = new Button("Download " + filename);
        b.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                FileResource res = new FileResource(new File(VaadinService
                        .getCurrent().getBaseDirectory() + "/" + filename));
                getMainWindow().open(res);

            }
        });
        return b;
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
