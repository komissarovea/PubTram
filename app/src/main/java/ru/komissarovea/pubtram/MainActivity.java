package ru.komissarovea.pubtram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.komissarovea.pubtram.fragments.MapFragment;
import ru.komissarovea.pubtram.fragments.NextStopsFragment;
import ru.komissarovea.pubtram.fragments.SettingsFragment;
import ru.komissarovea.pubtram.fragments.TransportFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String ARG_FRAGMENT_TAG = "fragment_id_";

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setTitle(getString(R.string.next_stops));
        setFragment(R.id.nav_next_stops);
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        setFragment(item.getItemId());

        //CharSequence itemTitle = item.getTitle().toString();
        //Toast toast = Toast.makeText(this, itemTitle, Toast.LENGTH_SHORT);
        //toast.show();

        // Highlight the selected item has been done by NavigationView
        //item.setChecked(true);

        // Set action bar title
        //setTitle(item.getTitle());

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(int id) {
        String tag = ARG_FRAGMENT_TAG + id;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if (fragment == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            fragment = FragmentFactory.newInstance(id);
            ft.replace(R.id.flContent, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
        } else
            fragmentManager.popBackStackImmediate(tag, 0);

        // Insert the fragment by replacing any existing fragment
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count == 0) {
            this.finish();
        }
    }

    public static class FragmentFactory {
        /**
         * Returns a new instance of this fragment for the given id.
         */
        static Fragment newInstance(int id) {
            Fragment fragment = null;
            switch (id) {
                case R.id.nav_next_stops:
                    fragment = new NextStopsFragment();
                    break;
                case R.id.nav_map:
                    fragment = new MapFragment();
                    break;
                case R.id.nav_transport:
                    fragment = new TransportFragment();
                    break;
                case R.id.nav_manage:
                    fragment = new SettingsFragment();
                    break;
            }
            return fragment;
        }
    }
}
