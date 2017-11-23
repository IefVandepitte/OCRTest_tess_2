package com.example.ief.ocrtest;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    Bitmap image; // onze afbeelding
    private TessBaseAPI mTess; // tess API referentie
    String datapath = ""; //pad naar de map met het taal data bestand
    private final String taal = "nld";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init afbeelding
        image = BitmapFactory.decodeResource(getResources(), R.drawable.perenvlaai);

        datapath = getFilesDir()+ "/tesseract/";

        //zekerheid dat trainingdata gekopieerd is
        checkFile(new File(datapath + "tessdata/"));

        // initialiseer Tesseract API
        String lang = taal;

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);
    }

    public void processImage(View view){
        String OCRResult = null;
        mTess.setImage(image);
        OCRResult = mTess.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setText(OCRResult);
    }

    private void copyFiles(){
        try {
            // locatie waar we het bestand willen
            String filepath = datapath +"/tessdata/"+taal+".traineddata";

            // krijg toegang tot assetManager
            AssetManager assetManager = getAssets();

            // open byte stream voor lees/schrijf operatie
            InputStream instream = assetManager.open("tessdata/"+taal+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //kopieer het bestand naar de locatie in filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1){
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void checkFile(File dir){
        // map bestaat niet maar we kunnen ze maken
        if (!dir.exists() && dir.mkdirs()){
            copyFiles();
        }
        // de map bestaat, maar er zit geen data in
        if (dir.exists()){
            String datafilepath = datapath + "/tessdata/"+taal+".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()){
                copyFiles();
            }
        }
    }
}
