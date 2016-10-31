package com.backand.backand_android_ad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationException;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "backand_ad";
    private final String RESOURCE = "https://graph.windows.net/";
    private final String CLIENT_ID = "7a3e5685-5a11-419d-b429-9b96bd7b8463";
    private final String REDIRECT = "https://api.backand.co/1/user/azuread/auth/app1";
    private final String AUTHORITY = "https://login.microsoftonline.com/e1c3d485-09ab-4746-b56f-543a8bc0e488";

    private AuthenticationContext mContext;
    private TextView mTextViewStatus;
    private EditText mClientIdInput;
    private EditText mRedirectUriInput;
    private AuthenticationResult mResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewStatus = (TextView) findViewById(R.id.editText);
        mClientIdInput = (EditText) findViewById(R.id.clientIdInput);
        mClientIdInput.setText(CLIENT_ID);
        mRedirectUriInput = (EditText) findViewById(R.id.redirectUriInput);
        mRedirectUriInput.setText(REDIRECT);
        Button button = (Button) findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });
        mContext = new AuthenticationContext(MainActivity.this, AUTHORITY, true);
    }

    private void authenticate() {
        mContext.acquireToken(MainActivity.this, RESOURCE, mClientIdInput.getText().toString(), mRedirectUriInput.getText().toString(), PromptBehavior.Auto, "", callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mContext != null) {
            mContext.onActivityResult(requestCode, resultCode, data);
        }
    }

    AuthenticationCallback<AuthenticationResult> callback = new AuthenticationCallback<AuthenticationResult>() {

        @Override
        public void onError(Exception exc) {
            if (exc instanceof AuthenticationException) {
                mTextViewStatus.setText("Cancelled");
                Log.d(TAG, "Cancelled");
            } else {
                mTextViewStatus.setText("Authentication error:" + exc.getMessage());
                Log.d(TAG, "Authentication error:" + exc.getMessage());
            }
        }

        @Override
        public void onSuccess(AuthenticationResult result) {
            mResult = result;

            if (result == null || result.getAccessToken() == null
                    || result.getAccessToken().isEmpty()) {
                mTextViewStatus.setText("Token is empty");
                Log.d(TAG, "Token is empty");
            } else {
                // request is successful
                Log.d(TAG, "Status:" + result.getStatus() + " Expired:"
                        + result.getExpiresOn().toString());
                mTextViewStatus.setText("PASSED! Access Token: " + result.getAccessToken() + "\n\nRefresh Token: " + result.getRefreshToken());
            }
        }
    };
}
