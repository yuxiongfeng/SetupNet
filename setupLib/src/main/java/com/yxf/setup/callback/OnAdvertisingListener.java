package com.yxf.setup.callback;

public interface OnAdvertisingListener {
    void onStartAdvertising(byte[] data);

    void onSuccess();

    void onFail(String msg);
}
