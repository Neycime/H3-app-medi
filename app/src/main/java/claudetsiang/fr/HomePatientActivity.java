package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.Adapters.DoctorListAdapter;
import claudetsiang.fr.UtilsService.SharedPreferenceClass;
import claudetsiang.fr.model.DoctorModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomePatientActivity extends AppCompatActivity {
    private LinearLayout logout_action,rdv_action, profile_action;
    TextView text_name, date;
    CircleImageView profile_image;
    String token;
    int id_actor;
    ImageView btn_back;
    SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_patient);

        text_name = findViewById(R.id.text_name);
        date = findViewById(R.id.date);
        profile_image = findViewById(R.id.profile_image);
        logout_action = findViewById(R.id.logout_action);
        rdv_action = findViewById(R.id.rdv_action);
        profile_action = findViewById(R.id.profile_action);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        id_actor = sharedPreferenceClass.getValue_int("id_actor");
        token = sharedPreferenceClass.getValue_string("token");
        logout_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferenceClass.clear();
                startActivity(new Intent(HomePatientActivity.this, LoginActivity.class));
                finish();
            }
        });

        rdv_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePatientActivity.this, DocltorListActivity.class));
                finish();
            }
        });

        profile_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePatientActivity.this, ProfilePatientPageActivity.class));
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