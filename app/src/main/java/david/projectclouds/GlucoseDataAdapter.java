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
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

 class GlucoseDataAdapter extends RecyclerView.Adapter<GlucoseDataAdapter.MyViewHolder> {
    private List<GlucoseData> glucoseDataList;
    //CONTEXT ENABLES AN ALERTBUILDER TO BE CREATED
    //CONSTRUCTOR CALLED INSIDE GLUCOSEDATAOPERATIONS INSIDE THE METHOD PREPAREDATA
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView concentration1;
        TextView time1;


        MyViewHolder(View view) {
            super(view);
            concentration1 = (TextView) view.findViewById(R.id.Concentration1);
            time1 = (TextView) view.findViewById(R.id.Time1);


        }


    }

    GlucoseDataAdapter(List<GlucoseData> glucoseDataList, Context context) {
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
    public void onBindViewHolder(GlucoseDataAdapter.MyViewHolder holder,  int position) {
        final GlucoseData glucoseData = glucoseDataList.get(position);
        holder.concentration1.setText(glucoseData.getConcentration1());
        holder.time1.setText(glucoseData.getTime1());
        final int position1 = position;
        //LONG CLICK ON CONCENTRATION OR TIME LETS YOU EDIT THOSE VALUES
        //USING CLASS CONTEXT VARIABLE AND ONLONGCLICKS
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
                input.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = input.getText().toString();
                        glucoseDataList.get(position1).setConcentration1(newValue);

                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH)+1;
                        System.out.println("MESEC:" + mMonth);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        String date = (String.valueOf(mDay) + "." + String.valueOf(mMonth) + "." + String.valueOf(mYear));
                        System.out.println("EDIT DATE" + date);
                        if(!(glucoseDataList.get(0).getDate()==null)){
                            date = glucoseDataList.get(0).getDate();
                        }
                        editNode(position1, newValue, "concentration", date);
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
                        glucoseDataList.get(position1).setTime1(newValue);
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
                        System.out.println("DATE V EDIT" + date);
                        editNode(position1, newValue, "time", date);

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


    @Override
    public int getItemCount() {
        return glucoseDataList.size();
    }
    //METODA, KI SPREMENI VREDNOST ENEGA ATRIBUTA
    //SPREMEMBA JE MOŽNA LE NA DANAŠNJI DATUM
    //INDEX POVE KATERI NODE SMO SPREMENILI
    //NEW VALUE JE NOVA VREDNOST ATRIBUTA
    //ATTRIBUTE NAM POVE KATERI ATTRIBUTE SMO SPREMENILI
    private void editNode(int index, String newValue, String attribute, String currentDate) {
        MainActivity.xmlChanged++;
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
            if (parser != null) {
                doc = parser.parse(is);
            }
            else{
                Toast.makeText(context,"File error",Toast.LENGTH_LONG).show();
            }
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        //Z INDEXOM J POSKRBIMO DA SAMO ENKRAT NASTAVIMO VREDNOST NODE-A KO NASTAVLJAMO KONCENTRACIJO
        int j = 0;
        //ROOT XML - > V XML-u ELEMENT DIABETIX, VSEBUJE SAMO ENEGA OTROKA-> ID
        Node rootXML = null;
        if (doc != null) {
            rootXML = doc.getDocumentElement();
        }
        else{
            Toast.makeText(context,"File error",Toast.LENGTH_LONG).show();

        }
        NodeList allDates = null;
        if (doc != null) {
            allDates = doc.getElementsByTagName("date");
        }
        Node id = null;
        if (rootXML != null) {
            id = rootXML.getFirstChild();
        }

        Node node;
        ArrayList<GlucoseData>timeArraylist = new ArrayList<>();
        //Z INDEXOM DNEVA POSKRBIMO, DA ZA NAZAJ NE MOREMO SPREMINJATI KONCENTRACIJE
        int indexDneva=0;
        for(int i = 0;i< allDates.getLength();i++){
            if(((Element) allDates.item(i)).getAttribute("date").equals(currentDate)){
                //USTVARIMO POMOŽNI ARRAYLIST, GLEDE NA TE VREDNOSTI PONOVNO NAPIŠEMO RELEVANTNI DEL XML

                if(attribute.equals("concentration")) {
                    System.out.println("CONCENTRATIONNNNNNNNNN");
                    System.out.println(indexDneva);
                            if(indexDneva==0) {
                                node = allDates.item(i+index);
                                ((Element)node).setAttribute("concentration",newValue);
                                //NAJDI NA KATEREM MESTU JE VREDNOST KI JO ŽELIMO SPREMENITI IN JO SPREMENI
                                glucoseDataList.get(index).setConcentration1(newValue);
                                notifyDataSetChanged();
                                System.out.println("NEW VALUE" + ((Element) node).getAttributeNode("concentration").getValue());
                            }
                    indexDneva++;





                }
                if(attribute.equals("time")){
                    node = allDates.item(i);

                    GlucoseData nl = new GlucoseData(((Element) allDates.item(i)).getAttribute("concentration"),((Element) allDates.item(i)).getAttribute("time"),((Element) allDates.item(i)).getAttribute("date"));
                    timeArraylist.add(nl);
                    id.removeChild(node);

                }
            }
        }

        if(attribute.equals("time")) {
            timeArraylist.get(index).setTime1(newValue);

        }

            for (int x = 0; x < glucoseDataList.size(); x++) {
                Collections.sort(timeArraylist, new Comparator<GlucoseData>() {
                    @Override
                    public int compare(GlucoseData o1, GlucoseData o2) {
                        System.out.println("o1" + o1.getTime1());
                        System.out.println("o2" + o2.getTime1());
                        return o1.getTime1().compareTo(o2.getTime1());
                    }
                });
                System.out.println("time ARRAY LIST" + timeArraylist.toString());

            }


            for (int array = 0; array < timeArraylist.size(); array++) {
                Element newChild = doc.createElement("date");
                newChild.setAttribute("date", timeArraylist.get(array).getDate());
                newChild.setAttribute("concentration", timeArraylist.get(array).getConcentration1());
                newChild.setAttribute("time", timeArraylist.get(array).getTime1());
                id.appendChild(newChild);
                notifyDataSetChanged();

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
            if (transformer != null) {
                transformer.transform(input, output);
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }


}

}

