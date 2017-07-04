package com.example.diksha.blogs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by diksha on 4/7/17.
 */

public class FragmentUtils {
    /**
     *      Attaching Fragments
     */

    public static void attachFragment(Fragment fragment, int containerId, FragmentManager fragmentManager){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

}
