package com.fuckolympus.arc.session;

import android.content.Context;
import com.fuckolympus.arc.camera.api.CameraApi;
import com.fuckolympus.arc.camera.api.CameraState;
import com.fuckolympus.arc.settings.Settings;

/**
 * Created by alex on 3.6.17.
 */
public final class Session {

    private Session(Context applicationContext) {
        cameraApi = new CameraApi(applicationContext);
    }

    private static Session instance;

    public static Session getInstance(Context applicationContext) {
        if (instance == null) {
            instance = new Session(applicationContext);
        }
        return instance;
    }

    private CameraApi cameraApi;

    private CameraState cameraState;

    private Settings settings;

    public CameraApi getCameraApi() {
        return cameraApi;
    }

    public CameraState getCameraState() {
        return cameraState;
    }

    public void setCameraState(CameraState cameraState) {
        this.cameraState = cameraState;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
