package in.assignmentsolutions.momskart;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.assignmentsolutions.momskart.fragements.DeshboardFragment;
import in.assignmentsolutions.momskart.fragements.ProductsFragment;
import in.assignmentsolutions.momskart.fragements.ProfileFragment;
import in.assignmentsolutions.momskart.fragements.UploadFragment;
import in.assignmentsolutions.momskart.fragements.WalletFragment;
import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.CountDrawable;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;

public class DeshboardActivity extends AppCompatActivity {

    BubbleNavigationConstraintView bubbleNavigationConstraintView;
    BubbleToggleView v1, v2, v3, v4, v5;

    String TAG = "DESHBOARD_CLASS";

    Menu defaultMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deshboard);

        bubbleNavigationConstraintView = findViewById(R.id.bottom_navigation_constraint);
        v1 = findViewById(R.id.c_item_rest);
        v2 = findViewById(R.id.c_item_rest_2);
        v3 = findViewById(R.id.c_item_rest_3);
        v4 = findViewById(R.id.c_item_rest_4);
        v5 = findViewById(R.id.c_item_rest_5);


        bubbleNavigationConstraintView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                Log.d(TAG, "position " + position);
                switch (position) {
                    case 0:
                        DeshboardFragment deshboardFragment = new DeshboardFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.contentView, deshboardFragment);
                        ft.commit();
                        break;
                    case 1:
                        try {
                            //getSupportActionBar().hide();
                        } catch (Exception e) {
                            //Toast.makeText(getContext(), "Exception in hiding the bar!!", Toast.LENGTH_LONG).show();
                        }
                        ProductsFragment productsFragment = new ProductsFragment();
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        ft2.replace(R.id.contentView, productsFragment);
                        ft2.commit();
                        break;
                    case 2:
                        UploadFragment uploadFragment = new UploadFragment();
                        FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                        ft3.replace(R.id.contentView, uploadFragment);
                        ft3.commit();
                        break;
                    case 3:
                        WalletFragment walletFragment = new WalletFragment();
                        FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                        ft4.replace(R.id.contentView, walletFragment);
                        ft4.commit();
                        break;
                    case 4:
                        ProfileFragment profileFragment = new ProfileFragment();
                        FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                        ft5.replace(R.id.contentView, profileFragment);
                        ft5.commit();
                        break;
                }
            }
        });

        if (SharedPreferencesHelper.getAttb(getApplicationContext(), Constants.SHARED_PREF, "wallet").equals("-1")) {
            initProfileBasicDetailRequest();
        }

        if(SharedPreferencesHelper.getAttb(getApplicationContext(), Constants.SHARED_PREF, "created_date").equals("-1")) {
            // get profile
            initProfileDetail();
        }

        DeshboardFragment deshboardFragment = new DeshboardFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentView, deshboardFragment);
        ft.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // set notification count
        getMenuInflater().inflate(R.menu.main_menu, menu);
        defaultMenu = menu;
        setCount(this, "9");
        return true;
    }

    public void setCount(Context context, String count) {

        MenuItem menuItem = defaultMenu.findItem(R.id.ic_notification);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_notification:
                Toast.makeText(getApplicationContext(), "Notification in progress", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.ic_help:
                Toast.makeText(getApplicationContext(), "Help in progress", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initProfileDetail() {
        final String user_id = SharedPreferencesHelper.getUserInfo(getApplicationContext(), Constants.SHARED_PREF);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_SELLER_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onProfileResponse(jsonObject);
                    } else {
                        onProfileResponseFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onProfileResponseFailed();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", user_id);
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onProfileResponse(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject.getJSONObject("result");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("aadhar_number"), "aadhar_number");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("pancard"), "pancard");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("aadhar_png"), "aadhar_png");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("pan_png"), "pan_png");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("full_address"), "full_address");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("bank_account"), "bank_account");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("bank_ifsc"), "bank_ifsc");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("bank_account_holder"), "bank_account_holder");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("passbook_png"), "passbook_png");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("created_date"), "created_date");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void onProfileResponseFailed() {
        // give dialog warning for internet connection
    }

    private void initProfileBasicDetailRequest() {
        final String user_id = SharedPreferencesHelper.getUserInfo(getApplicationContext(), Constants.SHARED_PREF);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_USER_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onDetailResponse(jsonObject);
                    } else {
                        onProfileResponseFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onProfileResponseFailed();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", user_id);
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onDetailResponse(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject.getJSONObject("result");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("name"), "name");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("email"), "email");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("mobile"), "mobile");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("profile"), "profile");
            SharedPreferencesHelper.setAttb(getApplicationContext(),Constants.SHARED_PREF, data.getString("wallet"), "wallet");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
