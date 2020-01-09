package com.example.talpe.weddingpusher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class EndActivity extends AppCompatActivity {


    final static String USER_KEY = "USER_KEY";
    final static String WEDDING_KEY = "WEDDING_KEY";
    final static String BUNDLE_KEY = "BUNDLE_KEY";
    UserBoundary user;
    ActionBoundary.ElementKey wedding;
    ImageButton anthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        anthor = findViewById(R.id.anthor_bt);

        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra(MainActivity.BUNDLE_KEY);
        if (b != null) {
            user = (UserBoundary)(b.getSerializable(MainActivity.USER_KEY));
            wedding =( ActionBoundary.ElementKey)(b.getSerializable(MainActivity.WEDDING_KEY));
        }

        anthor.setOnClickListener(v -> {

                        Intent intent1 = new Intent(EndActivity.this, MainActivity.class);
                        Bundle b1 = new Bundle();
                        b1.putSerializable(USER_KEY, user);
                        b1.putSerializable(WEDDING_KEY, wedding);
                        intent1.putExtra(BUNDLE_KEY, b1);
                        startActivity(intent1);
                        finish();

        });
    }
}
