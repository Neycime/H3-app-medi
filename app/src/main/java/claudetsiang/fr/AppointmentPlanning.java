package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.Adapters.AppointementPlanningAdapter;
import claudetsiang.fr.Adapters.PlanningAdapter;
import claudetsiang.fr.Adapters.TimeAdapter;
import claudetsiang.fr.UtilsService.SharedPreferenceClass;
import claudetsiang.fr.interfaces.RecyclerViewTimeClickListener;
import claudetsiang.fr.model.PlanningModel;
import claudetsiang.fr.model.TimeModel;

public class AppointmentPlanning extends AppCompatActivity{
    private ImageView btn_back;
    FloatingActionButton add_date;
    private TextView input_date,input_time;
    private SharedPreferenceClass sharedPreferenceClass;
    String token, id_actor;
    //int id_actor;
    private AppointmentPlanning activity;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    AppointementPlanningAdapter appointementPlanningAdapter;

    RecyclerView recyclerViewAppointmentPlanning;

    TextView no_data_text;
    ProgressBar progress_bar;
    ArrayList<PlanningModel> arrayList;
    ArrayList<TimeModel> arrayListTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_planning);

        this.activity = this;
        btn_back = findViewById(R.id.btn_back);


        sharedPreferenceClass = new SharedPreferenceClass(this);
        //id_actor = sharedPreferenceClass.getValue_int("id_actor");
        token = sharedPreferenceClass.getValue_string("token");
        id_actor = sharedPreferenceClass.getValue_string("id_doctor");


        progress_bar = findViewById(R.id.progress_bar);
        no_data_text = findViewById(R.id.no_data_text);

        recyclerViewAppointmentPlanning = findViewById(R.id.recycler_view_planning);
        recyclerViewAppointmentPlanning.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAppointmentPlanning.setHasFixedSize(true);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentPlanning.this, DocltorListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //Bundle bundle = getIntent().getExtras();
        //String id_actor = bundle.getString("id_actor");
        //Toast.makeText(activity, "id_actor " + id_actor, Toast.LENGTH_SHORT).show();

        getDoctorPlanningById(id_actor);
    }

    public void getDoctorPlanningById(String id_actor){
        arrayList = new ArrayList<>();
        arrayListTime = new ArrayList<>();
        progress_bar.setVisibility(View.VISIBLE);

        String API = getResources().getString(R.string.BASE_URL) + "/api/v1/date_time/date-time-list/"+id_actor;

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

                                        PlanningModel planningModel = new PlanningModel(
                                                jsonObject.getInt("id_date"),
                                                jsonObject.getJSONObject("date").getString("date"),
                                                jsonObject.getJSONObject("date").getJSONArray("asso_date_times")
                                        );
                                        arrayList.add(planningModel);
                                    }
                                    if(arrayList.size() > 0){
                                        no_data_text.setVisibility(View.GONE);
                                    }

                                    appointementPlanningAdapter = new AppointementPlanningAdapter((Context) activity, arrayList);
                                    recyclerViewAppointmentPlanning.setAdapter(appointementPlanningAdapter);

                                }else{
                                    no_data_text.setVisibility(View.VISIBLE);
                                }







                            }
                            progress_bar.setVisibility(View.GONE);
                        }catch(JSONException je){
                            je.printStackTrace();
                            progress_bar.setVisibility(View.GONE);
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
                        progress_bar.setVisibility(View.GONE);
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