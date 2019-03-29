package com.editphoto;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.editphoto.pack1.pack2.R;

public class DownloadedImageDisplay extends Activity implements View.OnTouchListener {
    Matrix matrix = new Matrix();
    private ImageView ivDownloadedImage,img;View viewmain=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_image_display);
        initializeVars();
        //showImage();
        ivDownloadedImage.setOnTouchListener(this);
        final Bitmap b1=((BitmapDrawable)ivDownloadedImage.getDrawable()).getBitmap();
        final Bitmap b2=((BitmapDrawable)img.getDrawable()).getBitmap();
        Bitmap r1 = getResizedBitmap(b1, 749, 587);
        Bitmap r2 = getResizedBitmap(b2, 749, 587);
        ivDownloadedImage.setImageBitmap(r1);
        img.setImageBitmap(r2);
        final Bitmap toBeCropped =((BitmapDrawable)img.getDrawable()).getBitmap();
        final Bitmap to =((BitmapDrawable)ivDownloadedImage.getDrawable()).getBitmap();
        Log.d("TESTMSG","cm x "+to.getWidth()+" h "+to.getHeight());
        Log.d("TESTMSG","c a v "+(toBeCropped.getWidth())+" h "+(toBeCropped.getHeight()));
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    private void initializeVars()
    {

        ivDownloadedImage = (ImageView) findViewById(R.id.ivdownloadedjpgfile);
        img = (ImageView) findViewById(R.id.img);
    }

    public void showImage()
    {
        String filePath;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            filePath = null;
        } else {
            filePath= extras.getString("JPGFileName");
        }

        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ivDownloadedImage.setImageBitmap(bitmap);
    }



    //// Image Zooming code

    private static final String TAG = "Touch";
    //  @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image

    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d("MYMSG", "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d("MYMSG", "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d("MYMSG", "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d("MYMSG", "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d("MYMSG", "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen
viewmain=view;

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);

    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }

    public void save(View view){


        final Bitmap toBeCropped =((BitmapDrawable)img.getDrawable()).getBitmap();
        final Bitmap to =((BitmapDrawable)ivDownloadedImage.getDrawable()).getBitmap();
        final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inTargetDensity = 1;
        toBeCropped.setDensity(Bitmap.DENSITY_NONE);
        float[] values = new float[9];
        matrix.getValues(values);
        int x =(int) values[Matrix.MTRANS_X];
        int y = (int)values[Matrix.MTRANS_Y];
        int   w = Math.round(values[Matrix.MSCALE_X]*to.getWidth());
      int  h = Math.round(values[Matrix.MSCALE_Y]*to.getHeight());

//
//        int fromHere = (int) (toBeCropped.getHeight() * 0.2);
//        Bitmap croppedBitmap = Bitmap.createBitmap(toBeCropped, 0, 0, toBeCropped.getWidth(), fromHere);

                  Log.d("TESTMSG","ivd "+ivDownloadedImage.getX()+" h "+ivDownloadedImage.getY()+" w "+toBeCropped.getWidth()+"  h "+toBeCropped.getHeight());
        Log.d("TESTMSG","img x "+img.getX()+" h "+img.getY()+" w "+to.getWidth()+"  h "+to.getHeight());
//          Log.d("TESTMSG","cm x "+(int)x+" y "+(int)y+" w "+to.getWidth()+" h "+to.getHeight());
         Log.d("TESTMSG","c a "+(int)x+" y "+(int)y+" w "+(int)w+" h "+(int)h);
//         Log.d("TESTMSG","c a v "+(int)x+" y "+(int)y+" w "+(toBeCropped.getWidth())+" h "+(toBeCropped.getHeight()));
//         Log.d("TESTMSG","c a u "+(int)x+" y "+(int)y+" w "+(toBeCropped.getWidth()-(int)x)+" h "+(toBeCropped.getHeight()-(int)y));
//         int w1=toBeCropped.getWidth()-(int)x;
//        int y1=toBeCropped.getHeight()-(int)y;
//       if(w1>0&&y1>0)
//       {
                Bitmap croppedBitmap = Bitmap.createBitmap(toBeCropped, x,y,50,50);
        img.setImageBitmap(croppedBitmap);
//        }






//        Bitmap bitmap;
//        if (img.getDrawable() instanceof BitmapDrawable) {
//            bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
//        } else {
//            Drawable d = img.getDrawable();
//            bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            d.draw(canvas);
//        }
//        Bitmap bi=null;
//        if (ivDownloadedImage.getDrawable() instanceof BitmapDrawable) {
//            bi = ((BitmapDrawable) ivDownloadedImage.getDrawable()).getBitmap();
//        } else {
//            Drawable d = ivDownloadedImage.getDrawable();
//            bi = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bi);
//            d.draw(canvas);
//        }
//        Bitmap bit = toOvalBitmap(bitmap,bi.getWidth(),bi.getHeight(),ivDownloadedImage);
//       img.setImageBitmap(bit);
}
    public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }
    public static Bitmap toOvalBitmap(@NonNull Bitmap bitmap,int w,int h,ImageView im) {

        int width = w;
        int height = h;
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        int color = 0xff424242;
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        RectF rect = new RectF(im.getX(), im.getY(), width, height);
        canvas.drawOval(rect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, im.getX(), im.getY(), paint);

        bitmap.recycle();

        return output;
    }
}