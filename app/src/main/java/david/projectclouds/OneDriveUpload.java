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
    public void uploadToOneDrive(String content, Context context){

        final String filename = "Temp.xml";
             File f = new File(context.getExternalFilesDir("diabetix"),filename);

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        System.out.println("EXISTS" + f.exists());
        System.out.println("MKDIRS" + f.canWrite());
        System.out.println("HIDDEN" + f.toURI());
        System.out.println("FileURI " + f.getAbsolutePath());
        System.out.println("STAY WOKE");
        //context ni vedno instanca Activity
        if(context instanceof Activity){
            System.out.println("WE GUCCI");

            mSaver = Saver.createSaver(ONEDRIVE_APP_ID);
            if (Build.VERSION.SDK_INT > 22) {
                mSaver.startSaving((Activity) context, filename,FileProvider.getUriForFile(context,"david.projectclouds.MainActivity",f));

            }
            else{
                mSaver.startSaving((Activity) context, filename, Uri.parse("file://" + f.getAbsolutePath()));

            }
        }


        System.out.println("mSAVE IN UPLOAD" + mSaver);

        System.out.println("URI: " + Uri.fromFile(f).toString());
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