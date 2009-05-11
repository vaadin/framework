package com.vaadin.demo.sampler;

import com.vaadin.terminal.ExternalResource;

public class NamedExternalResource extends ExternalResource {

    private String name;

    public NamedExternalResource(String name, String sourceURL) {
        super(sourceURL);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
