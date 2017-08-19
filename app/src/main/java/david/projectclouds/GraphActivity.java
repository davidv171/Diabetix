package david.projectclouds;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        System.out.println("MESEC:" + mMonth);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String date = (String.valueOf(mDay) + "." + String.valueOf(mMonth) + "." + String.valueOf(mYear));
        System.out.println("DATE " + date);
        setContentView(R.layout.activity_graph);
        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.getDescription().setEnabled(false);

        //IZ XML-A PRIDOBI VSE PODATKE TER JIH DAJ V ENOTEN ARRAYLIST
        ArrayList<GlucoseData>stats = new ArrayList<>();
        ArrayList<GlucoseData>statsToday = new ArrayList<>();

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
            is = new FileInputStream(new File(GraphActivity.this.getExternalFilesDir("diabetix"), "Diabetix.xml"));
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
        NodeList allDates = doc.getElementsByTagName("date");
        ArrayList<GlucoseData> timeArraylist = new ArrayList<>();
        //Z INDEXOM DNEVA POSKRBIMO, DA ZA NAZAJ NE MOREMO SPREMINJATI KONCENTRACIJE
        int indexDneva = 0;
        for (int i = 0; i < allDates.getLength(); i++) {
            //USTVARIMO POMOŽNI ARRAYLIST, GLEDE NA TE VREDNOSTI PONOVNO NAPIŠEMO RELEVANTNI DEL XML
            if(((Element)allDates.item(i)).getAttribute("date").equals(date)) {

                System.out.println("TUKAJ SEM");
                GlucoseData gd = new GlucoseData(((Element) allDates.item(i)).getAttribute("concentration"), ((Element) allDates.item(i)).getAttribute("time"), ((Element) allDates.item(i)).getAttribute("date"));
                stats.add(gd);
            }

        }
        for(int i = 0;i< stats.size();i++){
            //SPREMENI 01:30 v 01.30, ZA LAŽJO PRETVORBO V FLOAT
            //VZAMI VSAK TIME IZ STATS, PRETVORI GA, VSTAVI GA NAZAJ
            String replacement = stats.get(i).getTime1();
            replacement = replacement.replaceAll(":",".");

            System.out.println("RPL" + replacement);
            stats.get(i).setTime1(replacement);

        }

        List<Entry> entries = new ArrayList<Entry>();

        for(int i = 0;i<stats.size();i++){
            entries.add(new Entry(Float.valueOf(stats.get(i).getTime1()),Float.valueOf(stats.get(i).getConcentration1())));
        }

        LineDataSet dataSet = new LineDataSet(entries, date); // add entries to dataset
        dataSet.setColor(R.color.colorPrimary);
        dataSet.setValueTextColor(R.color.colorPrimary);
        dataSet.setFillColor(R.color.colorPrimary);
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextColor(R.color.colorPrimary);

        chart.setData(lineData);
    }
}
