package david.projectclouds;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SharedPreferences.OnSharedPreferenceChangeListener{
        private int CLOUD_DOWNLOAD = 1;
        //TODO: CHANGE ICON BASED ON DEFAULT

    private static final int REQUEST_WRITE_PERMISSIONS = 21;
    private static final int REQUEST_READ_PERMISSIONS = 22;
    //SPREMENLJIVKE, V KATERIH SO IMENA PAKETOV, S KATERIMI, KO V METODI preferredCloudServiceChooser()
    //IZBEREMO DEFAULT OBLAČNO STORITEV IN JO SHRANIMO V SHARED PREFERENCES, NATO PA UPORABIMO ZA ODPIRANJE INTENTA

    private static final String googleDrivePM = "com.google.android.apps.docs";
    private static final String dropboxPM = "com.dropbox.android";
    private static final String oneDrivePM = "com.microsoft.skydrive";
    private static final String MARKET_URL = "market://details?id=%s";

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
        getSupportActionBar().setDisplayShowTitleEnabled(true);
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
        drawer.addDrawerListener(toggle);
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        System.out.println("MENU CREATED");
        //TODO: OB SPREMEMBI SHARED PREFERENCES SPREMENI MENU IKONO
        final Menu menu2 = menu;
        getContext().getSharedPreferences("prefs",Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                System.out.println("SHARED PREFERENCES CHANGED");
                //KO SPREMENIMO DEFAULT CLOUD PROVIDERJA SPREMENIMO TUDI IKONO
                //SE IZVRŠI OB USTVARJANJU MENUJA IN OB KLIKIH
                sp = getContext().getSharedPreferences("prefs",Context.MODE_PRIVATE);
                String defaultCloud = sp.getString("default-cloud","null");
                Drawable icon = null;
                try {
                    icon = getPackageManager().getApplicationIcon(defaultCloud);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("DEFAULT CLOUD" + defaultCloud);
                if(!defaultCloud.equals("null")){

                    MenuItem item2 = menu2.findItem(R.id.action_upload);
                    item2.setIcon(icon);
                    System.out.println("CHANGE ICON");

                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //UPORABNIK SI IZBERE KATEREGA IZMED TREH CLOUD SERVICEOV BO UPORABLJAL SKOZI
            //GLEDE NA TO IZBIRO SE MU VEDNO ODPRE TISTI

            preferredCloudServiceChooser();


        }
        if(id == R.id.action_import){

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            sp = getContext().getSharedPreferences("prefs",Context.MODE_PRIVATE);
            String defaultCloud = sp.getString("default-cloud","null");
            System.out.print("DEF CLOUD" +  defaultCloud);
            if(!defaultCloud.equals("null")){
                intent.setPackage(defaultCloud);
            }
            startActivityForResult(Intent.createChooser(intent,"Pick a file!"),CLOUD_DOWNLOAD);

        }
        if (id == R.id.action_upload){
        //SHARE BUTTON INSTEAD

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
              //ČE JE INŠTALIRANO OBJAVI
              //ČE NI INŠTALIRANO PELJI DO MARKETA
              boolean googleDriveInstalled = true;
              PackageManager pm = getPackageManager();
              try {
                  pm.getPackageInfo(googleDrivePM, PackageManager.GET_ACTIVITIES);
              } catch (PackageManager.NameNotFoundException e) {
                  //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE
                  Toast.makeText(getContext(), "Google Drive application missing", Toast.LENGTH_SHORT).show();
                  Intent intentMP = new Intent(Intent.ACTION_VIEW);
                  intentMP.setData(Uri.parse(String.format(MARKET_URL, googleDrivePM)));
                  getContext().startActivity(intentMP);
                  googleDriveInstalled=false;
              }
              if (googleDriveInstalled) {
                  Toast.makeText(getContext(), "Google Drive is installed!", Toast.LENGTH_SHORT).show();
              }
        } else if (id == R.id.dropbox) {
              // OB KLIKU NA GUMB IZVEDEMO LOGIN SEKVENCO ZA DROPBOX
              // KER NAS DROPBOX LOGIN MINIMIRA IZ APLIKACIJE DOBIMO OAUTH2 TOKEN V ONRESUME
                //TRENUTNO USELESS, KER REŠUJEMO Z INTENTI


                  boolean dropboxInstalled = true;
                  PackageManager pm = getPackageManager();
                  try {
                      pm.getPackageInfo(dropboxPM, PackageManager.GET_ACTIVITIES);
                  } catch (PackageManager.NameNotFoundException e) {
                      //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE
                      Toast.makeText(getContext(), "OneDrive application missing", Toast.LENGTH_SHORT).show();
                      Intent intentMP = new Intent(Intent.ACTION_VIEW);
                      intentMP.setData(Uri.parse(String.format(MARKET_URL, dropboxPM)));
                      getContext().startActivity(intentMP);
                        dropboxInstalled=false;
                  }
                  if (dropboxInstalled) {
                      Toast.makeText(getContext(), "Dropbox is installed", Toast.LENGTH_SHORT).show();
                  }

              }




        else if (id == R.id.odrive) {
              boolean odriveInstalled = true;
              PackageManager pm = getPackageManager();
              try {
                  pm.getPackageInfo(oneDrivePM,PackageManager.GET_ACTIVITIES);
              } catch (PackageManager.NameNotFoundException e) {
                  //V PRIMERU DA NIMA INŠTALIRANO, GA PELJI DO MARKETPLACE
                  //MOGOČI CRASHI!
                  Toast.makeText(getContext(),"OneDrive application missing", Toast.LENGTH_SHORT).show();
                  Intent intentMP = new Intent(Intent.ACTION_VIEW);
                  intentMP.setData(Uri.parse(String.format(MARKET_URL, oneDrivePM)));
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

        }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CLOUD_DOWNLOAD){
            Intent intent = getIntent();
            System.out.println("EXTRAS" + intent.getExtras());
            if(data!=null) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    gdo.getContentsFromURI(uri, getContentResolver(), getContext());

                    gdo.parseXML(getContext(), date.getText().toString());
                } else {
                    Toast.makeText(getContext(), "Empty XML file", Toast.LENGTH_LONG).show();
                }
            }
        }
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
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/xml");

        sp = getContext().getSharedPreferences("prefs",Context.MODE_PRIVATE);
        String defaultCloud = sp.getString("default-cloud","null");
        System.out.print("DEF CLOUD" +  defaultCloud);
        if(!defaultCloud.equals("null")){
            sharingIntent.setPackage(defaultCloud);
        }
        sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), "david.projectclouds.MainActivity", file));
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }
    public void preferredCloudServiceChooser(){
        //PONUDI UPORABNIKU IZBIRO ENEGA IZMED TREH
        List<String> list = Arrays.asList("Google Drive", "Dropbox", "OneDrive","None");
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        System.out.println(Arrays.toString(cs)); // [foo, bar, waa]

        new AlertDialog.Builder(this)
                .setTitle("Choose your default cloud service")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String izbira = "null";

                        switch (which){
                            case 0:
                                izbira = googleDrivePM;

                                break;
                            case 1:
                                izbira=dropboxPM;
                                break;
                            //UPORABIMO MAINACTIVITY.THIS KER KOT APPLICATIONCONTEXT NE PASSAMO ACTVITIY AMPAK APPLICATION
                            case 2:
                                izbira =oneDrivePM;
                                break;
                            case 3:
                                izbira = "null";
                                break;
                            default:
                                izbira = "null";
                                Toast.makeText(getContext(),"Picking failure",Toast.LENGTH_SHORT);
                                break;
                        }
                        sp = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("default-cloud", izbira);
                        editor.apply();
                    }
                })

                .setIcon(android.R.drawable.ic_menu_info_details)
                .show();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}




