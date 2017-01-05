package com.vaadin.tests;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.tests.server.ClasspathHelper;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

@SuppressWarnings("deprecation")
public class VaadinClasses {

    public static void main(String[] args) {
        System.out.println("ComponentContainers");
        System.out.println("===================");
        for (Class<? extends ComponentContainer> c : getComponentContainers()) {
            System.out.println(c.getName());
        }
        System.out.println();
        System.out.println("Components");
        System.out.println("==========");
        for (Class<? extends Component> c : getComponents()) {
            System.out.println(c.getName());
        }
        System.out.println();
        System.out.println("Server side classes");
        System.out.println("===================");
        for (Class<?> c : getAllServerSideClasses()) {
            System.out.println(c.getName());
        }
    }

    public static List<Class<? extends Component>> getComponents() {
        return getServerClasses(Component.class::isAssignableFrom)
                .map(VaadinClasses::castComponentClass)
                .collect(Collectors.toList());
    }

    public static List<Class<? extends Object>> getThemeClasses() {
        return getServerClasses(clazz -> clazz.getPackage().getName()
                .equals("com.vaadin.ui.themes")).collect(Collectors.toList());
    }

    public static List<Class<? extends Object>> getAllServerSideClasses() {
        return getServerClasses(clazz -> true).collect(Collectors.toList());
    }

    public static List<Class<? extends ComponentContainer>> getComponentContainers() {
        return getServerClasses(ComponentContainer.class::isAssignableFrom)
                .filter(clazz -> clazz.getPackage().getName()
                        .startsWith("com.vaadin.ui"))
                .map(VaadinClasses::castContainerClass)
                .collect(Collectors.toList());
    }

    public static List<Class<? extends ComponentContainer>> getComponentContainersSupportingAddRemoveComponent() {
        List<Class<? extends ComponentContainer>> classes = getComponentContainers();
        classes.remove(PopupView.class);
        classes.remove(CustomComponent.class);
        classes.remove(DragAndDropWrapper.class);
        classes.remove(CustomComponent.class);
        classes.remove(LoginForm.class);
        classes.remove(UI.class);

        return classes;
    }

    public static List<Class<? extends ComponentContainer>> getComponentContainersSupportingUnlimitedNumberOfComponents() {
        List<Class<? extends ComponentContainer>> classes = getComponentContainersSupportingAddRemoveComponent();
        classes.remove(VerticalSplitPanel.class);
        classes.remove(HorizontalSplitPanel.class);
        classes.remove(Window.class);

        return classes;
    }

    public static Stream<Class<?>> getServerClasses(
            Predicate<? super Class<?>> predicate) {
        try {
            File testRoot = new File(
                    VaadinClasses.class.getResource("/").toURI());
            ClasspathHelper helper = new ClasspathHelper();
            return helper
                    .getVaadinClassesFromClasspath(
                            entry -> !testRoot.equals(new File(entry)))
                    .filter(predicate);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<? extends Component> castComponentClass(
            Class<?> clazz) {
        return (Class<? extends Component>) clazz;
    }

    private static Class<? extends ComponentContainer> castContainerClass(
            Class<?> clazz) {
        return (Class<? extends ComponentContainer>) clazz;
    }
}
