package claudetsiang.fr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import claudetsiang.fr.R;
import claudetsiang.fr.model.AppointmentItemModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {
    ArrayList<AppointmentItemModel> arrayListAppointment;
    Context context;

    public AppointmentAdapter(Context context, ArrayList<AppointmentItemModel> arrayList) {
        this.arrayListAppointment = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.MyViewHolder holder, int position) {
        final  String profile_image = arrayListAppointment.get(position).getAvatar();
        final  String name_value = arrayListAppointment.get(position).getUsername();
        final  String date_value = arrayListAppointment.get(position).getDate();
        final  String time_value = arrayListAppointment.get(position).getTime();

        holder.text_name.setText(name_value);
        holder.date.setText(date_value);
        holder.time.setText(time_value);
        Glide.with(context)
                .load(profile_image)
                .into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return arrayListAppointment.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView text_name, date, time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            text_name = itemView.findViewById(R.id.text_name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }
    }
}
