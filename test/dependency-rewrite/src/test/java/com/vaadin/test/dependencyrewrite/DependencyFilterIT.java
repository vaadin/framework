package com.vaadin.test.dependencyrewrite;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.vaadin.testbench.TestBenchTestCase;

public class DependencyFilterIT extends TestBenchTestCase {

    @Test
    public void dynamicallyAddedResources() {
        setDriver(new PhantomJSDriver());
        getDriver().get("http://localhost:8080/dynamic/");
        assertEquals(1L, ((JavascriptExecutor) getDriver())
                .executeScript("return window.jqueryLoaded"));
    }

    @Test
    public void initiallyLoadedResources() {
        setDriver(new PhantomJSDriver());
        getDriver().get("http://localhost:8080/initial/");
        // 2 because of https://github.com/vaadin/framework/issues/9181
        assertEquals(2L, ((JavascriptExecutor) getDriver())
                .executeScript("return window.jqueryLoaded"));
    }

}
