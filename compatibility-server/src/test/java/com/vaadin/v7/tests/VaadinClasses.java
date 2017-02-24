package com.vaadin.v7.tests;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.tests.server.ClasspathHelper;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Field;

@SuppressWarnings("deprecation")
public class VaadinClasses {

    private static final Set<String> WHITE_LIST_FQNS = new HashSet<String>();

    public static List<Class<? extends Field>> getFields() {
        return getServerClasses(Field.class::isAssignableFrom)
                .map(VaadinClasses::castFieldClass)
                .collect(Collectors.toList());
    }

    public static Stream<Class<?>> getServerClasses(
            Predicate<? super Class<?>> predicate) {
        try {
            File testRoot = new File(com.vaadin.tests.VaadinClasses.class
                    .getResource("/").toURI());
            File compatibilityTestRoot = new File(
                    VaadinClasses.class.getResource("/").toURI());
            ClasspathHelper helper = new ClasspathHelper(
                    fqn -> !fqn.startsWith("com.vaadin.v7.ui"));
            return helper.getVaadinClassesFromClasspath(
                    entry -> !compatibilityTestRoot.equals(new File(entry))
                            && !testRoot.equals(new File(entry)),
                    cls -> predicate.test(cls) && !cls.isInterface()
                            && !Modifier.isAbstract(cls.getModifiers()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class<? extends Component>> getComponents() {
        return getServerClasses(Component.class::isAssignableFrom)
                .map(VaadinClasses::castComponentClass)
                .collect(Collectors.toList());
    }

    private static Class<? extends Field> castFieldClass(Class<?> clazz) {
        return (Class<? extends Field>) clazz;
    }

    private static Class<? extends Component> castComponentClass(
            Class<?> clazz) {
        return (Class<? extends Component>) clazz;
    }

    protected static Set<String> getWhiteListFqns() {
        return WHITE_LIST_FQNS;
    }
}
