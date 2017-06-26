package david.projectclouds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.DownloadErrorException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by david on 28.5.2017.
 */

public class DropboxDownload extends AsyncTask {
    DropboxAuthentication da = new DropboxAuthentication();
    Context context;
    public DropboxDownload(Context context){
        this.context = context;
    }
    protected Object doInBackground(Object[] params) {
        //TODO: DOWNLOAD FROM DROPBOX, ČE MOŽNO, DA SI UPORABNIK IZBERE DATOTEKO, NI PA NUJNO, SAJ OVERWRITEAMO AVTOMATSKO
        //POLEK OVERWRITEANJA SMO TUDI VEDNO V ISTI MAPI
        //GLEDE NA TO DA SHRANJUJEMO AVTOMATSKO BREZ GRAFIČNEGA VMESNIKA NI POTREBE PO GRAFIČNEM VMESNIKU ZA IZBIRANJE
        SharedPreferences sp = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        String token = sp.getString("dropbox-token","");


        try {


            InputStream inputStream =  da.getClient(token).files().downloadBuilder("/Diabetix.xml").start().getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                total.append(line).append('\n');
            }

            System.out.println("INPUT STREAM "+  total);
            //TODO: INPUTSTREAM UPORABI, SHRANI GA V REALM
            //TODO: ZRIHTAJ REALM, ALI PA UPORABI LOKALNO DATOTEKO(NOT PREFERRED)

            } catch (DownloadErrorException e) {
                Toast.makeText(context,"Download error",Toast.LENGTH_LONG).show();

                e.printStackTrace();
            } catch (DbxException e) {
                Toast.makeText(context,"Dropbox error",Toast.LENGTH_LONG).show();

                e.printStackTrace();
            } catch (IOException e) {
            Toast.makeText(context,"File error",Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Object o) {
        Toast.makeText(context,"Download completed",Toast.LENGTH_LONG).show();
    }
}
