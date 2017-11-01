package com.dcdineshk.googlemap.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import com.dcdineshk.googlemap.utils.CircularImageView;
import com.dcdineshk.googlemap.utils.SharedPreferenceUtils;
import com.dcdineshk.googlemap.utils.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private String Storage_Path = "MyImageUploads/";
    private String Database_Path = "GeofenceUsers";

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private int data;


    @BindView(R.id.input_name_s)
    EditText mNameText;
    @BindView(R.id.input_email_s)
    EditText mEmailText;
    @BindView(R.id.input_password_s)
    EditText mPasswordText;
    @BindView(R.id.input_reEnterPassword_s)
    EditText mReEnterPasswordText;
    @BindView(R.id.btn_signup_s)
    Button mSignupButton;
    @BindView(R.id.link_login_s)
    TextView mLoginLink;
    private FirebaseDatabase mFirebaseInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        new SharedPreferenceUtils(this);
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        mFirebaseInstance = databaseReference.getDatabase();

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    signup(v);


            }
        });


        findViewById(R.id.userProfileImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    Snackbar bar;

    public void signup(View v) {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        } else {
            String name = mNameText.getText().toString();
            String email = mEmailText.getText().toString();
            String password = mPasswordText.getText().toString();

            // TODO: Implement your own signup logic here.
            mFireBaseSignup(email, password, name);
        }
    }
    public void onSignupSuccess() {
        mSignupButton.setEnabled(true);
        SharedPreferenceUtils.setSignup(true);
        SharedPreferenceUtils.setEmail(mEmailText.getText().toString().trim());
        SharedPreferenceUtils.setPassword(mPasswordText.getText().toString().trim());
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = mNameText.getText().toString();
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
            valid = false;
        } else {
            mNameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("Enter a valid email address");
            valid = false;
        } else {
            mEmailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            mPasswordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 10 || !(reEnterPassword.equalsIgnoreCase(password))) {
            mReEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            mReEnterPasswordText.setError(null);
        }
        if (FilePathUri == null){
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void mFireBaseSignup(final String email, String password, final String name) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Log.e("~~~~~~~~~~~~> " , firebaseAuth.getCurrentUser().getUid() +"");
                            UploadImageFileToFirebaseStorage(name , email ,firebaseAuth.getCurrentUser().getUid());
                            getUserData(firebaseAuth.getCurrentUser().getUid());
                            onSignupSuccess();
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

                            System.out.println("~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+updateInfo.getUuid()
                                    +"\n~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+updateInfo.getImageURL()
                                    +"\n~~~~~~~~~~~~~~~~~~~>>>>>>>>>>>  "+updateInfo.getUserName());

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

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    Uri FilePathUri;

    public void UploadImageFileToFirebaseStorage( final String name, final String email ,final String s) {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {


            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Hiding the progressDialog after done uploading.
                    @SuppressWarnings("VisibleForTests")
                    ImageUploadInfo imageUploadInfo = new ImageUploadInfo(name, taskSnapshot.getDownloadUrl().toString(),email,s);

                    // Adding image upload id s child element into databaseReference.
                    Log.e("~~~~~~~`>> " , "uuid____ " + s);
                    databaseReference.child(s).setValue(imageUploadInfo);
                }
            })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.

                            Log.e("~~~~~~~~~~~``> " , exception.getMessage());
                            // Showing exception erro message.
                            Toast.makeText(SignupActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.

                        }
                    });
        } else {
            Toast.makeText(SignupActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    private void imageLoad(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                showToast(exception.getMessage());
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                showToast("success !!!!!");
            }
        });


    }

    public void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignupActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(SignupActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            FilePathUri = data.getData();

            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    Bitmap bm = null;

    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        ((CircularImageView) findViewById(R.id.userProfileImageView)).setImageBitmap(bm);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ((CircularImageView) findViewById(R.id.userProfileImageView)).setImageBitmap(bm);
    }

}