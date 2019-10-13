package in.assignmentsolutions.momskart.fragements;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.assignmentsolutions.momskart.R;
import in.assignmentsolutions.momskart.adapters.ProductsListAdapter;
import in.assignmentsolutions.momskart.model.ProductModel;
import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.PaginationScrollListener;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;

import static android.support.v7.widget.RecyclerView.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProductsList extends Fragment {

    ProductsListAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    String user_id;

    private static final int PAGE_START = 0;

    private boolean isLoading = false;

    private boolean isLastPage = false;

    private int TOTAL_PAGES = 3;

    private int currentPage = PAGE_START;

    public MyProductsList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_products_list, container, false);

        rv = v.findViewById(R.id.id_my_products_recycler_view);
        progressBar = v.findViewById(R.id.id_progress_bar);

        adapter = new ProductsListAdapter();

        linearLayoutManager = new LinearLayoutManager(getContext(), VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
                currentPage += 1;
                loadNextPage(currentPage);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        user_id = SharedPreferencesHelper.getUserInfo(getContext(), Constants.SHARED_PREF);

        loadNextPage(0);

        adapter.notifyDataSetChanged();

        return v;
    }

    public void loadNextPage(int count) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_PRODUCTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onProductsSuccess(jsonObject);
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
                MyData.put("user_id", user_id);
                MyData.put("page", Integer.toString(count));
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onProductsSuccess(JSONObject jsonObject) {
        try {
            JSONArray objArray = jsonObject.getJSONArray("result");
            List<ProductModel> list = new ArrayList<>();
            for(int i=0;i<objArray.length();i++) {
                ProductModel pm = new ProductModel();
                JSONObject obj = objArray.getJSONObject(i);
                pm.setTitle(obj.getString("title"));
                pm.setDescription(obj.getString("description"));
                pm.setImageUrl1("image1");
                pm.setImageUrl2("image2");
                pm.setImageUrl3("image3");
                pm.setImageUrl4("image4");
                pm.setImageUrl5("image5");
                list.add(pm);
                pm = null;
                System.out.println( "product: " + obj.getString("title") + " " + obj.getString("description"));
            }

            adapter.addData(list);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
