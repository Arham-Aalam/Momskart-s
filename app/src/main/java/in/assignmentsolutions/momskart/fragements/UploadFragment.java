package in.assignmentsolutions.momskart.fragements;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import in.assignmentsolutions.momskart.DeshboardActivity;
import in.assignmentsolutions.momskart.MainActivity;
import in.assignmentsolutions.momskart.R;
import in.assignmentsolutions.momskart.utils.AppHelper;
import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;
import in.assignmentsolutions.momskart.utils.VolleyMultipartRequest;
import in.assignmentsolutions.momskart.utils.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {


    public UploadFragment() {
        // Required empty public constructor
    }

    ImageView imageView;
    EditText p_name, p_description;
    Button priceBtn, discountBtn, quantityBtn, stockBtn, submitBtn;
    TextView hiddenTextView;
    SearchableSpinner superCat, category, subCat;
    SearchableSpinner unit;

    Integer price = -1, discount = -1, quantityNum = -1, stockNum = -1;

    Typeface tf;

    List<String> superCategories = null, categories = null, subCategories = null, units = null;
    ArrayList<Integer> superCategoriesIds, categoriesIds, subCategoriesIds, unitsIds;

    int RESULT_OK = 7;

    Integer superCat_id = -1, cat_id = -1, sub_id = -1, unit_id = -1;
    String user_id;

    private String imagePath = null;

    Context ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload, container, false);

        imageView = v.findViewById(R.id.id_img_upload);
        p_name = v.findViewById(R.id.input_name);
        p_description = v.findViewById(R.id.input_description);
        priceBtn = v.findViewById(R.id.id_pick_price);
        discountBtn = v.findViewById(R.id.id_pick_discount);
        hiddenTextView = v.findViewById(R.id.id_total_price);
        quantityBtn = v.findViewById(R.id.id_pick_quantity);
        stockBtn = v.findViewById(R.id.id_pick_stock);
        submitBtn = v.findViewById(R.id.id_submit);

        superCat = v.findViewById(R.id.id_cat_super);
        category = v.findViewById(R.id.id_cat);
        subCat = v.findViewById(R.id.id_sub_cat);
        unit = v.findViewById(R.id.id_add_unit);

        ctx = container.getContext();

        //tf = Typeface.createFromAsset(v.getContext().getAssets(), "font/Rupee.ttf");

        listeners();

        getSuperCategories();

        //TODO: remove if category and subcategory needed
        category.setVisibility(View.GONE);
        subCat.setVisibility(View.GONE);
        getUnits();

        return v;
    }

    private void listeners() {
        priceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNumber2("Add Price", "what's your price?", 1, 10000, view, 1);
            }
        });

        discountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNumber2("Add Discount", "optional", 0, 100, view, 2);
            }
        });

        quantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNumber2("Add Quantity", "optional", 0, 1000, view, 3);
            }
        });

        stockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNumber2("Available stocks", "optional", 0, 100, view, 4);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // submit the form
                submitForm();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }
                else {
                    startGallery();
                }
            }
        });
    }

    public void pickNumber(String title, String message, int minValue, int maxValue, View v, final int ID) {
        final AlertDialog.Builder d = new AlertDialog.Builder(v.getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_layout, null);
        d.setTitle(title);
        d.setMessage(message);
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setMinValue(minValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d("UPLOAD_FRAG", "onValueChange: ");
                valuePickedCallback(ID, i1);
            }
        });
        d.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("UPLOAD_FLAG", "onClick: " + numberPicker.getValue());
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    private void pickNumber2(String title, String message, int minValue, int maxValue, View v, final int ID) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_text_dialog, null);
        ((TextView)dialogView.findViewById(R.id.id_dialog_title)).setText(title);
        final EditText editText = (EditText) dialogView.findViewById(R.id.id_dialog_edit_text);
        Button button1 = (Button) dialogView.findViewById(R.id.id_cancel);
        Button button2 = (Button) dialogView.findViewById(R.id.id_done);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valuePickedCallback(ID, Integer.parseInt(editText.getText().toString()));
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void valuePickedCallback(int id, int num) {
        switch (id) {
            case 1:
                price = num;
                int t = num + (num*35)/100 + 75;
                //tf = Typeface.createFromAsset(getAssets(), "font/Rupee.ttf");
                //hiddenTextView.setTypeface(tf);
                hiddenTextView.setText("Total price with 35% charge and 75 rs. delivery charges = " + t + " rs.");
                hiddenTextView.setVisibility(View.VISIBLE);
                priceBtn.setText("Price: " + num);
                break;
            case 2:
                discount = num;
                discountBtn.setText("Discount: "+ num + "%");
                break;
            case 3:
                // set quantity
                quantityBtn.setText("quantity: " + num);
                quantityNum = num;
                break;
            case 4:
                // set stock available
                stockBtn.setText("available stock: " + num);
                stockNum = num;
                break;
        }
    }

    public void getSuperCategories() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.SUPER_CAT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onSuperCatFetchSuccess(jsonObject);
                    } else {
                        Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_SHORT).show();
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
//
//            protected Map<String, String> getParams() {
//                Map<String, String> MyData = new HashMap<String, String>();
//                MyData.put("email", email);
//                MyData.put("password", password);
//                return MyData;
//            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onSuperCatFetchSuccess(JSONObject jsonObject) {
        superCategories = new ArrayList<>();
        superCategoriesIds = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("super_cat_list");

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                superCategories.add(obj.getString("name"));
                superCategoriesIds.add(obj.getInt("category_id_PK"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // writing list in spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_spinner_item, superCategories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        superCat.setAdapter(dataAdapter);

        superCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getContext(), "Item pos: " + i, Toast.LENGTH_SHORT).show();
                superCat_id = superCategoriesIds.get(i);

                //TODO: uncomment if category needed
                //getCategories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getCategories() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onCategorySuccess(jsonObject);
                    } else {
                        Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("super_cat_id", Integer.toString(superCat_id));
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onCategorySuccess(JSONObject jsonObject) {
        if (categories != null) {
            categories.clear();
            categoriesIds.clear();
        } else {
            categories = new ArrayList<>();
            categoriesIds = new ArrayList<>();
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("category_list");

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                categories.add(obj.getString("name"));
                categoriesIds.add(obj.getInt("category_id_PK"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // writing list in spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getContext(), "Item 2 pos : " + i, Toast.LENGTH_SHORT).show();
                cat_id = categoriesIds.get(i);

                //TODO: uncomment if sub-category needed
                //getSubCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getSubCategory() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SUB_CATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onSubCategorySuccess(jsonObject);
                    } else {
                        Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("cat_id", Integer.toString(cat_id));
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onSubCategorySuccess(JSONObject jsonObject) {
        if (subCategories != null) {
            subCategories.clear();
            subCategoriesIds.clear();
        } else {
            subCategories = new ArrayList<>();
            subCategoriesIds = new ArrayList<>();
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("subcategory");

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                subCategories.add(obj.getString("name"));
                subCategoriesIds.add(obj.getInt("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // writing list in spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_spinner_item, subCategories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCat.setAdapter(dataAdapter);

        subCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sub_id = subCategoriesIds.get(i);
                getUnits();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getUnits() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.UNITS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onUnitsSuccess(jsonObject);
                    } else {
                        Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Unable to get data, try again later", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
//
//            protected Map<String, String> getParams() {
//                Map<String, String> MyData = new HashMap<String, String>();
//                MyData.put("cat_id", Integer.toString(cat_id));
//                return MyData;
//            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onUnitsSuccess(JSONObject jsonObject) {
        if (units != null) {
            units.clear();
            unitsIds.clear();
        } else {
            units = new ArrayList<>();
            unitsIds = new ArrayList<>();
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("unit_list");

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                units.add(obj.getString("unit"));
                unitsIds.add(obj.getInt("unit_id_PK"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // writing list in spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_spinner_item, units);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unit.setAdapter(dataAdapter);

        unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                unit_id = unitsIds.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(cameraIntent, RESULT_OK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getContext(), resultCode + " " + requestCode, Toast.LENGTH_LONG).show();

            if(requestCode == RESULT_OK){
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                    imageView.setImageBitmap(bitmapImage);
                    imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    imagePath = getPath(returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2000:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGallery();
                } else {
                    Toast.makeText(getContext(), "No permission to access media", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    // Get Path of selected image
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

    private void submitForm() {
        submitBtn.setVisibility(View.GONE);
        if (checkForm()) {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.ADD_PRODUCT_URl, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    System.out.println( "product_data= " + resultResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(resultResponse);
                        if (jsonObject.getBoolean("status")) {
                            onSubmitSuccess();
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
                    Toast.makeText(getContext(), "Something Went wrong!!", Toast.LENGTH_LONG).show();
                    submitBtn.setVisibility(View.GONE);
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("product_id", "");
                    params.put("user_id", user_id);
                    params.put("product_name", p_name.getText().toString());
                    params.put("description", p_description.getText().toString());
                    params.put("price", Integer.toString(price + (35*price)/100 + 75));
                    params.put("seller_price", Integer.toString(price));
                    params.put("discount_price", Integer.toString(discount));
                    params.put("super_cat_id", Integer.toString(superCat_id));
                    params.put("cat_id", Integer.toString(cat_id));
                    params.put("sub_cat_id", Integer.toString(sub_id));
                    params.put("unit_id", Integer.toString(unit_id));
                    params.put("unit_value", Integer.toString(quantityNum));
                    params.put("stock", Integer.toString(stockNum));
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView
                    params.put("image", new DataPart("image.jpg", AppHelper.getFileDataFromDrawable(getContext(), imageView.getDrawable()), "image/jpeg"));
                    //params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                    return params;
                }
            };
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        }
    }

    // upload checks
    private boolean checkForm() {
        if (imagePath == null) {
            Toast.makeText(getContext(), "please add an image", Toast.LENGTH_LONG).show();
            return false;
        }
        if (p_name.getText().toString().isEmpty()) {
            p_name.setError("what is product name?");
            return false;
        }
        if (p_description.getText().toString().isEmpty()) {
            p_description.setError("provide some details.");
            return false;
        }
        if (price == -1) {
            priceBtn.setError("Add price.");
            return false;
        }
        if (discount == -1) {
            discount = 0;
        }
        if (superCat_id == -1) {
            Toast.makeText(getContext(), "choose super category", Toast.LENGTH_LONG).show();
            return false;
        }
        if (cat_id == -1) {
            Toast.makeText(getContext(), "choose category", Toast.LENGTH_LONG).show();
            return false;
        }
        if (sub_id == -1) {
            Toast.makeText(getContext(), "choose subcategory", Toast.LENGTH_LONG).show();
            return false;
        }
        if (unit_id == -1) {
            Toast.makeText(getContext(), "choose unit", Toast.LENGTH_LONG).show();
            return false;
        }
        if (quantityNum == -1) {
            quantityBtn.setError("how much quantity?");
            return false;
        }
        if (stockNum == -1) {
            stockBtn.setError("How much stock?");
            return false;
        }
        user_id = SharedPreferencesHelper.getUserInfo(getContext(), Constants.SHARED_PREF);

        if (user_id.equals("-1")) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void onSubmitSuccess() {
        Toast.makeText(getContext(), "kudos!! Product Added", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), DeshboardActivity.class);
        startActivity(intent);
    }
}
