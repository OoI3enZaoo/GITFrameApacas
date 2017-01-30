package com.admin.gitframeapacas.Views;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.gitframeapacas.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Admin on 12/11/2559.
 */

public class SignUpActivity extends AppCompatActivity {

    public static int startYear;
    public static int startMonth;
    public static int startDay;
    private static ProgressDialog dialog;
    public EditText lblFName;
    public EditText lblLName;
    public EditText lblBD;
    TextView txtError;
    private EditText lblEmail;
    private EditText lblPassword;
    private EditText lblPassword2;
    // private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;

    public SignUpActivity() {
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_createaccount);
        txtError = (TextView) findViewById(R.id.txtError);
        lblEmail = (EditText) findViewById(R.id.lblEmail);
        lblPassword = (EditText) findViewById(R.id.lblPassword);
        lblPassword2 = (EditText) findViewById(R.id.lblPassword2);
        lblFName = (EditText) findViewById(R.id.lblFName);
        lblLName = (EditText) findViewById(R.id.lblLName);
        lblBD = (EditText) findViewById(R.id.lblBD);


        lblBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Signup", "click");
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                System.out.println("the selected " + mDay);
                DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this,
                        new mDateSetListener(), mYear, mMonth, mDay);
                dialog.show();

            }
        });

        Button btnSummitAccount = (Button) findViewById(R.id.btnSignup);
        btnSummitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lblEmail.getText().toString().trim().equals("") && lblPassword.getText().toString().trim().equals("") && lblPassword2.getText().toString().trim().equals("") && lblFName.getText().toString().trim().equals("") && lblLName.getText().toString().trim().equals("")) {

                    new CountDownTimer(4000, 10) {
                        public void onTick(long millisUntilFinished) {
                            txtError.setText("Enter Your Data");
                        }

                        public void onFinish() {
                            txtError.setText("");
                        }
                    }.start();
                }
                if (!lblPassword.getText().toString().equals("") && !lblPassword2.getText().toString().equals("") && !lblPassword.getText().toString().trim().equals(lblPassword2.getText().toString())) {
                    Log.i("Login", "Not Same Password");
                    new CountDownTimer(4000, 10) {
                        public void onTick(long millisUntilFinished) {
                            txtError.setText("Check your Password");
                        }
                        public void onFinish() {
                            txtError.setText("");
                        }
                    }.start();
                }
                if (lblPassword.getText().toString().trim().equals(lblPassword2.getText().toString()) && !lblFName.getText().toString().trim().equals("") && !lblLName.getText().toString().trim().equals("")) {
                    Log.i("Login", "Success");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading. Please wait...");
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    new InsertUserTask().execute();

                }
            }
        });
    }

    class InsertUserTask extends AsyncTask<String, Void, String> {

        private String emailStatus = "";
        @Override
        protected String doInBackground(String... strings) {


            OkHttpClient client1 = new OkHttpClient();
            RequestBody formBody1 = new FormBody.Builder()
                    .add("email", lblEmail.getText().toString().trim())
                    .build();
            Request request1 = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/api/checkEmail.jsp")
                    .post(formBody1)
                    .build();

            try {
                Response response1 = client1.newCall(request1).execute();
                emailStatus = response1.body().string();
                emailStatus = emailStatus.trim().toString();
                Log.i("Login", "email statuc: " + emailStatus);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (emailStatus.equals("0")) {
                long lowerLimit = 1L;
                long upperLimit = 999999999999999999L;
                Random r = new Random();
                long number = lowerLimit + ((long) (r.nextDouble() * (upperLimit - lowerLimit)));
                Log.i("number", String.valueOf(r));
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("user_id", String.valueOf(number))
                        .add("user_pwd", lblPassword.getText().toString().trim())
                        .add("fname", lblFName.getText().toString().trim())
                        .add("lname", lblLName.getText().toString().trim())
                        .add("bdate", lblBD.getText().toString().trim())
                        .add("email", lblEmail.getText().toString().trim())
                        .build();
                Request request = new Request.Builder()
                        .url("http://sysnet.utcc.ac.th/aparcas/api/insert_user.jsp")
                        .post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    //message = response.body().string();
                    // Do something with the response.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "1";
            } else {

                return "Fail";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("1")) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                /*DBUser db = new DBUser(getApplicationContext());
                db.updateStatus(1);
                db.updateName(lblFName.getText().toString().trim());*/
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);


            } else {
                new CountDownTimer(4000, 10) {
                    public void onTick(long millisUntilFinished) {
                        txtError.setText("Use another email");
                    }

                    public void onFinish() {
                        txtError.setText("");
                    }
                }.start();
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();

            }
            dialog.dismiss();


        }
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // getCalender();
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            lblBD.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mYear).append("-")
                    .append(mMonth + 1).append("-")
                    .append(mDay).append("")
            );
            Log.i("Date", "" + lblBD.getText().toString());


        }
    }
}

