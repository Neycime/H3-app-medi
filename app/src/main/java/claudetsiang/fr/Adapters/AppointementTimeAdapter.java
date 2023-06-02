package claudetsiang.fr.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.R;
import claudetsiang.fr.UtilsService.SharedPreferenceClass;
import claudetsiang.fr.interfaces.RecyclerViewDoctorlistClickListener;
import claudetsiang.fr.interfaces.RecyclerViewTimeClickListener;
import claudetsiang.fr.model.TimeModel;

public class AppointementTimeAdapter extends RecyclerView.Adapter<AppointementTimeAdapter.TimeViewHolder> implements RecyclerViewTimeClickListener {
    ArrayList<TimeModel> arrayListTime;
    Context context;
    SharedPreferenceClass sharedPreferenceClass;
    SharedPreferences sharedPreferences;
    String token,id_doctor;
    int id_actor;


    public AppointementTimeAdapter(Context context, ArrayList<TimeModel> arrayList) {
        this.arrayListTime = arrayList;
        this.context = context;
        sharedPreferenceClass = new SharedPreferenceClass(context);
        token = sharedPreferenceClass.getValue_string("token");
        id_actor = sharedPreferenceClass.getValue_int("id_actor");
        id_doctor = sharedPreferenceClass.getValue_string("id_doctor");

    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.time_layout, parent, false);
        TimeViewHolder timeViewHolder = new TimeViewHolder(view);
        return timeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointementTimeAdapter.TimeViewHolder holder, int position) {
        final  String time_value = arrayListTime.get(position).getTime();
        holder.time_text.setText(time_value);
    }

    @Override
    public int getItemCount() {
        return arrayListTime.size();
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(context.getApplicationContext(), "id_doctor" + id_doctor, Toast.LENGTH_SHORT).show();
        String id_date = arrayListTime.get(position).getId_date();
        String id_time = arrayListTime.get(position).getId_time();

        //Toast.makeText(context.getApplicationContext(), arrayListTime.get(position).getTime(), Toast.LENGTH_SHORT).show();
        addAppointement(id_actor, id_doctor,id_date, id_time);
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView time_text;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            time_text = itemView.findViewById(R.id.time_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(getAdapterPosition());
                }
            });
        }


    }

    public  void  addAppointement(int id_actor, String id_doctor, String id_date, String id_time){
        HashMap body = new HashMap<>();
        body.put("id_actor",id_actor);
        body.put("id_doctor",id_doctor);
        body.put("id_date",id_date);
        body.put("id_time",id_time);


        //final String API = "BASE_URL/api/v1/auth/login";
        //final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/auth/login";
        final String API = context.getResources().getString(R.string.BASE_URL) + "/api/v1/appointment/add-appointment";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

                            }
                        }catch(JSONException je){
                            je.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if(error instanceof ServerError && response != null){
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject obj = new JSONObject(res);
                                //Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            }catch(JSONException | UnsupportedEncodingException je){
                                je.printStackTrace();
                            }
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization",token);

                return headers;
            }
        };

        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
}
