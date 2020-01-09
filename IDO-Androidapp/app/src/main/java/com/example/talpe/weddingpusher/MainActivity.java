package com.example.talpe.weddingpusher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView buckysImageView;
    ImageView buckyButton;
    ImageView sendbt;
    FirebaseAuth auth;
    FirebaseFirestore db;
    UserBoundary user;
    ActionBoundary.ElementKey wedding;
    ActionBoundary blessingAction = new ActionBoundary();
    EditText bless;
    final static String USER_KEY = "USER_KEY";
    final static String WEDDING_KEY = "WEDDING_KEY";
    final static String BUNDLE_KEY = "BUNDLE_KEY";

     Boolean waitToURL = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buckyButton = (ImageView) findViewById(R.id.buckysButton);
        sendbt = (ImageView)findViewById(R.id.send_button);
        bless = findViewById(R.id.bless_text);
       // buckysImageView = (ImageView) findViewById(R.id.buckysImageView);

        blessingAction.setProperties(new HashMap<>());
        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra(SignUpActivity.BUNDLE_KEY);
        if (b != null) {
            user = (UserBoundary)(b.getSerializable(LoginActivity.USER_KEY));
            wedding =( ActionBoundary.ElementKey)(b.getSerializable(LoginActivity.WEDDING_KEY));
        }

        sendbt.setOnClickListener(v -> {
            blessingAction.setType("Blessing");
            blessingAction.setPlayer(user.getKey());
            blessingAction.setElement(wedding);
            blessingAction.getProperties().put("BlessingText", " " +bless.getText().toString());

            @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> shareBlessing = new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    String url = SystemVars.getBaseUrl()+"/actions";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
                    try {
                        if(waitToURL)
                            throw new RuntimeException("wait to process image, try again in few seconds");
                        ResponseEntity<ActionBoundary> response =  restTemplate.postForEntity(url,blessingAction,ActionBoundary.class);
                        Log.i("Post Blessing ", "Succeed");
                        Intent intent1 = new Intent(MainActivity.this, EndActivity.class);
                        Bundle b1 = new Bundle();
                        b1.putSerializable(USER_KEY, user);
                        b1.putSerializable(WEDDING_KEY, wedding);
                        intent1.putExtra(BUNDLE_KEY, b1);
                        startActivity(intent1);
                        finish();
                    }catch (Throwable t){
                        Log.e("Post Blessing error", t.getMessage());
                        runOnUiThread(()->
                                Toast.makeText(getApplicationContext(),
                                        "share blessing error "+ t.getMessage(),
                                        Toast.LENGTH_SHORT)
                                        .show());

                    }
                    return null;
                }
            };
            shareBlessing.execute();
        });

        //Disable the button if the user has no camera
        if(!hasCamera())
            buckyButton.setEnabled(false);
    }

    //Check if the user has a camera
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //Launching the camera
    public void launchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Take a picture and pass results along to onActivityResult
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    //If you want to return the image taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //Get the photo
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            buckyButton.setImageBitmap(bitmap);

            try{
                FirebaseStorage storage;
                FirebaseApp.initializeApp(getApplicationContext());
                storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://i-do-fa424.appspot.com");
                StorageReference riversRef = storageRef.child("Photos/"
                        + md5(user.getKey().getSmartspace() + user.getKey().getEmail()
                        + new Date() + Math.random()).toUpperCase() + ".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

//            double maxSize = 450;
//            double scale = maxSize / Math.max(bitmap.getHeight(), bitmap.getWidth());
//            bitmap = getResizedBitmap(bitmap, (int) (scale * bitmap.getWidth()),
//                    (int) (scale * bitmap.getHeight()));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteData = baos.toByteArray();
                waitToURL = true;
                riversRef.putBytes(byteData)
                        // Register observers to listen for when the download is done
                        // or if it fails
                        .addOnFailureListener(exception ->
                                Toast.makeText(getApplicationContext(),
                                        "fail upload file",
                                        Toast.LENGTH_SHORT)
                                        .show())
                        .addOnSuccessListener(taskSnapshot -> {
                            riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                Log.i("profilePic", "onActivityResult: " + uri);
                                //image url;
                                Log.i("imageURL",uri.toString());
                                blessingAction.getProperties().
                                        put("BlessingPictureURL",uri.toString());
                                waitToURL = false;

                            });
                        });


            }catch (Exception e){
                Log.e("Upload Fail",""+e.getMessage());
                e.printStackTrace();
                runOnUiThread(()->{
                    Toast.makeText(getApplicationContext(),
                            "upload image error "+ e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();
                });
                waitToURL = false;

            }
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }




}
