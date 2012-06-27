/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ObjectElement;
import com.google.gwt.dom.client.ScriptElement;

/**
 * ResourceLoader lets you dynamically include external scripts and styles on
 * the page and lets you know when the resource has been loaded.
 * 
 * You can also preload resources, allowing them to get cached by the browser
 * without being evaluated. This enables downloading multiple resources at once
 * while still controlling in which order e.g. scripts are executed.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class ResourceLoader {
    /**
     * Event fired when a resource has been loaded.
     */
    public static class ResourceLoadEvent {
        private ResourceLoader loader;
        private String resourceUrl;
        private final boolean preload;

        /**
         * Creates a new event.
         * 
         * @param loader
         *            the resource loader that has loaded the resource
         * @param resourceUrl
         *            the url of the loaded resource
         * @param preload
         *            true if the resource has only been preloaded, false if
         *            it's fully loaded
         */
        public ResourceLoadEvent(ResourceLoader loader, String resourceUrl,
                boolean preload) {
            this.loader = loader;
            this.resourceUrl = resourceUrl;
            this.preload = preload;
        }

        /**
         * Gets the resource loader that has fired this event
         * 
         * @return the resource loader
         */
        public ResourceLoader getResourceLoader() {
            return loader;
        }

        /**
         * Gets the absolute url of the loaded resource.
         * 
         * @return the absolute url of the loaded resource
         */
        public String getResourceUrl() {
            return resourceUrl;
        }

        /**
         * Returns true if the resource has been preloaded, false if it's fully
         * loaded
         * 
         * @see ResourceLoader#preloadResource(String, ResourceLoadListener)
         * 
         * @return true if the resource has been preloaded, false if it's fully
         *         loaded
         */
        public boolean isPreload() {
            return preload;
        }
    }

    /**
     * Event listener that gets notified when a resource has been loaded
     */
    public interface ResourceLoadListener {
        /**
         * Notified this ResourceLoadListener that a resource has been loaded
         * 
         * @see ResourceLoadEvent
         * 
         * @param event
         *            a resource load event with information about the loaded
         *            resource
         */
        public void onResourceLoad(ResourceLoadEvent event);
    }

    private static final ResourceLoader INSTANCE = GWT
            .create(ResourceLoader.class);

    private ApplicationConnection connection;

    private final Set<String> loadedResources = new HashSet<String>();
    private final Set<String> preloadedResources = new HashSet<String>();

    private final Map<String, Collection<ResourceLoadListener>> loadListeners = new HashMap<String, Collection<ResourceLoadListener>>();
    private final Map<String, Collection<ResourceLoadListener>> preloadListeners = new HashMap<String, Collection<ResourceLoadListener>>();

    private final Element head;

    /**
     * Creates a new resource loader. You should generally not create you own
     * resource loader, but instead use {@link ResourceLoader#get()} to get an
     * instance.
     */
    protected ResourceLoader() {
        Document document = Document.get();
        head = document.getElementsByTagName("head").getItem(0);

        // detect already loaded scripts and stylesheets
        NodeList<Element> scripts = document.getElementsByTagName("script");
        for (int i = 0; i < scripts.getLength(); i++) {
            ScriptElement element = ScriptElement.as(scripts.getItem(i));
            String src = element.getSrc();
            if (src != null && src.length() != 0) {
                loadedResources.add(src);
            }
        }

        NodeList<Element> links = document.getElementsByTagName("link");
        for (int i = 0; i < links.getLength(); i++) {
            LinkElement linkElement = LinkElement.as(links.getItem(i));
            String rel = linkElement.getRel();
            String href = linkElement.getHref();
            if ("stylesheet".equalsIgnoreCase(rel) && href != null
                    && href.length() != 0) {
                loadedResources.add(href);
            }
        }
    }

    /**
     * Returns the default ResourceLoader
     * 
     * @return the default ResourceLoader
     */
    public static ResourceLoader get() {
        return INSTANCE;
    }

    /**
     * Load a script and notify a listener when the script is loaded. Calling
     * this method when the script is currently loading or already loaded
     * doesn't cause the script to be loaded again, but the listener will still
     * be notified when appropriate.
     * 
     * 
     * @param scriptUrl
     *            the url of the script to load
     * @param resourceLoadListener
     *            the listener that will get notified when the script is loaded
     */
    public void loadScript(final String scriptUrl,
            final ResourceLoadListener resourceLoadListener) {
        final String url = getAbsoluteUrl(scriptUrl);
        if (loadedResources.contains(url)) {
            if (resourceLoadListener != null) {
                resourceLoadListener.onResourceLoad(new ResourceLoadEvent(this,
                        url, false));
            }
            return;
        }

        if (preloadListeners.containsKey(url)) {
            preloadResource(url, new ResourceLoadListener() {
                public void onResourceLoad(ResourceLoadEvent event) {
                    loadScript(url, resourceLoadListener);
                }
            });
            return;
        }

        if (addListener(url, resourceLoadListener, loadListeners)) {
            ScriptElement scriptTag = Document.get().createScriptElement();
            scriptTag.setSrc(url);
            scriptTag.setType("text/javascript");
            addOnloadHandler(scriptTag, url, false);
            head.appendChild(scriptTag);
        }
    }

    private static String getAbsoluteUrl(String url) {
        AnchorElement a = Document.get().createAnchorElement();
        a.setHref(url);
        return a.getHref();
    }

    /**
     * Download a resource and notify a listener when the resource is loaded
     * without attempting to interpret the resource. When a resource has been
     * preloaded, it will be present in the browser's cache (provided the HTTP
     * headers allow caching), making a subsequent load operation complete
     * without having to wait for the resource to be downloaded again.
     * 
     * Calling this method when the resource is currently loading, currently
     * preloading, already preloaded or already loaded doesn't cause the
     * resource to be preloaded again, but the listener will still be notified
     * when appropriate.
     * 
     * @param url
     *            the url of the resource to preload
     * @param resourceLoadListener
     *            the listener that will get notified when the resource is
     *            preloaded
     */
    public void preloadResource(String url,
            ResourceLoadListener resourceLoadListener) {
        url = getAbsoluteUrl(url);
        if (loadedResources.contains(url) || preloadedResources.contains(url)) {
            if (resourceLoadListener != null) {
                resourceLoadListener.onResourceLoad(new ResourceLoadEvent(this,
                        url, !loadedResources.contains(url)));
            }
            return;
        }

        if (addListener(url, resourceLoadListener, preloadListeners)
                && !loadListeners.containsKey(url)) {
            // Inject loader element if this is the first time this is preloaded
            // AND the resources isn't already being loaded in the normal way

            Element element = getPreloadElement(url);
            addOnloadHandler(element, url, true);

            // TODO Remove object when loaded (without causing spinner in FF)
            Document.get().getBody().appendChild(element);
        }
    }

    private static Element getPreloadElement(String url) {
        if (BrowserInfo.get().isIE()) {
            ScriptElement element = Document.get().createScriptElement();
            element.setSrc(url);
            element.setType("text/cache");
            return element;
        } else {
            ObjectElement element = Document.get().createObjectElement();
            element.setData(url);
            element.setType("text/plain");
            element.setHeight("0px");
            element.setWidth("0px");
            return element;
        }
    }

    private native void addOnloadHandler(Element element, String url,
            boolean preload)
    /*-{
        var self = this;
        var done = $entry(function() {
            element.onloadDone = true;
            element.onload = null;
            element.onreadystatechange = null;
            self.@com.vaadin.terminal.gwt.client.ResourceLoader::onResourceLoad(Ljava/lang/String;Z)(url, preload);
        });
        element.onload = function() {
            if (!element.onloadDone) {
                done(); 
            }
        };
        element.onreadystatechange = function() { 
            if (("loaded" === element.readyState || "complete" === element.readyState) && !element.onloadDone ) {
                done();
            }
        };       
    }-*/;

    /**
     * Load a stylesheet and notify a listener when the stylesheet is loaded.
     * Calling this method when the stylesheet is currently loading or already
     * loaded doesn't cause the stylesheet to be loaded again, but the listener
     * will still be notified when appropriate.
     * 
     * @param stylesheetUrl
     *            the url of the stylesheet to load
     * @param resourceLoadListener
     *            the listener that will get notified when the stylesheet is
     *            loaded
     */
    public void loadStylesheet(final String stylesheetUrl,
            final ResourceLoadListener resourceLoadListener) {
        final String url = getAbsoluteUrl(stylesheetUrl);
        if (loadedResources.contains(url)) {
            if (resourceLoadListener != null) {
                resourceLoadListener.onResourceLoad(new ResourceLoadEvent(this,
                        url, false));
            }
            return;
        }

        if (preloadListeners.containsKey(url)) {
            preloadResource(url, new ResourceLoadListener() {
                public void onResourceLoad(ResourceLoadEvent event) {
                    loadStylesheet(url, resourceLoadListener);
                }
            });
            return;
        }

        if (addListener(url, resourceLoadListener, loadListeners)) {
            LinkElement linkElement = Document.get().createLinkElement();
            linkElement.setRel("stylesheet");
            linkElement.setType("text/css");
            linkElement.setHref(url);

            if (BrowserInfo.get().isSafari()) {
                // Safari doesn't fire onload events for link elements
                // See http://www.phpied.com/when-is-a-stylesheet-really-loaded/
                // TODO Stop checking after some timeout
                Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
                    public boolean execute() {
                        if (isStyleSheetPresent(url)) {
                            onResourceLoad(url, false);
                            return false; // Stop repeating
                        } else {
                            return true; // Continue repeating
                        }
                    }
                }, 10);
            } else {
                addOnloadHandler(linkElement, url, false);
            }

            head.appendChild(linkElement);
        }
    }

    private static native boolean isStyleSheetPresent(String url)
    /*-{
        for(var i = 0; i < $doc.styleSheets.length; i++) {
            if ($doc.styleSheets[i].href === url) {
                return true;
            }
        }
        return false;
    }-*/;

    private static boolean addListener(String url,
            ResourceLoadListener listener,
            Map<String, Collection<ResourceLoadListener>> listenerMap) {
        Collection<ResourceLoadListener> listeners = listenerMap.get(url);
        if (listeners == null) {
            listeners = new HashSet<ResourceLoader.ResourceLoadListener>();
            listeners.add(listener);
            listenerMap.put(url, listeners);
            return true;
        } else {
            listeners.add(listener);
            return false;
        }
    }

    private void onResourceLoad(String resource, boolean preload) {
        Collection<ResourceLoadListener> listeners;
        if (preload) {
            preloadedResources.add(resource);
            listeners = preloadListeners.remove(resource);
        } else {
            if (preloadListeners.containsKey(resource)) {
                // Also fire preload events for potential listeners
                onResourceLoad(resource, true);
            }
            preloadedResources.remove(resource);
            loadedResources.add(resource);
            listeners = loadListeners.remove(resource);
        }
        if (listeners != null && !listeners.isEmpty()) {
            ResourceLoadEvent event = new ResourceLoadEvent(this, resource,
                    preload);
            for (ResourceLoadListener listener : listeners) {
                if (listener != null) {
                    listener.onResourceLoad(event);
                }
            }
        }
    }

}
