package david.projectclouds;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 24.6.2017.
 */

public class GlucoseDataOperations {
    private List<GlucoseData> glucoseDataList = new ArrayList<>();

    private GlucoseDataAdapter mAdapter;

    public String getPrevTime() {
        return prevTime;
    }

    public void setPrevTime(String prevTime) {
        this.prevTime = prevTime;
    }

    public String getPrevConcentration() {
        return prevConcentration;
    }

    public void setPrevConcentration(String prevConcentration) {
        this.prevConcentration = prevConcentration;
    }

    private String prevTime;
    private String prevConcentration;
    public GlucoseDataOperations(String prevTime,String prevConcentration){
        this.prevConcentration = prevConcentration;
        this.prevTime = prevTime;
    }
    public GlucoseDataOperations(){

    }
    public void prepareGlucoseListData(RecyclerView recyclerView,Context context){

        mAdapter = new GlucoseDataAdapter(glucoseDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        //VERTIKALNI DIVIDER, MED VSAKIM ELEMENTOM JE ČRTA
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));
        GlucoseData glucoseData = new GlucoseData("12.2.2017","12","13","33","33");
        glucoseDataList.add(glucoseData);
        glucoseDataList.remove(glucoseData);

    }
    //FUNKCIJA ADD ITEM, KI JO KLIČEMO V MAINACTIVITY, KO SE PRITISNE GUMB FAB
    //PREVERI, ALI JE ZA TA DATUM ŽE DODAN KAKŠNA KONCENTRACIJA IN ČE JE, DODA NOVEGA
    //V PRIMERU DA STA ŽE DODANI DVE KONCENTRACIJI, SE PREMAKNE DRUGA NA MESTO PRVE, NA MESTO DRUGE PA PRIDE NOVA KONCENTRACIJA
    public void addItem(String time, String date,String concentration) {
        //TODO: IF ALREADY SAVED DATE SAVE TO SECOND TEXTVIEWS, ELSE SAVE TO FIRST

        boolean ujemanje = false;
        int indeksUjemanja=-1;
        for(int i = 0;i<glucoseDataList.size();i++){
            System.out.println("SIZE" + glucoseDataList.size());
            System.out.println("INDEKS" + i);
            System.out.println("DATE " + glucoseDataList.get(i).getDate());
            System.out.println("PRIMERJALNI DATE " + glucoseDataList.get(glucoseDataList.size()-1).getDate());
            if(glucoseDataList.get(i).getDate().equals(date)) {
                indeksUjemanja = i;
                ujemanje = true;
            }

        }
        GlucoseData glucoseData = new GlucoseData(date, null,null, null, null);

        GlucoseData glucoseData2;

        System.out.println("PODATKI: " + date);

        if(!ujemanje) {
            glucoseData = new GlucoseData(date, concentration,time, null, null);
            glucoseData.setConcentration1(concentration);
            glucoseData.setTime1(time);
            glucoseDataList.add(glucoseData);
            mAdapter.notifyDataSetChanged();

        }
        else{
            System.out.println("UJEMANJE");
            System.out.println("PREV TIME" + prevConcentration);
            glucoseData2 = new GlucoseData(date,prevConcentration,prevTime,concentration,time);
            System.out.println("GET CONCENTRATION" + glucoseData.getConcentration1());
            System.out.println("xD" + glucoseDataList.get(indeksUjemanja).getConcentration1());
            System.out.println("DATE" + date);

            glucoseDataList.add(glucoseData2);
            glucoseDataList.remove(indeksUjemanja);
            mAdapter.notifyItemRemoved(indeksUjemanja);
            mAdapter.notifyDataSetChanged();

            System.out.println("ITEM COUNT" + mAdapter.getItemCount());
        }
    }
    //METODA S KATERO TRI INTEGERJE PRETVORIMO V STRING, PRIPRAVLJEN NA TEXTVIEW
    public String dateAppender(int day, int month, int year){
        String date = (String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year));
        return date;
    }
}
