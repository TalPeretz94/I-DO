package com.example.talpe.weddingpusher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    final static String USER_KEY = "USER_KEY";
    final static String WEDDING_KEY = "WEDDING_KEY";
    final static String BUNDLE_KEY = "BUNDLE_KEY";
    private static final int QR_REQUSET_CODE = 0;

    ImageButton continuebt;
    TextView id, smartspace;
    String weddingSmartspace;
    UserBoundary user;
    ImageButton qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        continuebt = (ImageButton)findViewById(R.id.startbt);
        qr = (ImageButton)findViewById(R.id.qr_bt);
        id = findViewById(R.id.codeEvent);
        smartspace = findViewById(R.id.codeSmartspace);

        Intent intent = getIntent();

        Bundle b = intent.getBundleExtra(SignUpActivity.BUNDLE_KEY);

        if (b != null) {
             user = (UserBoundary)(b.getSerializable(SignUpActivity.STRING_KEY));
            if (user != null) {
                weddingSmartspace = user.getKey().getSmartspace();
            }
        }

        qr.setOnClickListener(v ->{
            Intent qrIntent = new Intent(this, QR_Activity.class);
            startActivityForResult(qrIntent, QR_REQUSET_CODE);
        });

        continuebt.setOnClickListener(v -> {
            @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> checkInTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected  Void doInBackground(Void... urls) {
                    String url = SystemVars.getBaseUrl()+"/actions";
                    ActionBoundary checkInAction = new ActionBoundary();
                    checkInAction.setType("CheckIn");
                    checkInAction.setPlayer(user.getKey());
                    String weddingID = id.getText().toString();
                    ActionBoundary.ElementKey wedding = new ActionBoundary.ElementKey(smartspace.getText().toString(),weddingID);
                    checkInAction.setElement(wedding);

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
                    try{
                        try {
                            ResponseEntity<ActionBoundary> response =  restTemplate.postForEntity(url,checkInAction,ActionBoundary.class);

                        }catch (HttpStatusCodeException e){
                            if(!e.getStatusCode().equals(HttpStatus.CONFLICT))
                                throw e;
                        }
                        //checked in!
                        Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle b1 = new Bundle();

                        b1.putSerializable(USER_KEY, user);
                        b1.putSerializable(WEDDING_KEY, wedding);

                        intent1.putExtra(BUNDLE_KEY, b1);
                        startActivity(intent1);
                        finish();
                    }
                    catch (HttpStatusCodeException e)
                    {
                        String message = e.getMessage()+" "+e.getStatusText()+" ";
                        String jsonResponse = e.getResponseBodyAsString();
                        if (StringUtils.hasText(jsonResponse)) {
                            try {
                               HashMap<String,String> map =  new ObjectMapper().readValue(jsonResponse, new TypeReference<HashMap<String,String>>(){});
                               if( map.containsKey("message"))
                                message+=map.get("message");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        Log.e("CheckIn Error",message);
                        final String msg = message;
                        runOnUiThread(()->
                            Toast.makeText(getApplicationContext(),
                                    "Check In error "+ msg,
                                    Toast.LENGTH_LONG)
                                    .show());

                    }
                    return null;
                }

            };
            checkInTask.execute();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==QR_REQUSET_CODE) {
            if (resultCode == RESULT_OK && data!=null) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(data.getStringExtra(QR_Activity.QR_TEXT_KEY), JsonObject.class);
                id.setText(json.get("id").getAsString());
                smartspace.setText(json.get("smartspace").getAsString());
            }
            else
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
