package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.Adapters.AppointmentAdapter;
import claudetsiang.fr.Adapters.DoctorListAdapter;
import claudetsiang.fr.UtilsService.SharedPreferenceClass;
import claudetsiang.fr.model.AppointmentItemModel;

public class MyAppointmentListDoctor extends AppCompatActivity {
    ImageView btn_back;
    RecyclerView recyclerViewAppointment;
    TextView no_data_text;
    ProgressBar progress_bar;
    AppointmentAdapter appointmentAdapter;
    private MyAppointmentListDoctor activity;
    ArrayList<AppointmentItemModel> arrayList;
    private SharedPreferenceClass sharedPreferenceClass;
    String token;
    int id_actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment_list_doctor);

        this.activity = this;
        btn_back = findViewById(R.id.btn_back);
        progress_bar = findViewById(R.id.progress_bar);
        no_data_text = findViewById(R.id.no_data_text);
        recyclerViewAppointment = findViewById(R.id.recycler_view_appointment);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        id_actor = sharedPreferenceClass.getValue_int("id_actor");
        token = sharedPreferenceClass.getValue_string("token");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAppointmentListDoctor.this, HomeDoctorActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        fetchAllAppointmentByDoctor(id_actor);
    }

    public void fetchAllAppointmentByDoctor(int id_actor){
        arrayList = new ArrayList<>();
        String API = getResources().getString(R.string.BASE_URL) + "/api/v1/appointment/patient/" + id_actor;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                JSONArray jsonArray = response.getJSONArray("data");
                                //Toast.makeText(activity, response.toString(), Toast.LENGTH_SHORT).show();
                                if(jsonArray.length() > 0){
                                    for(int i = 0; i < jsonArray.length(); i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        //Toast.makeText(activity, String.valueOf(jsonObject.getInt("id_actor")), Toast.LENGTH_SHORT).show();
                                        AppointmentItemModel appointmentItemModel = new AppointmentItemModel(
                                                jsonObject.getInt("id_appointment"),
                                                jsonObject.getJSONObject("date").getString("date"),
                                                jsonObject.getJSONObject("time").getString("time"),
                                                jsonObject.getJSONObject("patient").getString("avatar"),
                                                jsonObject.getJSONObject("patient").getString("name")

                                        );

                                        arrayList.add(appointmentItemModel);
                                    }
                                    if(arrayList.size() > 0){
                                        no_data_text.setVisibility(View.GONE);
                                    }
                                    recyclerViewAppointment.setLayoutManager(new LinearLayoutManager(activity));
                                    recyclerViewAppointment.setHasFixedSize(true);
                                    appointmentAdapter = new AppointmentAdapter(activity, arrayList);
                                    recyclerViewAppointment.setAdapter(appointmentAdapter);



                                }else{
                                    no_data_text.setVisibility(View.VISIBLE);
                                }
                            }
                        }catch(JSONException je){
                            je.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                        NetworkResponse response = error.networkResponse;
                        if(error instanceof ServerError && response != null){
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject obj = new JSONObject(res);
                                Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}