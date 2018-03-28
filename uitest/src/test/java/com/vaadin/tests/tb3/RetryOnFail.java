package com.vaadin.tests.tb3;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RetryOnFail implements TestRule {
    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base,
            final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null;
                int retryCount = getRetryCount();

                for (int i = 0; i <= retryCount; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
                        caughtThrowable = t;
                        System.err
                                .println(String.format("%s: run %s/%s failed.",
                                        description.getDisplayName(), i + 1,
                                        retryCount + 1));
                        System.err.println(t.getMessage());
                    }
                }
                throw caughtThrowable;
            }

            private int getRetryCount() {
                String retryCount = System
                        .getProperty("com.vaadin.testbench.max.retries");

                if (retryCount != null && retryCount != "") {
                    return Integer.parseInt(retryCount);
                }

                return 0;
            }
        };
    }
}
