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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.http.client.HttpClient;
import org.junit.runners.Parameterized;
import org.junit.runners.model.InitializationError;
import org.openqa.selenium.remote.internal.ApacheHttpClient;
import org.openqa.selenium.remote.internal.HttpClientFactory;

import com.vaadin.testbench.parallel.ParallelRunner;

/**
 * This runner is loosely based on FactoryTestRunner by Ted Young
 * (http://tedyoung.me/2011/01/23/junit-runtime-tests-custom-runners/). The
 * generated test names give information about the parameters used (unlike
 * {@link Parameterized}).
 * 
 * @since 7.1
 */
public class TB3Runner extends ParallelRunner {

    /**
     * Socket timeout for HTTP connections to the grid hub. The connection is
     * closed after 30 minutes of inactivity to avoid builds hanging for up to
     * three hours per connection if the test client crashes/hangs.
     */
    private static final int SOCKET_TIMEOUT = 30 * 60 * 1000;

    static {

        // reduce socket timeout to avoid tests hanging for three hours
        try {
            Field field = ApacheHttpClient.Factory.class
                    .getDeclaredField("defaultClientFactory");
            assert (Modifier.isStatic(field.getModifiers()));
            field.setAccessible(true);
            field.set(null, new HttpClientFactory() {
                @Override
                public HttpClient getGridHttpClient(int connection_timeout,
                        int socket_timeout) {

                    if (socket_timeout == 0 || socket_timeout > SOCKET_TIMEOUT) {
                        return super.getGridHttpClient(connection_timeout,
                                SOCKET_TIMEOUT);
                    }

                    return super.getGridHttpClient(connection_timeout,
                            socket_timeout);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Changing socket timeout for TestBench failed", e);
        }
    }

    public TB3Runner(Class<?> klass) throws InitializationError {
        super(klass);
    }

}
