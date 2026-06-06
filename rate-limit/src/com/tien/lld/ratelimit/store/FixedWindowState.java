package com.tien.lld.ratelimit.store;

public final class FixedWindowState {
    private long windowStartMillis;
    private int requestCount;

    public FixedWindowState(long windowStartMillis, int requestCount) {
        this.windowStartMillis = windowStartMillis;
        this.requestCount = requestCount;
    }

    public long getWindowStartMillis() {
        return windowStartMillis;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void reset(long newWindowStartMillis) {
        this.windowStartMillis = newWindowStartMillis;
        this.requestCount = 0;
    }

    public void incrementRequestCount() {
        this.requestCount++;
    }
}

