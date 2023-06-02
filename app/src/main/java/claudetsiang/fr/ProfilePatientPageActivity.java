package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ProfilePatientPageActivity extends AppCompatActivity {
    private ImageView btn_back, photo;
    String token;
    int id_actor;
    CircleImageView profile_image;
    EditText input_name, input_email, input_address, input_tel;
    SharedPreferenceClass sharedPreferenceClass;
    Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_patient_page);
        sharedPreferenceClass = new SharedPreferenceClass(this);
        id_actor = sharedPreferenceClass.getValue_int("id_actor");
        token = sharedPreferenceClass.getValue_string("token");
        btn_back = findViewById(R.id.btn_back);
        profile_image = findViewById(R.id.profile_image);
        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_address = findViewById(R.id.input_address);
        input_tel = findViewById(R.id.input_tel);
        photo = findViewById(R.id.photo);
        btn_save = findViewById(R.id.btn_save);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePatientPageActivity.this, HomePatientActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_value = input_name.getText().toString();
                String email_value = input_email.getText().toString();
                String address_value = input_address.getText().toString();
                String tel_value = input_tel.getText().toString();
                if(TextUtils.isEmpty(name_value) || TextUtils.isEmpty(email_value)){
                    Toast.makeText(getApplicationContext(), "le nom et l'email sont obligatoire", Toast.LENGTH_SHORT).show();
                }else {
                    updateDataUser(id_actor, name_value, email_value, address_value, tel_value);
                }

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
                                input_name.setText(jsonObject.getString("name"));
                                input_email.setText(jsonObject.getString("email"));
                                if(!jsonObject.getString("address").equals("null")){
                                    input_address.setText(jsonObject.getString("address"));
                                }
                                if(!jsonObject.getString("tel").equals("null")){
                                    input_tel.setText(jsonObject.getString("tel"));
                                }

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

    public void updateDataUser(int id_actor, String name, String email, String address, String tel){
        HashMap<String, String> userData = new HashMap<>();
        userData.put("name",name);
        userData.put("email",email);
        userData.put("address",address);
        userData.put("tel",tel);

        //final String API_REGISTER = "https:///androidapi.herokuapp.com/api/v1/users/register";
        final String API = getResources().getString(R.string.BASE_URL) + "/api/v1/actor/update/"+id_actor;

        JsonObjectRequest  request = new JsonObjectRequest(Request.Method.PUT, API, new JSONObject(userData),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                //String token = response.getString("token");
                                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                                fetchDataCurrentUser(id_actor);
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
                                Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();

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