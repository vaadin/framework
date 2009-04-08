package com.itmill.toolkit.demo.sampler;

/**
 * A NamedExternalResource pointing to the javadoc for the given class. Knows
 * where the javadocs are located for some common APIs, but one can also specify
 * a javadoc baseurl. The name will be set to the class simpleName.
 * 
 */
public class APIResource extends NamedExternalResource {

    private static final String ITMILL_BASE = "http://toolkit.itmill.com/demo/doc/api/";
    private static final String JAVA_BASE = "http://java.sun.com/javase/6/docs/api/";
    private static final String SERVLET_BASE = "http://java.sun.com/products/servlet/2.5/docs/servlet-2_5-mr2";
    private static final String PORTLET_BASE = "http://developers.sun.com/docs/jscreator/apis/portlet";

    public APIResource(Class clazz) {
        this(resolveBaseUrl(clazz), clazz);
    }

    public APIResource(String baseUrl, Class clazz) {
        super(resolveName(clazz), getJavadocUrl(baseUrl, clazz));
    }

    private static String getJavadocUrl(String baseUrl, Class clazz) {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        String path = clazz.getName().replaceAll("\\.", "/");
        path = path.replaceAll("\\$", ".");
        return baseUrl + path + ".html";
    }

    /**
     * Tries to resolve the javadoc baseurl for the given class by looking at
     * the packagename.
     * 
     * @param clazz
     * @return
     */
    private static String resolveBaseUrl(Class clazz) {
        String name = clazz.getName();
        if (name.startsWith("javax.servlet.")) {
            return SERVLET_BASE;
        } else if (name.startsWith("javax.portlet.")) {
            return PORTLET_BASE;
        } else if (name.startsWith("java.") || name.startsWith("javax.")) {
            return JAVA_BASE;
        }
        return ITMILL_BASE;
    }

    private static String resolveName(Class clazz) {
        Class ec = clazz.getEnclosingClass();
        return (ec != null ? ec.getSimpleName() + "." : "")
                + clazz.getSimpleName();
    }
}
