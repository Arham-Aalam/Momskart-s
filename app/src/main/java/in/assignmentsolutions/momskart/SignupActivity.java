package in.assignmentsolutions.momskart;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.assignmentsolutions.momskart.utils.Constants;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    EditText _passwordTextConfirm;
    EditText _phoneText;
    Button _signupButton;
    TextView _loginLink, acceptBtn;
    CheckBox checkBox;
    boolean isAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // TODO: bind the views with variables
        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _passwordTextConfirm = (EditText) findViewById(R.id.input_password_confirm);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
        _phoneText = (EditText) findViewById(R.id.input_mobile);
        acceptBtn = (TextView) findViewById(R.id.id_accept);
        checkBox = findViewById(R.id.id_check_box);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // page to terms and conditions
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                iAccept(b);
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                    R.style.Theme_AppCompat_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();
            final String name = _nameText.getText().toString();
            final String email = _emailText.getText().toString();
            final String password = _passwordText.getText().toString();
            final String mobNum = _phoneText.getText().toString();

            // TODO: Implement your own signup logic here.
            SmartLocation.with(this).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {

                        @Override
                        public void onLocationUpdated(Location location) {
                            final double lat = location.getLatitude();
                            final double log = location.getLongitude();

                            String deviceId = "";
                            if (Build.VERSION.SDK_INT >= 26) {
                                if (ActivityCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    Toast.makeText(getApplicationContext(), "problem while getting Device ID", Toast.LENGTH_SHORT);
                                    return;
                                }
                                deviceId = getSystemService(TelephonyManager.class).getImei();
                            }else{
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    deviceId = getSystemService(TelephonyManager.class).getDeviceId();
                                } else {
                                    TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                                    deviceId = tManager.getDeviceId();
                                }
                            }
                            // Make a signup request
                            makeSignupRequest(name, email, mobNum, password, Double.toString(lat), Double.toString(log), deviceId);
                            progressDialog.dismiss();
                        }


                    });
        }



        /*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
        */
    }

    void makeSignupRequest(final String name, final String email, final String mobNum, final String password , final String lat, final String log, final  String deviceId) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SIGNUP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onSignupSuccess();
                    } else {
                        onSignupFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onSignupFailed();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("full_name", name);
                MyData.put("email", email);
                MyData.put("mobile_number", mobNum);
                MyData.put("password", password);
                MyData.put("latitude", lat);
                MyData.put("longitude", log);
                MyData.put("device_token", deviceId);
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Toast.makeText(this, "You are registered!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 1);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passConfirm = _passwordTextConfirm.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!password.equals(passConfirm)) {
            _passwordTextConfirm.setError("Password does not matched!");
            valid = false;
        }
        if (_phoneText.getText().toString().length() != 10) {
            _phoneText.setError("Mobile number should be of length 10!!");
            valid = false;
        }

        if(!isAccepted) {
            checkBox.setError("Accept terms and conditions");
            valid = false;
        }

            return valid;
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2:
                Log.d(TAG, "request 2");
                final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                        R.style.Theme_AppCompat_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();
                String name = _nameText.getText().toString();
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();

                // TODO: Implement your own signup logic here.
                SmartLocation.with(this).location()
                        .oneFix()
                        .start(new OnLocationUpdatedListener() {

                            @Override
                            public void onLocationUpdated(Location location) {
                                double lat = location.getLatitude();
                                double log = location.getLongitude();
                                Log.d(TAG, lat + " " + log);
                                String deviceId = "";
                                if (Build.VERSION.SDK_INT >= 26) {
                                    if (ActivityCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        Toast.makeText(getApplicationContext(), "problem while getting Device ID", Toast.LENGTH_SHORT);
                                        return;
                                    }
                                    deviceId = getSystemService(TelephonyManager.class).getImei();
                                }else{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        deviceId = getSystemService(TelephonyManager.class).getDeviceId();
                                    } else {
                                        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                                        deviceId = tManager.getDeviceId();
                                    }
                                }

                                // Make a signup request
                                Toast.makeText(getApplicationContext(), lat + " " + log + " " + deviceId, Toast.LENGTH_SHORT);
                                Log.d(TAG, lat + " " + log + " " + deviceId);
                                progressDialog.dismiss();
                                onSignupSuccess();
                            }


                        });
                break;
        }
    }

    private void iAccept(boolean flag) {
        isAccepted = flag;
    }
}

