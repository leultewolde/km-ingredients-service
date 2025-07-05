package com.splunk.logging;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HttpEventCollectorLogbackAppender<E> extends AppenderBase<E> {
    private String url;
    private String token;
    private Layout<E> layout;

    @Override
    protected void append(E eventObject) {
        // no-op for stub
    }

}
