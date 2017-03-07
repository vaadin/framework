/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.benchmarks;

import com.vaadin.ui.Label;

/*
 * This simple test shows the performance difference between the StringTokenizer implementation and the String.split() implementation in AbstractComponent.
 * Your results will vary.
 * The real world use case motivating it was a 10k Row table, which generated labels for 10 columns.
 * This is 1/10th of what this performance tester demonstrates.
 *
 * Please run with -server and -Xloggc:/tmp/gclog.vgc -verbose:gc -XX:+PrintCompilation
 *
 * My results Win 7 64, i7 2760QM 2.4Ghz, Java 7 21.
 *
 * Proposed Patch with StringTokenizer:
 * 13 GC activations, 1.009GB allocated memory over time, total time 948ms
 *
 * Current String.split implementation:
 * 31 GC activations, 2.277 GB allocated memory over time, total time 1557ms
 *
 */
public class PerformanceTester8759 {

    public static void main(String[] args) throws InterruptedException {
        warmup();

        long start = System.currentTimeMillis();
        runBenchmark(1000000);
        long end = System.currentTimeMillis();
        System.out.println("took " + (end - start) + " ms");

    }

    private static void warmup() throws InterruptedException {
        runBenchmark(10000);
        System.gc();
        System.out.println("warmup and gc complete. sleeping 5 seconds.");
        Thread.sleep(5000l);
        System.out.println("woke up - go.");
    }

    private static void runBenchmark(int loops) {
        Label label = null;
        for (int i = 0; i < loops; i++) {
            label = new Label();
            label.setStyleName("mainStyle");
            label.addStyleName("foo bar  baz");
            label.addStyleName("vaadin");
        }
    }

}
