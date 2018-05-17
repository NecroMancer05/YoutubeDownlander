package com.bekircan.youtubedownloader;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    private Bundle bundle;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        /*
        //TODO something wrong in this !
        //Sending Action (Receive)
        Intent intent = getIntent();
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String action = intent.getAction();
        String intentString = intent.getType();
        //Get links from another app
        if (Intent.ACTION_SEND.equals(action) && intentString != null) {
            if ("text/plain".equals(intentString)) {
                handleSendText(intent); // Handle text being sent
                Toast.makeText(this, "inner if", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Share from youtube !", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "inner else", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "if else", Toast.LENGTH_SHORT).show();
        }
        */

    }

    /*
    private void getLink() {

        Toast.makeText(this, "gteLink", Toast.LENGTH_SHORT).show();

        //need to gteIntent so we cant
        //Sending Action (Receive)
        Intent intent = getIntent();
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String action = intent.getAction();
        String intentString = intent.getType();
        //Get links from another app
        if (Intent.ACTION_SEND.equals(action) && intentString != null) {
            if ("text/plain".equals(intentString)) {
                handleSendText(intent); // Handle text being sent
                Toast.makeText(this, "inner if", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Share from youtube !", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "if else", Toast.LENGTH_SHORT).show();
        }
    }
    */


    @Override
    protected void onStart() {
        super.onStart();

        //Sending Action (Receive)
        Intent intent = getIntent();
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String action = intent.getAction();
        String intentString = intent.getType();
        //Get links from another app
        if (Intent.ACTION_SEND.equals(action) && intentString != null) {
            if ("text/plain".equals(intentString)) {
                handleSendText(intent); // Handle text being sent
            }else{
                Toast.makeText(this, "Share from youtube !", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*
    @Override
    protected void onResume() {
        super.onResume();

        //Sending Action (Receive)
        Intent intent = getIntent();
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String action = intent.getAction();
        String intentString = intent.getType();
        //Get links from another app
        if (Intent.ACTION_SEND.equals(action) && intentString != null) {
            if ("text/plain".equals(intentString)) {
                handleSendText(intent); // Handle text being sent
            }else{
                Toast.makeText(this, "Share from youtube !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    */


    /*
    @Override
    protected void onStart() {
        super.onStart();

        //Sending Action (Receive)
        Intent intent = getIntent();
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String action = intent.getAction();
        String intentString = intent.getType();
        //Get links from another app
        if (Intent.ACTION_SEND.equals(action) && intentString != null) {
            if ("text/plain".equals(intentString)) {
                handleSendText(intent); // Handle text being sent
            }else{
                Toast.makeText(this, "Share from youtube !", Toast.LENGTH_SHORT).show();
            }
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }
    */


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Go menu and press exit for close the app.", Toast.LENGTH_SHORT).show();
    }

    //Recive handler
    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared

            bundle = new Bundle();
            bundle.putString("sharedLink", sharedText);

        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){

                case 0:
                    infoFragment infoFragment = new infoFragment();
                    infoFragment.setArguments(bundle);
                    return infoFragment;

                    /*
                case 1:
                    downloadFragment downloadFragment = new downloadFragment();
                    return downloadFragment;
                 */

                 default:
                     return null;
            }
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 1;
        }
    }
}
