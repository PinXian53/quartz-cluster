package com.pino.quartzcluster.util;

import com.pino.quartzcluster.exception.InternalServerErrorException;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    private ThreadUtils() {

    }

    public static void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalServerErrorException(e);
        }
    }
}
