package com.oney.WebRTCModule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;

import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;

import java.util.Objects;

public class ScreenCaptureController extends AbstractVideoCaptureController {
    /**
     * The {@link Log} tag with which {@code ScreenCaptureController} is to log.
     */
    private static final String TAG = ScreenCaptureController.class.getSimpleName();

    private static final int DEFAULT_FPS = 30;

    private final Intent mediaProjectionPermissionResultData;

    private final OrientationEventListener orientatationListener;

    public ScreenCaptureController(Context context, int width, int height, Intent mediaProjectionPermissionResultData) {
        super(width, height, DEFAULT_FPS);

        this.mediaProjectionPermissionResultData = mediaProjectionPermissionResultData;

        this.orientatationListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                try {
                    DisplayMetrics displayMetrics = DisplayUtils.getDisplayMetrics((Activity) context);
                    int width = displayMetrics.widthPixels;
                    int height = displayMetrics.heightPixels;

                    // Commenting this to avoid resetting of VideoCapture when the phone is orientation listener
                    // videoCapturer.changeCaptureFormat(width, height, DEFAULT_FPS);
                    } catch (Exception ex) {
                    // We ignore exceptions here. The video capturer runs on its own
                    // thread and we cannot synchronize with it.
                    Log.e(TAG, "Media Projecion Security Exception" + Objects.requireNonNull(ex.getMessage()));
                }
            }
        };

        if (this.orientatationListener.canDetectOrientation()) {
            this.orientatationListener.enable();
        }
    }

    @Override
    protected VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = new ScreenCapturerAndroid(
            mediaProjectionPermissionResultData,
            new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    Log.w(TAG, "Media projection stopped.");
                    orientatationListener.disable();
                }
            });


        return videoCapturer;
    }
}
