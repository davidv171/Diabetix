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
    public void removeItem(int index){
        glucoseDataList.remove(index);
        mAdapter.notifyDataSetChanged();
    }
}
