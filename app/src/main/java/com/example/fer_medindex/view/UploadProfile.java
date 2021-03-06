package com.example.fer_medindex.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fer_medindex.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfile extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile);

        getSupportActionBar().setTitle("Upload Profile Picture");

        Button buttonUploadPicChoose = findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        progressBar = findViewById(R.id.progressBar);
        imageViewUploadPic = findViewById(R.id.imageView_upload_profile_user);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();
        //Set User's current DP in ImageView ( if uploaded already). We will Picasso since imageViewer setImage
        //Regular URIs.
        //Ng?????i d??ng t???i ???nh l??n r???i
        Picasso.with(UploadProfile.this).load(uri).into(imageViewUploadPic);

        // Ch???n h??nh ???nh ????? t???i l??n
        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        //C???p nh???t h??nh ???nh l??n profile
        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                uploadPic();
            }
        });

    }

    private void uploadPic() {
        if(uriImage != null){
        // Save the image with uid of the currently logged user
            // tham chi???u l??u tr??? kh??ng gian l??u tr??? , l???a ch???n hi???n th??? v??? tr?? l??u h??nh ???nh d?????i d???ng con v?? t??n l?? uid ng?????i d??ng
            // c???ng th??m ph???n m??? r???ng h??nh ???nh
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "." + getFileExtension(uriImage));

            //Upload image to Storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // ?????t ???nh h??? s?? trong ch??? ????? xem h??nh ???nh v?? tham chi???u cho h??nh ???nh n??y ???????c l??u tr??? trong t???p tham chi???u
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override // download ???nh v??? th??nh c??ng
                        public void onSuccess(Uri uri) {
                            Uri downloaduri = uri;
                            firebaseUser = authProfile.getCurrentUser();

                            //Finally set the display image of user after upload
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloaduri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                    //Qu?? tr??nh upload di???n ra th??nh c??ng
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfile.this,"Upload Successful!",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UploadProfile.this,UserProfile.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        //Gi??p cho m???t ???ng d???ng qu???n l?? quy???n truy c???p ?????n d??? li???u ???????c l??u b???i ???ng d???ng ????
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        // /* l?? ch???n b???t c??? h??nh ???nh lo???i g?? c??ng ???????c
        intent.setType("image/*");
        // l???y n???i dung tr??n Internet ,b??n trong android
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // PICK_IMAGE_REQUEST= 1 y??u c???u ch???n ???nh l?? ????ng
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // ki???m tra m?? y??u c???u c?? b???ng y??u c???u h??nh ???nh ko ? , d??? li???u ko r???ng
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            // ?????t h??nh ???nh ng?????i d??ng ch???n v??o trong n???n h??nh ???nh android
            // ng?????i d??ng ch???n h??nh ???nh trong d??? li???u ??i???n tho???i
            uriImage = data.getData();
            imageViewUploadPic.setImageURI(uriImage);

        }
    }
    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Menu Item ???????c ch???n
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // l???y id c???a m???c menu ???????c l??u tr??? v??o int id
        int id = item.getItemId();
        if(id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if(id == R.id.menu_update_profile) {
            Intent intent = new Intent(UploadProfile.this,UpdateProfile.class);
            startActivity(intent);
            finish(); // kh??ng mu???n c?? nhi???u ho???t ?????ng tr??ng l???p ??ang ch???y
        }else if (id == R.id.menu_update_email){
            Intent intent = new Intent(UploadProfile.this,UpdateEmail.class);
            startActivity(intent);
            finish();
        }/*else if (id == R.id.menu_settings) {
            Toast.makeText(UserProfile.this,"menu_setting",Toast.LENGTH_SHORT).show();
        }*/else if(id == R.id.menu_change_password){
            Intent intent = new Intent(UploadProfile.this,ChangePassword.class);
            startActivity(intent);
            finish();
        }/*else if(id==R.id.menu_delete_profile){
            Intent intent = new Intent(UserProfile.this,DeleteProfile.class);
            startActivity(intent);
        }*/ else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UploadProfile.this,"Logged Out",Toast.LENGTH_SHORT).show();
            //quay l???i ho???t ?????ng ch??nh c???a Activity
            Intent intent = new Intent(UploadProfile.this,LoginActivity.class);

            //Xo?? ng??n s???p ????? ng??n ng?????i d??ng quay l???i ho???t ?????ng h??? s?? ng?????i d??ng ???? ????ng xu???t
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();// ????ng UserProfile
        }else{ // N???u ko ch???n item n??o
            Toast.makeText(UploadProfile.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}