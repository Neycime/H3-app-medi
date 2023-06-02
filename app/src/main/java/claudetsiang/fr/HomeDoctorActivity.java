package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.UtilsService.SharedPreferenceClass;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeDoctorActivity extends AppCompatActivity {
    private LinearLayout logout_action,planning_action, profile_action, my_rdv_action;
    TextView text_name, date;
    CircleImageView profile_image;
    String token;
    int id_actor;
    SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_doctor);

        text_name = findViewById(R.id.text_name);
        date = findViewById(R.id.date);
        profile_image = findViewById(R.id.profile_image);
        logout_action = findViewById(R.id.logout_action);
        planning_action = findViewById(R.id.planning_action);
        profile_action = findViewById(R.id.profile_action);
        my_rdv_action = findViewById(R.id.my_rdv_action);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        id_actor = sharedPreferenceClass.getValue_int("id_actor");
        token = sharedPreferenceClass.getValue_string("token");
        logout_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferenceClass.clear();
                startActivity(new Intent(HomeDoctorActivity.this, LoginActivity.class));
                finish();
            }
        });

        planning_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeDoctorActivity.this, PlanningDoctorActivity.class));
                finish();
            }
        });

        profile_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeDoctorActivity.this, ProfileDoctorPageActivity.class));
                finish();
            }
        });

        my_rdv_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeDoctorActivity.this, MyAppointmentListDoctor.class));
                finish();
            }
        });

        fetchDataCurrentUser(id_actor);
    }

    public void fetchDataCurrentUser(int id_actor){

        String API = getResources().getString(R.string.BASE_URL) + "/api/v1/actor/"+id_actor;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")) {
                                JSONObject jsonObject = response.getJSONObject("data");
                                text_name.setText(jsonObject.getString("name"));
                                date.setText(jsonObject.getString("date"));
                                Glide.with(getApplicationContext())
                                        .load(jsonObject.getString("avatar"))
                                        .into(profile_image);
                                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }catch(JSONException je){
                            je.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        NetworkResponse response = error.networkResponse;
                        if(error instanceof ServerError && response != null){
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject obj = new JSONObject(res);
                                //Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
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