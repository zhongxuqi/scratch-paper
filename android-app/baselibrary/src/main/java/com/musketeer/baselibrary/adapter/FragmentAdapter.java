package com.musketeer.baselibrary.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongxuqi on 16-5-7.
 */
public class FragmentAdapter {
    protected List<Fragment> fragmentList;
    protected FragmentManager fragmentManager;
    protected int fragment_content;
    protected Fragment showFragment;

    public FragmentAdapter(FragmentManager fm, int fc) {
        fragmentList = new ArrayList<>();
        this.fragmentManager = fm;
        this.fragment_content = fc;
    }

    public void addFragment(Fragment fragment) {
        if (fragment == null) return;
        if (showFragment == null) {
            showFragment = fragment;
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment_content, fragment);
        fragmentTransaction.hide(fragment);
        fragment.onPause();
        fragmentTransaction.show(showFragment);
        showFragment.onResume();
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void addAllFragment(List<Fragment> fragments) {
        if (fragments == null || fragments.size() == 0) return;
        if (showFragment == null) {
            showFragment = fragments.get(0);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment: fragments) {
            fragmentTransaction.add(fragment_content, fragment);
            if (fragment == showFragment) continue;
            fragmentTransaction.hide(fragment);
            fragment.onPause();
        }
        fragmentTransaction.show(showFragment);
        showFragment.onResume();
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void showFragment(Fragment fragment) {
        if (fragment == null || showFragment == fragment) return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(showFragment);
        showFragment.onPause();
        fragmentTransaction.show(fragment);
        fragment.onResume();
        fragmentTransaction.commitAllowingStateLoss();
        showFragment = fragment;
    }

    public void removeFragment(Fragment fragment) {
        fragmentList.remove(fragment);
        if (showFragment == fragment) {
            if (fragmentList.size() > 0) {
                showFragment = fragmentList.get(0);
            } else {
                showFragment = null;
            }
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        if (showFragment != null) {
            fragmentTransaction.show(showFragment);
            showFragment.onResume();
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

}