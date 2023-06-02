package claudetsiang.fr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import claudetsiang.fr.UtilsService.SharedPreferenceClass;

public class CustomeDialog extends AppCompatDialogFragment {
    private TextView input_date;
    private TextView input_time;
    private  CustomeDialogListerner listerner;
    private SharedPreferenceClass sharedPreferenceClass;
    String token;
    int id_actor;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custome_layout_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Ajouter une date")
                .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String date_value = input_date.getText().toString();
                        String time_value = input_time.getText().toString();
                        if(TextUtils.isEmpty(date_value) || TextUtils.isEmpty(time_value)){
                            Toast.makeText(getContext(), "Tous les champs sont obligatoire", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), date_value + " | " + time_value, Toast.LENGTH_SHORT).show();
                            addDateTime(date_value,time_value) ;
                        }

                        listerner.applyTexts(date_value, time_value);
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

         input_date = view.findViewById(R.id.input_date);
         input_time = view.findViewById(R.id.input_time);

         sharedPreferenceClass = new SharedPreferenceClass(getContext());
         id_actor = sharedPreferenceClass.getValue_int("id_actor");
         token = sharedPreferenceClass.getValue_string("token");


        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                      getContext(), android.R.style.Theme_Holo_Light_Dialog
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

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listerner = (CustomeDialogListerner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    public interface CustomeDialogListerner{
        void applyTexts(String date, String time);
    }

    private void addDateTime(String date_value, String time_value) {
        HashMap body = new HashMap<>();
        body.put("id_actor",id_actor);
        body.put("date_value",date_value);
        body.put("time_value",time_value);



        //final String API_REGISTER = "https://androidapi.herokuapp.com/api/v1/auth/login";
        final String API = "http://10.0.2.2:5000/api/v1/appointment/doctor-plannig/add-date-time";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(!response.getBoolean("success")){
                                Toast.makeText(getActivity(), "Enregistement réussie", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                //headers.put("Authorization",token);

                return body;
            }
        };

        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

}
