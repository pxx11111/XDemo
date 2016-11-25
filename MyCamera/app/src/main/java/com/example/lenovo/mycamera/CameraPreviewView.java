package com.example.lenovo.mycamera;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by lenovo on 2016/11/24.
 */

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera mCamera;
    private  SurfaceView surfaceView;
    private  String Tag="==";
    public CameraPreviewView(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                focus();
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if(mCamera!=null)
            {
                mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            }
            else
            {
                Log.i(Tag,"camera is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (holder.getSurface() == null) {
            return;
        }
if(mCamera!=null){
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }}else
{
    Log.i(Tag,"camera is null");
}
    }



    public void focus() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {

                }
            }
        });
    }
}
