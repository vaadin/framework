/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
                        System.err.println(String.format(
                                "%s: run %s/%s failed.",
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
