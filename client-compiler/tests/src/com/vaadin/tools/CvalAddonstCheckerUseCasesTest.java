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

package com.vaadin.tools;

import static com.vaadin.tools.CvalAddonsChecker.VAADIN_AGPL;
import static com.vaadin.tools.CvalAddonsChecker.VAADIN_CVAL;
import static com.vaadin.tools.CvalChecker.computeLicenseName;
import static com.vaadin.tools.CvalChecker.deleteCache;
import static com.vaadin.tools.CvalCheckerTest.INVALID_KEY;
import static com.vaadin.tools.CvalCheckerTest.VALID_KEY;
import static com.vaadin.tools.CvalCheckerTest.addLicensedJarToClasspath;
import static com.vaadin.tools.CvalCheckerTest.cachedPreferences;
import static com.vaadin.tools.CvalCheckerTest.captureSystemOut;
import static com.vaadin.tools.CvalCheckerTest.expiredLicenseProvider;
import static com.vaadin.tools.CvalCheckerTest.productNameAgpl;
import static com.vaadin.tools.CvalCheckerTest.productNameCval;
import static com.vaadin.tools.CvalCheckerTest.readSystemOut;
import static com.vaadin.tools.CvalCheckerTest.restoreSystemOut;
import static com.vaadin.tools.CvalCheckerTest.saveCache;
import static com.vaadin.tools.CvalCheckerTest.unreachableLicenseProvider;
import static com.vaadin.tools.CvalCheckerTest.validEvaluationLicenseProvider;
import static com.vaadin.tools.CvalCheckerTest.validLicenseProvider;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tools.CvalChecker.CvalServer;

/**
 * Tests for Use Cases
 */
public class CvalAddonstCheckerUseCasesTest {

    enum License {
        NONE, EVAL, INVALID, REAL, EVAL_EXPIRED, REAL_EXPIRED
    };

    enum Version {
        AGPL, CVAL
    };

    enum Validated {
        YES, NO, OLD_KEY
    };

    enum Network {
        ON, OFF
    };

    enum Compile {
        YES, NO
    };

    enum Cached {
        YES, NO
    };

    enum Message {
        AGPL("AGPL"), VALID(">.* valid"), INVALID("not valid"), NO_LICENSE(
                "not found"), NO_VALIDATED("has not been validated"), EXPIRED(
                "has expired"), EVALUATION("evaluation");

        String msg;

        Message(String s) {
            msg = s;
        }
    }

    @Test
    public void testUseCases() throws Exception {
        useCase(1, License.NONE, Version.AGPL, Validated.NO, Network.OFF,
                Compile.YES, Cached.NO, Message.AGPL);

        useCase(2, License.NONE, Version.CVAL, Validated.NO, Network.ON,
                Compile.NO, Cached.NO, Message.NO_LICENSE);

        useCase(3, License.NONE, Version.CVAL, Validated.NO, Network.OFF,
                Compile.NO, Cached.NO, Message.NO_LICENSE);

        useCase(4, License.EVAL, Version.CVAL, Validated.NO, Network.ON,
                Compile.YES, Cached.YES, Message.EVALUATION);

        useCase(5, License.INVALID, Version.CVAL, Validated.NO, Network.OFF,
                Compile.YES, Cached.NO, Message.NO_VALIDATED);

        useCase(6, License.INVALID, Version.CVAL, Validated.NO, Network.ON,
                Compile.NO, Cached.NO, Message.INVALID);

        useCase(7, License.REAL, Version.CVAL, Validated.NO, Network.ON,
                Compile.YES, Cached.YES, Message.VALID);

        useCase(8, License.REAL, Version.CVAL, Validated.NO, Network.OFF,
                Compile.YES, Cached.NO, Message.NO_VALIDATED);

        useCase(9, License.REAL, Version.CVAL, Validated.YES, Network.OFF,
                Compile.YES, Cached.YES, Message.VALID);

        useCase(10, License.EVAL_EXPIRED, Version.CVAL, Validated.NO,
                Network.ON, Compile.NO, Cached.YES, Message.EXPIRED);

        useCase(11, License.EVAL_EXPIRED, Version.CVAL, Validated.YES,
                Network.OFF, Compile.NO, Cached.YES, Message.EXPIRED);

        useCase(12, License.REAL_EXPIRED, Version.CVAL, Validated.YES,
                Network.OFF, Compile.NO, Cached.YES, Message.EXPIRED);

        useCase(13, License.REAL_EXPIRED, Version.CVAL, Validated.NO,
                Network.ON, Compile.NO, Cached.YES, Message.EXPIRED);

        useCase(14, License.INVALID, Version.CVAL, Validated.OLD_KEY,
                Network.OFF, Compile.YES, Cached.NO, Message.NO_VALIDATED);
    }

    @Test
    public void testMultipleLicenseUseCases() throws Exception {
        addLicensedJarToClasspath("test.foo", VAADIN_CVAL);
        System.setProperty(computeLicenseName("test.foo"), VALID_KEY);

        useCase(15, License.REAL, Version.CVAL, Validated.YES, Network.OFF,
                Compile.YES, Cached.YES, Message.NO_VALIDATED);

        useCase(16, License.REAL, Version.CVAL, Validated.YES, Network.ON,
                Compile.NO, Cached.YES, Message.INVALID);
    }

    private void useCase(int number, License lic, Version ver, Validated val,
            Network net, Compile res, Cached cached, Message msg)
            throws Exception {

        if (ver == Version.AGPL) {
            addLicensedJarToClasspath(productNameAgpl, VAADIN_AGPL);
            addLicensedJarToClasspath(productNameCval, null);
        } else {
            addLicensedJarToClasspath(productNameAgpl, null);
            addLicensedJarToClasspath(productNameCval, VAADIN_CVAL);
        }

        String licenseName = computeLicenseName(productNameCval);

        if (lic == License.NONE) {
            System.getProperties().remove(licenseName);
        } else if (lic == License.INVALID) {
            System.setProperty(licenseName, INVALID_KEY);
        } else {
            System.setProperty(licenseName, VALID_KEY);
        }

        if (val == Validated.NO) {
            deleteCache(productNameCval);
        } else {
            String type = lic == License.EVAL || lic == License.EVAL_EXPIRED ? "evaluation"
                    : null;
            Boolean expired = lic == License.EVAL_EXPIRED
                    || lic == License.REAL_EXPIRED ? true : null;
            String key = val == Validated.OLD_KEY ? "oldkey" : null;
            saveCache(productNameCval, key, expired, null, type);
        }

        CvalServer licenseProvider = validLicenseProvider;
        if (net == Network.OFF) {
            licenseProvider = unreachableLicenseProvider;
        } else if (lic == License.EVAL_EXPIRED || lic == License.REAL_EXPIRED) {
            licenseProvider = expiredLicenseProvider;
        } else if (lic == License.EVAL) {
            licenseProvider = validEvaluationLicenseProvider;
        }

        CvalAddonsChecker addonChecker = new CvalAddonsChecker();
        addonChecker.setLicenseProvider(licenseProvider).setFilter(".*test.*");

        captureSystemOut();

        String testNumber = "Test #" + number + " ";
        String message;
        try {
            addonChecker.run();
            message = readSystemOut();
            if (res == Compile.NO) {
                Assert.fail(testNumber + "Exception not thrown:" + message);
            }
        } catch (Exception e) {
            restoreSystemOut();
            message = e.getMessage();
            if (res == Compile.YES) {
                Assert.fail(testNumber + "Unexpected Exception: "
                        + e.getMessage());
            }
        }

        // System.err.println("\n> " + testNumber + " " + lic + " " + ver + " "
        // + val + " " + net + " " + res + " " + cached + "\n" + message);

        Assert.assertTrue(testNumber + "Fail:\n" + message
                + "\nDoes not match:" + msg.msg,
                message.matches("(?s).*" + msg.msg + ".*"));

        String c = cachedPreferences(productNameCval);
        Assert.assertTrue(testNumber + "Fail: cacheExists != "
                + (cached == Cached.YES) + "\n  " + c,
                (c != null) == (cached == Cached.YES));
    }
}
