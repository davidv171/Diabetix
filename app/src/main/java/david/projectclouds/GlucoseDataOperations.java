package david.projectclouds;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 24.6.2017.
 */

public class GlucoseDataOperations {
    private List<GlucoseData> glucoseDataList = new ArrayList<>();

    private GlucoseDataAdapter mAdapter;
    //DA SE NE IZVAJA 2X, TO ŠE TREBA ZRIHTAT
    private int wtf=0;
    //DATE ZA NASTAVLJANJE TEKSTA, SAJ GA V ADAPTERJU NE GRE
    private String date = null;
    public GlucoseDataOperations(){

    }
    public void prepareGlucoseListData(RecyclerView recyclerView,Context context){

        mAdapter = new GlucoseDataAdapter(glucoseDataList,context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        //VERTIKALNI DIVIDER, MED VSAKIM ELEMENTOM JE ČRTA
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));

        //DATE-I SE OHRANJAJO ZA STARE

    }
    //FUNKCIJA ADD ITEM, KI JO KLIČEMO V MAINACTIVITY, KO SE PRITISNE GUMB FAB
    //PREVERI, ALI JE ZA TA DATUM ŽE DODAN KAKŠNA KONCENTRACIJA IN ČE JE, DODA NOVEGA
    //V PRIMERU DA STA ŽE DODANI DVE KONCENTRACIJI, SE PREMAKNE DRUGA NA MESTO PRVE, NA MESTO DRUGE PA PRIDE NOVA KONCENTRACIJA
    public void addItem(String time, String concentration) {
        //IMPORTING FROM XML WILL BE DONE THROUGH A DIFFERENT METHOD, WITH DATE AS AN ARGUMENT
        GlucoseData glucoseData = new GlucoseData(concentration,time);
        glucoseDataList.add(glucoseData);
        System.out.println(mAdapter);
        mAdapter.notifyDataSetChanged();

    }
    //METODA S KATERO TRI INTEGERJE PRETVORIMO V STRING, PRIPRAVLJEN NA TEXTVIEW
    public String dateAppender(int day, int month, int year){
        String date = (String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year));
        return date;
    }


    public String parseXML(Context context,int position) {
        glucoseDataList.clear();
        InputStream is = null;
        is = context.getResources().openRawResource(R.raw.diabetix);
        XmlPullParserFactory xmlFactoryObject = null;
        XmlPullParser myparser = null;
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            myparser = xmlFactoryObject.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            myparser.setInput(is, null);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        int event = 0;
        try {
            event = myparser.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        String date = null;
        String glucose = null;
        String time = null;
        String id = null;
        int idChange = 0;

        wtf++;
        if (wtf > 1) {

            while (event != XmlPullParser.END_DOCUMENT) {
                idChange++;
                String name = myparser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("id")) {
                            id = myparser.getAttributeValue(null, "id");



                        }
                        break;

                    case XmlPullParser.END_TAG:

                        if (name.equals("date")) {
                            date = myparser.getAttributeValue(null, "date");
                            glucose = myparser.getAttributeValue(null, "concentration");
                            time = myparser.getAttributeValue(null, "time");
                            System.out.println("Data: " + id + " " + date + " " + glucose + " " + time);
                            //TODO: GLEDE NA VIEW SPREMENI PRIDOBI PODATKE IZ XML-A(VIEW POSITION = 0 -> ZADNJI ID
                            System.out.println("position "+ position);

                            if (id.equals(String.valueOf(position))) {
                                System.out.println("POSITION DATE" +date );
                                this.date = date;
                                addItem(time, glucose);
                            }


                        }
                        break;

                }
                try {
                    event = myparser.next();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
        return this.date;
    }

}
