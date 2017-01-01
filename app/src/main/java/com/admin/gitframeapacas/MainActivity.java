package com.admin.gitframeapacas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



public class MainActivity extends BaseActivity {

    private static final String TAG = "AnonymousAuth";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID","Login");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });


    }


}
