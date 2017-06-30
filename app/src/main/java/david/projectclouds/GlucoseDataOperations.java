package david.projectclouds;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;

import com.dropbox.core.util.IOUtil;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by david on 24.6.2017.
 */

public class GlucoseDataOperations {
    private List<GlucoseData> glucoseDataList = new ArrayList<>();
    private String lastID;
    private GlucoseDataAdapter mAdapter;

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
        mAdapter.notifyDataSetChanged();

    }
    //METODA S KATERO TRI INTEGERJE PRETVORIMO V STRING, PRIPRAVLJEN NA TEXTVIEW
    public String dateAppender(int day, int month, int year){
        String date = (String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year));
        return date;
    }


    public void parseXML(Context context, String currentDate) {
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


            while (event != XmlPullParser.END_DOCUMENT) {

                String name = myparser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("id")) {
                            id = myparser.getAttributeValue(null, "id");
                            //PREŠTEJEMO KOLIKO ID-JEV IMAMO V DATOTEKI
                            //KOLIKO JE ZADNJI ID TOLIKŠNA JE VELIKOST
                            this.lastID = String.valueOf(Integer.getInteger(id+1));
                            System.out.println(this.lastID);

                        }
                        break;
                    //TODO: V PRIMERU DANAŠNJEGA DATUMA DAJ V DANAŠNJI POGLED

                    case XmlPullParser.END_TAG:

                        if (name.equals("date")) {
                            date = myparser.getAttributeValue(null, "date");
                            glucose = myparser.getAttributeValue(null, "concentration");
                            time = myparser.getAttributeValue(null, "time");

                            System.out.println("Data: " + id + " " + date + " " + glucose + " " + time);
                            //TODO: GLEDE NA DATUM PRIDOBI PODATKE
                            System.out.println("CURRENT DATE" + currentDate);
                            if(currentDate.equals(date)){
                                System.out.println("CONNECTED" + date);
                               addItem(time,glucose);
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
    public void addToXML(Context context,String currentDate, String concentration, String time){
        try{
            InputStream is = context.getResources().openRawResource(R.raw.diabetix);
            //Create instance of DocumentBuilderFactory
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            //Get the DocumentBuilder
            DocumentBuilder parser = factory.newDocumentBuilder();
            //Create blank DOM Document
            Document doc=parser.parse(is);
            Element root = doc.getDocumentElement();
            System.out.println("ROOT FIRST CHILD" + root.getNodeName());
           NodeList dateL = doc.getElementsByTagName("date");
            //PREVERIMO SAMO ZADNJI VNOS, ČE SO DATUMI RAZLIČNI, POMENI DA JE NAZADNJE VNOS BIL STAREJŠI
            //ZATO NAREDIMO NOVI ID
                Node dateN = dateL.item(dateL.getLength()-1);
                String domDate = ((Element)dateN).getAttribute("date");
                if(currentDate.equals(domDate)) {
                    System.out.println("V IF");
                    //ČE STA ENAKA APPENDAJ NOVI CHILDNODE, DODAJ ATRIBUTE
                    Element newChild = doc.createElement("date");
                    newChild.setAttribute("date",currentDate);
                    newChild.setAttribute("concentration",concentration);
                    newChild.setAttribute("time",time);

                    dateN.appendChild(newChild);
                    System.out.println("NEW CHILD TIME" + newChild.getAttribute("time"));
                    System.out.println("domDate " + domDate);
                    System.out.println("JE NA SEZNAMU " + doc.getElementsByTagName("date").getLength());

                }
                else{
                    //ČE NI NA SEZNAMU USTVARI NOVI ELEMENT Z LASTNIM ID-JEM, DATE-OM IN ATRIBUTI
                    Element newChild = doc.createElement("id");
                    newChild.setAttribute("id",this.lastID);
                    root.appendChild(newChild);
                    Element newNewChild = doc.createElement("date");
                    newNewChild.setAttribute("date",currentDate);
                    newNewChild.setAttribute("concentration",concentration);
                    newNewChild.setAttribute("time",time);
                    newChild.appendChild(newNewChild);
                    System.out.println("NI NA SEZNAMU " + doc.getElementsByTagName("id").getLength());
                    System.out.println("domDate " + domDate);

                }




        }catch(Exception ex){
            ex.printStackTrace();
        }
    }



    }


