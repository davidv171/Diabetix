package david.projectclouds;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by david on 24.6.2017.
 */

public class GlucoseDataAdapter extends RecyclerView.Adapter<GlucoseDataAdapter.MyViewHolder> {
    private List<GlucoseData> glucoseDataList;
    //CONTEXT ENABLES AN ALERTBUILDER TO BE CREATED
    //CONSTRUCTOR CALLED INSIDE GLUCOSEDATAOPERATIONS INSIDE THE METHOD PREPAREDATA
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView concentration1;
        public TextView time1;


        public MyViewHolder(View view) {
            super(view);
            concentration1 = (TextView) view.findViewById(R.id.Concentration1);
            time1 = (TextView) view.findViewById(R.id.Time1);


        }


    }

    public GlucoseDataAdapter(List<GlucoseData> glucoseDataList, Context context) {
        this.glucoseDataList = glucoseDataList;
        this.context = context;
    }

    @Override
    public GlucoseDataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        System.out.println("VIEW TYPE" + viewType);

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.glucose_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GlucoseDataAdapter.MyViewHolder holder, final int position) {
        final GlucoseData glucoseData = glucoseDataList.get(position);
        holder.concentration1.setText(glucoseData.getConcentration1());
        holder.time1.setText(glucoseData.getTime1());
        //LONG CLICK ON CONCENTRATION OR TIME LETS YOU EDIT THOSE VALUES
        //USING CLASS CONTEXT VARIABLE AND ONLONGCLICKS
        //TODO: V XMLU SPREMENI VRSTNI RED GLEDE NA ČAS, PRAV TAKO SPREMEMBE SHRANI V XML
        holder.concentration1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit concentration");

// Set up the input
                final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = input.getText().toString();
                        glucoseDataList.get(position).setConcentration1(newValue);

                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH)+1;
                        System.out.println("MESEC:" + mMonth);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        String date = (String.valueOf(mDay) + "." + String.valueOf(mMonth) + "." + String.valueOf(mYear));
                        System.out.println("EDIT DATE" + date);
                        editNode(position, newValue, "concentration", date);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }


        });
        holder.time1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit time of input");

// Set up the input
                final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_DATETIME);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = input.getText().toString();
                        glucoseDataList.get(position).setTime1(newValue);
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH)+1;
                        System.out.println("MESEC:" + mMonth);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        String date = (String.valueOf(mDay) + "." + String.valueOf(mMonth) + "." + String.valueOf(mYear));
                        if(newValue.length()==3){
                            newValue = "0" + newValue;
                        }
                        if(!(glucoseDataList.get(0).getDate()==null)){
                            date = glucoseDataList.get(0).getDate();
                        }
                        editNode(position, newValue, "time", date);

                        sortData();
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }


        });

    }

    public void sortData() {
        //TODO: SORTIRAJ XML

       /* Collections.sort(glucoseDataList, new Comparator<GlucoseData>() {
            @Override
            public int compare(GlucoseData o1, GlucoseData o2) {
                System.out.println("o1" + o1.getTime1());
                System.out.println("o2" + o2.getTime1());
                return o1.getTime1().compareTo(o2.getTime1());
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return glucoseDataList.size();
    }
    //TODO: ZOPTIMIZIRAJ, INDEXA NE RABIŠ VREDNOST SE SPREMINJA NA INDEXU V ARRAYLISTU IZ UREJENEGA XML-A
    //METODA, KI SPREMENI VREDNOST ENEGA ATRIBUTA
    //SPREMEMBA JE MOŽNA LE NA DANAŠNJI DATUM
    //INDEX POVE KATERI NODE SMO SPREMENILI
    //NEW VALUE JE NOVA VREDNOST ATRIBUTA
    //ATTRIBUTE NAM POVE KATERI ATTRIBUTE SMO SPREMENILI
    public void editNode(int index, String newValue, String attribute, String currentDate) {
        Document doc = null;
        //Create instance of DocumentBuilderFactory
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        //Get the DocumentBuilder
        DocumentBuilder parser = null;
        try {
            parser = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }


        InputStream is = null;
        try {
            is = new FileInputStream(new File(context.getExternalFilesDir("diabetix"), "Diabetix.xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            doc = parser.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int j = 0;
        NodeList allDates = doc.getElementsByTagName("date");
        for (int i = 0; i < allDates.getLength() ; i++) {
            Node oneDate = allDates.item(i);
            System.out.println("CURRENT DATE" + currentDate);
            System.out.println("CURRENT DATE ELEMENT" + ((Element) oneDate).getAttribute("date"));
            System.out.println("EDIT NODE CONTENT");
            System.out.println("DATE ELEMENT" + ((Element) oneDate).getAttribute("date"));
            if (((Element) oneDate).getAttribute("date").equals(currentDate)) {
                System.out.println("IN IF");
                j++;

                if (j == 1) {
                    Node node = allDates.item(i + index);

                    if(attribute.equals("time")){
                        //TODO: SORT THE NEW ATTRIBUTE
                        //PLAN:
                        //NOVI NODE POSTAVI GLEDE NA VREDNOSTI GLUCOSELIST ČASOV
                        //INSERT ZA TISTIM, KI IMA MANJŠI ČAS ALI INSERT PRED TISTIM, KI IMA VEČJI ČAS
                        //allDates.item(i+index).appendChild();

                    }
                    ((Element) node).setAttribute(attribute, newValue);

                    System.out.println("ATRIBUTE" + attribute + " " + newValue);
                }


            }
        }
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        Result output = new StreamResult(new File(context.getExternalFilesDir("diabetix"), "Diabetix.xml"));
        Source input = new DOMSource(doc);

        try {
            transformer.transform(input, output);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        

}

}

