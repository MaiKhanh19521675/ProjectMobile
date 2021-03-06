package com.example.fer_medindex.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fer_medindex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr , editTextPwdNew , editTextConfirmPwdNew;
    private TextView textViewAuthenticated;
    private Button buttonChangePwd , buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        editTextConfirmPwdNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_password);
        // Disable editText for New Password , Confirm New Password and Make Change Pwd Button unclickable till user ishn
        // t???t nh???p m???t kh???u m???i ????? nh???p m???t kh???u c?? , hi???n t???i
        editTextPwdNew.setEnabled(false);
        editTextConfirmPwdNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);
        //x??c th???c firebase
        authProfile = FirebaseAuth.getInstance();
        // s??? d???ng bi???n auth l??u n?? trong bi???n ng?????i d??ng
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){ // ki???m tra xem ng?????i d??ng c?? null equals c?? ngh??a l?? b???ng
            Toast.makeText(ChangePassword.this,"Something went wrong! User's details not available",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePassword.this,UserProfile.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticateUser(firebaseUser);
        }
    }
 // ReAuthenticate User before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // l???y ???????c m???t kh???u hi???n t???i m?? ng?????i d??ng ??ang s??? d???ng
                userPwdCurr = editTextPwdCurr.getText().toString();
                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePassword.this,"Password is needed",Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Please enter your current password to authenticate");
                    editTextPwdCurr.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    // t???o th??ng tin x??c th???c m?? ???????c ???? th???y trong c???p nh???t c??c ho???t ?????ng email
                    //ReAuthenticate User now l???y m???t kh???u trong firebase v?? d??ng bi???n ng?????i d??ng firebase nh???n email c?? trong firebase
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwdCurr);
                    // chuy???n th??ng tin ????ng nh???p ch??? c???n tr??nh nghe ho??n ch???nh
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                //Disable editText for Current Password. Enable EditText for New Password and Confirm New Password
                                //T???t editText cho M???t kh???u Hi???n t???i. B???t EditText cho m???t kh???u m???i v?? x??c nh???n m???t kh???u m???i
                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextConfirmPwdNew.setEnabled(true);
                                //Enable Change Pwd Button . Disable Authenticate Button
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);
                                // thay ?????i m???t kh???u Set TextView to show User is authenticated/verified
                                textViewAuthenticated.setText("You are authenticated/Verified"+"You can change password now!");
                                Toast.makeText(ChangePassword.this,"Password has been verified"+
                                        "Change password now",Toast.LENGTH_SHORT).show();
                                //Update color of Change Password Button
                                buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePassword.this,R.color.yellow));
                                // Tr??nh nghe nh???n chu???t ????? thay ?????i m???t kh???u
                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }else {
                                try{
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(ChangePassword.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextConfirmPwdNew.getText().toString();
       String userPwdCurr = editTextPwdCurr.getText().toString();

        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePassword.this,"New Password is needed",Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangePassword.this,"Please confirm your new password",Toast.LENGTH_SHORT).show();
            editTextConfirmPwdNew.setError("Please re-enter your new password");
            editTextConfirmPwdNew.requestFocus();
        } else if(!userPwdNew.matches(userPwdConfirmNew)){
            Toast.makeText(ChangePassword.this,"Password did not match",Toast.LENGTH_SHORT).show();
            editTextConfirmPwdNew.setError("Please re-enter same password");
            editTextConfirmPwdNew.requestFocus();
        } else if(userPwdNew.matches(userPwdCurr)){
            Toast.makeText(ChangePassword.this,"New Password cannot be same as old password",Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new password");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            // s??? d???ng bi???n ng?????i d??ng firebase , g???i ph????ng th???c c???p nh???t m???t kh???u
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){ // thay ?????i m???t kh???u th??nh c??ng
                        Toast.makeText(ChangePassword.this,"Password has been changed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePassword.this,UserProfile.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePassword.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
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
            Intent intent = new Intent(ChangePassword.this,UpdateProfile.class);
            startActivity(intent);
            finish(); // kh??ng mu???n c?? nhi???u ho???t ?????ng tr??ng l???p ??ang ch???y
        }else if (id == R.id.menu_update_email){
            Intent intent = new Intent(ChangePassword.this,UpdateEmail.class);
            startActivity(intent);
            finish();
        }/*else if (id == R.id.menu_settings) {
            Toast.makeText(UserProfile.this,"menu_setting",Toast.LENGTH_SHORT).show();
        }*/else if(id == R.id.menu_change_password){
            Intent intent = new Intent(ChangePassword.this,ChangePassword.class);
            startActivity(intent);
            finish();
        }/*else if(id==R.id.menu_delete_profile){
            Intent intent = new Intent(UserProfile.this,DeleteProfile.class);
            startActivity(intent);
        }*/ else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(ChangePassword.this,"Logged Out",Toast.LENGTH_SHORT).show();
            //quay l???i ho???t ?????ng ch??nh c???a Activity
            Intent intent = new Intent(ChangePassword.this,LoginActivity.class);

            //Xo?? ng??n s???p ????? ng??n ng?????i d??ng quay l???i ho???t ?????ng h??? s?? ng?????i d??ng ???? ????ng xu???t
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();// ????ng UserProfile
        }else{ // N???u ko ch???n item n??o
            Toast.makeText(ChangePassword.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}