package claudetsiang.fr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import claudetsiang.fr.UtilsService.SharedPreferenceClass;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout logout_action;
    private  LinearLayout rdv_action;
    SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout_action = findViewById(R.id.logout_action);
        rdv_action = findViewById(R.id.rdv_action);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        logout_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferenceClass.clear();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

        rdv_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferenceClass.clear();
                startActivity(new Intent(HomeActivity.this, DoctorActivity.class));
                finish();
            }
        });
    }
}