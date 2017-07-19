package com.fuckolympus.arc.camera.api;

/**
 * Created by alex on 7.6.17.
 */
public enum ShutterMode {

    FST_PUSH("1stpush"),

    SND_PUSH("2ndpush"),

    FST_SND_PUSH("1st2ndpush"),

    FST_RELEASE("1strelease"),

    SND_RELEASE("2ndrelease"),

    SND_FST_RELEASE("2nd1strelease");

    private String com;

    public String getCom() {
        return com;
    }

    ShutterMode(String com) {
        this.com = com;
    }
}
