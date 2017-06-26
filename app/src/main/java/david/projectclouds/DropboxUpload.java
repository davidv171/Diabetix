package david.projectclouds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.dropbox.core.BadRequestException;
import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.WriteMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by david on 15.5.2017.
 */

public class DropboxUpload extends AsyncTask {
    DropboxAuthentication da = new DropboxAuthentication();
    Context context;
    //TODO: ROBUSTNOST UPLOADANJA, V PRIMERU, DA NE USPE UPLOAD(POTEČEN KLJUČ), APLIKACIJA NE SPOROČI UPORABNIKU
    //NE VEM KAKO DOLGO TRAJA KLJUČ. 1 DAN NAJMANJ
    public DropboxUpload(Context context){
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        InputStream is = new ByteArrayInputStream(params[0].toString().getBytes());
        SharedPreferences sp = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        String token = sp.getString("dropbox-token","");
        System.out.println("TOKEN" + token + " OAUTH2TOKEN" + da.getOAuth2Token() );
        if(token==null){
            System.out.println("YOU NEED TO LOG IN");
        }

        try {
            da.getClient(token).files().uploadBuilder("/Diabetix.xml").withMode(WriteMode.OVERWRITE).uploadAndFinish(is);
        } catch (DbxException e) {
            e.printStackTrace();
            System.out.println("Error uploading");
            //SPOROČIMO, ČE SE JE DOGODILA NAPAKA, DA JO LAHKO PRIKAŽEMO NA POSTEXECUTE

            return true;

        } catch (IOException e) {
            e.printStackTrace();


        }
        return false;


    }

    @Override
    protected void onPostExecute(Object o) {
        System.out.println("OBJECT" + o);
        if(o.equals(true)){
            Toast.makeText(context,"Please log in to Dropbox", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show();

        }

    }
}
