package david.projectclouds;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by david on 24.6.2017.
 */

public class GlucoseDataAdapter extends RecyclerView.Adapter<GlucoseDataAdapter.MyViewHolder>{
    private List<GlucoseData>glucoseDataList;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView date;
        public TextView concentration1;
        public TextView concentration2;
        public TextView time1;
        public TextView time2;


        public MyViewHolder(View view){
            super(view);
            date = (TextView)view.findViewById(R.id.Date);
            concentration1 = (TextView)view.findViewById(R.id.Concentration1);
            concentration2 = (TextView)view.findViewById(R.id.Concentration2);
            time1 = (TextView)view.findViewById(R.id.Time1);
            time2 = (TextView)view.findViewById(R.id.Time2);

        }


    }
    public GlucoseDataAdapter(List<GlucoseData>glucoseDataList){
        this.glucoseDataList = glucoseDataList;
    }

    @Override
    public GlucoseDataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        System.out.println("VIEW TYPE" + viewType);

       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.glucose_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GlucoseDataAdapter.MyViewHolder holder, int position) {

        GlucoseData glucoseData = glucoseDataList.get(position);
            holder.date.setText(String.valueOf(glucoseData.getDate()));
            holder.concentration1.setText(glucoseData.getConcentration1());
            holder.time1.setText(glucoseData.getTime1());
            holder.time2.setText(glucoseData.getTime2());
            holder.concentration2.setText(glucoseData.getConcentration2());

        }



    @Override
    public int getItemCount() {
        return glucoseDataList.size();
    }

}
