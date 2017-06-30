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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by david on 24.6.2017.
 */

public class GlucoseDataAdapter extends RecyclerView.Adapter<GlucoseDataAdapter.MyViewHolder>{
    private List<GlucoseData>glucoseDataList;
    //CONTEXT ENABLES AN ALERTBUILDER TO BE CREATED
    //CONSTRUCTOR CALLED INSIDE GLUCOSEDATAOPERATIONS INSIDE THE METHOD PREPAREDATA

    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView concentration1;
        public TextView time1;



        public MyViewHolder(View view){
            super(view);
            concentration1 = (TextView)view.findViewById(R.id.Concentration1);
            time1 = (TextView)view.findViewById(R.id.Time1);


        }


    }
    public GlucoseDataAdapter(List<GlucoseData>glucoseDataList,Context context){
        this.glucoseDataList = glucoseDataList;
        this.context = context;
    }

    @Override
    public GlucoseDataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        System.out.println("VIEW TYPE" + viewType);

       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.glucose_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GlucoseDataAdapter.MyViewHolder holder, final int position) {
            final GlucoseData glucoseData = glucoseDataList.get(position);
            holder.concentration1.setText(glucoseData.getConcentration1());
            holder.time1.setText(glucoseData.getTime1());
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
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newValue = input.getText().toString();
                            glucoseDataList.get(position).setConcentration1(newValue);
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
                        System.out.println("GLUCOSEDATA LIST" + glucoseDataList.get(position));

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
    Collections.sort(glucoseDataList, new Comparator<GlucoseData>() {
        @Override
        public int compare(GlucoseData o1, GlucoseData o2) {
            System.out.println("o1" + o1.getTime1());
            System.out.println("o2" + o2.getTime1());
            return o1.getTime1().compareTo(o2.getTime1());
        }
    });

}
    @Override
    public int getItemCount() {
        return glucoseDataList.size();
    }

}
