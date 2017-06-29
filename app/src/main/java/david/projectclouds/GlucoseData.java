package david.projectclouds;

import android.opengl.GLU;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by david on 24.6.2017.
 */

public class GlucoseData {
    //DATUM in 2 KONCENTRACIJI, KOLIKOR JIH DOVOLIMO UPORABNIKU VNEST
    //VSAKA KONCENTRACIJA IMA TUDI SOVPADAJOČ ČAS VNOSA, KI SE AVTOMATSKO ZABELEŽI V MAIN ACTIVITY IN DA NA PRAVILNI
    //TEXTVIEW
    private String date;
    private String concentration1;
    private String time1;






    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }




    public String getConcentration1() {
        return concentration1;
    }

    public void setConcentration1(String concentration1) {
        this.concentration1 = concentration1;
    }

    public GlucoseData(String date, String concentration1,String time1) {
        this.date = date;
        this.concentration1 = concentration1;
        this.time1 = time1;


    }

    public GlucoseData(String concentration1,String time1) {
        this.concentration1 = concentration1;
        this.time1 = time1;


    }

    public String getDate(){

        return this.date;
    }

    public void setDate(String date){
       this.date = date;
    }


}
