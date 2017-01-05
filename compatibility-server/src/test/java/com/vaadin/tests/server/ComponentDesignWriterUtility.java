package com.vaadin.tests.server;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.components.colorpicker.ColorPickerGradient;
import com.vaadin.ui.components.colorpicker.ColorPickerGrid;
import com.vaadin.ui.components.colorpicker.ColorPickerHistory;
import com.vaadin.ui.components.colorpicker.ColorPickerPopup;
import com.vaadin.ui.components.colorpicker.ColorPickerPreview;
import com.vaadin.ui.components.colorpicker.ColorPickerSelect;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Utility class for outputting the declarative syntax of Vaadin components.
 */
public class ComponentDesignWriterUtility {

    private static final Set<String> WHITE_LIST_FQNS = new HashSet<>();
    private static final Document document = new Document("");
    private static final DesignContext designContext = new DesignContext(
            document);

    static {
        WHITE_LIST_FQNS.add(DragAndDropWrapper.class.getName());
        WHITE_LIST_FQNS.add(Navigator.EmptyView.class.getName());

        WHITE_LIST_FQNS.add(ColorPickerGradient.class.getName());
        WHITE_LIST_FQNS.add(ColorPickerPopup.class.getName());
        WHITE_LIST_FQNS.add(ColorPickerPreview.class.getName());
        WHITE_LIST_FQNS.add(ColorPickerGrid.class.getName());
        WHITE_LIST_FQNS.add(ColorPickerSelect.class.getName());
        WHITE_LIST_FQNS.add(ColorPickerHistory.class.getName());

        WHITE_LIST_FQNS
                .add(com.vaadin.v7.ui.components.colorpicker.ColorPickerGradient.class
                        .getName());
        WHITE_LIST_FQNS
                .add(com.vaadin.v7.ui.components.colorpicker.ColorPickerPopup.class
                        .getName());
        WHITE_LIST_FQNS
                .add(com.vaadin.v7.ui.components.colorpicker.ColorPickerPreview.class
                        .getName());
        WHITE_LIST_FQNS
                .add(com.vaadin.v7.ui.components.colorpicker.ColorPickerGrid.class
                        .getName());
        WHITE_LIST_FQNS
                .add(com.vaadin.v7.ui.components.colorpicker.ColorPickerSelect.class
                        .getName());
        WHITE_LIST_FQNS
                .add(com.vaadin.v7.ui.components.colorpicker.ColorPickerHistory.class
                        .getName());

        // ==================================================================
        // Classes that cannot be loaded
        // ==================================================================
        WHITE_LIST_FQNS.add(
                "com.vaadin.server.communication.PushAtmosphereHandler$AtmosphereResourceListener");
        WHITE_LIST_FQNS
                .add("com.vaadin.server.communication.PushAtmosphereHandler");
        WHITE_LIST_FQNS
                .add("com.vaadin.server.communication.PushRequestHandler$1");
        WHITE_LIST_FQNS
                .add("com.vaadin.server.communication.PushRequestHandler$2");
        WHITE_LIST_FQNS.add("com.vaadin.server.LegacyVaadinPortlet");
        WHITE_LIST_FQNS.add("com.vaadin.server.RestrictedRenderResponse");
        WHITE_LIST_FQNS
                .add("com.vaadin.server.VaadinPortlet$VaadinGateInRequest");
        WHITE_LIST_FQNS.add(
                "com.vaadin.server.VaadinPortlet$VaadinHttpAndPortletRequest");
        WHITE_LIST_FQNS
                .add("com.vaadin.server.VaadinPortlet$VaadinLiferayRequest");
        WHITE_LIST_FQNS.add(
                "com.vaadin.server.VaadinPortlet$VaadinWebLogicPortalRequest");
        WHITE_LIST_FQNS.add(
                "com.vaadin.server.VaadinPortlet$VaadinWebSpherePortalRequest");
        WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortlet");
        WHITE_LIST_FQNS.add("com.vaadin.server.VaadinPortletRequest");

        designContext.setShouldWriteDefaultValues(true);
    }

    @SafeVarargs
    public static List<String> getDeclarativeSyntax(
            Class<? extends Component>... components) {
        return getDeclarativeSyntax(Arrays.asList(components));
    }

    public static List<String> getDeclarativeSyntax(
            List<Class<? extends Component>> components) {
        List<String> declarativeStrings = components.stream()
                .map(ComponentDesignWriterUtility::getDeclarativeSyntax)
                .collect(Collectors.toList());

        return declarativeStrings;
    }

    @Test
    public void vaadin8ComponentsElementStartsWithVaadinPrefix()
            throws URISyntaxException {
        Assert.assertTrue(getVaadin8Components().stream()
                .map(ComponentDesignWriterUtility::getDeclarativeSyntax)
                .allMatch(element -> element.startsWith("<vaadin-")));
    }

    @Test
    public void vaadin7ComponentsElementStartsWithVaadinPrefix()
            throws URISyntaxException {
        Assert.assertTrue(getVaadin7Components().stream()
                .map(ComponentDesignWriterUtility::getDeclarativeSyntax)
                .allMatch(element -> element.startsWith("<vaadin7-")));
    }

    private static String getDeclarativeSyntax(
            Class<? extends Component> componentClass) {
        try {
            Component component = componentClass.newInstance();
            Element element = document.createElement(Design.getComponentMapper()
                    .componentToTag(component, designContext));
            component.writeDesign(element, designContext);
            return element.toString();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not write the declarative syntax for component "
                            + componentClass.getName(),
                    e);
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        System.out.println("Vaadin 8 components:");
        printFullDeclarativeSyntax(getVaadin8Components());

        System.out.println("Vaadin 7 components:");
        printFullDeclarativeSyntax(getVaadin7Components());

        System.out.println("\nClases that are explicitely excluded from "
                + "the design support introspection:");
        WHITE_LIST_FQNS.forEach(System.out::println);
    }

    private static void printFullDeclarativeSyntax(
            List<Class<? extends Component>> components) {
        components.stream().forEach(component -> System.out
                .println(getDeclarativeSyntax(component)));
    }

    private static List<Class<? extends Component>> getVaadin8Components()
            throws URISyntaxException {
        List<Class<? extends Component>> vaadin8Components = getVaadinComponentsFromClasspath(
                "/server/target/classes");
        if (vaadin8Components.isEmpty()) {
            throw new RuntimeException(
                    "No vaadin 8 components found on your classpath.");
        }
        return vaadin8Components;
    }

    private static List<Class<? extends Component>> getVaadin7Components()
            throws URISyntaxException {
        List<Class<? extends Component>> vaadin7Components = getVaadinComponentsFromClasspath(
                "compatibility-server");
        if (vaadin7Components.isEmpty()) {
            throw new RuntimeException(
                    "No vaadin 7 components found on your classpath.");
        }
        return vaadin7Components;
    }

    @SuppressWarnings("unchecked")
    private static List<Class<? extends Component>> getVaadinComponentsFromClasspath(
            String classpathFilter) throws URISyntaxException {
        File testRoot = new File(
                ComponentDesignWriterUtility.class.getResource("/").toURI());
        List<Class<? extends Component>> classes = new ClasspathHelper(
                WHITE_LIST_FQNS::contains).getVaadinClassesFromClasspath(
                        entry -> entry.contains(classpathFilter)
                                && !testRoot.equals(new File(entry)),
                        cls -> Component.class.isAssignableFrom(cls)
                                && !cls.isInterface()
                                && !Modifier.isAbstract(cls.getModifiers()))
                        .map(cls -> (Class<? extends Component>) cls)
                        .collect(Collectors.toList());
        return classes;
    }

}
