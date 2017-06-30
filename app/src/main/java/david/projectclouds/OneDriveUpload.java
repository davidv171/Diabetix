package david.projectclouds;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.microsoft.onedrivesdk.saver.ISaver;
import com.microsoft.onedrivesdk.saver.Saver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by david on 29.5.2017.
 */

public class OneDriveUpload {
    private ISaver mSaver;
    private String ONEDRIVE_APP_ID = "afbdf902-f875-4c83-b37f-bc36e1db48de";
    public void uploadToOneDrive(String content, Context context, File file){

        final String filename = "Temp.xml";

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        System.out.println("STAY WOKE");
        if(context instanceof Activity){
            System.out.println("WE GUCCI");

            mSaver = Saver.createSaver(ONEDRIVE_APP_ID);
            if (Build.VERSION.SDK_INT > 22) {
                //CONTENT:// FILE PATH TYPE, DOESNT WORK
                //NOFILESPECIFIED TUKAJ
                System.out.println("CONTETN URI" + FileProvider.getUriForFile(context,"david.projectclouds.MainActivity",file));

                mSaver.startSaving((Activity) context, filename,FileProvider.getUriForFile(context,"david.projectclouds.MainActivity",file));

            }
            else{
                //FILE:// FILE PATH, WORKS ON ANDROID VERSIONS UNDER 7.0
                System.out.println("FILE:// LUL");
                mSaver.startSaving((Activity) context, filename, Uri.parse("file://" + file.getAbsolutePath()));

            }
        }


        System.out.println("mSAVE IN UPLOAD" + mSaver);

        System.out.println("URI: " + Uri.fromFile(file).toString());
        //USTVARIMO SAMO TEMPORARRNI FILE IN GA NATO IZBRIŠEMO. PRED TEM VPIŠEMO V FILE CELOTEN XML(ALI KARKOLI PAČ BO)
        //IN GA NALOŽIMO NA ONE DRIVE
    }
    public ISaver getSaver(){
        if (mSaver!=null){
            System.out.println("MSAVER " + mSaver);

            return mSaver;
        }
        else{
            return null;
        }
    }

}
