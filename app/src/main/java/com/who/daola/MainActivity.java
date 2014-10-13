package com.who.daola;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.who.daola.service.FenceTriggerService;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, TargetListFragment.OnFragmentInteractionListener, FenceListFragment.OnFragmentInteractionListener,  NotificationListFragment.OnFragmentInteractionListener, ActionMode.Callback {

    public static final String TAG = MainActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ActionMode mActionMode;
    private int mSelectedItem;
    private TargetListFragment mTargetsFragment;
    private FenceListFragment mFencesFragment;
    private NotificationListFragment mNotificationFragment;
    private TargetListFragment geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (FenceTriggerService.getInstance()==null ) {
            Intent startServiceIntent = new Intent(this, FenceTriggerService.class);
            this.startService(startServiceIntent);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        mActionMode = this.startActionMode(this);
        mSelectedItem = position;
        onSectionAttached(mSelectedItem);
        restoreActionBar();
        if (position == 0) {
            mNotificationFragment = NotificationListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mNotificationFragment)
                    .commit();
        } else if (position == 1) {
            mFencesFragment = FenceListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mFencesFragment)
                    .commit();
            setTitle(mTitle);
        } else if (position == 2) {
            mTargetsFragment = TargetListFragment.newInstance("hello", "world");
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mTargetsFragment)
                    .commit();
        } else if (position == 3) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_notifications);

                break;
            case 1:
                mTitle = getString(R.string.title_fences);
                break;
            case 2:
                mTitle = getString(R.string.title_targets);
                break;
            case 3:
                mTitle = getString(R.string.title_settings);
                break;
            default:
                mTitle = getString(R.string.title_activity_main);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen() && mSelectedItem == 0) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }  else if (!mNavigationDrawerFragment.isDrawerOpen() && mSelectedItem == 1) {
            getMenuInflater().inflate(R.menu.geo, menu);
            restoreActionBar();
            return true;
        } else if (!mNavigationDrawerFragment.isDrawerOpen() && mSelectedItem == 2) {
            getMenuInflater().inflate(R.menu.geo, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (R.id.action_add == id && mSelectedItem == 1) {
            return mFencesFragment.onOptionsItemSelected(item);
        } else if (R.id.action_add == id && mSelectedItem == 2) {
            return mTargetsFragment.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    // 4. Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        // Inflate a menu resource providing context menu items
        //MenuInflater inflater = mode.getMenuInflater();
        //getMenuInflater().inflate(R.menu.geo, menu);
        return false;
    }

    // 5. Called when the user click share item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


        return true;
    }

    // 6. Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//        if (mSelectedItem==1) {
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.geo, menu);
//        }
        return true; // Return false if nothing is done
    }

    // 7. Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }
}
