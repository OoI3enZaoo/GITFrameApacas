package com.admin.gitframeapacas;

import android.app.ProgressDialog;
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

    private static final String TAG = "ben2";
    EditText mID;
    EditText mPWD;
    TextView txtSkip;
    ProgressDialog dialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);
        mID = (EditText) findViewById(R.id.lbl_id);
        mPWD = (EditText) findViewById(R.id.lbl_pwd);
        txtSkip = (TextView) findViewById(R.id.txtSkip);
        dialog = new ProgressDialog(MainActivity.this);
        statusLogin();
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Loading. Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

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
        DBUser db = new DBUser(getApplicationContext());

        int status = db.getStatus();
        String name = db.getName();
        Log.i("ben", "number of row: " + status + name);

        if (status == 1) {
            Log.i("ben", "in if number of row" + status);
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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
                    .add("user_email", strings[0])
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
        protected void onPostExecute(String x) {
            super.onPostExecute(x);

            message = message.toString().trim();
            Log.i("checkLogin", "message: " + message);
            String status = message.substring(0, 1);
            Log.i("checkLogin", "status: " + status);
            String name = message.substring(2);
            Log.i("checkLogin", "name: " + name);
            if (status.toString().trim().equals("0")) {
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                DBUser db = new DBUser(getApplicationContext());
                db.updateStatus(1);
                db.updateName(name);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
            dialog.dismiss();




        }
    }

}
