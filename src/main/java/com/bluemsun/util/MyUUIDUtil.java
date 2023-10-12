package com.bluemsun.util;

import java.util.Random;
import java.util.UUID;

public class MyUUIDUtil
{
    public static Long get10bitUUID() {
        Long bits = UUID.randomUUID().getMostSignificantBits();
        Long ans = new Random().nextLong() % 9 + 1;
        ans *= 1000000000L;
        ans += bits % 1000000000L;
        return Math.abs(ans);
    }
}
