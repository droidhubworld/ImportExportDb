package com.droidhubworld.dbexporter;

public interface ExporterListener {
    void success(String s);

    void fail(String message, String exception);
}
