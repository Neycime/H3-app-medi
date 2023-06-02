package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.UtilsService.SharedPreferenceClass;

public class LoginActivity extends AppCompatActivity {
    private TextView register_text;
    private EditText input_email, input_password;
    private Button btn_login;
    private ProgressBar progress_bar;
    private String email_value, password_value;
    //store
    private SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register_text = findViewById(R.id.register_text);

        register_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        progress_bar = findViewById(R.id.progress_bar);
        btn_login = findViewById(R.id.btn_login);

        //Initialisation du store
        sharedPreferenceClass = new SharedPreferenceClass(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email_value = input_email.getText().toString();
                password_value = input_password.getText().toString();

                if(TextUtils.isEmpty(email_value) || TextUtils.isEmpty(password_value)){
                    Toast.makeText(LoginActivity.this, "Tous les champs sont obligatoire", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(view);
                }
            }
        });
    }

    public void loginUser(View view){
        progress_bar.setVisibility(view.VISIBLE);
        HashMap<String, String> userData = new HashMap<>();
        userData.put("email",email_value);
        userData.put("password",password_value);

        //final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/auth/login";
        final String API = getResources().getString(R.string.BASE_URL) + "/api/v1/auth/login";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API, new JSONObject(userData),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                String token = response.getString("token");
                                JSONObject currentUser = response.getJSONObject("actor");
                                int id_actor = currentUser.getInt("id_actor");
                                int feature = currentUser.getInt("id_feature");
                                sharedPreferenceClass.setValue_string("token", token);
                                sharedPreferenceClass.setValue_int("id_actor", id_actor);
                                sharedPreferenceClass.setValue_int("feature", feature);
                                Toast.makeText(LoginActivity.this, response.getString("token"), Toast.LENGTH_SHORT).show();
                                if(feature == 1){
                                    startActivity(new Intent(LoginActivity.this, HomePatientActivity.class));
                                    finish();
                                }else if(feature == 2){
                                    startActivity(new Intent(LoginActivity.this, HomeDoctorActivity.class));
                                    finish();
                                }
                            }
                            progress_bar.setVisibility(view.GONE);
                        }catch(JSONException je){
                            je.printStackTrace();
                            progress_bar.setVisibility(view.GONE);
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
                                Toast.makeText(LoginActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                progress_bar.setVisibility(view.GONE);
                            }catch(JSONException | UnsupportedEncodingException je){
                                je.printStackTrace();
                                progress_bar.setVisibility(view.GONE);
                            }
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");

                return userData;
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