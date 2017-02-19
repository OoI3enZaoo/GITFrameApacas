package com.admin.gitframeapacas.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.Data.MemberResponse;
import com.admin.gitframeapacas.R;
import com.admin.gitframeapacas.SQLite.DBUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "BENLoginActtivity";
    EditText mID;
    EditText mPWD;
    TextView txtSkip;
    private ProgressDialog dialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);
        mID = (EditText) findViewById(R.id.lbl_id);
        mPWD = (EditText) findViewById(R.id.lbl_pwd);
        txtSkip = (TextView) findViewById(R.id.txtSkip);
        dialog = new ProgressDialog(LoginActivity.this);
        statusLogin();

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                long lowerLimit = 1L;
                long upperLimit = 9999999999999999L;
                Random r = new Random();
                Long userId = lowerLimit + ((long) (r.nextDouble() * (upperLimit - lowerLimit)));

                DBUser db = new DBUser(getApplicationContext());
                db.updateStatus(1);
                db.updateUserType("user");
                db.updateUserID(String.valueOf(userId));


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
                String result = response.body().string();

                Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<MemberResponse>>() {
                }.getType();
                Collection<MemberResponse> enums = gson.fromJson(result, collectionType);
                MemberResponse[] result1 = enums.toArray(new MemberResponse[enums.size()]);
                if (result1[0].getCnt().equals("0")) {
                    return "0";
                } else {
                    String fullname = result1[0].getFname() + " " + result1[0].getLname();
                    String uid = result1[0].getUser_id();
                    DBUser db = new DBUser(getApplicationContext());
                    db.updateStatus(1);
                    db.updateName(fullname);
                    db.updateUserID(uid);
                    db.updateUserType("member");
                    Log.i(TAG, "UserId: " + uid);
                    return "1";
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return strings[0];
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("0")) {
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }

            dialog.dismiss();




        }
    }

}
