package com.fuckolympus.arc.camera.api;

import org.apache.commons.lang3.builder.Builder;

/**
 * Created by alex on 18.7.17.
 */
public final class CameraState {

    private double focalValue;

    private double expComp;

    private String shutterSpeedValue;

    private CameraState(CameraStateBuilder cameraStateBuilder) {
        this.focalValue = cameraStateBuilder.focalValue;
        this.expComp = cameraStateBuilder.expComp;
        this.shutterSpeedValue = cameraStateBuilder.shutterSpeedValue;
    }

    public double getFocalValue() {
        return focalValue;
    }

    public double getExpComp() {
        return expComp;
    }

    public String getShutterSpeedValue() {
        return shutterSpeedValue;
    }

    public static class CameraStateBuilder implements Builder<CameraState> {

        private double focalValue;
        private double expComp;
        private String shutterSpeedValue;

        @Override
        public CameraState build() {
            return new CameraState(this);
        }
    }
}
