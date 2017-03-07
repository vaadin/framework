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
package com.vaadin.tests.util;

import java.util.concurrent.atomic.AtomicLong;

public class PortableRandom {
    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 48) - 1;
    private AtomicLong seed;

    public PortableRandom(long seed) {
        this.seed = new AtomicLong(0L);
        setSeed(seed);
    }

    synchronized public void setSeed(long seed) {
        seed = (seed ^ multiplier) & mask;
        this.seed.set(seed);
    }

    public int nextInt(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }

        if ((n & -n) == n) {
            return (int) ((n * (long) next(31)) >> 31);
        }

        int bits, val;
        do {
            bits = next(31);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }

    protected int next(int bits) {
        long oldseed, nextseed;
        AtomicLong seed = this.seed;
        do {
            oldseed = seed.get();
            nextseed = (oldseed * multiplier + addend) & mask;
        } while (!seed.compareAndSet(oldseed, nextseed));
        return (int) (nextseed >>> (48 - bits));
    }

    public boolean nextBoolean() {
        return next(1) != 0;
    }

}
