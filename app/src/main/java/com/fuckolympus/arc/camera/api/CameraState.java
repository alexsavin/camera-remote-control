package com.fuckolympus.arc.camera.api;

import org.apache.commons.lang3.builder.Builder;

/**
 * Created by alex on 18.7.17.
 */
public final class CameraState {

    private String takeMode;

    private double focalValue;

    private double expComp;

    private String shutterSpeedValue;

    private CameraState(CameraStateBuilder cameraStateBuilder) {
        this.takeMode = cameraStateBuilder.takeMode;
        this.focalValue = cameraStateBuilder.focalValue;
        this.expComp = cameraStateBuilder.expComp;
        this.shutterSpeedValue = cameraStateBuilder.shutterSpeedValue;
    }

    public String getTakeMode() {
        return takeMode;
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

        private String takeMode;
        private double focalValue;
        private double expComp;
        private String shutterSpeedValue;

        public CameraStateBuilder withTakeMode(String takeMode) {
            this.takeMode = takeMode;
            return this;
        }

        public CameraStateBuilder withFocalValue(double focalValue) {
            this.focalValue = focalValue;
            return this;
        }

        public CameraStateBuilder withExpComp(double expComp) {
            this.expComp = expComp;
            return this;
        }

        public CameraStateBuilder withShutterSpeedValue(String shutterSpeedValue) {
            this.shutterSpeedValue = shutterSpeedValue;
            return this;
        }

        @Override
        public CameraState build() {
            return new CameraState(this);
        }
    }
}
