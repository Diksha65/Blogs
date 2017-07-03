package com.example.diksha.blogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity{

    private static final String FRAGMENT_HOME = "Home";
    private static final String FRAGMENT_OTHER = "Other";
    private BottomNavigationView navigation;
    private FragmentManager fragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewFragment(PublicBlogFragment.newInstance(), FRAGMENT_HOME);
                    break;
                case R.id.navigation_dashboard:
                    viewFragment(MembersBlogFragment.newInstance(), FRAGMENT_OTHER);
                    break;
                case R.id.navigation_notifications:
                    viewFragment(AdminBlogFragment.newInstance(), FRAGMENT_OTHER);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_containcer, PublicBlogFragment.newInstance()).commit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void viewFragment(Fragment fragment, String name){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_containcer, fragment);
        final int count = fragmentManager.getBackStackEntryCount();
        if( name.equals( FRAGMENT_OTHER) ) {
            fragmentTransaction.addToBackStack(name);
        }
        fragmentTransaction.commit();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                if( fragmentManager.getBackStackEntryCount() <= count){

                    fragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.removeOnBackStackChangedListener(this);
                    navigation.getMenu().getItem(0).setChecked(true);
                }
            }
        });
    }
}