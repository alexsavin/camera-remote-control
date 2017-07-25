package com.fuckolympus.arc.camera.api;

import android.content.Context;
import com.android.volley.Request;
import com.fuckolympus.arc.camera.vo.Caminfo;
import com.fuckolympus.arc.camera.vo.Desclist;
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
import java.util.List;

/**
 * Created by alex on 4.6.17.
 */
public class CameraApi {

    public static final String CAMERA_URL = "http://192.168.0.10";
    //public static final String CAMERA_URL = "http://alexandersavin.me";

    public static final String GET_CAMINFO = "/get_caminfo.cgi";

    public static final String SWITCH_MODE_SHUTTER = "/switch_cammode.cgi?mode=shutter";

    public static final String SWITCH_MODE_REC = "/switch_cammode.cgi?mode=rec";

    public static final String SWITCH_MODE_PLAY = "/switch_cammode.cgi?mode=play";

    public static final String EXEC_SHUTTER = "/exec_shutter.cgi?com=%s";

    public static final String GET_CAMPROP_DESCLIST = "/get_camprop.cgi?com=desc&propname=desclist";

    public static final String EXEC_PWOFF = "/exec_pwoff.cgi";

    public static final String GET_IMGLIST = "/get_imglist.cgi?DIR=%s";

    public static final String GET_THUMBNAIL = "/get_thumbnail.cgi?DIR=%s";

    private Context context;

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

    public CameraApi(Context context) {
        this.context = context;
    }

    public void getCameraInfo(final Callback<Caminfo> successCallback, final Callback<String> failureCallback) {
        HttpUtil.makeRequest(context, Request.Method.GET, CAMERA_URL + GET_CAMINFO,
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
        HttpUtil.makeRequest(context, Request.Method.GET,  String.format(CAMERA_URL + GET_IMGLIST, basePath),
                new HttpUtil.SuccessResponseHandler() {
                    @Override
                    public void handle(String response) {
                        List<ImageFile> imageFiles = parseImageList(response);
                        successCallback.apply(imageFiles);
                    }
                },
                new FailureDelegateCallback(failureCallback));
    }

    public void getCameraProps(final Callback<Desclist> successCallback, final Callback<String> failureCallback) {
        HttpUtil.makeRequest(context, Request.Method.GET, CAMERA_URL + GET_CAMPROP_DESCLIST,
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

    public void executeShutter(ShutterMode shutterMode, final Callback<String> successCallback, final Callback<String> failureCallback) {
        makeGetRequest(String.format(CAMERA_URL + EXEC_SHUTTER, shutterMode.getCom()), new DelegateCallback(successCallback),
                new FailureDelegateCallback(failureCallback));
    }

    public void powerOff(final Callback<String> successCallback, final Callback<String> failureCallback) {
        makeGetRequest(CAMERA_URL + EXEC_PWOFF, new DelegateCallback(successCallback), new FailureDelegateCallback(failureCallback));
    }

    private void makeGetRequest(String url, final DelegateCallback successCallback, final FailureDelegateCallback failureCallback) {
        HttpUtil.makeRequest(context, Request.Method.GET, url, successCallback, failureCallback);
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
            imageFile.name = fields[0] + '/' + fields[1];
            imageFile.size = Long.parseLong(fields[2]);
            imageFile.thumbnailPath = String.format(CAMERA_URL + GET_THUMBNAIL, imageFile.name);
            imageFiles.add(imageFile);
        }

        return imageFiles;
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
