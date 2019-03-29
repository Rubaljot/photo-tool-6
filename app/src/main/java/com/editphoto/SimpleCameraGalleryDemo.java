package com.editphoto;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.editphoto.pack1.pack2.R;

import java.io.File;

public class SimpleCameraGalleryDemo extends Activity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    ImageView imgview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_camera_gallery_demo);

        imgview = (ImageView) findViewById(R.id.imageView1);
        Button buttonCamera = (Button) findViewById(R.id.btn_take_camera);
        Button buttonGallery = (Button) findViewById(R.id.btn_select_gallery);
        buttonCamera.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
// call android default camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//                intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
//// ******** code for crop image
//                intent.putExtra("crop", "true");
//                intent.putExtra("aspectX", 0);
//                intent.putExtra("aspectY", 0);
//                intent.putExtra("outputX", 200);
//                intent.putExtra("outputY", 150);

                try {

                   // intent.putExtra("return-data", true);
                    startActivityForResult(intent, PICK_FROM_CAMERA);

                } catch (ActivityNotFoundException e) {
// Do nothing for now
                }
            }
        });
        buttonGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub


                Intent viewMediaIntent = new Intent();
                viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(Environment.getExternalStorageDirectory()+File.separator+"a.jpg");
                viewMediaIntent.setDataAndType(Uri.fromFile(file), "image/*");
                viewMediaIntent.putExtra("crop","true");
                viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(viewMediaIntent,1);


//                Intent intent = new Intent();
//// call android default gallery
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//// ******** code for crop image
//                intent.putExtra("crop", "true");
//                intent.putExtra("aspectX", 0);
//                intent.putExtra("aspectY", 0);
//                intent.putExtra("outputX", 200);
//                intent.putExtra("outputY", 150);
//
//                try {
//
//                    intent.putExtra("return-data", true);
//                    startActivityForResult(Intent.createChooser(intent,
//                            "Complete action using"), PICK_FROM_GALLERY);
//
//                } catch (ActivityNotFoundException e) {
//// Do nothing for now
//                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FROM_CAMERA) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                Bitmap bitmap = cropAndGivePointedShape(photo);
                imgview.setImageBitmap(bitmap);

            }
        }

        if (requestCode == PICK_FROM_GALLERY) {
            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable("data");
                imgview.setImageBitmap(photo);

            }
        }
    }
    private Bitmap cropAndGivePointedShape(Bitmap originalBitmap)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        canvas.drawRect(0, 0, 20, 20, p);
        Point a = new Point(0, 20);
        Point b = new Point(20, 20);
        Point c = new Point(0, 40);
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();
        canvas.drawPath(path, p);
        a = new Point(0, 40);
        b = new Point(0, 60);
        c = new Point(20, 60);
        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();
        canvas.drawPath(path, p);
        canvas.drawRect(0, 60, 20, originalBitmap.getHeight(), p);
        return bmOverlay;
    }
}