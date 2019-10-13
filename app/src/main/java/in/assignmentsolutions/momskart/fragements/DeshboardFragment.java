package in.assignmentsolutions.momskart.fragements;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.suke.widget.SwitchButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.assignmentsolutions.momskart.R;
import in.assignmentsolutions.momskart.utils.Constants;
import in.assignmentsolutions.momskart.utils.SharedPreferencesHelper;
import io.ghyeok.stickyswitch.widget.StickySwitch;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeshboardFragment extends Fragment implements OnChartValueSelectedListener {

    PieChart chart;
    SwitchButton sswitch;

    private static String user_id = null;

    public DeshboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_deshboard, container, false);

        chart = v.findViewById(R.id.chart);
        sswitch = v.findViewById(R.id.id_status_switch);


        sswitch.setChecked(true);
        sswitch.setShadowEffect(true);//disable shadow effect
        sswitch.setEnabled(true);//disable button
        sswitch.setEnableEffect(true);//disable the switch animation

        chart.setVisibility(View.GONE);

        //chart.highlightValues(null);

        sswitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Toast.makeText(getContext(), "checked called", Toast.LENGTH_LONG).show();
                postAvailability(isChecked);
            }
        });

        if (user_id == null)
            user_id = SharedPreferencesHelper.getUserInfo(getContext(), Constants.SHARED_PREF);

        getDashboardData();

        /////////////////
        return v;
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Total");
        //s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        //s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        //s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        //s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void getDashboardData() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.DASHBOARD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onDashboardResponse(jsonObject);
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

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user_id", user_id);
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onDashboardResponse(JSONObject jsonObject) {

        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        /////////////////
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setEntryLabelColor(Color.rgb(0, 0, 0));

        //chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(42f);
        chart.setTransparentCircleRadius(50f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        ArrayList<PieEntry> entries = new ArrayList<>();
//        for (int i = 0; i < 4 ; i++) {
//            entries.add(new PieEntry((float) ((Math.random() * 5) + 10 / 5),
//                    "same",
//                    getResources().getDrawable(R.drawable.avatar)));
//        }

        try {
            entries.add(new PieEntry(jsonObject.getInt("newOrder"), "new orders", getResources().getDrawable(R.drawable.avatar)));
            entries.add(new PieEntry(jsonObject.getInt("cancelOrder"), "cancel orders", getResources().getDrawable(R.drawable.avatar)));
            entries.add(new PieEntry(jsonObject.getInt("pendingOrder"), "pending orders", getResources().getDrawable(R.drawable.avatar)));
            entries.add(new PieEntry(jsonObject.getInt("deliveredOrder"), "delivered", getResources().getDrawable(R.drawable.avatar)));
            entries.add(new PieEntry(jsonObject.getInt("dispatchOrder"), "dispatched", getResources().getDrawable(R.drawable.avatar)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PieDataSet dataSet = new PieDataSet(entries, "Total values");
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(tfLight);
        chart.setData(data);

        chart.setVisibility(View.VISIBLE);

    }

    private void postAvailability(final boolean flag) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SELLER_AVAILABILITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        onAvailabilityResponse(jsonObject);
                    } else {
                        onAvailabilityFailed(flag);
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
                MyData.put("isAvailable", Boolean.toString(flag));
                return MyData;
            }
        };
        MyRequestQueue.add(stringRequest);
    }

    private void onAvailabilityResponse(JSONObject jsonObject) {
        try {
            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onAvailabilityFailed(boolean flag) {
        Toast.makeText(getContext(), "Unable to update, Try again!!", Toast.LENGTH_LONG).show();
        sswitch.toggle();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDashboardData();
    }
}
