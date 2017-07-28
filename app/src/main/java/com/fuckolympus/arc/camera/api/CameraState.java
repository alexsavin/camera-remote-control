package com.fuckolympus.arc.camera.api;

import com.fuckolympus.arc.camera.vo.Desc;
import com.fuckolympus.arc.camera.vo.Desclist;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;

import static com.fuckolympus.arc.camera.api.CameraApi.EXPCOMP_PROP;
import static com.fuckolympus.arc.camera.api.CameraApi.FOCALVALUE_PROP;
import static com.fuckolympus.arc.camera.api.CameraApi.SHUTSPEEDVALUE_PROP;
import static com.fuckolympus.arc.camera.api.CameraApi.TAKEMODE_PROP;

/**
 * Created by alex on 18.7.17.
 */
public final class CameraState {

    public String takeMode;

    public String focalValue;

    public String expComp;

    public String shutterSpeedValue;

    private String[] takeModeEnum;

    private String[] shutterSpeedValueEnum;

    private String[] focalValueEnum;

    public String[] getTakeModeEnum() {
        return takeModeEnum;
    }

    public String[] getShutterSpeedValueEnum() {
        return shutterSpeedValueEnum;
    }

    public String[] getFocalValueEnum() {
        return focalValueEnum;
    }

    public static class CameraStateBuilder implements Builder<CameraState> {

        private Desclist desclist;

        public CameraStateBuilder setDesclist(Desclist desclist) {
            this.desclist = desclist;
            return this;
        }

        @Override
        public CameraState build() {
            Validate.notNull(desclist);

            return buildCameraState();
        }

        private CameraState buildCameraState() {
            CameraState cameraState = new CameraState();

            Desc takeMode = getPropDesc(TAKEMODE_PROP);
            if (null != takeMode) {
                cameraState.takeMode = takeMode.value;
                cameraState.takeModeEnum = StringUtils.split(takeMode.valueEnum, StringUtils.SPACE);
            }

            Desc shutterSpeedValue = getPropDesc(SHUTSPEEDVALUE_PROP);
            if (null != shutterSpeedValue) {
                cameraState.shutterSpeedValue = shutterSpeedValue.value;
                cameraState.shutterSpeedValueEnum = StringUtils.split(shutterSpeedValue.valueEnum, StringUtils.SPACE);
            }

            Desc focalValue = getPropDesc(FOCALVALUE_PROP);
            if (null != focalValue) {
                cameraState.focalValue = focalValue.value;
                cameraState.focalValueEnum = StringUtils.split(focalValue.valueEnum, StringUtils.SPACE);
            }


            cameraState.expComp = getPropValue(EXPCOMP_PROP);

            return cameraState;
        }

        private String getPropValue(String propname) {
            for (Desc desc : desclist.descs) {
                if (propname.equalsIgnoreCase(desc.propname)) {
                    return desc.value;
                }
            }
            return StringUtils.EMPTY;
        }

        private Desc getPropDesc(String propname) {
            for (Desc desc : desclist.descs) {
                if (propname.equalsIgnoreCase(desc.propname)) {
                    return desc;
                }
            }
            return null;
        }
    }
}
