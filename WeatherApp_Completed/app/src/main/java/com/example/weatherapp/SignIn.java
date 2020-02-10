package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends Activity {

    TextInputEditText textEmail, textPassword;
    ProgressBar progressBar;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();



        if(auth.getCurrentUser()!=null)
        {
            Intent i = new Intent(SignIn.this, CommentActivity.class);
            startActivity(i);
        }
        else {
            setContentView(R.layout.activity_signin);

            RelativeLayout kayout=findViewById(R.id.rl);
            kayout.setBackgroundResource(BgImage.getInstance().getImageName());
            textEmail=findViewById(R.id.email_ed_login);
            textPassword=findViewById(R.id.password_ed_login);
            progressBar=findViewById(R.id.progressBarLogin);
            reference = FirebaseDatabase.getInstance().getReference().child("Users");

        }
    }

    public void LoginUser(View v)
    {
        progressBar.setVisibility(View.VISIBLE);
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();

        if(!email.equals("") && !password.equals(""))
        {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignIn.this, CommentActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
    public void gotoRegister(View v)
    {
        Intent i = new Intent(SignIn.this, RegisterActivity.class);
        startActivity(i);
    }

    public void forgotPassword(View v)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(SignIn.this);

        LinearLayout container = new LinearLayout(SignIn.this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ip.setMargins(50,0,0,100);

        final EditText input = new EditText(SignIn.this);
        input.setLayoutParams(ip);
        input.setGravity(Gravity.TOP | Gravity.START);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setLines(1);
        input.setMaxLines(1);

        container.addView(input, ip);

        alert.setMessage("Nhập địa chỉ email.");
        alert.setTitle("Quên mật khẩu");
        alert.setView(container);
        alert.setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String entered_email = input.getText().toString();
                auth.sendPasswordResetEmail(entered_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Xin kiểm tra hòm thư email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
