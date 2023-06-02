package claudetsiang.fr.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import claudetsiang.fr.AppointmentPlanning;
import claudetsiang.fr.R;
import claudetsiang.fr.interfaces.RecyclerViewTimeClickListener;
import claudetsiang.fr.model.PlanningModel;
import claudetsiang.fr.model.TimeModel;

public class AppointementPlanningAdapter extends RecyclerView.Adapter<AppointementPlanningAdapter.MyViewHolder>{
    ArrayList<PlanningModel> arrayList;
    Context context;
    //final private RecyclerViewClickListener clickListener;
    ArrayList<TimeModel> arrayListTimeData;
    public AppointementPlanningAdapter(Context context, ArrayList<PlanningModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.planning_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        myViewHolder.add_time.setVisibility(View.GONE);
        myViewHolder.edit_date.setVisibility(View.GONE);
        myViewHolder.delete_date.setVisibility(View.GONE);


        myViewHolder.arrow_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myViewHolder.time_container.getVisibility() == View.GONE){
                    myViewHolder.time_container.setVisibility(View.VISIBLE);
                    myViewHolder.arrow_bottom.setVisibility(View.GONE);
                    myViewHolder.arrow_top.setVisibility(View.VISIBLE);
                }
            }
        });


        myViewHolder.arrow_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myViewHolder.time_container.getVisibility() == View.VISIBLE){
                    myViewHolder.time_container.setVisibility(View.GONE);
                    myViewHolder.arrow_bottom.setVisibility(View.VISIBLE);
                    myViewHolder.arrow_top.setVisibility(View.GONE);
                }
            }
        });


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointementPlanningAdapter.MyViewHolder holder, int position) {
        final  String date_value = arrayList.get(position).getDate();
        JSONArray arrayListTime = arrayList.get(position).getAsso_date_times();
        arrayListTimeData = new ArrayList<>();


        holder.date.setText(date_value);

        for(int i = 0; i < arrayListTime.length(); i++){
            JSONObject jsonObject = null;
            try {
                jsonObject = arrayListTime.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TimeModel timeModel = null;
            try {
                timeModel = new TimeModel(
                        jsonObject.getString("id_date"),
                        jsonObject.getJSONObject("time").getString("id_time"),
                        jsonObject.getJSONObject("time").getString("time")
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            arrayListTimeData.add(timeModel);
        }

        holder.recycler_view_time.setLayoutManager(new GridLayoutManager(context.getApplicationContext(), 3));
        holder.recycler_view_time.setHasFixedSize(true);
        AppointementTimeAdapter appointementTimeAdaptereAdapter = new AppointementTimeAdapter(context.getApplicationContext(), arrayListTimeData);
        holder.recycler_view_time.setAdapter(appointementTimeAdaptereAdapter);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView card_planning;
        ImageView arrow_bottom,arrow_top,time_view, add_time,edit_date, delete_date;
        TextView date;
        RelativeLayout time_container;
        RecyclerView recycler_view_time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_planning = itemView.findViewById(R.id.card_planning);
            arrow_bottom = itemView.findViewById(R.id.arrow_bottom);
            arrow_top = itemView.findViewById(R.id.arrow_top);
            //time_view = itemView.findViewById(R.id.time_view);
            add_time = itemView.findViewById(R.id.add_time);
            edit_date = itemView.findViewById(R.id.edit_date);
            delete_date = itemView.findViewById(R.id.delete_date);
            date = itemView.findViewById(R.id.date);
            time_container = itemView.findViewById(R.id.time_container);
            recycler_view_time = itemView.findViewById(R.id.recycler_view_time);
        }
    }
}
