package david.projectclouds;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxHost;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.android.AuthActivity;
import com.dropbox.core.http.HttpRequestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxRawClientV2;
import com.dropbox.core.v2.auth.DbxUserAuthRequests;
import com.dropbox.core.v2.files.WriteMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.dropbox.core.DbxRequestConfig.newBuilder;

public class DropboxAuthentication {
    final static private String APP_KEY = "fls1sdivuf3356e";

        public void dropboxLogin(Context context) {

            Auth.startOAuth2Authentication(context, APP_KEY);

        }

        public String getOAuth2Token(){

            String accessToken = Auth.getOAuth2Token();

            return accessToken;
        }


    public static DbxClientV2 getClient(String accessToken) {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("ProjektClouds").build();
        DbxClientV2 client = new DbxClientV2(config, accessToken);

        return client;
    }
    //METODA KI NAM NALOŽI FILE NA DROPBOX, V PODMAPO /DIABETIX
    //IZBRANO IMAMO OVERWRITE TOREJ V PRIMERU DA JE IME DATOTEKE ENAKO JO PREPIŠEMO
    //DANO V ASYNC TASK PO ANDROID PRIPOROČILU, PRIDE DO NAPAKE ČE TEGA NE STORIMO
    public void uploadToDropbox(String xml,Context cxt){
        new DropboxUpload(cxt).execute(xml);

    }
    public void downloadFromDropbox(Context cxt){
        new DropboxDownload(cxt).execute();
    }


    }


