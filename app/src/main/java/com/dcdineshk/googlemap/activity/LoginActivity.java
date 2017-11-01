package com.dcdineshk.googlemap.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dcdineshk.googlemap.R;
import com.dcdineshk.googlemap.model.ImageUploadInfo;
import com.dcdineshk.googlemap.utils.SharedPreferenceUtils;
import com.dcdineshk.googlemap.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ProgressDialog pdia;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase mFirebaseInstance;
    private String Database_Path = "GeofenceUsers";


    @BindView(R.id.input_email)
    EditText mEemailText;
    @BindView(R.id.input_password)
    EditText mPasswordText;
    @BindView(R.id.btn_login)
    Button mLoginButton;
    @BindView(R.id.link_signup)
    TextView mSignupLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        new SharedPreferenceUtils(this);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        mFirebaseInstance = databaseReference.getDatabase();

        if (!checkPermission()) {            requestPermission();        }

        if (SharedPreferenceUtils.getSignup()){
            mEemailText.setText(SharedPreferenceUtils.getEmail());
            mPasswordText.setText(SharedPreferenceUtils.getPassword());
        }

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                login();
            }
        });

        mSignupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");
        if (!validate()) {
            onLoginFailed();
            return;
        } else {
            String email = mEemailText.getText().toString();
            String password = mPasswordText.getText().toString();

            Log.e("~~~~~~~~~~~~~~~~~~~>> " ,email);
            Log.e("~~~~~~~~~~~~~~~~~~~>> " ,password);
            //if (auth.getCurrentUser()!=null){
                pdia = ProgressDialog.show(LoginActivity.this,"","Please wait...");
                mLogin(email,password);
            //}
            // TODO: Implement your own authentication logic here.

           /* new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginSuccess();
                            // onLoginFailed();
                        }
                    }, 3000);*/
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        mLoginButton.setEnabled(true);
        SharedPreferenceUtils.setSignup(true);
        SharedPreferenceUtils.setEmail(mEemailText.getText().toString().trim());
        SharedPreferenceUtils.setPassword(mPasswordText.getText().toString().trim());
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mLoginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = mEemailText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEemailText.setError("enter a valid email address");
            valid = false;
        } else {
            mEemailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            mPasswordText.setError("between 6 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        return valid;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
        }
    }

    private void mLogin(String email, final String password){
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            pdia.dismiss();
                                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        } else {
                            getUserData(auth.getCurrentUser().getUid());
                            pdia.dismiss();
                            onLoginSuccess();
                        }
                    }
                });
    }

    private void getUserData(final String uuid) {

        if (SharedPreferenceUtils.getSaveSwitch()){
            SharedPreferenceUtils.setSaveSwitch(false);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");
                Log.e("!!!!!!!!!!!!!!11" , dataSnapshot.getChildren().toString() +"");

                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.e("~~~~~~~~~> " , postSnapshot.getKey());
                        if (uuid.equalsIgnoreCase(postSnapshot.getKey())){
                            ImageUploadInfo updateInfo = postSnapshot.getValue(ImageUploadInfo.class);

                            System.out.println("~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+updateInfo.getUuid()+"\n~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+updateInfo.getImageURL()+"\n~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+updateInfo.getUserName());
                            if (!SharedPreferenceUtils.getUsername().trim().equalsIgnoreCase("")
                                    && SharedPreferenceUtils.getUUID().trim().equalsIgnoreCase("")
                                    && !SharedPreferenceUtils.getImageURL().trim().equalsIgnoreCase(""))
                            {
                                SharedPreferenceUtils.setUsername("");
                                SharedPreferenceUtils.setImageURL("");
                                SharedPreferenceUtils.setUUID("");
                            }

                            String username = updateInfo.getUserName();
                            String userImage = updateInfo.getImageURL();
                            SharedPreferenceUtils.setUUID(updateInfo.getUuid());
                            System.out.println("~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+updateInfo.getUuid()
                                    +"\n~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+userImage
                                    +"\n~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+username);
                            SharedPreferenceUtils.setImageURL(userImage);
                            SharedPreferenceUtils.setUsername(username);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

    }


}