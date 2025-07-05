package com.splunk.logging;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;

public class HttpEventCollectorLogbackAppender<E> extends AppenderBase<E> {
    private String url;
    private String token;
    private Layout<E> layout;

    @Override
    protected void append(E eventObject) {
        // no-op for stub
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }

    public Layout<E> getLayout() {
        return layout;
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
    }

    public String getUrl() {
        return url;
    }
}
