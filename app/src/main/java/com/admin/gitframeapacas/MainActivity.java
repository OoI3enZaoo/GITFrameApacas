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

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends BaseActivity {

    private static final String TAG = "AnonymousAuth";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);
        TextView viewById = (TextView)findViewById(R.id.btnSkip);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInAnonymously();
            }
        });
        mAuth = FirebaseAuth.getInstance();
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
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
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
        TextView btnReset = (TextView) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signOut();
            }
        });

    }




    @Override
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
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID",user.getUid());
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

}
