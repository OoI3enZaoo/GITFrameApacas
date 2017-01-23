package com.admin.gitframeapacas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Admin on 12/11/2559.
 */

public class SignUpActivity extends BaseActivity {

    TextView txtError;
    boolean check = false;
    private EditText lblEmail;
    private EditText lblPassword;
    private EditText lblPassword2;
    private EditText lblName;
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
        lblName = (EditText) findViewById(R.id.lblName);

/*
        Button btnSummitAccount = (Button) findViewById(R.id.btnSignup);
        btnSummitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lblEmail.getText().toString().trim().equals("")) {

                    new CountDownTimer(4000, 10) {
                        public void onTick(long millisUntilFinished) {
                            txtError.setText("Enter Your Email");
                        }

                        public void onFinish() {
                            txtError.setText("");
                        }
                    }.start();


                } else {
                    new CheckEmailTask().execute(lblEmail.getText().toString().trim());
                    check = true;
                }
                if (check == false && !lblEmail.getText().toString().trim().equals("") && lblPassword.getText().toString().trim().equals("") && lblPassword2.getText().toString().trim().equals("")) {

                    new CountDownTimer(4000, 10) {
                        public void onTick(long millisUntilFinished) {
                            txtError.setText("Enter Your password");
                        }

                        public void onFinish() {
                            txtError.setText("");
                        }
                    }.start();

                    if (!lblPassword.getText().toString().trim().equals("") && !lblPassword2.getText().toString().trim().equals("")) {

                        if (lblPassword.getText().toString().trim() != lblPassword2.getText().toString().trim()) {
                            new CountDownTimer(4000, 10) {
                                public void onTick(long millisUntilFinished) {
                                    txtError.setText("Your password doesn't match");
                                }

                                public void onFinish() {
                                    txtError.setText("");
                                }
                            }.start();

                        }
                    }
                }

                if (lblName.getText().toString().trim().equals("") && !lblPassword.getText().toString().trim().equals("") && !lblPassword2.getText().toString().trim().equals("") && !lblEmail.getText().toString().trim().equals("")) {
                    new CountDownTimer(4000, 10) {
                        public void onTick(long millisUntilFinished) {
                            txtError.setText("Enter Your Name");
                        }

                        public void onFinish() {
                            txtError.setText("");
                        }
                    }.start();
                }
                if (!lblName.getText().toString().trim().equals("") && !lblPassword.getText().toString().trim().equals("") && !lblPassword2.getText().toString().trim().equals("") && !lblEmail.getText().toString().trim().equals("")) {
                    new CheckNameTask().execute(lblName.getText().toString().trim());
                }


            }
        });

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private void signInAnonymously() {
        showProgressDialog();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "False Login", Toast.LENGTH_SHORT).show();
                    //  mTextViewProfile.setTextColor(Color.RED);
                    //   mTextViewProfile.setText(task.getException().getMessage());
                } else {
                    Toast.makeText(getApplicationContext(), "Success Login", Toast.LENGTH_SHORT).show();
                    // mTextViewProfile.setTextColor(Color.DKGRAY);

                }
                hideProgressDialog();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        boolean isSignedIn = (user != null);

        if (isSignedIn) {

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", user.getUid());
            bundle.putString("TYPE", "Member");
            intent.putExtras(bundle);
            startActivity(intent);

            // mTextViewProfile.setText("Email: " + user.getEmail());
            // mTextViewProfile.append("\n");
            //  mTextViewProfile.append("Firebase ID: " + user.getUid());
        } else {
            //  mTextViewProfile.setText(null);
        }

        // findViewById(R.id.button_anonymous_sign_in).setEnabled(!isSignedIn);
        //  findViewById(R.id.button_anonymous_sign_out).setEnabled(isSignedIn);
        //   findViewById(R.id.button_link_account).setEnabled(isSignedIn);

        hideProgressDialog();
    }

    class CheckNameTask extends AsyncTask<String, Void, String> {
        private String status;

        @Override
        protected String doInBackground(String... strings) {
            String mName = strings[0];
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody1 = new FormBody.Builder()
                    .add("Name", mName)
                    .build();
            Request request1 = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/SelectNameMember.jsp")
                    .post(formBody1)
                    .build();

            try {
                Response response = client.newCall(request1).execute();
                status = response.body().string();
                status = status.trim().toString();
                return status;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("ben", "Name status: " + s);
            if (status.equals("Success")) {

                new CountDownTimer(4000, 10) {
                    public void onTick(long millisUntilFinished) {
                        txtError.setText("Use another Name");
                    }

                    public void onFinish() {
                        txtError.setText("");
                    }
                }.start();

            } else {

                mAuth = FirebaseAuth.getInstance();
                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {

                        } else {

                        }
                        updateUI(user);
                    }
                };


                signInAnonymously();

            }

        }
    }

    class CheckEmailTask extends AsyncTask<String, Void, String> {
        private String status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String mEmail = strings[0];
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody1 = new FormBody.Builder()
                    .add("email", mEmail)
                    .build();
            Request request1 = new Request.Builder()
                    .url("http://sysnet.utcc.ac.th/aparcas/SelectEmailMember.jsp")
                    .post(formBody1)
                    .build();

            try {
                Response response = client.newCall(request1).execute();
                status = response.body().string();
                status = status.trim().toString();
                return status;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("ben", "Email status: " + s);

            if (status.equals("Success")) {

                new CountDownTimer(4000, 10) {
                    public void onTick(long millisUntilFinished) {
                        txtError.setText("Use another email");
                    }

                    public void onFinish() {
                        txtError.setText("");
                    }
                }.start();
            }
            check = false;
        }
    }*/

    }
}