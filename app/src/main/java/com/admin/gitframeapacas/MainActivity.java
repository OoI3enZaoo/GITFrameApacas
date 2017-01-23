package com.admin.gitframeapacas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends BaseActivity {

    private static final String TAG = "AnonymousAuth";
    EditText mID;
    EditText mPWD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);
        TextView txtSkip = (TextView) findViewById(R.id.txtSkip);
        mID = (EditText) findViewById(R.id.lbl_id);
        mPWD = (EditText) findViewById(R.id.lbl_pwd);
        statusLogin();
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new CheckLogin().execute(mID.getText().toString().trim(), mPWD.getText().toString().trim());

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

    public void statusLogin() {
        DBHelper db = new DBHelper(getApplicationContext());
        int status = db.getStatus();
        String name = db.getName();
        Log.i("ben", "number of row: " + status + name);
        if (status == 1) {
            Log.i("ben", "in if number of row" + status);
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", name);
            intent.putExtras(bundle);
            Log.i("ben", "Hello" + name);
            startActivity(intent);
        } else {
            Log.i("ben", "in else number of row: " + status + name);
        }
    }

    public class CheckLogin extends AsyncTask<String, Void, String> {

        String message = "";

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", strings[0])
                    .add("user_pwd", strings[1])
                    .build();
            Request request = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/api/checkLogin.jsp")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                message = response.body().string();
                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
                }
            return strings[0];
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            Log.i("ben", "Message: " + message.toString().trim());
            if (message.toString().trim().equals("0")) {
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                DBHelper db = new DBHelper(getApplicationContext());
                db.insertAccount(name, 1);
                Log.i("ben", "S: " + name);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID", name);
                intent.putExtras(bundle);
                startActivity(intent);
            }




        }
    }


}
