package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.Adapters.PlanningAdapter;
import claudetsiang.fr.Adapters.TimeAdapter;
import claudetsiang.fr.UtilsService.SharedPreferenceClass;
import claudetsiang.fr.interfaces.RecyclerViewClickListener;
import claudetsiang.fr.model.PlanningModel;
import claudetsiang.fr.model.TimeModel;

public class PlanningDoctorActivity extends AppCompatActivity  implements RecyclerViewClickListener {
    private ImageView btn_back;
    FloatingActionButton add_date;
    private TextView input_date,input_time;
    private CustomeDialog.CustomeDialogListerner listerner;
    private SharedPreferenceClass sharedPreferenceClass;
    String token;
    int id_actor;
    private PlanningDoctorActivity activity;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    PlanningAdapter planningAdapter;
    TimeAdapter timeAdapter;

    RecyclerView recyclerViewPlanning;
    RecyclerView recyclerViewTime;
    TextView no_data_text;
    ProgressBar progress_bar;
    ArrayList<PlanningModel> arrayList;
    ArrayList<TimeModel> arrayListTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning_doctor);

        this.activity = this;
        btn_back = findViewById(R.id.btn_back);
        add_date = findViewById(R.id.add_date);


        sharedPreferenceClass = new SharedPreferenceClass(this);
        id_actor = sharedPreferenceClass.getValue_int("id_actor");
        token = sharedPreferenceClass.getValue_string("token");


        progress_bar = findViewById(R.id.progress_bar);
        no_data_text = findViewById(R.id.no_data_text);
        recyclerViewPlanning = findViewById(R.id.recycler_view_planning);

        recyclerViewPlanning.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPlanning.setHasFixedSize(true);

        LayoutInflater inflater = getLayoutInflater();
        View planning_item = inflater.inflate(R.layout.planning_item, null);
        recyclerViewTime = planning_item.findViewById(R.id.recycler_view_time);

        recyclerViewTime.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewTime.setHasFixedSize(true);


        getPlanning();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlanningDoctorActivity.this, HomeDoctorActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    public void getPlanning(){
        arrayList = new ArrayList<>();
        arrayListTime = new ArrayList<>();
        progress_bar.setVisibility(View.VISIBLE);
        String API = getResources().getString(R.string.BASE_URL) +  "/api/v1/date_time/date-time-list/"+id_actor;

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

                                        /*for(int j = 0; j < planningModel.getAsso_date_times().length(); j++){
                                            JSONObject jsonObjectTime = planningModel.getAsso_date_times().getJSONObject(j);

                                            TimeModel timeModel = new TimeModel(
                                                    jsonObjectTime.getInt("id_date"),
                                                    jsonObjectTime.getJSONObject("time").getInt("id_time"),
                                                    jsonObjectTime.getJSONObject("time").getString("time")
                                            );
                                            arrayListTime.add(timeModel);
                                        }*/
                                        arrayList.add(planningModel);
                                    }
                                    if(arrayList.size() > 0){
                                        no_data_text.setVisibility(View.GONE);
                                    }

                                    planningAdapter = new PlanningAdapter(activity, arrayList, PlanningDoctorActivity.this);
                                    recyclerViewPlanning.setAdapter(planningAdapter);

                                    /*timeAdapter = new TimeAdapter(activity, arrayListTime);
                                    recyclerViewTime.setAdapter(planningAdapter);*/

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

    public void getTime(int id_date) {

        arrayListTime = new ArrayList<>();
        String API = getResources().getString(R.string.BASE_URL) + "/api/v1/date_time/time-list/" + id_date;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                Toast.makeText(activity, response.toString(), Toast.LENGTH_SHORT).show();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    TimeModel timeModel = new TimeModel(
                                            jsonObject.getString("id_date"),
                                            jsonObject.getJSONObject("time").getString("id_time"),
                                            jsonObject.getJSONObject("time").getString("time")
                                    );
                                    arrayListTime.add(timeModel);
                                }

                                timeAdapter = new TimeAdapter(activity, arrayListTime);
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject obj = new JSONObject(res);
                                Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException | UnsupportedEncodingException je) {
                                je.printStackTrace();
                            }
                        }
                        progress_bar.setVisibility(View.GONE);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);

                return headers;
            };
        };

        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    public void openDialog() {
        //CustomeDialog customeDialog = new CustomeDialog();
        //customeDialog.show(getSupportFragmentManager(), "Custome Dialog");

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custome_layout_dialog, null);

        input_date = view.findViewById(R.id.input_date);
        input_time = view.findViewById(R.id.input_time);

        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PlanningDoctorActivity.this, android.R.style.Theme_Holo_Light_Dialog
                        ,dateSetListener, year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year+"/"+month+"/"+day;
                input_date.setText(date);
            }
        };

        input_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int hrs = calendar.get(Calendar.HOUR);
                final int mins = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(PlanningDoctorActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog
                        ,timeSetListener, hrs,mins, true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });
        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hrs, int mins) {
                String time = hrs+":"+mins;
                input_time.setText(time);
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(activity)
        .setView(view)
        .setTitle("Ajouter une date")
        .setPositiveButton("Valider",null)
        .setNegativeButton("Annuler",  null)
        .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date_value = input_date.getText().toString();
                        String time_value = input_time.getText().toString();
                        if(TextUtils.isEmpty(date_value) || TextUtils.isEmpty(time_value)){
                            Toast.makeText(activity,"Tous les champs sont obligatoire", Toast.LENGTH_SHORT).show();
                        }else{
                           //oast.makeText(activity, date_value + " | " + time_value, Toast.LENGTH_SHORT).show();
                            addDateTime(date_value,time_value) ;
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();

    }

    //Fonctionne mais pas utilisé
    public void openTimeViewDialog(int id_date, String date){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.planning_doctor_time, null);
        //getTime(id_date);
        recyclerViewTime = view.findViewById(R.id.recycler_view_planning_time);

        recyclerViewTime.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewTime.setHasFixedSize(true);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(date)
                .setNegativeButton("Fermer",  null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {


                Toast.makeText(activity, "date id" + id_date, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void openAddTimeToDateDialog(int id_date, String date){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custome_layout_dialog, null);


        input_date = view.findViewById(R.id.input_date);
        input_date.setVisibility(View.GONE);
        input_time = view.findViewById(R.id.input_time);

        input_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int hrs = calendar.get(Calendar.HOUR);
                final int mins = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(PlanningDoctorActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog
                        ,timeSetListener, hrs,mins, true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });
        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hrs, int mins) {
                String time = hrs+":"+mins;
                input_time.setText(time);
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(date)
                .setPositiveButton("Valider",null)
                .setNegativeButton("Annuler",  null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String time_value = input_time.getText().toString();
                        if(TextUtils.isEmpty(time_value)){
                            Toast.makeText(activity,"Champ obligatoire", Toast.LENGTH_SHORT).show();
                        }else{
                            //oast.makeText(activity, date_value + " | " + time_value, Toast.LENGTH_SHORT).show();
                            addTimeToDate(id_date, time_value) ;
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    public void openUpdateDialog(int id_date, String date){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custome_layout_dialog, null);


        input_date = view.findViewById(R.id.input_date);
        input_date.setText(date);
        input_time = view.findViewById(R.id.input_time);
        input_time.setVisibility(View.GONE);

        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PlanningDoctorActivity.this, android.R.style.Theme_Holo_Light_Dialog
                        ,dateSetListener, year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = year+"/"+month+"/"+day;
                input_date.setText(date);
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setTitle("Editer une date")
                .setPositiveButton("Valider",null)
                .setNegativeButton("Annuler",  null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date_value = input_date.getText().toString();
                        if(TextUtils.isEmpty(date_value)){
                            Toast.makeText(activity,"Champ obligatoire", Toast.LENGTH_SHORT).show();
                        }else{
                            //oast.makeText(activity, date_value + " | " + time_value, Toast.LENGTH_SHORT).show();
                            updateDate(id_date, date_value) ;
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void openDeleteDialog(int id_date, int position ){
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Voulez-vous supprimer cette date ?")
                .setPositiveButton("OUI",null)
                .setNegativeButton("NON",  null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            deleteDate(id_date, position); ;
                            dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void addDateTime(String date_value, String time_value) {
        HashMap body = new HashMap<>();
        body.put("id_actor",id_actor);
        body.put("date_value",date_value);
        body.put("time_value",time_value);



        //final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/auth/login";
        final String API = getResources().getString(R.string.BASE_URL) + "/api/v1/date_time/doctor-plannig/add-date-time";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                //Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                getPlanning();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void addTimeToDate(int id_date, String time_value){

        HashMap body = new HashMap<>();
        body.put("id_date",id_date);
        body.put("time_value",time_value);

//final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/auth/login";
        final String API = getResources().getString(R.string.BASE_URL) + "/api/v1/date_time/doctor-plannig/add-time-to-date";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                //Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                getPlanning();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private  void updateDate(int id_date, String date_value){
        HashMap body = new HashMap<>();
        body.put("date_value",date_value);

        //final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/auth/login";
        final String API = getResources().getString(R.string.BASE_URL) + "/api/v1/date_time/doctor-plannig/update/"+id_date;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, API, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                getPlanning();
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void deleteDate(int id_date, int position){
        //final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/auth/login";
        final String API = getResources().getString(R.string.BASE_URL) + "/api/v1/date_time/doctor-plannig/delete/"+id_date;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                arrayList.remove(position);
                                planningAdapter.notifyItemRemoved(position);
                                //getPlanning();
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

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(activity, "position" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        openDeleteDialog(arrayList.get(position).getId_date(), position);
        //Toast.makeText(activity, "date " + arrayList.get(position).getDate(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewButtonClick(int position) {
        openTimeViewDialog(arrayList.get(position).getId_date(), arrayList.get(position).getDate());
    }

    @Override
    public void onAddButtonClick(int position) {
        openAddTimeToDateDialog(arrayList.get(position).getId_date(), arrayList.get(position).getDate());
        //Toast.makeText(activity, "position" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditButtonClick(int position) {
        openUpdateDialog(arrayList.get(position).getId_date(), arrayList.get(position).getDate());
        //Toast.makeText(activity, "date " + arrayList.get(position).getDate(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteButtonClick(int position) {
        openDeleteDialog(arrayList.get(position).getId_date(), position);
        //Toast.makeText(activity, "position" + position, Toast.LENGTH_SHORT).show();
    }
}
