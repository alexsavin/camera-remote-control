package com.fuckolympus.arc.session;

import android.content.Context;
import com.fuckolympus.arc.camera.api.CameraApi;
import com.fuckolympus.arc.camera.api.CameraState;

/**
 * Created by alex on 3.6.17.
 */
public final class Session {

    private Session(Context applicationContext) {
        cameraApi = new CameraApi(applicationContext);
        //cameraState = new CameraState();
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

    public CameraApi getCameraApi() {
        return cameraApi;
    }

    public CameraState getCameraState() {
        return cameraState;
    }
}
