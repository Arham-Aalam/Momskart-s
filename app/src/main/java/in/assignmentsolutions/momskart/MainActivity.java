package in.assignmentsolutions.momskart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appus.splash.Splash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    TextView forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Splash.Builder splash = new Splash.Builder(this, getSupportActionBar());
//        splash.setSplashImage(getResources().getDrawable(R.drawable.ic_launcher));
//        splash.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        splash.setSplashImageColor(getResources().getColor(R.color.colorAccent));
//        splash.perform();

        if (SharedPreferencesHelper.isLogged(this, Constants.SHARED_PREF)) {
            //app:bt_titlePadding="@dimen/default_nav_item_title_max_width"
            Intent intent = new Intent(getApplicationContext(), DeshboardActivity.class);
            startActivityForResult(intent, 1);
            finish();
        }

        // TODO: bind the views with variables
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        forgotPass = findViewById(R.id.link_forgot_pass);

        //_emailText.clearFocus();
        //_emailText.setFocusableInTouchMode(false);
        //_emailText.setFocusable(false);
        //_emailText.setFocusableInTouchMode(true);
        //_emailText.setFocusable(true);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(i);
            }
        });

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        makeRequestLogin(email, password);
        progressDialog.dismiss();
        /*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
        */
    }

    void makeRequestLogin(final String email, final String password) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onLoginSuccess(jsonObject.getJSONObject("data"));
                    } else {
                        onLoginFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onLoginFailed();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("email", email);
                MyData.put("password", password);
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(JSONObject jsonObject) {
        _loginButton.setEnabled(true);
        SharedPreferencesHelper.setLogged(this, Constants.SHARED_PREF, jsonObject);
        Intent intent = new Intent(getApplicationContext(), DeshboardActivity.class);
        startActivityForResult(intent, 1);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Wrong Credentials! try again", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError("password more than 4 digits");
            valid = false;
        } else {
            _passwordText.setError(null);
        }



        return valid;
    }

}
