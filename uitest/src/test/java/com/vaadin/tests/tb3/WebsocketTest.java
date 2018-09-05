package com.vaadin.tests.tb3;

import com.vaadin.testbench.parallel.TestCategory;

/**
 * A {@link MultiBrowserTest} which restricts the tests to the browsers which
 * support websocket
 *
 * @author Vaadin Ltd
 */
@TestCategory("push")
public abstract class WebsocketTest extends MultiBrowserTest {
}
