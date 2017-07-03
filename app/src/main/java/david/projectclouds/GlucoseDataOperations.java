package david.projectclouds;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

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
import org.xml.sax.SAXParseException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
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
    private File file;
    private Document doc;
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
    public void addItem(String time, String concentration,String date) {
        //IMPORTING FROM XML WILL BE DONE THROUGH A DIFFERENT METHOD, WITH DATE AS AN ARGUMENT
        GlucoseData glucoseData = new GlucoseData(concentration,time,date);
        glucoseDataList.add(glucoseData);

        mAdapter.notifyDataSetChanged();

    }
    //METODA S KATERO TRI INTEGERJE PRETVORIMO V STRING, PRIPRAVLJEN NA TEXTVIEW
    public String dateAppender(int day, int month, int year){
        String date = (String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year));
        return date;
    }


    public void parseXML(Context context, String currentDate) {
        System.out.println("PARSE XML " + currentDate);
        glucoseDataList.clear();
        this.file = new File(context.getExternalFilesDir("diabetix"),"Diabetix.xml");

        InputStream is = null;
        try {
            is = new FileInputStream(new File(context.getExternalFilesDir("diabetix"), "Diabetix.xml"));
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
                System.out.println("CREATING NEW FILE");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();

        }


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
        String date;
        String glucose = null;
        String time = null;
        String id = null;


            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myparser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        System.out.println("PARSER TEXT" + myparser.getText());
                        if (name.equals("id")) {

                            id = myparser.getAttributeValue(null, "id");
                            //PREŠTEJEMO KOLIKO ID-JEV IMAMO V DATOTEKI
                            //KOLIKO JE ZADNJI ID TOLIKŠNA JE VELIKOST
                            System.out.println("IDS IN XML" + id);
                            this.lastID = String.valueOf(Integer.getInteger(id));
                            if(this.lastID==null){
                                this.lastID = "1";
                            }
                            System.out.println("LASTID"+ this.lastID);

                        }
                        break;

                    case XmlPullParser.END_TAG:

                        if (name.equals("date")) {
                            date = myparser.getAttributeValue(null, "date");
                            glucose = myparser.getAttributeValue(null, "concentration");
                            time = myparser.getAttributeValue(null, "time");

                            if(currentDate.equals(date)){
                                System.out.println("EDIT NODE");
                               addItem(time,glucose,currentDate);

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
            //VEDNO KO PREBEREMO PODATKE JIH TUDI SORTIRAMO
            mAdapter.sortData();



    }
    public void addToXML(Context context,String currentDate, String concentration, String time){
        file = new File(context.getExternalFilesDir("diabetix"),"Diabetix.xml");
        try {
            //Create instance of DocumentBuilderFactory
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            //Get the DocumentBuilder
            DocumentBuilder parser = factory.newDocumentBuilder();
            //Create blank DOM Document

                //V DATOTEKO NAPIŠI ROOT ELEMENT Z ID-jEM, ČE JE PRAZNA
                if(file.length()==0) {
                    System.out.println("PRAZNA DATOTEKA");
                    Writer writer = new OutputStreamWriter(new FileOutputStream(this.file),"UTF-8");
                    try {
                        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                "<diabetix>\n" +

                                "</diabetix>");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            InputStream is = new FileInputStream(new File(context.getExternalFilesDir("diabetix"), "Diabetix.xml"));

            String domDate=null;
            doc = parser.parse(is);
            Element root = doc.getDocumentElement();
            System.out.println("ROOT FIRST CHILD" + root.getNodeName());
            Node lastIDNode = root.getLastChild();

            NodeList dateL = doc.getElementsByTagName("date");
            //PREVERIMO SAMO ZADNJI VNOS, ČE SO DATUMI RAZLIČNI, POMENI DA JE NAZADNJE VNOS BIL STAREJŠI
            //ZATO NAREDIMO NOVI ID
            if ((dateL.getLength()==0)){

                Element newChild = doc.createElement("id");
                //NEW CHILD == <ID
                //ROOT = <DIABETIX
                //NEWNEWCHILD = <DATE
                newChild.setAttribute("id", "1");
                this.lastID = "1";
                root.appendChild(newChild);
                Element newNewChild = doc.createElement("date");

                newNewChild.setAttribute("date", currentDate);
                newNewChild.setAttribute("concentration", concentration);
                newNewChild.setAttribute("time", time);

                newChild.appendChild(newNewChild);

                System.out.println("NI DATOTEKE" + doc.getElementsByTagName("id").getLength());
                System.out.println("domDate " + domDate);

            }
        else{

                System.out.println("ROOT FIRST CHILD" + root.getNodeName());

                Node dateN = dateL.item(dateL.getLength() - 1);
                domDate = ((Element) dateN).getAttribute("date");
                if (currentDate.equals(domDate)) {
                    System.out.println("V IF");
                    //ČE STA ENAKA APPENDAJ NOVI CHILDNODE, DODAJ ATRIBUTE
                    Element newChild = doc.createElement("date");
                    newChild.setAttribute("date", currentDate);
                    newChild.setAttribute("concentration", concentration);
                    newChild.setAttribute("time", time);

                    lastIDNode.appendChild(newChild);
                    System.out.println("NEW CHILD TIME" + newChild.getAttribute("time"));
                    System.out.println("domDate " + domDate);
                    System.out.println("JE NA SEZNAMU " + doc.getElementsByTagName("date").getLength());

                } else {
                    //ČE NI NA SEZNAMU USTVARI NOVI ELEMENT Z LASTNIM ID-JEM, DATE-OM IN ATRIBUTI
                    //ČE JE PRAZNI SEZNAM DODAJ TO:
                    //TODO: SOČASNO PREVERI ČE JE PRAZNI SEZNAM
                    Element newChild = doc.createElement("date");
                    newChild.setAttribute("date", currentDate);
                    newChild.setAttribute("concentration", concentration);
                    newChild.setAttribute("time", time);

                    lastIDNode.appendChild(newChild);
                    System.out.println("NEW CHILD TIME" + newChild.getAttribute("time"));
                    System.out.println("domDate " + domDate);
                    System.out.println("JE NA SEZNAMU " + doc.getElementsByTagName("date").getLength());

                }





            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(new File(context.getExternalFilesDir("diabetix"), "Diabetix.xml"));
            Source input = new DOMSource(doc);

            transformer.transform(input, output);



            System.out.println("SAVED");


        }catch(Exception ex){
            ex.printStackTrace();
        }
    }





public void deleteFile(){
   this.file.delete();
}
//IZ CONTENTA, KI JE REZULTAT INTENTA(DATA V ONRESULT) PREBEREMO VSEBINO DATOTEKE
    //KO VSEBINO PREBEREMO AVTOMATSKO KLIČEMO METODO WRITETOFILE, KI PREBRANO VSEBINO NAPIŠE V NAŠO DATOTEKO DIABETIX.XML
    //GOOGLE DRIVE IMA ŽE DEFINIRANO METODO ZA PRIDOBITEV VSEBINE ZATO NAD GOOGLE DRIVEOM KLIČEMO LE WRITETOFILE
    //KO SE ZAKLJUČI WRITE TO FILE SE ŠE ENKRAT IZ MAIN ACTIVITY ZAŽENE PARSEXML, DA SE OSVEŽIJO PODATKI
    //TODO: NAPAČNE XML-E NE SPREJMI
public void getContentsFromURI(Uri uri, ContentResolver contentResolver){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(contentResolver.openInputStream(uri)));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
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
        System.out.println(contentsAsString);
        writeToFile(contentsAsString);
    }
    public void writeToFile(String xml){
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(this.file),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            writer.write(xml);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}





