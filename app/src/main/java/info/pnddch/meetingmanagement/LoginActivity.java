package info.pnddch.meetingmanagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.pnddch.meetingmanagement.utilInterface.CMResponse;
import info.pnddch.meetingmanagement.utilities.CommunicationManager;
import info.pnddch.meetingmanagement.utilities.FERRPDialogs;
import info.pnddch.meetingmanagement.utilities.SessionManager;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, CMResponse {
    private static final String[] DUMMY_CREDENTIALS = new String[]{"foo@example.com:hello", "bar@example.com:world"};
    private static final int REQUEST_READ_CONTACTS = 0;
    Context context;
    private CommunicationManager mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private View mLoginFormView;
    private EditText mPasswordView;
    private View mProgressView;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;

    class C03181 implements TextView.OnEditorActionListener {
        C03181() {
        }

        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id != R.id.login && id != 0) {
                return false;
            }
            LoginActivity.this.attemptLogin();
            return true;
        }
    }

    class C03192 implements OnClickListener {
        C03192() {
        }

        public void onClick(View view) {
            LoginActivity.this.attemptLogin();
        }
    }

    class C03203 implements OnClickListener {
        C03203() {
        }

        public void onClick(View view) {
//            LoginActivity.this.startActivity(new Intent(LoginActivity.this.context, ForgetPasswordActivity.class));
            String url = "http://pnddch.info/password_reset/";
//            url = "http://pnddch.info/mm/";
            Intent intentURLViewerActivity = new Intent(context, URLViewerActivity.class);
            intentURLViewerActivity.putExtra("url", url);
            intentURLViewerActivity.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivityIfNeeded(intentURLViewerActivity, 0);

        }
    }

    class C03214 implements OnClickListener {
        C03214() {
        }

        @TargetApi(23)
        public void onClick(View v) {
            LoginActivity.this.requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 0);
        }
    }

    private interface ProfileQuery {
        public static final int ADDRESS = 0;
        public static final int IS_PRIMARY = 1;
        public static final String[] PROJECTION = new String[]{"data1", "is_primary"};
    }

    protected void onCreate(Bundle savedInstanceState) {
        this.context = this;
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        this.mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        this.mPasswordView = (EditText) findViewById(R.id.password);
        this.mPasswordView.setOnEditorActionListener(new C03181());
        ((Button) findViewById(R.id.email_sign_in_button)).setOnClickListener(new C03192());
        this.mLoginFormView = findViewById(R.id.login_form);
        this.mProgressView = findViewById(R.id.login_progress);
        ((Button) findViewById(R.id.btnForgetPassWord)).setOnClickListener(new C03203());
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String server_client_id = "60779150521-kuropp0psgvu9b8kv1gus7e32enmhdoo.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(server_client_id)
                .requestServerAuthCode(server_client_id)
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
            SessionManager.setToken(this, account.getIdToken());
            SessionManager.setAuthCode(this, account.getServerAuthCode());
            SessionManager.setUserId(this, account.getId());
            startActivity(new Intent(this, MainActivity.class));
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    private void populateAutoComplete() {
        if (mayRequestContacts()) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @SuppressLint("ResourceType")
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
            Snackbar.make(this.mEmailView, (int) R.string.permission_rationale, -2).setAction(17039370, new C03214());
        } else {
            requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 0);
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && grantResults.length == 1 && grantResults[0] == 0) {
            populateAutoComplete();
        }
    }

    private void attemptLogin() {
        if (this.mAuthTask == null) {
            this.mEmailView.setError(null);
            this.mPasswordView.setError(null);
            String email = this.mEmailView.getText().toString();
            String password = this.mPasswordView.getText().toString();
//            String email = "abbasgis@gmail.com";
//            String password = "shakir123@abc";

            boolean cancel = false;
            View focusView = null;
            if (TextUtils.isEmpty(password)) {
                this.mPasswordView.setError(getString(R.string.error_field_required));
                focusView = this.mPasswordView;
                cancel = true;
            }
            if (!(TextUtils.isEmpty(password) || isPasswordValid(password))) {
                this.mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = this.mPasswordView;
                cancel = true;
            }
            if (TextUtils.isEmpty(email)) {
                this.mEmailView.setError(getString(R.string.error_field_required));
                focusView = this.mEmailView;
                cancel = true;
            }
            if (cancel) {
                focusView.requestFocus();
                return;
            }
            showProgress(true);
            new CommunicationManager(this).getRequest("mb_login/?email=" + email + "&password=" + password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(13)
    private void showProgress(final boolean show) {
        float f = 1.0f;
        int i = 8;
        int i2 = 0;
        int i3;
        if (Build.VERSION.SDK_INT >= 13) {
            float f2;
            @SuppressLint("ResourceType") int shortAnimTime = getResources().getInteger(17694720);
            View view = this.mLoginFormView;
            if (show) {
                i3 = 8;
            } else {
                i3 = 0;
            }
            view.setVisibility(i3);
            ViewPropertyAnimator duration = this.mLoginFormView.animate().setDuration((long) shortAnimTime);
            if (show) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            duration.alpha(f2).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            View view2 = this.mProgressView;
            if (!show) {
                i2 = 8;
            }
            view2.setVisibility(i2);
            ViewPropertyAnimator duration2 = this.mProgressView.animate().setDuration((long) shortAnimTime);
            if (!show) {
                f = 0.0f;
            }
            duration2.alpha(f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            return;
        }
        View view3 = this.mProgressView;
        if (show) {
            i3 = 0;
        } else {
            i3 = 8;
        }
        view3.setVisibility(i3);
        View view2 = this.mLoginFormView;
        if (!show) {
            i = 0;
        }
        view2.setVisibility(i);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, "data"), ProfileQuery.PROJECTION, "mimetype = ?", new String[]{"vnd.android.cursor.item/email_v2"}, "is_primary DESC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(0));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @SuppressLint("ResourceType")
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        this.mEmailView.setAdapter(new ArrayAdapter(this, 17367050, emailAddressCollection));
    }

    public void consumeResponse(String response, boolean success) {
        showProgress(false);
        if (success) {
            try {
                JSONObject resObj = new JSONObject(response);
                if (resObj.getString("result").equalsIgnoreCase("200")) {
                    String token = resObj.getString("token");
                    int userId = resObj.getInt("userid");
                    SessionManager.setToken(this, token);
                    SessionManager.setUserId(this, String.valueOf(userId));
                    SessionManager.setTaskSyncDate(this, null);
                    SessionManager.setUsersSyncDate(this, null);
                    startActivity(new Intent(this, MainActivity.class));
                    FERRPDialogs.showFERRPInfoAlert(this, "Info", "Successfully LogIn");
                    return;
                }
                FERRPDialogs.showFERRPInfoAlert(this, "Info", "Attempt Failed");
                return;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        FERRPDialogs.showFERRPInfoAlert(this, "Info", "Attempt Failed");
    }


}
