package com.admin.gitframeapacas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends BaseActivity {

    private static final String TAG = "AnonymousAuth";

    //  private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);
        TextView txtSkip = (TextView) findViewById(R.id.txtSkip);

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // signInAnonymously();
            }
        });
        /*mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putString("ID","Login");
                //intent.putExtras(bundle);
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


   /* @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signInAnonymously() {
        showProgressDialog();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "False Login", Toast.LENGTH_SHORT).show();
                    //  mTextViewProfile.setTextColor(Color.RED);
                    //   mTextViewProfile.setText(task.getException().getMessage());
                } else {
                    Toast.makeText(MainActivity.this, "Success Login", Toast.LENGTH_SHORT).show();
                    // mTextViewProfile.setTextColor(Color.DKGRAY);

                }
                hideProgressDialog();
            }
        });
    }

    private void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setMessage(R.string.logout);
        alert.setCancelable(false);
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                updateUI(null);
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    private void updateUI(FirebaseUser user) {
        boolean isSignedIn = (user != null);

        if (isSignedIn) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", user.getUid());
            bundle.putString("TYPE", "User");
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

*/


}
