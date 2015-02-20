package com.Fiplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Credentials;
import com.wordnik.client.model.WhoAmI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import utils.IAppConstants;
import utils.PrefUtil;

/**
* A login screen that offers login via email/password.
*/
public class LoginActivity extends BaseFragmentActivity implements LoaderCallbacks<Cursor>{
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView mFitLogo;
    private TextView mFitTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFitLogo = (ImageView)findViewById(R.id.login_fit_logo);
        mFitLogo.setImageResource(R.drawable.fiplus);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mFitTerms = (TextView) findViewById(R.id.loginPolicy);
        setPrivacyTerms();

    }

    private void setPrivacyTerms()
    {
        String text = mFitTerms.getText().toString();
        int privacy1 = text.indexOf("Privacy");
        int privacy2 = privacy1 + 13;

        int terms1 = text.indexOf("Terms");
        int terms2 = terms1 + 11;

        mFitTerms.setMovementMethod(LinkMovementMethod.getInstance());
        mFitTerms.setText(text, TextView.BufferType.SPANNABLE);

        Spannable mySpannable = (Spannable)mFitTerms.getText();
        ClickableSpan privacySpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(getBaseContext(), PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        };

        ClickableSpan termsSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(getBaseContext(), TermsOfUseActivity.class);
                startActivity(intent);
            }
        };
        mySpannable.setSpan(privacySpan, privacy1, privacy2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mySpannable.setSpan(termsSpan, terms1, terms2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address and password.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        else if(TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }


    /**
     * Creates a new intent for SignUpActivity
     */
    public void signUpFunction(View view)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {

            UsersApi userApi = new UsersApi();
            userApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            userApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
            Credentials credentials = new Credentials();
            credentials.setEmail(mEmail);
            credentials.setPassword(mPassword);
            try {
                userApi.getInvoker().setContext(getApplicationContext());
                userApi.login(credentials);

                WhoAmI id = userApi.whoAmI();

                PrefUtil.putString(getApplicationContext(), IAppConstants.DSP_URL, IAppConstants.DSP_URL);
                PrefUtil.putString(getApplicationContext(), IAppConstants.EMAIL, mEmail);
                PrefUtil.putString(getApplicationContext(), IAppConstants.PWD, mPassword);
                PrefUtil.putString(getApplicationContext(), IAppConstants.USER_ID, id.getUser_id());
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mAuthTask = null;
            showProgress(false);

            if (result !=null){
                String errorMsg = "";
                try {
                    JSONObject jObj = new JSONObject(result);
                    JSONArray jArray = jObj.getJSONArray("error");
                    JSONObject obj = jArray.getJSONObject(0);
                    errorMsg = obj.getString("message");
                } catch (JSONException e) {
                    errorMsg = result;
                }
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setTitle("Error").setMessage(errorMsg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else {
                Intent in= new Intent(LoginActivity.this,MainScreenActivity.class);
                startActivity(in);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}



