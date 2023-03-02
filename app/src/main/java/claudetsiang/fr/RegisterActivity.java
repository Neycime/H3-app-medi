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
import com.android.volley.NetworkError;
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

public class RegisterActivity extends AppCompatActivity {
    private TextView login_text;
    private EditText input_username, input_email, input_password, input_passwordConfirm;
    private Button btn_register;
    private ProgressBar progress_bar;

    private String username_value, email_value, password_value, passwordConfirm_value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        login_text = findViewById(R.id.login_text);
        input_username = findViewById(R.id.input_username);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_passwordConfirm = findViewById(R.id.input_passwordConfirm);
        progress_bar = findViewById(R.id.progress_bar);
        btn_register = findViewById(R.id.btn_register);

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username_value = input_username.getText().toString();
                email_value = input_email.getText().toString();
                password_value = input_password.getText().toString();
                passwordConfirm_value = input_passwordConfirm.getText().toString();

                if(TextUtils.isEmpty(username_value) || TextUtils.isEmpty(email_value) ||
                   TextUtils.isEmpty(password_value) || TextUtils.isEmpty(passwordConfirm_value)){
                    Toast.makeText(RegisterActivity.this, "Tous les champs sont obligatoire", Toast.LENGTH_SHORT).show();
                }else if(password_value.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Mot de passe trop court, minimum 6 caractères", Toast.LENGTH_SHORT).show();
                }else if(!password_value.equals(passwordConfirm_value)){
                    Toast.makeText(RegisterActivity.this, "Les deux mot de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(view);
                }
            }
        });
    }

    public void registerUser(View view){
        progress_bar.setVisibility(view.VISIBLE);
        HashMap<String, String> userData = new HashMap<>();
        userData.put("username",username_value);
        userData.put("email",email_value);
        userData.put("password",password_value);
        userData.put("passwordConfirm",passwordConfirm_value);

        final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/users/register";

        JsonObjectRequest  request = new JsonObjectRequest(Request.Method.POST, API_REGISTER, new JSONObject(userData),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            try {
                                if(response.getBoolean("success")){
                                    //String token = response.getString("token");
                                    Toast.makeText(RegisterActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
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
                                Toast.makeText(RegisterActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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