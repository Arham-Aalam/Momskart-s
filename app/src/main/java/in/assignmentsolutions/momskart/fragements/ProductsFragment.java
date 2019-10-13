package in.assignmentsolutions.momskart.fragements;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import in.assignmentsolutions.momskart.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    SmartTabLayout tabLayout;
    ViewPager viewPager;
    Adapter adapter;

    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_products, container, false);

        tabLayout = v.findViewById(R.id.id_product_tabLayout);
        viewPager = v.findViewById(R.id.id_product_viewpager);

        //tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        //tabLayout.setCustomTabColorizer();
        tabLayout.setDividerColors(getResources().getColor(R.color.colorAccent));

        setupViewPager(viewPager);
        //tabLayout.setupWithViewPager(viewPager);
        tabLayout.setViewPager(viewPager);
        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new MyOrderesList(), "New Orders");
        adapter.addFragment(new MyProductsDelivered(), "Delivered products");
        adapter.addFragment(new MyProductsList(), "Products list");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
