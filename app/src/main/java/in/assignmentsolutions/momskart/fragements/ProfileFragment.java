package in.assignmentsolutions.momskart.fragements;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swiftsynq.otpcustomview.CustomOtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import in.assignmentsolutions.momskart.MainActivity;
import in.assignmentsolutions.momskart.R;
import in.assignmentsolutions.momskart.utils.AppHelper;
import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;
import in.assignmentsolutions.momskart.utils.VolleyMultipartRequest;
import in.assignmentsolutions.momskart.utils.VolleySingleton;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    Button logoutBtn;
    ImageView profileImage;
    TextView emailText;
    TextView phoneText;
    TextView nameText, adharText, bankText, panText, addressText, passwordText;

    String imagePath = null, infoImagePath;

    ImageView infoImage = null;

    static final int RESULT_OK = 7, RESULT_OK2 = 8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        logoutBtn = v.findViewById(R.id.id_logout);
        profileImage = v.findViewById(R.id.id_profile_image);
        emailText = v.findViewById(R.id.id_profile_email);
        phoneText = v.findViewById(R.id.id_profile_number);
        nameText = v.findViewById(R.id.id_profile_name);

        adharText = v.findViewById(R.id.id_adhar);
        bankText = v.findViewById(R.id.id_bank);
        panText = v.findViewById(R.id.id_pan);
        addressText = v.findViewById(R.id.id_address);
        passwordText = v.findViewById(R.id.id_password);

        listeners(v);

        loadBasisDetails();

        return v;
    }

    private void loadBasisDetails() {
        String name = SharedPreferencesHelper.getAttb(getContext(), Constants.SHARED_PREF, "name");
        String email = SharedPreferencesHelper.getAttb(getContext(), Constants.SHARED_PREF, "email");
        String phone = SharedPreferencesHelper.getAttb(getContext(), Constants.SHARED_PREF, "mobile");
        String profileImg = SharedPreferencesHelper.getAttb(getContext(), Constants.SHARED_PREF, "profile");

        emailText.setText(email);
        nameText.setText(name);
        phoneText.setText(phone);
        // Glide to make image request
    }

    private void listeners(View v) {
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelper.setLogout(getContext(), Constants.SHARED_PREF);
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile("Update Profile");
            }
        });

        ((LinearLayout) v.findViewById(R.id.id_cont_email)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile("Update Profile");
            }
        });

        ((LinearLayout) v.findViewById(R.id.id_cont_phone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile("Update Profile");
            }
        });

        ((LinearLayout) v.findViewById(R.id.id_cont_adhar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAdhar();
            }
        });

        ((LinearLayout) v.findViewById(R.id.id_cont_bank)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBankDetails();
            }
        });

        ((LinearLayout) v.findViewById(R.id.id_cont_pan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePan();
            }
        });

        ((LinearLayout) v.findViewById(R.id.id_cont_address)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAddressDetails();
            }
        });

        passwordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void updateProfile(String title) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_profile_options, null);

        dialogView.findViewById(R.id.id_update_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                uploadImage(7);
            }
        });

        dialogView.findViewById(R.id.id_update_rest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                updateRest();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    private void uploadImage(int IMG_CODE) {
        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2000);
        }
        else {
            startGallery(IMG_CODE);
        }
    }

    private void startGallery(int IMG_CODE) {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(cameraIntent, IMG_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_OK){
            if (null == data)
                return;
            Uri returnUri = data.getData();
            Bitmap bitmapImage = null;
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                profileImage.setImageBitmap(bitmapImage);
                profileImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                profileImage.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                imagePath = getPath(returnUri);
                updateImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == RESULT_OK2) {
            if (null == data)
                return;
            Uri returnUri = data.getData();
            Bitmap bitmapImage = null;
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                infoImage.setImageBitmap(bitmapImage);
                infoImage.setScaleType(ImageView.ScaleType.FIT_XY);
                infoImage.setAdjustViewBounds(true);
                infoImagePath = getPath(returnUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //getUserDetail
        //http://assignmentsolutions.in/momskartWebservice/mobileapi/
    }

    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getContext(),    contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void updateRest() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_email_phone, null);

        ((TextView)dialogView.findViewById(R.id.id_email)).setText(emailText.getText().toString());
        ((TextView)dialogView.findViewById(R.id.id_phone)).setText(phoneText.getText().toString());
        ((TextView)dialogView.findViewById(R.id.id_name)).setText(nameText.getText().toString());

        dialogView.findViewById(R.id.id_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        dialogView.findViewById(R.id.id_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkInfo(((EditText)dialogView.findViewById(R.id.id_name)), ((EditText)dialogView.findViewById(R.id.id_email)), ((EditText)dialogView.findViewById(R.id.id_phone))))
                    return;
                ((LinearLayout)dialogView.findViewById(R.id.btn_container)).setVisibility(View.GONE);
                ((ProgressBar)dialogView.findViewById(R.id.id_progress_bar)).setVisibility(View.VISIBLE);
                updateEmailPhone(((EditText)dialogView.findViewById(R.id.id_name)).getText().toString(), ((EditText)dialogView.findViewById(R.id.id_email)).getText().toString(), ((EditText)dialogView.findViewById(R.id.id_phone)).getText().toString());
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void updateEmailPhone(String name, String email, String phone) {

        final String user_id = SharedPreferencesHelper.getUserInfo(getContext(), Constants.SHARED_PREF);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_NAME_PHONE_EMAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onUpdateResponse(jsonObject);
                    } else {
                        onUpdateFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", user_id);
                MyData.put("name", name);
                MyData.put("email", email);
                MyData.put("mobile", phone);
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onUpdateResponse(JSONObject jsonObject) {
        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
        try {
            JSONObject object = jsonObject.getJSONObject("result");
            SharedPreferencesHelper.setAttb(getContext(), Constants.SHARED_PREF, object.getString("name"), "NAME");
            SharedPreferencesHelper.setAttb(getContext(), Constants.SHARED_PREF, object.getString("email"), "EMAIL");
            SharedPreferencesHelper.setAttb(getContext(), Constants.SHARED_PREF, object.getString("mobile"), "MOBILE");

            nameText.setText(object.getString("name"));
            phoneText.setText(object.getString("mobile"));
            emailText.setText(object.getString("email"));

            SharedPreferencesHelper.setAttb(getContext(), Constants.SHARED_PREF, object.getString("name"), "name");
            SharedPreferencesHelper.setAttb(getContext(), Constants.SHARED_PREF, object.getString("email"), "email");
            SharedPreferencesHelper.setAttb(getContext(), Constants.SHARED_PREF, object.getString("mobile"), "mobile");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void onUpdateFailed() {
        Toast.makeText(getContext(), "Something Went wrong!", Toast.LENGTH_SHORT).show();
    }

    private boolean checkInfo(EditText name, EditText email, EditText phone) {
        if (name.getText().toString().equals("")) {
            name.setText("Add name!");
            return false;
    }
        if(email.getText().toString().equals("")) {
            email.setText("Add email!");
            return false;
        }

        if(phone.getText().toString().length() != 10) {
            phone.setError("Add phone with 10 digits!");
            return false;
        }
        return true;
    }

    private void updateImage() {

        final String user_id = SharedPreferencesHelper.getUserInfo(getContext(), Constants.SHARED_PREF);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.UPDATE_PROFILE_IMAGE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                System.out.println( "PRO: " + resultResponse);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        System.out.println("pr:" + jsonObject.toString());
                        //SharedPreferencesHelper.setAttb(getContext(), Constants.SHARED_PREF, jsonObject.getString("url"), "profile");
                        //Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageUpdateFailed();
                Toast.makeText(getContext(), "Unable to upload your pic.", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("user_image", new DataPart("image.jpg", AppHelper.getFileDataFromDrawable(getContext(), profileImage.getDrawable()), "image/jpeg"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
    }

    private void imageUpdateFailed() {
        // update previous image
    }

    private void updateInfo(String title, int itemCount) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_text_dialog_layout, null);

        CustomOtpView otpView = dialogView.findViewById(R.id.id_info_input);

        otpView.setItemCount(itemCount);

        ((TextView)dialogView.findViewById(R.id.id_add_info)).setText(title);

        ((ImageView)dialogView.findViewById(R.id.id_add_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // upload image
                infoImage = view.findViewById(R.id.id_add_image);
                uploadImage(8);
            }
        });

        dialogView.findViewById(R.id.id_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        dialogView.findViewById(R.id.id_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.equals("Add Adhar"))
                    uploadInfo(otpView.getText().toString(), Constants.UPDATE_PROFILE_ADHAR, "aadhar_number", "aadhar_png");
                else if(title.equals("Add Pan"))
                    uploadInfo(otpView.getText().toString(), Constants.UPDATE_PROFILE_PAN, "pancard", "pan_png");
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void uploadInfo(String data, String url, String paramKey, String imageKey) {
        final String user_id = SharedPreferencesHelper.getUserInfo(getContext(), Constants.SHARED_PREF);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageUpdateFailed();
                Toast.makeText(getContext(), "Something Went wrong!!", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put(paramKey, data);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put(imageKey, new DataPart("image.jpg", AppHelper.getFileDataFromDrawable(getContext(), infoImage.getDrawable()), "image/jpeg"));
                //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
    }

    private void updateAdhar() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_profile_options, null);
        CardView option1 =  dialogView.findViewById(R.id.id_update_pic);
        CardView option2  = dialogView.findViewById(R.id.id_update_rest);
        ((TextView)option1.getChildAt(0)).setText("View Adhar Details");
        ((TextView)option2.getChildAt(0)).setText("Edit Adhar Details");
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                // see details preview
                previewAPInfo("aadhar_number", "aadhar_png");
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                updateInfo("Add Adhar", 12);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    private void updatePan() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_profile_options, null);
        CardView option1 =  dialogView.findViewById(R.id.id_update_pic);
        CardView option2  = dialogView.findViewById(R.id.id_update_rest);
        ((TextView)option1.getChildAt(0)).setText("View PAN Details");
        ((TextView)option2.getChildAt(0)).setText("Edit PAN Details");
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                // see details preview
                previewAPInfo("pancard", "pan_png");
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                updateInfo("Add Pan", 10);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void updateBankDetails() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_profile_options, null);
        CardView option1 =  dialogView.findViewById(R.id.id_update_pic);
        CardView option2  = dialogView.findViewById(R.id.id_update_rest);
        ((TextView)option1.getChildAt(0)).setText("View Bank Details");
        ((TextView)option2.getChildAt(0)).setText("Edit Bank Details");
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                // see details preview
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                editBank();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void updateAddressDetails() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_profile_options, null);
        CardView option1 =  dialogView.findViewById(R.id.id_update_pic);
        CardView option2  = dialogView.findViewById(R.id.id_update_rest);
        ((TextView)option1.getChildAt(0)).setText("View address Details");
        ((TextView)option2.getChildAt(0)).setText("Edit address Details");
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                // see details preview
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                editAddress();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void previewAPInfo(String numberKey, String imageKey) {
        String number = SharedPreferencesHelper.getAttb(getContext(), Constants.SHARED_PREF, numberKey);
        String imgUrl = SharedPreferencesHelper.getAttb(getContext(), Constants.SHARED_PREF, imageKey);

        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.preview_ap_details_dialog, null);

        ImageView btn = dialogView.findViewById(R.id.id_btn_back);

        TextView text = dialogView.findViewById(R.id.id_number);
        text.setText(number);

        // Load image here

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void editAddress() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_address_details_layout, null);


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

//        RelativeLayout submit_layout;
//        EditText et_first_name;
//        EditText et_last_name;
//        EditText et_company_name;
//        EditText et_full_add;
//        EditText city_name_list;
//        EditText country_name_list;
//        EditText state_name_list;
//        EditText et_pincode;
//        EditText et_mobile;
//        ImageView img_back;
//        Spinner citySpinner ;
//        Spinner countrySpinner ;
//        Spinner stateSpinner ;
//        ArrayList<CountryResult>country_list;
//        ArrayList<CityResult>city_list;
//        ArrayList<StateResult>state_list;
//        //ArrayList<String> country_name=new ArrayList<String>();
//        //ArrayList<String>city_name=new ArrayList<String>();
//        Boolean IsEdit= false;
//        String delivery_id ="";
//        String countryID;
//        String cityId;
//        String stateId;
//        String name;
//        String lastName;
//        String companyName;
//        String fullAdd;
//        String pinCode;
//        String mobile;
//        TextView text_toolbar;
//        ImageView img_cart;
//        TextView  txt_count ;
//        ArrayAdapter<CountryData> dataAdapter = null;
//        ArrayAdapter<StateData> stateDataAdapter=null;
//        ArrayAdapter<CityData> cityDataAdapter=null;
//        final ArrayList<CountryData> country_name=new ArrayList<CountryData>();
//        final ArrayList<StateData> state_name=new ArrayList<StateData>();
//        final ArrayList<CityData> city_name=new ArrayList<CityData>();
//        String cityName;
//        String stateName;
//        String countryName;
    }

    private void previewAddress() {

    }

    private void editBank() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_bank_details_layout, null);


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void getCountryListData() {

    }

    private void getCityListData() {

    }

    private void getStateListData() {

    }

}
