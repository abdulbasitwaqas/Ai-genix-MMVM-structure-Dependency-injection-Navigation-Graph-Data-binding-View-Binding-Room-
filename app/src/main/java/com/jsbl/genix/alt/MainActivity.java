package com.jsbl.genix.alt;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.jsbl.genix.R;
import com.jsbl.genix.views.adapters.CartItemAdapter;
import com.jsbl.genix.views.fragments.DashBoard;
import com.jsbl.genix.views.fragments.MyTrips;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    public static DrawerLayout drawer;
    public  static ImageView imageviewMenu;
    public static TextView toolbarText;
    public static Boolean isFragmentLoaded=false;
    public static NavigationView navigationView;
    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alt_activity_main);

        //loadFragment
        loadFragment(new DashBoard());

        //initialization
        init();

        //click Listeners
        onClickListeners();

        navigationView.setNavigationItemSelectedListener(this);

//        removeFocus();


    }

    @Override
    public void onBackPressed() {
        CartItemAdapter.Companion.setRedeemPoints(0);
        super.onBackPressed();
    }

    private void onClickListeners() {

        imageviewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!drawer.isDrawerOpen(Gravity.LEFT)) {

                        drawer.openDrawer(Gravity.LEFT);

                    } else
                        drawer.closeDrawers();
                } catch (Exception e) {
                    Log.e("Exception Menu Drawer", "" + e);
                }
            }
        });

    }

    private void init() {

        //drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navView);
        View headerView = navigationView.getHeaderView(0);
/*
        //ImageViews
        imageviewMenu = (ImageView) findViewById(R.id.imageViewLeft);*/

        //TextViews
        toolbarText= (TextView) findViewById(R.id.tvGenixToolbar);


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.trips:
                loadFragment(new MyTrips());
                break;
            case R.id.games:
                loadFragment(new MyTrips());
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        isFragmentLoaded=true;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navHostFragment, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

    }

    private void removeFocus() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}