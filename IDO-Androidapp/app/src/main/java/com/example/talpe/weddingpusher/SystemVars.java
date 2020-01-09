package com.example.talpe.weddingpusher;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public abstract class SystemVars {
    private  static String smartspace;
    private static String serverIP ="smartspace-2019b-sean.herokuapp.com:80";

    public static String getBaseUrl() {
        return baseUrl;
    }

    private static String baseUrl = "http://"+serverIP+"/smartspace";


    static public String getSmartspace() {
        if(smartspace == null){
            @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,String> getSmartspace = new AsyncTask<Void,Void,String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    try
                    {
                        ResponseEntity<String> response =  restTemplate.getForEntity(baseUrl+"/system/smartspace",String.class);
                        return response.getBody();
                    }
                    catch (Exception ignore) {
                        return "";
                    }
                }
            };
            try {
                smartspace = getSmartspace.execute().get();
            }catch (Exception i){return  null;}
        }
        return smartspace;
    }

    static public String getServerIp() {
        return serverIP;
    }

}
