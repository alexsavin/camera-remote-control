package com.fuckolympus.arc.camera.api;

import android.content.Context;
import android.graphics.Bitmap;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fuckolympus.arc.camera.vo.Caminfo;
import com.fuckolympus.arc.camera.vo.Desclist;
import com.fuckolympus.arc.camera.vo.Get;
import com.fuckolympus.arc.camera.vo.ImageFile;
import com.fuckolympus.arc.error.CommunicationException;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.HttpUtil;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;
import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 4.6.17.
 */
public class CameraApi {

    public static final String CAMERA_URL = "http://192.168.0.10";

    public static final String GET_CAMINFO = "/get_caminfo.cgi";

    public static final String SWITCH_MODE_SHUTTER = "/switch_cammode.cgi?mode=shutter";

    public static final String SWITCH_MODE_REC = "/switch_cammode.cgi?mode=rec";

    public static final String SWITCH_MODE_PLAY = "/switch_cammode.cgi?mode=play";

    public static final String EXEC_SHUTTER = "/exec_shutter.cgi?com=%s";

    public static final String GET_CAMPROP_DESCLIST = "/get_camprop.cgi?com=desc&propname=desclist";

    public static final String SET_CAMPROP = "/set_camprop.cgi?com=set&propname=%s";

    public static final String GET_CAMPROP = "/get_camprop.cgi?com=get&propname=%s";

    public static final String EXEC_PWOFF = "/exec_pwoff.cgi";

    public static final String GET_IMGLIST = "/get_imglist.cgi?DIR=%s";

    public static final String GET_THUMBNAIL = "/get_thumbnail.cgi?DIR=%s";

    public static final String TAKEMODE_PROP = "takemode";
    public static final String FOCALVALUE_PROP = "focalvalue";
    public static final String SHUTSPEEDVALUE_PROP = "shutspeedvalue";
    public static final String EXPCOMP_PROP = "expcomp";

    public static final String SET_VALUE_XML = "<set><value>%s</value></set>";

    private String basePath = "/DCIM/100OLYMP";

    private XmlParserCreator parserCreator = new XmlParserCreator() {
        @Override
        public XmlPullParser createParser() {
            try {
                return XmlPullParserFactory.newInstance().newPullParser();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private GsonXml gsonXml = new GsonXmlBuilder().setSameNameLists(true).setXmlParserCreator(parserCreator).create();

    private RequestQueue requestQueue;

    public CameraApi(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getCameraInfo(final Callback<Caminfo> successCallback, final Callback<String> failureCallback) {
        HttpUtil.makeGetRequest(requestQueue, CAMERA_URL + GET_CAMINFO,
                new HttpUtil.SuccessResponseHandler() {
                    @Override
                    public void handle(String response) {
                        try {
                            Caminfo caminfo = gsonXml.fromXml(response, Caminfo.class);
                            successCallback.apply(caminfo);
                        } catch (Exception e) {
                            throw new CommunicationException(e.getMessage());
                        }
                    }
                },
                new FailureDelegateCallback(failureCallback));
    }

    public void switchToShutterMode(final Callback<String> successCallback, final Callback<String> failureCallback) {
        makeGetRequest(CAMERA_URL + SWITCH_MODE_SHUTTER, new DelegateCallback(successCallback), new FailureDelegateCallback(failureCallback));
    }

    public void switchToRecMode(final Callback<String> successCallback, final Callback<String> failureCallback) {
        makeGetRequest(CAMERA_URL + SWITCH_MODE_REC, new DelegateCallback(successCallback), new FailureDelegateCallback(failureCallback));
    }

    public void switchToPlayMode(final Callback<String> successCallback, final Callback<String> failureCallback) {
        makeGetRequest(CAMERA_URL + SWITCH_MODE_PLAY, new DelegateCallback(successCallback), new FailureDelegateCallback(failureCallback));
    }

    public void getImageList(final Callback<List<ImageFile>> successCallback, final Callback<String> failureCallback) {
        HttpUtil.makeGetRequest(requestQueue, String.format(CAMERA_URL + GET_IMGLIST, basePath),
                new HttpUtil.SuccessResponseHandler() {
                    @Override
                    public void handle(String response) {
                        List<ImageFile> imageFiles = parseImageList(response);
                        successCallback.apply(imageFiles);
                    }
                },
                new FailureDelegateCallback(failureCallback));
    }

    public void getThumbnail(String imageName, int width, int height,
                             final Callback<Bitmap> successCallback, final Callback<String> failureCallback) {
        HttpUtil.loadImageRequest(requestQueue, String.format(CAMERA_URL + GET_THUMBNAIL, imageName), width, height,
                new HttpUtil.SuccessImageResponseHandler() {
                    @Override
                    public void handle(Bitmap bitmap) {
                        successCallback.apply(bitmap);
                    }
                }, new FailureDelegateCallback(failureCallback));
    }

    public void getCameraProps(final Callback<Desclist> successCallback, final Callback<String> failureCallback) {
        HttpUtil.makeGetRequest(requestQueue, CAMERA_URL + GET_CAMPROP_DESCLIST,
                new HttpUtil.SuccessResponseHandler() {
                    @Override
                    public void handle(String response) {
                        try {
                            Desclist desclist = gsonXml.fromXml(response, Desclist.class);
                            successCallback.apply(desclist);
                        } catch (Exception e) {
                            throw new CommunicationException(e.getMessage());
                        }
                    }
                },
                new FailureDelegateCallback(failureCallback));
    }

    public void executeShutter(ShutterMode shutterMode,
                               final Callback<String> successCallback, final Callback<String> failureCallback) {
        makeGetRequest(String.format(CAMERA_URL + EXEC_SHUTTER, shutterMode.getCom()), new DelegateCallback(successCallback),
                new FailureDelegateCallback(failureCallback));
    }

    public void powerOff(final Callback<String> successCallback, final Callback<String> failureCallback) {
        makeGetRequest(CAMERA_URL + EXEC_PWOFF, new DelegateCallback(successCallback), new FailureDelegateCallback(failureCallback));
    }

    private void makeGetRequest(String url, final DelegateCallback successCallback, final FailureDelegateCallback failureCallback) {
        HttpUtil.makeGetRequest(requestQueue, url, successCallback, failureCallback);
    }

    private List<ImageFile> parseImageList(String response) {
        List<ImageFile> imageFiles = new ArrayList<>();

        String[] lines = StringUtils.split(response, System.getProperty("line.separator"));
        for (String line : lines) {
            String[] fields = StringUtils.split(line, ',');
            if (fields.length < 6) {
                continue;
            }
            ImageFile imageFile = new ImageFile();
            imageFile.path = fields[0];
            imageFile.name = fields[1];
            imageFile.size = Long.parseLong(fields[2]);
            imageFiles.add(imageFile);
        }

        Collections.reverse(imageFiles);
        return imageFiles;
    }

    public void setCameraProp(String prop, String value,
                              final Callback<String> successCallback, final Callback<String> failureCallback) {
        HttpUtil.makePostRequest(requestQueue, CAMERA_URL + String.format(SET_CAMPROP, prop), String.format(SET_VALUE_XML, value),
                new DelegateCallback(successCallback), new FailureDelegateCallback(failureCallback));
    }

    public void getCameraProp(String prop,
                              final Callback<String> successCallback, final Callback<String> failureCallback) {
        HttpUtil.makeGetRequest(requestQueue, CAMERA_URL + String.format(GET_CAMPROP, prop),
                new HttpUtil.SuccessResponseHandler() {
                    @Override
                    public void handle(String response) {
                        Get get = gsonXml.fromXml(response, Get.class);
                        successCallback.apply(get.value);
                    }
                }, new FailureDelegateCallback(failureCallback));
    }

    private class DelegateCallback implements HttpUtil.SuccessResponseHandler {

        private final Callback<String> successCallback;

        DelegateCallback(Callback<String> successCallback) {
            this.successCallback = successCallback;
        }

        @Override
        public void handle(String response) {
            successCallback.apply(response);
        }
    }

    private class FailureDelegateCallback implements HttpUtil.ErrorResponseHandler {

        private final Callback<String> failureCallback;

        FailureDelegateCallback(Callback<String> failureCallback) {
            this.failureCallback = failureCallback;
        }

        @Override
        public void handle(int statusCode, String message) {
            failureCallback.apply(message);
        }
    }
}
