package com.itmill.toolkit.demo.sampler;

import com.itmill.toolkit.terminal.ExternalResource;

public class APIResource extends ExternalResource {

    private static final String BASE_URL = "http://toolkit.itmill.com/demo/doc/api/";

    private String name;

    public APIResource(Class clazz) {
        this(BASE_URL, clazz);
    }

    public APIResource(String baseUrl, Class clazz) {
        super(getJavadocUrl(baseUrl, clazz));
        name = clazz.getSimpleName();
    }

    private static String getJavadocUrl(String baseUrl, Class clazz) {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        String path = clazz.getName().replaceAll("\\.", "/");
        return baseUrl + path + ".html";
    }

    public String getName() {
        return name;
    }
}
