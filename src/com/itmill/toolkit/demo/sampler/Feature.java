package com.itmill.toolkit.demo.sampler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.itmill.toolkit.ui.Component;

/**
 * Represents one feature or sample, with associated example.
 * <p>
 * 
 * </p>
 * 
 */
abstract public class Feature {

    public static final Object PROPERTY_ICON = "Icon";
    public static final Object PROPERTY_NAME = "Name";
    public static final Object PROPERTY_DESCRIPTION = "Description";

    private static final Object MUTEX = new Object();
    private String javaSource = null;

    /**
     * Gets the name of this feature. Defaults to class simplename, override if
     * needed. Try not to exceed 25 characters too much.
     * 
     * @return
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the description for this feature. Should describe what the example
     * intends to showcase. May contain HTML. 100 words should be enough, and
     * about 7 rows...
     * 
     * @return the description
     */
    abstract public String getDescription();

    /**
     * Gets related resources, i.e links to resources related to the example.
     * <p>
     * Good candidates are resources used to make the example (CSS, images,
     * custom layouts), documentation links (reference manual), articles (e.g.
     * pattern description, usability discussion).
     * </p>
     * <p>
     * May return null, if the example has no related resources.
     * </p>
     * <p>
     * The name of the NamedExternalResource will be shown in the UI. <br/> Note
     * that Javadoc should be referenced via {@link #getRelatedAPI()}.
     * </p>
     * 
     * @see #getThemeBase()
     * @return related external stuff
     */
    abstract public NamedExternalResource[] getRelatedResources();

    /**
     * Gets related API resources, i.e links to javadoc of used classes.
     * <p>
     * Good candidates are IT Mill classes being demoed in the example, or other
     * classes playing an important role in the example.
     * </p>
     * <p>
     * May return null, if the example uses no interesting classes.
     * <p>
     * 
     * @return
     */
    abstract public APIResource[] getRelatedAPI();

    /**
     * Gets related Features; the classes returned should extend Feature.
     * <p>
     * Good candidates are Features similar to this one, Features using the
     * functionality demoed in this one, and Features being used in this one.
     * </p>
     * <p>
     * May return null, if no other Features are related to this one.
     * <p>
     * 
     * @return
     */
    abstract public Class[] getRelatedFeatures();

    /**
     * Gets the name of the icon for this feature, usually simpleName +
     * extension.
     * 
     * @return
     */
    public String getIconName() {
        String icon = getClass().getSimpleName() + ".png";
        return icon;
    }

    /**
     * Get the example instance. Override if instantiation needs parameters.
     * 
     * @return
     */
    public Component getExample() {

        String className = this.getClass().getName() + "Example";
        try {
            Class<?> classObject = getClass().getClassLoader().loadClass(
                    className);
            return (Component) classObject.newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

    }

    public String getSource() {

        // TODO get's .txt for now, change to .java!
        synchronized (MUTEX) {
            if (javaSource == null) {
                StringBuffer src = new StringBuffer();
                try {
                    InputStream is = getClass().getResourceAsStream(
                            getClass().getSimpleName() + "Example.txt");
                    BufferedReader bis = new BufferedReader(
                            new InputStreamReader(is));
                    for (String line = bis.readLine(); null != line; line = bis
                            .readLine()) {
                        src.append(line);
                        src.append("\n");
                    }
                    javaSource = src.toString();
                } catch (Exception e) {
                    System.err.println("Could not read source for "
                            + getPathName());
                    javaSource = "Sorry, no source available right now.";
                }
            }
        }
        return javaSource;

    }

    public String getSourceHTML() {
        return getSource();
    }

    /**
     * Gets the name used when resolving the path for this feature. Usually no
     * need to overrride.
     * 
     * @return
     */
    public String getPathName() {
        return getClass().getSimpleName();
    }

    /**
     * Gets the base url used to reference theme resources.
     * 
     * @return
     */
    protected static final String getThemeBase() {
        return SamplerApplication.getThemeBase();
    }

    public String toString() {
        return getName();
    }

}