package claudetsiang.fr;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Locale;
import java.util.Map;

import claudetsiang.fr.Adapters.DoctorListAdapter;
import claudetsiang.fr.Adapters.PlanningAdapter;
import claudetsiang.fr.Adapters.TimeAdapter;
import claudetsiang.fr.UtilsService.SharedPreferenceClass;
import claudetsiang.fr.interfaces.RecyclerViewClickListener;
import claudetsiang.fr.interfaces.RecyclerViewDoctorlistClickListener;
import claudetsiang.fr.model.DoctorModel;
import claudetsiang.fr.model.PlanningModel;
import claudetsiang.fr.model.TimeModel;

public class DocltorListActivity extends AppCompatActivity implements RecyclerViewDoctorlistClickListener {
    ImageView btn_back;
    EditText input_search;
    RecyclerView recyclerViewDoctor;
    TextView no_data_text;
    ProgressBar progress_bar;

    ArrayList<DoctorModel> arrayList = new ArrayList<>();
    ArrayList<DoctorModel> arrayListCopy = new ArrayList<>();


    DoctorListAdapter doctorListAdapter;
    private  DocltorListActivity activity;
    private SharedPreferenceClass sharedPreferenceClass;
    String token;
    int id_actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docltor_list);

        this.activity = this;
        btn_back = findViewById(R.id.btn_back);
        input_search = findViewById(R.id.input_search);

        arrayListCopy = arrayList;
        sharedPreferenceClass = new SharedPreferenceClass(this);
        id_actor = sharedPreferenceClass.getValue_int("id_actor");
        token = sharedPreferenceClass.getValue_string("token");

        progress_bar = findViewById(R.id.progress_bar);
        no_data_text = findViewById(R.id.no_data_text);
        recyclerViewDoctor = findViewById(R.id.recycler_view_doctor_list);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocltorListActivity.this, HomePatientActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        fetchDoctorList();



        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                arrayListCopy = filterData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        

    }

    public void fetchDoctorList(){
        progress_bar.setVisibility(View.VISIBLE);

        String API = getResources().getString(R.string.BASE_URL) + "/api/v1/actor/doctor/list/";

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
                                        DoctorModel doctorModel = new DoctorModel(
                                                jsonObject.getString("id_actor"),
                                                jsonObject.getString("name"),
                                                jsonObject.getString("email"),
                                                jsonObject.getString("avatar"),
                                                jsonObject.getString("address"),
                                                jsonObject.getString("tel")
                                        );

                                        arrayList.add(doctorModel);
                                    }
                                    if(arrayList.size() > 0){
                                        no_data_text.setVisibility(View.GONE);
                                    }
                                    recyclerViewDoctor.setLayoutManager(new LinearLayoutManager(activity));
                                    recyclerViewDoctor.setHasFixedSize(true);
                                    doctorListAdapter = new DoctorListAdapter(activity, arrayList, DocltorListActivity.this);
                                    recyclerViewDoctor.setAdapter(doctorListAdapter);



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

    public ArrayList<DoctorModel> filterData(String searchKey){
        ArrayList<DoctorModel> arrayListFilter = new ArrayList<>();
        if(searchKey != null){
            for (DoctorModel doctorModelData: arrayList) {
                final String name = doctorModelData.getName().toLowerCase();
                if (name.contains(searchKey.toLowerCase())) {
                    arrayListFilter.add(doctorModelData);
                }
            }
            doctorListAdapter.setFilter(arrayListFilter);
        }else{
            arrayListFilter = arrayList;
        }

            return arrayListFilter;
    }


    @Override
    public void onItemClick(int position) {
            //Toast.makeText(activity, arrayListCopy.get(position).getId_actor(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DocltorListActivity.this, AppointmentPlanning.class);
            sharedPreferenceClass = new SharedPreferenceClass(this);
            sharedPreferenceClass.setValue_string("id_doctor", arrayListCopy.get(position).getId_actor());
            intent.putExtra("id_actor", arrayListCopy.get(position).getId_actor());
            startActivity(intent);
    }
}