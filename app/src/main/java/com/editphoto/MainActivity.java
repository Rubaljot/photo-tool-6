package com.editphoto;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editphoto.pack1.pack2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //reference to ImageView And Buttons
    ImageView imvphoto;
    Button btgallery;
    Button btcamera;
    LinearLayout frameslayout;
    ArrayList<Integer> al;
    Bitmap camerabitmap=null;
    Bitmap gallertbitmap=null;
    Bitmap framebitmap=null;
    Bitmap out=null;
    String imagefrom="";
    boolean isedit;
    TextView tvsaved;
    ImageView imvdefaultl;
    int CROP_PIC_REQUEST_CODE=99;
    AlertDialog.Builder ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ad = new AlertDialog.Builder(this);
        ad.setTitle("Alert Dialog");
        ad.setIcon(R.drawable.cameraicon);
        ad.setMessage("CHOOSE CAMERA");

//        cl obj = new cl();
        ad.setPositiveButton("Back Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra("frontorback",false);
                startActivityForResult(intent, 10);
            }
        });
        //------------------------------

        ad.setNegativeButton("Front Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra("frontorback",true);
                startActivityForResult(intent, 12);
            }
        });
        //-------------------------------
        ad.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ad.create();
        tvsaved=(TextView) findViewById(R.id.tvsaved);
        //memory to ImageView and Buttons
        imvphoto=(ImageView)findViewById(R.id.imvphoto);
        imvdefaultl=(ImageView)findViewById(R.id.imvdefault);


        btgallery=(Button)findViewById(R.id.btgallery);
        btcamera=(Button)findViewById(R.id.btcamera);
        //bind buttons with listeners
        btgallery.setOnClickListener(gallerylistener);
        btcamera.setOnClickListener(cameralistener);


        //this function is used to android version
        checkAndroidVersion();


    }


    //gallery open listener
    View.OnClickListener gallerylistener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                Intent in = new Intent(Intent.ACTION_PICK);
                // Filter for image type
                in.setType("image/*");
                startActivityForResult(in,11);//here we send 11 as request code to gallery ,you can send any other number
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

        }
    };


    //camera open listener
    View.OnClickListener cameralistener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
if(isCameraAvailable(getApplicationContext())){
                ad.show();
}
                else{
    Toast.makeText(getApplicationContext(),"Camera Feature is not supported by this device",Toast.LENGTH_LONG).show();
                }
//                Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(in,10);
                //here we send 10 as request code to camera,you can send any other number
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

        }
    };
    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }




    //after you open camera or gallery it will send you back bitmap or uri.
    //bitmap from camera
    //uri from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//onActivityResult  is function in which response came from camera or gallery
        if(requestCode==10)  //back from camera   //if we receive 10 as request code sent by us that means response from camera
        {
            if(resultCode==RESULT_OK)///R
            {
                imvdefaultl.setVisibility(View.INVISIBLE);
                String imgPath = data.getStringExtra(CameraActivity.EXTRA_IMAGE_PATH);
                Toast.makeText(getApplicationContext(),imgPath,Toast.LENGTH_SHORT).show();
//                imvphoto.setImageBitmap(BitmapHelper.decodeSampledBitmap(imgPath, imvphoto.getWidth(), imvphoto.getHeight()));
                //bitmap is actually a data structure which store image in form of array of bytes
                //Bitmap bmp = (Bitmap) (data.getExtras().get("data"));
                Uri uri=Uri.parse(imgPath);
               // Bitmap bmp = StringToBitMap(imgPath);
                try{
                    File imgFile = new  File(imgPath);
                    if(imgFile.exists()){
                        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        Matrix matrix = new Matrix();

                        matrix.postRotate(90);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, imvphoto.getWidth(), imvphoto.getHeight(), true);

                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                   // Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    camerabitmap = rotatedBitmap;
                    out=camerabitmap;
                        imvphoto.setImageBitmap(rotatedBitmap);
                    //set bitmap to ImageView
                    // imvphoto.setImageBitmap(bmp);
                    imagefrom = "camera";}
                }
                catch(Exception ex) {

                }
            }
        }
        else if(requestCode==12)  //back from camera   //if we receive 10 as request code sent by us that means response from camera
        {
            if(resultCode==RESULT_OK)///R
            {
                imvdefaultl.setVisibility(View.INVISIBLE);
                String imgPath = data.getStringExtra(CameraActivity.EXTRA_IMAGE_PATH);
                Toast.makeText(getApplicationContext(),imgPath,Toast.LENGTH_SHORT).show();
//                imvphoto.setImageBitmap(BitmapHelper.decodeSampledBitmap(imgPath, imvphoto.getWidth(), imvphoto.getHeight()));
                //bitmap is actually a data structure which store image in form of array of bytes
                //Bitmap bmp = (Bitmap) (data.getExtras().get("data"));
                Uri uri=Uri.parse(imgPath);
                // Bitmap bmp = StringToBitMap(imgPath);
                try{
                    File imgFile = new  File(imgPath);
                    if(imgFile.exists()){
                        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        Matrix matrix = new Matrix();

                        matrix.postRotate(270);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, imvphoto.getWidth(), imvphoto.getHeight(), true);

                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                        // Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        camerabitmap = rotatedBitmap;
                        out=camerabitmap;
                        imvphoto.setImageBitmap(rotatedBitmap);
                        //set bitmap to ImageView
                        // imvphoto.setImageBitmap(bmp);
                        imagefrom = "camera";}
                }
                catch(Exception ex) {

                }
            }
        }
        else if(requestCode==11)  //back from gallery   //if we receive 11 as request code sent by us that means response from gallery
        {
            if(resultCode==RESULT_OK)
            {
                imvdefaultl.setVisibility(View.INVISIBLE);
                //uri is actually address of image  which is already stored in our phone
                Uri uri = data.getData();
                getRealPathFromURI(getApplicationContext(),uri);
                try {
                    gallertbitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    out=gallertbitmap;
                    imagefrom="gallery";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //set uri to ImageView
                imvphoto.setImageURI(uri);
            }
        }
      else  if (requestCode == CROP_PIC_REQUEST_CODE&& resultCode == RESULT_OK) {

            if (data != null) {
               // Toast.makeText(getApplicationContext(), CROP_PIC_REQUEST_CODE+" "+RESULT_OK,Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(500);
                    Uri imageUri = data.getData();
                    Bitmap bitmap= null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                        out=bitmap;
                        imvphoto.setImageBitmap(out);
                        frameslayout.removeAllViews();
                        frameslayout.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }



    public  void add(Bitmap bmpframe,Bitmap bmpimg){
        try {
            Bitmap frame = bmpframe;
//            Bitmap urImage = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.four);//edit
//            frame = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.frame1);
            Bitmap urImage = bmpimg;//edit
            out = combineImages(frame, urImage);
            imvphoto.setImageBitmap(out);

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public Bitmap combineImages(Bitmap frame, Bitmap image) {

        Bitmap cs = null;
        Bitmap rs = null;

        rs = Bitmap.createScaledBitmap(frame, image.getWidth(),
                image.getHeight(), true);

        cs = Bitmap.createBitmap(rs.getWidth(), rs.getHeight(),
                Bitmap.Config.RGB_565);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(image, 0, 0, null);
        comboImage.drawBitmap(rs, 0, 0, null);

        if (rs != null) {
            rs.recycle();
            rs = null;
        }
        Runtime.getRuntime().gc();

        return cs;
    }




    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-Edited" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            imvdefaultl.setVisibility(View.VISIBLE);
            tvsaved.setText("Image saved at \n\n"+Environment.getExternalStorageDirectory().toString()+File.separator+file.getName());
            //f1=new File(root+File.separator+fname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mytoolbarmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.m1) {
            frameslayout = (LinearLayout) (findViewById(R.id.frameslayout));
            frameslayout.removeAllViews();
            frameslayout.setVisibility(View.VISIBLE);
            final MenuItem mi=item;
            final ImageView imageView = new ImageView(getApplicationContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 180));
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(imageView.getLayoutParams());
            marginParams.setMargins(5, 1, 25, 0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(R.drawable.ef);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    frameslogic(mi);
                }
            });


            final ImageView imageView2 = new ImageView(getApplicationContext());
            imageView2.setLayoutParams(new LinearLayout.LayoutParams(300, 180));
            ViewGroup.MarginLayoutParams marginParams2 = new ViewGroup.MarginLayoutParams(imageView2.getLayoutParams());
            marginParams2.setMargins(25, 1, 25, 2);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(marginParams2);
            imageView2.setLayoutParams(layoutParams2);
            imageView2.setImageResource(R.drawable.ec);
            imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   cropimagelogic();

                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    frameslayout.addView(imageView);
                    frameslayout.addView(imageView2);
                    if(isedit){
                        long time= System.currentTimeMillis();
                        saveImage(out,time+"");
                        imvdefaultl.setVisibility(View.VISIBLE);
                        frameslayout.setVisibility(View.INVISIBLE);
                        mi.setTitle("Edit");
                        isedit=false;
                        imagefrom="";
                        gallertbitmap=null;
                        camerabitmap=null;
                        out=null;
                        framebitmap=null;
                        imvphoto.setImageResource(R.drawable.photoicon);
                    }

                }
            });


        }
        else if(item.getItemId() == R.id.m2){
            Intent in=new Intent(MainActivity.this,MainActivity.class);
            startActivity(in);

        }
        return true;
    }



    public void frameslogic(MenuItem item){
        if(isedit){
            long time= System.currentTimeMillis();
            saveImage(out,time+"");
            imvdefaultl.setVisibility(View.VISIBLE);
            frameslayout.setVisibility(View.INVISIBLE);
            item.setTitle("Edit");
            isedit=false;
            imagefrom="";
            gallertbitmap=null;
            camerabitmap=null;
            out=null;
            framebitmap=null;
            imvphoto.setImageResource(R.drawable.photoicon);
        }
        else{
            if(imagefrom.equals("")){
                Toast.makeText(getApplicationContext(),"first choose image ",Toast.LENGTH_SHORT).show();
            }
            else {
                tvsaved.setText("");
                makeframes();
                item.setTitle("Save");
                isedit = true;
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void cropimagelogic(){
        if(out!=null){
            Uri uri=  getImageUri(getApplicationContext(),out);
            doCrop(uri);
        }
        else if(imagefrom.equals("gallery")){
         Uri uri=  getImageUri(getApplicationContext(),gallertbitmap);
         doCrop(uri);
        }
        else if(imagefrom.equals("camera")){
           Uri uri= getImageUri(getApplicationContext(),camerabitmap);
           doCrop(uri);
        }
        else{
            Toast.makeText(getApplicationContext(),"first choose image",Toast.LENGTH_SHORT).show();
        }
    }
    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("circleCrop", "true");
            cropIntent.putExtra("scaleUpIfNeeded", true);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void makeframes(){
        try{
            frameslayout = (LinearLayout) (findViewById(R.id.frameslayout));
            frameslayout.removeAllViews();
            frameslayout.setVisibility(View.VISIBLE);
            al=new ArrayList<>();
            al.add(R.drawable.frame1);
            al.add(R.drawable.frame3);
            al.add(R.drawable.frame4);
            al.add(R.drawable.frame5);
            al.add(R.drawable.frame6);
            al.add(R.drawable.frame7);
            al.add(R.drawable.frame8);
            al.add(R.drawable.frame13);
            al.add(R.drawable.frame11);
            al.add(R.drawable.frame2);
            al.add(R.drawable.frame12);
            al.add(R.drawable.frame14);
            al.add(R.drawable.frame16);
            al.add(R.drawable.frame19);
            al.add(R.drawable.frame25);
            for (int m = 0; m < al.size(); m++) {
//                Log.d("ALLOOP",al.get(m)+"");
                final ImageView imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(180, 180));
                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(imageView.getLayoutParams());
                marginParams.setMargins(5, 2, 25, 2);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                imageView.setLayoutParams(layoutParams);
                imageView.setImageResource(al.get(m));
               // Picasso.get().load(al.get(m)).resize(180,180).centerInside().into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                final int index=m;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        framebitmap=   BitmapFactory.decodeResource(getResources(),al.get(index));
                        if(out!=null){
                            add(framebitmap,out);
                        }
                      else  if(imagefrom.equals("gallery")){
                            add(framebitmap,gallertbitmap);}
                        else if(imagefrom.equals("camera")){
                            add(framebitmap,camerabitmap);
                        }

                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        frameslayout.addView(imageView);

                    }
                });
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }






    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //this function is used to check runtime permissions of camera/write to external storage /read from external storage
            checkAndRequestPermissions();

        } else {
            // code for lollipop and pre-lollipop devices
        }

    }




    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 10);
            return false;
        }
        return true;
    }




    //when we on permission response will come in this function like OnActivityResult
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
//        Log.d("in activity on request", "Permission callback called-------");
        switch (requestCode) {
            case 10: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                        Log.d("in activity on request", "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
//                        Log.d("in activity on request", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(getApplicationContext(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }



    //this is dialog to on permission
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);

            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }


    public static class Log {

        private static final String TAG = "CameraSpike";

        public static void d(String msg) {
            d(msg, null);
        }

        public static void d(String msg, Throwable e) {
            android.util.Log.d(TAG, Thread.currentThread().getName() + "| " + msg, e);
        }

        public static void i(String msg) {
            i(msg, null);
        }

        public static void i(String msg, Throwable e) {
            android.util.Log.i(TAG, Thread.currentThread().getName() + "| " + msg, e);
        }

        public static void e(String msg) {
            e(msg, null);
        }

        public static void e(String msg, Throwable e) {
            android.util.Log.e(TAG, Thread.currentThread().getName() + "| " + msg, e);
        }

        public static void v(String msg) {
            android.util.Log.v(TAG, Thread.currentThread().getName() + "| " + msg);
        }

        public static void w(String msg) {
            android.util.Log.w(TAG, Thread.currentThread().getName() + "| " + msg);
        }

    }

}
