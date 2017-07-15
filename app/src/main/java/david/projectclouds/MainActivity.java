package david.projectclouds;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.DbxClientV2;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.microsoft.onedrivesdk.picker.IPickerResult;
import com.microsoft.onedrivesdk.saver.SaverException;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {






    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;

        private static final int RC_SIGN_IN = 15;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 404;
    private static final String TAG = "d2";
    //STATIČNE SPREMENLJIVKE ZA KASNEJŠE LOČEVANJE MED BRANJEM ČE SMO DOBILI DOVOLJENJE OD UPORABNIKA
    //ZA BRANJE ALI PISANJE DATOTEKE
    //OBOJE JE POTREBNO ZA NORMALNO DELOVANJE PROGRAMA ZATO V PRIMERU, DA NE DOBIMO DOVOLJENJA APLIKACIJO VRNEMO NA GLAVNI MENI
    private static final int REQUEST_WRITE_PERMISSIONS = 21;
    private static final int REQUEST_READ_PERMISSIONS = 22;
    //GOOGLE API KLIENTA, googleapiclient je za google drive, signin za google sign in
    GoogleApiClient mGoogleApiClient;
    GoogleApiClient mGoogleSignInApiClient;
    private String fileName = "DiabetixBackup.txt";
    private static final int REQUEST_CODE_OPENER = 4;
    private DriveId mDriveID=null;
        //RAZRED DROPBOXAUTHENTICATION, KI VSEBUJE METODO ZA DROPBOX LOGIN
        // POLEK LOGINA DOBI METODA ONRESUME ACCESSTOKEN!!!
        //KO SE ZAČNE DROPBOXLOGIN SE NAMREČ APLIKACIJA MINIMIRA, TOKEN DOBIMO, KO APLIKACIJO NAZAJ ODPREMO(TOREJ KO KONČAMO Z OAUTH2)
    //DROPBOX IN ONE DRIVE KONSTRUKTORJA
    //DROPBOX KONSTRUKTOR JE USTVARJEN ZA AVTENTIKACIJO, TOREJ PRIDOBITEV ACCESS TOKENA, KI GA DOBIMO PREKO METODA DROPBOXLOGIN
    //ACCESS TOKEN SE NATO PREKO METODE GETOAUTH2TOKEN SHRANI V SHARED PREFERENCES
    //PRAV TAKO STA DEFINIRANI METODI, KI POSKRBITA ZA DOWNLOAD IN UPLOAD DATOTEKE
    //DOWNLOADFROMDROPBOX in UPLOADFROMDROPBOX OBA USTVARITA ASYNCTASK(V RAZREDU DROPBOXUPLOAD ALI DOWNLOAD IN Z UPORABO KONTEKSTA MAINACTIVITY IN METODE
    //GETCLIENT(TA METODA USTVARI DROPBOX KLIENT, NAD KATERIM KLIČEMO UPLOADBUILDERJA IN DOWNLOADBUILDERJA
    //UPLOAD IN DOWNLOAD BUILDERJA STA DEFINIRANA V DROPBOX SDK IN POSKRBITA ZA UPLOAD IN DOWNLOAD DATOTEKE
    //UPLOAD IN DOWNLOAD TO ALI FROM DROPBOX UPORABIMO  DIALOGBOXIH
    DropboxAuthentication da = new DropboxAuthentication();

    //ZAENKRAT NE RABI NOBENE AVTENTIKACIJE
    //TUKAJ ZAŽENEMO FILE PICKERJA
    //V ON ACTIVITY RESULT PRIDOBIMO REZULTAT IZBIRE
    OneDriveDownload oneDriveDownload = new OneDriveDownload();
    OneDriveUpload oneDriveUpload = new OneDriveUpload();
    private String concentration = null;
    private String time = null;

    SharedPreferences sp;
    private File file;
    //ŠTEVILO S KATERIM PREVERJAMO ALI SMO XML SPREMENILI
    //xmlChanged++ SE IZVEDE V ALERTBUILDERJU V MAINACTIVITY(ADD BUTTON)
    //IN TUDI V GLUCOSEDATAADAPTER IN SICER V METODI EDITNODE(TO SE IZVEDE VEDNO KO SPREMENIMO ENO ŠTEVILO)
    static int xmlChanged=0;
   private GlucoseDataOperations gdo;

   private RecyclerView recyclerView;
    private TextView date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        file = new File(getApplicationContext().getExternalFilesDir("diabetix"),"Diabetix.xml");




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        gdo = new GlucoseDataOperations();
        gdo.prepareGlucoseListData(recyclerView,getContext());
        date = (TextView)findViewById(R.id.Date);
        final Calendar mcurrentDate=Calendar.getInstance();
        final int year=mcurrentDate.get(Calendar.YEAR);
        final int month=mcurrentDate.get(Calendar.MONTH);
        final int day=mcurrentDate.get(Calendar.DAY_OF_MONTH);
        System.out.println("DAY" + day);
        String currentDate = String.valueOf(day) +"." +  String.valueOf(month+1)+"." + String.valueOf(year);


        gdo.parseXML(getApplicationContext(), currentDate);
        date.setText(currentDate);
        final String finalCurrentDate = currentDate;
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatePickerDialog   mDatePicker =new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                    {
                        date.setText(new StringBuilder().append(selectedday).append(".").append(selectedmonth+1).append(".").append(selectedyear));
                        String pickedDate = date.getText().toString();
                        if(!pickedDate.equals(finalCurrentDate)){
                            fab.hide();
                        }
                        else{
                            fab.show();
                        }
                        gdo.parseXML(getApplicationContext(),pickedDate);

                    }
                },year, month, day);
                mDatePicker.setTitle("Please select date");
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                System.out.println("ONCLICK CALLED");

                mDatePicker.show();
            }
        });


        //USTVARIMO DATOTEKO





        //GOOGLE SIGN IN API CLIENT IN API CLIENT ZA GOOGLE DRIVE.
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this,1,null)
                .build();




        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                //METODA S KATERO PRIKAŽEMO DIALOG ALERTBUILDERJA ZA VNOS ŠTEVILKE
                createAddDialog();


            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        requestReadWritePermissions();
        System.out.println("END OF ONCREATE");
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(xmlChanged!=0){
            System.out.println("XMLCH" + xmlChanged);
            Toast.makeText(getContext(),"You have unsaved changes, do you wish to backup?",Toast.LENGTH_SHORT).show();
            createCloudChooser();
            xmlChanged=0;

        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }
        if(id == R.id.action_import){

            List<String> list = Arrays.asList("Google Drive", "Dropbox", "OneDrive");
            CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
            System.out.println(Arrays.toString(cs)); // [foo, bar, waa]
            new AlertDialog.Builder(this)
                    .setTitle("Choose cloud service")
                    .setItems(cs, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0: downloadFromDrive();
                                    break;
                                case 1:
                                    //PREVERJAMO ČE SO POTREBNE APLIKACIJE INŠTALIRANE
                                    //ČE NISO JIH PELJEMO NA MARKET PLACE
                                    if (Build.VERSION.SDK_INT > 16) {

                                        boolean dropboxInstalled = true;
                                        PackageManager pm = getPackageManager();
                                        try {
                                            pm.getPackageInfo("com.dropbox.android", PackageManager.GET_ACTIVITIES);
                                        } catch (PackageManager.NameNotFoundException e) {
                                            //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE
                                            Toast.makeText(getContext(), "Dropbox application is missing", Toast.LENGTH_SHORT).show();
                                            Intent intentMP = new Intent(Intent.ACTION_VIEW);
                                            intentMP.setData(Uri.parse(String.format("market://details?id=%s", "com.dropbox.android")));
                                            getContext().startActivity(intentMP);

                                        }
                                        if (dropboxInstalled) {
                                            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                                            chooseFile.setPackage("com.dropbox.android");
                                            chooseFile.setType("*text/xml");
                                            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                                            startActivityForResult(chooseFile, 69);
                                        }
                                    }
                                    else{
                                        da.downloadFromDropbox(getApplicationContext());

                                    }
                                    break;
                                //UPORABIMO MAINACTIVITY.THIS KER KOT APPLICATIONCONTEXT NE PASSAMO ACTVITIY AMPAK APPLICATION
                                case 2: oneDriveDownload.startFilePicker(MainActivity.this);
                                    break;
                            }
                        }
                    })

                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .show();

        }
        if (id == R.id.action_upload){

          createCloudChooser();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


          if (id == R.id.gdrive) {
              System.out.println("GDRIVE CLICKED");
              //IZBRIŠE DEFAULT RAČUN IN PONOVNO UPORABNIKU PONUDI DIALOG
              //S KATERIM SI LAHKO IZBERE NOVI RAČUN
              mGoogleSignInApiClient.clearDefaultAccountAndReconnect();
              Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInApiClient);
              startActivityForResult(signInIntent, RC_SIGN_IN);

        } else if (id == R.id.dropbox) {
              // OB KLIKU NA GUMB IZVEDEMO LOGIN SEKVENCO ZA DROPBOX
              // KER NAS DROPBOX LOGIN MINIMIRA IZ APLIKACIJE DOBIMO OAUTH2 TOKEN V ONRESUME
                //TRENUTNO USELESS, KER REŠUJEMO Z INTENTI

              if (Build.VERSION.SDK_INT > 16) {

                  boolean dropboxInstalled = true;
                  PackageManager pm = getPackageManager();
                  try {
                      pm.getPackageInfo("com.dropbox.android", PackageManager.GET_ACTIVITIES);
                  } catch (PackageManager.NameNotFoundException e) {
                      //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE
                      Toast.makeText(getContext(), "OneDrive application missing", Toast.LENGTH_SHORT).show();
                      Intent intentMP = new Intent(Intent.ACTION_VIEW);
                      intentMP.setData(Uri.parse(String.format("market://details?id=%s", "com.dropbox.android")));
                      getContext().startActivity(intentMP);
                        dropboxInstalled=false;
                  }
                  if (dropboxInstalled) {
                      Toast.makeText(getContext(), "Dropbox is installed", Toast.LENGTH_SHORT).show();
                  }

              }
              else{
                  da.dropboxLogin(this);

              }



        } else if (id == R.id.odrive) {
              boolean odriveInstalled = true;
              PackageManager pm = getPackageManager();
              try {
                  pm.getPackageInfo("com.microsoft.skydrive",PackageManager.GET_ACTIVITIES);
              } catch (PackageManager.NameNotFoundException e) {
                  //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE

                  //TODO: VPELJI LOGIKO, KI UPORABNIKA PRIPELJE DIREKTNO DO ONEDRIVE DA SE LAHKO PRIJAVI ITD.
                  //MOGOČI CRASHI!
                  Toast.makeText(getContext(),"OneDrive application missing", Toast.LENGTH_SHORT).show();
                  Intent intentMP = new Intent(Intent.ACTION_VIEW);
                  intentMP.setData(Uri.parse(String.format("market://details?id=%s", "com.microsoft.skydrive")));
                  getContext().startActivity(intentMP);
                  odriveInstalled=false;

              }
                if(odriveInstalled){
                    Toast.makeText(getContext(),"One Drive is installed",Toast.LENGTH_SHORT).show();
                }


          }  else if (id == R.id.stats){
              Intent intentStats = new Intent(this,GraphActivity.class);
              startActivity(intentStats);

        }  else if (id == R.id.nav_send) {
              Intent intent = new Intent(Intent.ACTION_SEND);
              intent.setType("message/rfc822");
              intent.putExtra(Intent.EXTRA_EMAIL,"david.vuckovic7@gmail.com");
              intent.putExtra(Intent.EXTRA_SUBJECT, "Reporting a bug");
              intent.putExtra(Intent.EXTRA_TEXT, "Please describe the bug in as much detail as possible");
              Intent mailer = Intent.createChooser(intent, null);
              startActivity(mailer);

        }
            else if(id == R.id.nav_about){
            //If you use the Google Drive Android API in your application, you must include the Google Play Services attribution text as part of a "Legal Notices"
              // section in your application. Including legal notices as an independent menu item, or as part of an "About" menu item, is recommended.
            String legal = GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this);
              System.out.println("LEGAL : " + legal);
              AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(this);
              LicenseDialog.setTitle("Legal Notices");
              LicenseDialog.setMessage(legal);
              LicenseDialog.show();

          }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
        public void onResume(){
            super.onResume();
            //SHRANIMO V SHARED PREFERENCES ZA KASNEJŠO UPORABO
            //IZVEDE SE VEDNO, KO APLIKACIJO MINIMIRAMO
            if(da.getOAuth2Token()!=null) {
                sp = this.getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("dropbox-token", da.getOAuth2Token());
                editor.apply();
            }
        }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("CONNECTION FAILED " + connectionResult);
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(this,"Unable to connect to Google",Toast.LENGTH_SHORT).show();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("ONACTIVITY RESULT");
        System.out.println("REQUEST CODE" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        //KODA SE IZVEDE VEDNO KO KONČAMO Z INTENTSENDERJEM(IZBIRA DATOTEK, TAKO PRI DOWNLOADU KOT PRI UPLOADU)
        //DOWNLOADFROMDRIVE
        //GOOGLE DOWNLOADDRIVE RQCODE = 2
        //DROPBOX RQCODE = 69
        //ONEDRIVE RQCODE = ??
        if(requestCode == 2){

                try{
                    mDriveID  = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    System.out.println("ONACTIVITY DRIVE ID " + mDriveID);
                    DriveFile file = mDriveID.asDriveFile();
                    file.open(mGoogleApiClient,DriveFile.MODE_READ_ONLY,null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                            if(driveContentsResult.getStatus().isSuccess()) {
                                DriveContents contents =  driveContentsResult.getDriveContents();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                                StringBuilder builder = new StringBuilder();
                                String line;
                                try {
                                    while ((line = reader.readLine()) != null) {
                                        builder.append(line);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String contentsAsString = builder.toString();

                                System.out.println("VSEBINA:" + contentsAsString);
                                gdo.writeToFile(contentsAsString);
                                gdo.parseXML(getContext(),date.getText().toString());
                            }
                            else{
                                System.out.println("FAIL");
                            }
                        }
                    });



                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }

        }
        if(requestCode==3){
            System.out.println("gDRIVE UPLOAD DATA" + data);
            System.out.println(resultCode);
            if(resultCode==-1){
                Toast.makeText(getContext(),"Google Drive upload successful",Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode ==69) {
            Intent intent = getIntent();
            System.out.println("EXTRAS" + intent.getExtras());
            System.out.println("DROPBOX DATA " + data.getData());
            Uri uri = data.getData();
            if(data.getData()!=null){
                gdo.getContentsFromURI(uri,getContentResolver(),getContext());
                gdo.parseXML(getContext(),date.getText().toString());
            }



        }

        try {
            IPickerResult result = oneDriveDownload.getFilePicker().getPickerResult(requestCode, resultCode, data);
            // Handle the case if nothing was picked
            Toast.makeText(getContext(),"Nothing was picked",Toast.LENGTH_SHORT);
            if (result != null) {
                // Do something with the picked file
                Log.d("main", "Link to file '" + result.getName() + ": " + result.getLink());
                return;
            }
        }
        catch (NullPointerException e){
            Log.d("main","File picker is null");
        }

        // Handle non-OneDrive picker request
        super.onActivityResult(requestCode, resultCode, data);
        // check that the file was successfully saved to OneDrive
        try {
            oneDriveUpload.getSaver().handleSave(requestCode, resultCode, data);
            gdo.getContentsFromURI(data.getData(),getContentResolver(),getContext());
            gdo.parseXML(getContext(),date.getText().toString());

            System.out.println("ONE DRIVE DATA" + data);
        } catch (final SaverException e) {
            // Log error information
            e.printStackTrace();
            System.out.println("ERROR TYPE "+ e.getErrorType());
            Toast.makeText(MainActivity.this,e.getErrorType().toString(), Toast.LENGTH_LONG).show();
            System.out.println("DEBUG" + e.getDebugErrorInfo());
        }
        catch(NullPointerException e){
            Log.d("main","ISaver == Null");

        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.getStatus());
        if (result.isSuccess()) {
            mGoogleSignInApiClient.connect();

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(this,"Google sign in successful!",Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(this,"Google sign in unsuccessful!",Toast.LENGTH_SHORT).show();
            System.out.println("SIGN IN FAILED");
            // Signed out, show unauthenticated UI.

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void downloadFromDrive(){

        //PRVO PREVERIMO AVTOMATSKO, ČE OBSTAJA NA GOOGLE DRIVE-U DATOTEKA Z IMENOM DIABETIXBACKUP(vedno enak file name, v Google Drive se sam zamenja)

           //KO SE KONČNO POVEŽE, SE IZVEDE INTENTSENDER, KO INTENTSENDER ZAKLJUČIMO(izberemo SELECT), se izvede ONACTIVITYRESULT, KI PRIDOBI
            //DRIVE ID IZBRANE DATOTEKE
            if(mGoogleApiClient.isConnected()){
                System.out.println("SuCCESS CONNECTION");
                IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder().setMimeType(new String[]{"text/xml"}).build(mGoogleApiClient);

                try{
                    startIntentSenderForResult(intentSender,2,null,0,0,0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Log.w(TAG, "Unable to send intent", e);

                }
            }

    }

    public void uploadToDrive(final String content){
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
            .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        Log.i(TAG, "Failed to create new contents.");
                        System.out.println("TEST");
                        Toast.makeText(MainActivity.this, "Failed to create new contents", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG, "Ready to create new contents.");
                    DriveContents driveContents = driveContentsResult.getDriveContents();
                    OutputStream outputStream = driveContents.getOutputStream();
                    Writer writer = new OutputStreamWriter(outputStream);
                    try {
                        writer.write(content);
                        writer.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }


                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("text/xml").setTitle(fileName).build();

                    IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialDriveContents(driveContentsResult.getDriveContents()).build(mGoogleApiClient);
                    try{
                        startIntentSenderForResult(intentSender,3,null,0,0,0);

                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();

                    }
                }
            });




}


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("ON CONNECTED SE IZVEDE");
    }


    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("CONNECTION SUSPENDED");
    }

    //PERMISSIONS

    public void requestReadWritePermissions(){
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                +ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)||(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "App needs this permission to run", Toast.LENGTH_SHORT).show();
            }else{
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_PERMISSIONS);
                System.out.println("PERMISSIONS_GRANTED");

                }
            } else {


                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_PERMISSIONS);

            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    System.out.println("PERMISSION_GRANTED");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    System.exit(0);

                }
                return;
            }
            case REQUEST_WRITE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    System.out.println("PERMISSION_GRANTED");
                } else {
                    System.exit(0);

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public Context getContext(){
        return MainActivity.this;
    }
    public void createAddDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Vpišite delež sladkorja");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        input.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //PREVCONCENTRATION IN PREVTIME STA SPREMENLJIVKI KI SLUŽITA KOT CACHE
                // V PRIMERU, DA V ISTI DAN SHRANIMO 2x ALI VEČKRAT KONCENTRACIJO, SE PREJŠNJA SHRANI
                //TO STORIMO ZATO, DA JO LAHKO PO DOMAČE PREMAKNEMO GOR
                //SET FUNCKIJA SE UPORABLJA, DA NE PODVAJAMO KONSTruKTORJEV, SAJ SE OSNOVNI UPORABI ZA KLIC FUNKCIJE ADDITEM
                //SHRANJUJE SE LE PREJŠNJA VREDNOST
                //MOŽNA BOLJŠA IMPLEMENTACIJA Z NEIZOGIBNO IMPLEMENTACIJO XML-a

                try{
                    concentration= (input.getText().toString());}
                catch (ParseException e){
                    Toast.makeText(getContext(),"Vpišite pravilne številke!",Toast.LENGTH_SHORT).show();
                }

                final Calendar c = Calendar.getInstance();
                String hour = (String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
                int minute = c.get(Calendar.MINUTE);
                if(String.valueOf(minute).length()==1){
                    time = hour +":0" +  String.valueOf(minute);

                }
                if(String.valueOf(hour).length()==1){
                    time = "0" + hour + ": "+ String.valueOf(minute);
                }

                if(String.valueOf(hour).length()==1&&String.valueOf(minute).length()==1){
                    time = "0" + hour  + ":" +"0" + String.valueOf(minute);

                }
                if(String.valueOf(hour).length()>1&&String.valueOf(minute).length()>1) {
                    time = hour + ":" + String.valueOf(minute);
                }
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH)+1;
                System.out.println("MESEC:" + mMonth);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                String date = gdo.dateAppender(mDay,mMonth,mYear);
                xmlChanged++;
                gdo.addItem(time,concentration,date);
                gdo.addToXML(getContext(),date,concentration,time);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void createCloudChooser(){
        List<String> list = Arrays.asList("Google Drive", "Dropbox", "OneDrive");
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        System.out.println(Arrays.toString(cs));
        new AlertDialog.Builder(this)
                .setTitle("Choose cloud service")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:

                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(new File(getContext().getExternalFilesDir("diabetix"),"Diabetix.xml"));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                InputStreamReader isr = new InputStreamReader(fis);
                                BufferedReader bufferedReader = new BufferedReader(isr);
                                StringBuilder sb = new StringBuilder();
                                String line;
                                try {
                                    while ((line = bufferedReader.readLine()) != null) {
                                        sb.append(line);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String content = sb.toString();
                                uploadToDrive(content);
                                break;
                            case 1:
                                //USTVARIMO DATOTEKO
                                PackageManager pm = getPackageManager();

                                if (Build.VERSION.SDK_INT > 16) {

                                    boolean dropboxInstalled = true;
                                    try {
                                        pm.getPackageInfo("com.dropbox.android", PackageManager.GET_ACTIVITIES);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE
                                        Toast.makeText(getContext(), "OneDrive application missing", Toast.LENGTH_SHORT).show();
                                        Intent intentMP = new Intent(Intent.ACTION_VIEW);
                                        intentMP.setData(Uri.parse(String.format("market://details?id=%s", "com.dropbox.android")));
                                        getContext().startActivity(intentMP);

                                    }
                                    if (dropboxInstalled) {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/xml");
                                        intent.setPackage("com.dropbox.android");
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), "david.projectclouds.MainActivity", file));
                                        getContext().startActivity(Intent.createChooser(intent, "Upload to Dropbox"));
                                    }
                                }
                                else{
                                    da.uploadToDropbox(gdo.returnFileContent(), getApplicationContext());

                                }


                                break;
                            case 2:
                                Toast.makeText(getContext(),"You can only upload to your personal account, not business account, thanks Microsoft",Toast.LENGTH_SHORT).show();
                                //USTVARIMO DATOTEKO

                                //POMEMBNO, DA USTVARJAŠ FILE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                // file = new File(getApplicationContext().getExternalFilesDir("diabetix"),"Diabetix.xml");
                                ///oneDriveUpload.uploadToOneDrive("<12-2-2017>6</12-2-2017>" , getContext(),file);
                                //OBČASNI PROblEMI Z NALAGANJEM NA ŠOLSKI ONE DRIVE; OSEBNI DELUJE OK??
                                if (Build.VERSION.SDK_INT > 16) {

                                    pm = getPackageManager();
                                    boolean odInstalled = true;
                                    try {
                                        pm.getPackageInfo("com.microsoft.skydrive", PackageManager.GET_ACTIVITIES);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE
                                        Toast.makeText(getContext(), "OneDrive application missing", Toast.LENGTH_SHORT).show();
                                        Intent intentMP = new Intent(Intent.ACTION_VIEW);
                                        intentMP.setData(Uri.parse(String.format("market://details?id=%s", "com.microsoft.skydrive")));
                                        getContext().startActivity(intentMP);
                                        odInstalled = false;
                                    }
                                    if (odInstalled) {
                                        Intent intentOD = new Intent(Intent.ACTION_SEND);
                                        intentOD.setType("text/xml");
                                        intentOD.setPackage("com.microsoft.skydrive");
                                        intentOD.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intentOD.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), "david.projectclouds.MainActivity", file));
                                        getContext().startActivity(Intent.createChooser(intentOD, "Upload to OneDrive"));
                                    }
                                }
                                else{
                                    oneDriveUpload.uploadToOneDrive(gdo.returnFileContent(),getContext(),file);
                                }
                                break;
                        }
                    }
                })

                .setIcon(android.R.drawable.ic_menu_info_details)
                .show();
    }


}

