package com.example.lenovo.mycamera;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.graphics.Rect;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.VideoSource;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.captain_miao.grantap.CheckPermission;
import com.example.captain_miao.grantap.listeners.PermissionListener;
import com.example.captain_miao.grantap.utils.PermissionUtils;

import static com.example.captain_miao.grantap.ShadowPermissionActivity.setPermissionListener;

public class MainActivity extends Activity {
    private Camera mCamera;
    private CameraPreviewView previewView;
    private MediaRecorder recorder;
    private boolean isrecording = false;
    private Button btn;
    private String Tag="camera is null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reqPhonePermisson();

    }



    private void init()
    {
        if (!checkCameraHardware(this)) {
            return;
        };
        mCamera = getCameraInstance();

        previewView = new CameraPreviewView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(previewView);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isrecording) {
                    releaseMediaRecoder();
                    //mCamera.lock();
                    btn.setText("Capture");
                    isrecording = false;
                }
                else {
                    if (prepareVideoRecorder()&&mCamera!=null) {
                        recorder.start();
                        btn.setText("Stop");
                        isrecording = true;
                    }
                    else {
                        releaseMediaRecoder();
                    }
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecoder();
        releaseCamera();
    }

    private void releaseMediaRecoder(){
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
            if(mCamera!=null){
            mCamera.lock();}else
            {
                Log.i(Tag,"camera is null");
            }
        }
    }

    private void releaseCamera(){
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 创建MediaRecorder实例，并为之设定基本属性
     * @return
     */
    private boolean prepareVideoRecorder(){
        recorder = new MediaRecorder();
        if(mCamera!=null){
        mCamera.unlock();
        recorder.setCamera(mCamera);
        }
        else
        {
            Log.i(Tag,"mCamera is null");
        }

        recorder.setVideoSource(VideoSource.CAMERA);
        recorder.setAudioSource(AudioSource.CAMCORDER);

        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        recorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        recorder.setPreviewDisplay(previewView.getHolder().getSurface());
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 获取输出video文件目录
     * @return
     */
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "MyCamera/");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                mediaStorageDir.mkdirs();

            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }



    /**
     * 获取camera实例
     * @return
     */
    public static Camera getCameraInstance(){
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 检测手机有无摄像头
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(context, "successfully detact camera!", Toast.LENGTH_LONG).show();
            return true;
        }
        else {
            Toast.makeText(context, "not detact camera!!!", Toast.LENGTH_LONG).show();
            return false;
        }
    }


        private void reqPhonePermisson() {
            final boolean isGranted = PermissionUtils.hasSelfPermissions(this,
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
            if (isGranted)
                return;
            CheckPermission.from(this).setPermissions
                    (Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).setPermissionListener
                    (new PermissionListener() {
                        @Override
                        public void permissionGranted() {
                              init();
                        }

                        @Override
                        public void permissionDenied() {
                            Toast.makeText(MainActivity.this,"需要权限",Toast.LENGTH_LONG).show();
                        }
                    }).check();
        }
    }






