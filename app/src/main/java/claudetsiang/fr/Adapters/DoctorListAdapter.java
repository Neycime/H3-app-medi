package claudetsiang.fr.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import claudetsiang.fr.DocltorListActivity;
import claudetsiang.fr.R;
import claudetsiang.fr.interfaces.RecyclerViewDoctorlistClickListener;
import claudetsiang.fr.model.DoctorModel;
import de.hdodenhof.circleimageview.CircleImageView;



public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorlisViewtHolder> {
    Context context;
    ArrayList<DoctorModel> arrayList;
    final private RecyclerViewDoctorlistClickListener clickListener;
    public DoctorListAdapter(Context context, ArrayList<DoctorModel> arrayList, RecyclerViewDoctorlistClickListener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;


    }

    @NonNull
    @Override
    public DoctorlisViewtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_list_item, parent, false);

        DoctorlisViewtHolder doctorlisViewtHolder = new DoctorlisViewtHolder(view);
        return doctorlisViewtHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorlisViewtHolder holder, int position) {
        final  String profile_image = arrayList.get(position).getAvatar();
        final  String name_value = arrayList.get(position).getName();

        holder.doctor_name.setText(name_value);
        Glide.with(context)
                .load(profile_image)
                .into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class DoctorlisViewtHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView doctor_name;
        public DoctorlisViewtHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            doctor_name = itemView.findViewById(R.id.doctor_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void setFilter(ArrayList<DoctorModel> arrayListData) {
        arrayList = arrayListData;
        notifyDataSetChanged();
    }
}
