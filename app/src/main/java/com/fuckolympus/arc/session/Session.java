package com.fuckolympus.arc.session;

import com.fuckolympus.arc.camera.api.CameraApi;
import com.fuckolympus.arc.camera.api.CameraState;
import com.fuckolympus.arc.settings.Settings;

/**
 * Created by alex on 3.6.17.
 */
public final class Session {

    private Session() {
        cameraApi = new CameraApi();
    }

    private static Session instance;

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
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
