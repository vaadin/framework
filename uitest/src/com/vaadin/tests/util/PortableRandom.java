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
