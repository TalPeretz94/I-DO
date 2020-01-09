package com.example.talpe.weddingpusher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class SignUpActivity extends AppCompatActivity {

    TextView email;
    TextView smartspace;
    ImageButton next;
    final static String STRING_KEY = "USER_KEY";
    final static String BUNDLE_KEY = "BUNDLE_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email= findViewById(R.id.email);
        smartspace= findViewById(R.id.smart_space);
        next = findViewById(R.id.nextbt);
        final SignUpForm userInfo = new SignUpForm();
        try {
            smartspace.setText(SystemVars.getSmartspace());
        }
        catch (Exception ignored){
            Log.e("error",ignored.getMessage());
        }

        next.setOnClickListener(v -> {
            @SuppressLint("StaticFieldLeak") AsyncTask<String,Void,Void> singupTask = new AsyncTask<String, Void, Void>() {
                @Override
                protected  Void doInBackground(String... urls) {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.setErrorHandler(new SignUpResponseHandler());
                   userInfo.setEmail(email.getText().toString());
                   userInfo.setUsername(email.getText().toString().split("@")[0]);
                   userInfo.setAvatar("=)");
                   UserBoundary user = null;
                   restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
                   ResponseEntity<UserBoundary> response =  restTemplate.postForEntity(urls[0],userInfo,UserBoundary.class);
                   if(response.getStatusCode() == HttpStatus.ALREADY_REPORTED){
                       //user already exist - get his info
                       Log.i("Http: ","Response : "+ response.getStatusCode()+" , "+ response.getStatusCode().name());
                       String url = urls[1]+"/"+smartspace.getText()+"/"+userInfo.getEmail();
                       ResponseEntity<UserBoundary> r =  restTemplate.getForEntity(url,UserBoundary.class);
                       user = r.getBody();
                   }
                    else {
                       //created user
                       user = response.getBody();
                   }
                   if(user != null){
                       Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                       Bundle b = new Bundle();
                       b.putSerializable(STRING_KEY, user);
                       intent.putExtra(BUNDLE_KEY, b);
                       startActivity(intent);
                       finish();
                   }
                    return null;
                }
            };
            singupTask.execute(SystemVars.getBaseUrl()+"/users",SystemVars.getBaseUrl()+"/users/login");
        });
    }

    public class SignUpResponseHandler implements ResponseErrorHandler{

        SignUpResponseHandler() {
        }

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return !response.getStatusCode().series().equals(HttpStatus.Series.SUCCESSFUL);
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            Log.e("HttpError: ","Response error: "+ response.getStatusCode()+" , "+ response.getStatusText());
        }
    }
    public  static class SignUpForm{
        public enum UserRole{PLAYER}
        private String email,username,avatar;
        private UserRole role = UserRole.PLAYER;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public UserRole getRole() {
            return role;
        }

        public void setRole(UserRole role) {
            this.role = role;
        }
    }
}
