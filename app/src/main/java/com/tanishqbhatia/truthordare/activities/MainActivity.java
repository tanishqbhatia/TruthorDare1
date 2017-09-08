package com.tanishqbhatia.truthordare.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;

import com.tanishqbhatia.bottom_navigation.BottomNavigationBar;
import com.tanishqbhatia.bottom_navigation.BottomNavigationItem;
import com.tanishqbhatia.fragment_navigation.BackStackActivity;
import com.tanishqbhatia.truthordare.R;
import com.tanishqbhatia.truthordare.fragments.DashboardFragment;
import com.tanishqbhatia.truthordare.fragments.FragmentInstance;
import com.tanishqbhatia.truthordare.fragments.HomeFragment;
import com.tanishqbhatia.truthordare.fragments.NotificationsFragment;
import com.tanishqbhatia.truthordare.fragments.SearchFragment;
import com.tanishqbhatia.truthordare.fragments.UserFragment;

import static com.tanishqbhatia.bottom_navigation.BottomNavigationBar.OnTabSelectedListener;

public class MainActivity extends BackStackActivity implements OnTabSelectedListener {
    private static final String STATE_CURRENT_TAB_ID = "current_tab_id";
    private static final int MAIN_TAB_ID = 0;

    private BottomNavigationBar bottomNavBar;
    private Fragment curFragment;
    private int curTabId;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        setUpBottomNavBar();

        if (state == null) {
            bottomNavBar.selectTab(MAIN_TAB_ID, false);
            showFragment(rootTabFragment(MAIN_TAB_ID));
        }
    }

    private void setUpBottomNavBar() {
        bottomNavBar = findViewById(R.id.bottomNavigationBar);
        bottomNavBar.setMode(BottomNavigationBar.MODE_FIXED_NO_TITLE);
        bottomNavBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavBar.setActiveColor(R.color.grey_900)
                .setInActiveColor(R.color.grey_500)
                .setBarBackgroundColor(R.color.grey_50);
        bottomNavBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home, R.string.home))
                .addItem(new BottomNavigationItem(R.drawable.ic_search, R.string.search))
                .addItem(new BottomNavigationItem(R.drawable.ic_dashboard, R.string.dashboard))
                .addItem(new BottomNavigationItem(R.drawable.ic_notifications, R.string.notifications))
                .addItem(new BottomNavigationItem(R.drawable.ic_user, R.string.user))
                .setFirstSelectedPosition(curTabId)
                .initialise();
        bottomNavBar.setTabSelectedListener(this);
    }

    @NonNull
    private Fragment rootTabFragment(int tabId) {
        switch (tabId) {
            case 0:
                return HomeFragment.newInstance(FragmentInstance.getInstance());
            case 1:
                return SearchFragment.newInstance(FragmentInstance.getInstance());
            case 2:
                return DashboardFragment.newInstance(FragmentInstance.getInstance());
            case 3:
                return NotificationsFragment.newInstance(FragmentInstance.getInstance());
            case 4:
                return UserFragment.newInstance(FragmentInstance.getInstance());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        curFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerFl);
        curTabId = savedInstanceState.getInt(STATE_CURRENT_TAB_ID);
        bottomNavBar.selectTab(curTabId, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_TAB_ID, curTabId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Pair<Integer, Fragment> pair = popFragmentFromBackStack();
        if (pair != null) {
            backTo(pair.first, pair.second);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTabSelected(int position) {
        if (curFragment != null) {
            pushFragmentToBackStack(curTabId, curFragment);
        }
        curTabId = position;
        Fragment fragment = popFragmentFromBackStack(curTabId);
        if (fragment == null) {
            fragment = rootTabFragment(curTabId);
        }
        replaceFragment(fragment);
    }

    @Override
    public void onTabReselected(int position) {
        backToRoot();
    }

    @Override
    public void onTabUnselected(int position) {}

    public void showFragment(@NonNull Fragment fragment) {
        showFragment(fragment, true);
    }

    public void showFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        if (curFragment != null && addToBackStack) {
            pushFragmentToBackStack(curTabId, curFragment);
        }
        replaceFragment(fragment);
    }

    private void backTo(int tabId, @NonNull Fragment fragment) {
        if (tabId != curTabId) {
            curTabId = tabId;
            bottomNavBar.selectTab(curTabId, false);
        }
        replaceFragment(fragment);
        getSupportFragmentManager().executePendingTransactions();
    }

    private void backToRoot() {
        if (isRootTabFragment(curFragment, curTabId)) {
            return;
        }
        resetBackStackToRoot(curTabId);
        Fragment rootFragment = popFragmentFromBackStack(curTabId);
        assert rootFragment != null;
        backTo(curTabId, rootFragment);
    }

    private boolean isRootTabFragment(@NonNull Fragment fragment, int tabId) {
        return fragment.getClass() == rootTabFragment(tabId).getClass();
    }

    private void replaceFragment(@NonNull Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.replace(R.id.fragmentContainerFl, fragment);
        tr.commitAllowingStateLoss();
        curFragment = fragment;
    }
}