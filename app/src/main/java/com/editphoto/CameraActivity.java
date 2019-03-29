package com.editphoto;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import com.editphoto.pack1.pack2.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Parameter;
import java.util.List;

import static com.editphoto.CameraHelper.cameraAvailable;
import static com.editphoto.CameraHelper.getCameraInstance;
import static com.editphoto.MediaHelper.getOutputMediaFile;
import static com.editphoto.MediaHelper.saveToFile;

public class CameraActivity extends Activity implements PictureCallback {

    protected static final String EXTRA_IMAGE_PATH = "response";

    private Camera camera;
    boolean flashlight=false;
ImageView imgflashlight,imgclick;
boolean frontorback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setResult(RESULT_CANCELED);
        imgflashlight=(ImageView)findViewById(R.id.imgflash);
        imgclick=(ImageView)findViewById(R.id.imgclick);
        // Camera may be in use by another activity or the system or not available at all
       // camera = getCameraInstance();
      Intent in=getIntent();
      frontorback=in.getBooleanExtra("frontorback",false);
        if(!frontorback){
        camera=Camera.open();
        }
        else{
            camera=Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            imgflashlight.setVisibility(View.INVISIBLE);


        }
        if (cameraAvailable(camera)) {
            initCameraPreview();
            int cameraId = -1;
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraId = i;
                    break;
                }
            }
            setCameraDisplayOrientation(CameraActivity.this,cameraId,camera);
            Camera.Parameters parameters=camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            Camera.Size size = sizes.get(0);
            for(int i=0;i<sizes.size();i++)
            {
                if(sizes.get(i).width > size.width)
                    size = sizes.get(i);
            }
            parameters.setPictureSize(size.width, size.height);
            camera.setParameters(parameters);
            if(!frontorback){
          setautofocus();}
        } else {
            finish();
        }
    }

    private void setautofocus() {
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);

    }

    private void initCameraPreview() {
        CameraPreview cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera);


    }
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    @FromXML
    public void onCaptureClick(View button) {

//        Camera.Parameters p = camera.getParameters();
//        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//        camera.setParameters(p);
//        camera.startPreview();
      camera.takePicture(null, null, this);
    }
    @FromXML
    public void onflashlight(View button) {



        if(!flashlight) {
            imgflashlight.setImageResource(R.mipmap.fo);
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            camera.startPreview();
            flashlight=true;

        }
        else{
            imgflashlight.setImageResource(R.mipmap.fof);
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.startPreview();
            flashlight=false;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        MainActivity.Log.d("Picture taken");
        try {
            String s = new String(data, "UTF-8");


        String path = savePictureToFileSystem(data);
        setResult(path);
        finish();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String savePictureToFileSystem(byte[] data) {
        File file = getOutputMediaFile();
       saveToFile(data, file);
        return file.getAbsolutePath();
    }

    private void setResult(String path) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
