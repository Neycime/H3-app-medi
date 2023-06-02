package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import claudetsiang.fr.UtilsService.SharedPreferenceClass;

public class MainActivity extends AppCompatActivity {
    private Button btn_login, btn_register;
    private SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        sharedPreferenceClass = new SharedPreferenceClass(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On appel le store ayant la clé current_user qui a été initialisé lors du login
                // et on vérifie s'il contient le token
                SharedPreferences current_user_pref = getSharedPreferences("current_user", MODE_PRIVATE);
                if(current_user_pref.contains("token") && sharedPreferenceClass.getValue_int("feature") == 1){
                    startActivity(new Intent(MainActivity.this, HomePatientActivity.class));
                    finish();
                }else if(current_user_pref.contains("token") && sharedPreferenceClass.getValue_int("feature") == 2){
                    Intent intent = new Intent(MainActivity.this, HomeDoctorActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }
}