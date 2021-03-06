package com.imperialsoupgmail.tesseractexample;

/*
Developed by Happyandhappy
2017/10/17 Using Teesseract and OpenCV lib
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private static int IMG_RESULT = 1;
    Bitmap image;
    private TessBaseAPI mTess;
    String datapath = "";
    ImageView imageView;
    Button detect_text_button;

    static {
        System.loadLibrary("native");
    }

    public native static int convertGray(long matAddrRgba, long matAddrGray);
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                    Log.d(TAG, "OpenCV loaded succeffully!!");
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug())
        {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
        }
        else
        {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    // Requestion of External storage read/write permission using Code  (Can set it using manifest permission)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 333) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detect_text_button.setEnabled(true);
            } else {
                detect_text_button.setEnabled(false);
            }
        } else if(requestCode == 334) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 45);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 45);
        }

        detect_text_button=(Button) findViewById(R.id.OCRbutton);
        imageView = (ImageView)findViewById(R.id.imageView);
        detect_text_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_RESULT);
            }
        });
        //initialize Tesseract API
        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();
        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, language);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == IMG_RESULT && resultCode == RESULT_OK && null != data) {
                Uri URI = data.getData();
                String[] FILE = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(URI,FILE, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(FILE[0]);
                String ImageDecode = cursor.getString(columnIndex);
                cursor.close();
                image = BitmapFactory.decodeFile(ImageDecode);
                //Image Resizing
                //image = scaleBitmap(image,1296,2304);
                convertBinary();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG).show();
        }
    }

    public void processImage(){
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setMovementMethod(new ScrollingMovementMethod());  // Scrolling setup of TextView

        int string_length;
        char[] charArray = OCRresult.toCharArray();
        string_length=charArray.length;
        String[] stringArray=new String[string_length];
        String Result="";
        for (int i=0;i<string_length;i++) stringArray[i]=String.valueOf(charArray[i]);

        for (int i=1;i< string_length-1;i++){
            if (charArray[i-1]=='\n' && (charArray[i]!='a' && charArray[i]!='A') && charArray[i+1]==' ') stringArray[i]=" Bullet :";
        }

        for (int i=0;i<string_length;i++) Result+=stringArray[i];
        OCRTextView.setText(Result);
        // set image from Gallery to loaded_image
        imageView.setImageBitmap(image);
    }

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
                copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }


    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }

            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertBinary(){
        Mat src = new Mat();
        Mat dst = new Mat();
        Utils.bitmapToMat(image,src);
        //Utils.bitmapToMat(image,dst);
        MainActivity.convertGray(src.getNativeObjAddr(),dst.getNativeObjAddr());
        Utils.matToBitmap(dst,image);
        src.release();
        dst.release();
        // Tesss func;
        processImage();
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());
        return output;
    }
}
