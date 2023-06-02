package claudetsiang.fr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;

import claudetsiang.fr.R;
import claudetsiang.fr.model.TimeModel;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
    ArrayList<TimeModel> arrayListTime;
    Context context;

    public TimeAdapter(Context context, ArrayList<TimeModel> arrayList) {
        this.arrayListTime = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.time_layout, parent, false);
        TimeViewHolder timeViewHolder = new TimeViewHolder(view);
        return timeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        final  String time_value = arrayListTime.get(position).getTime();
        holder.time_text.setText(time_value);
    }

    @Override
    public int getItemCount() {
        return arrayListTime.size();
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView time_text;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            time_text = itemView.findViewById(R.id.time_text);

        }
    }
}
