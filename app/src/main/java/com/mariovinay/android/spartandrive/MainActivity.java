package com.mariovinay.android.spartandrive;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.About;
import com.mariovinay.android.spartandrive.authentication.AuthenticateActivity;
import com.mariovinay.android.spartandrive.cloud.DriveFragment;
import com.mariovinay.android.spartandrive.cloud.FileInfoFragment;
import com.mariovinay.android.spartandrive.cloud.SearchFragment;
import com.mariovinay.android.spartandrive.filesystem.DiskFragment;
import com.mariovinay.android.spartandrive.utilities.RoundedImageView;

import java.io.InputStream;

public class MainActivity extends AuthenticateActivity
        implements NavigationView.OnNavigationItemSelectedListener, DriveFragment.OnFragmentInteractionListener, DiskFragment.OnFragmentInteractionListener, FileInfoFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    String folderNameToCreate = new String();

        public void initializeThisDamnActivity() {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new BottomSheet.Builder(MainActivity.this).title("New").sheet(R.menu.bottomsheet).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.createfolder:
                                    LayoutInflater layoutInflater
                                            = (LayoutInflater) getBaseContext()
                                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View popupView = layoutInflater.inflate(R.layout.folder, null);
                                    final PopupWindow popupWindow = new PopupWindow(
                                            popupView,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);

                                    //EditText et = (EditText) popupWindow.findViewById(R.id.folderNameValue);
                                    //et.requestFocus();
                                    //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                                    Button btnOk = (Button) popupView.findViewById(R.id.ok);
                                    btnOk.setOnClickListener(new Button.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            View parent = (View)v.getParent().getParent();
                                            EditText tv = (EditText) parent.findViewById(R.id.folderNameValue);
                                            folderNameToCreate = tv.getText().toString();
                                            //check if its in root or under some other folder
                                            if(getCurrentFolderId() == "root") {
                                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                        .setTitle(tv.getText().toString()).build();
                                                Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                                                        getGoogleApiClient(), changeSet).setResultCallback(callbackCreateFolder);
                                            } else {
                                                Drive.DriveApi.fetchDriveId(getGoogleApiClient(), getCurrentFolderId())
                                                        .setResultCallback(idCallbackForFolderInFolder);
                                            }


                                            popupWindow.dismiss();

                                        }
                                    });
                                    Button btnCancel = (Button) popupView.findViewById(R.id.cancel);
                                    btnCancel.setOnClickListener(new Button.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            popupWindow.dismiss();
                                        }
                                    });
                                    View v = findViewById(R.id.fragment_placeholder);
                                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, -200);
                                    popupWindow.setFocusable(true);
                                    popupWindow.update();
                                    break;
                                case R.id.createupload:
                                    DiskFragment fragment = new DiskFragment(MainActivity.this);
                                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.fragment_placeholder, fragment,"DiskFragment");
                                    ft.addToBackStack(null);
                                    ft.commit();
                                    break;
                            }
                        }
                    }).show();


                /*
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                // New snackbar experiment
                // Create the Snackbar
                Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
                // Get the Snackbar's layout view
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                // Hide the text
                TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
                textView.setVisibility(View.INVISIBLE);

                // Inflate our custom view
                View snackView = getLayoutInflater().inflate(R.layout.snackbar_newoptions, null);
                // Configure the    view
                //ImageView imageView = (ImageView) snackView.findViewById(R.id.image);
                //imageView.setImageBitmap(image);
                //TextView textViewTop = (TextView) snackView.findViewById(R.id.text);
                //textViewTop.setText(text);
                //textViewTop.setTextColor(Color.WHITE);
                // Add the view to the Snackbar's layout
                layout.addView(snackView, 0);
                // Show the Snackbar
                snackbar.show();
                */
                    // New snackbar expirement
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View anotherView = navigationView.inflateHeaderView(R.layout.nav_header_main);

            /*View mainView =  LayoutInflater.from(this).inflate(R.layout.progress_layout, null);
            ProgressBar headerView = (ProgressBar) mainView.findViewById(R.id.progressBar);
            headerView.setMax(100);
            headerView.setProgress(20);
            navigationView.addHeaderView(mainView);*/

            DriveFragment fragment = new DriveFragment(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, "DriveFragment");
            ft.addToBackStack(null);
            ft.commit();



            if (Plus.PeopleApi.getCurrentPerson(getGoogleApiClient()) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(getGoogleApiClient());
                String personName = currentPerson.getDisplayName();
                Person.Image personPhoto = currentPerson.getImage();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(getGoogleApiClient());
                System.out.println(personName);
                System.out.println(email);
                TextView tv = (TextView)anotherView.findViewById(R.id.userName);
                TextView tve = (TextView)anotherView.findViewById(R.id.userEmail);
                tv.setText(personName);
                tve.setText(email);
                new DownloadImageTask((RoundedImageView) anotherView.findViewById(R.id.profileimageView))
                        .execute(personPhoto.getUrl());

            } else {
                System.out.println("It is null. WTF");
            }

            new UpdateDriveStatistics(super.getRESTCredential()).execute();
        }

    final ResultCallback<DriveApi.DriveIdResult> idCallbackForFolderInFolder = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Cannot find DriveId. Are you authorized to view this file?");
                return;
            }
            DriveId driveId = result.getDriveId();
            DriveFolder folder = driveId.asDriveFolder();
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(folderNameToCreate).build();
            folder.createFolder(getGoogleApiClient(), changeSet)
                    .setResultCallback(createFolderCallback);
        }
    };

    final ResultCallback<DriveFolder.DriveFolderResult> createFolderCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {

                @Override
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while trying to create a folder");
                        return;
                    }
                    showMessage("Folder successfully created");
                }
            };





    final ResultCallback<DriveFolder.DriveFolderResult> callbackCreateFolder = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the folder");
                return;
            }
            showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            System.out.println("Searched for " + query);
            //search("root",query);
            SearchFragment fragment = new SearchFragment(this, query);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, "SearchFragment");
            ft.addToBackStack(null);
            ft.commit();
            //new YouTubeClient().execute(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_viewtoggle) {
           changeTheLayout(item);
        }

        if(id == R.id.refresh) {
            //Intent intent = new Intent(this, MainFileActivity.class);
            //startActivity(intent);
            DriveFragment fragment = new DriveFragment(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, "DriveFragment");
            ft.addToBackStack(null);
            ft.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_signout) {

        } else if (id == R.id.nav_usagereport) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    public String getCurrentFolderId() {
        String s = new String();
        //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("DriveFragment");
        if(fragment instanceof DriveFragment) {
            DriveFragment temp = (DriveFragment)fragment;
            s = temp.getFOLDER_ID();
        }
        return s;
    }

    public  void changeTheLayout(MenuItem item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        if(fragment instanceof DriveFragment) {
            DriveFragment temp = (DriveFragment)fragment;
            temp.changeLayout(item);
            System.out.println("Curren displaying drive fragment");
        }
        if(fragment instanceof DiskFragment) {
            DiskFragment temp = (DiskFragment)fragment;
            temp.changeLayout(item);
            System.out.println("Curren displaying disk fragment");
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        System.out.println("Successfully authenticated!");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        if(fragment instanceof SearchFragment) {

        } else {
            initializeThisDamnActivity();
        }

        /*Intent prevIntent = getIntent();

        FOLDER_ID = prevIntent.getStringExtra("folderID");
        if(FOLDER_ID ==""||FOLDER_ID==null) {
            DriveFolder drivFolder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
            FOLDER_ID = drivFolder.getDriveId().getResourceId();
        }
        Drive.DriveApi.fetchDriveId(getGoogleApiClient(), FOLDER_ID)
                .setResultCallback(idCallback);
                */
    }

    @Override
    public void onBackPressed() {
        System.out.println("On back pressed;");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(fragment instanceof DriveFragment) {
            DriveFragment temp = (DriveFragment)fragment;
            if(!DriveFragment.getFolderStack().isEmpty()) {
                DriveFragment.getFolderStack().pop();
            if(DriveFragment.getFolderStack().isEmpty()) {
                temp.setFOLDER_ID("");
                temp.fetch();
                System.out.println("Parent folder is root ");
                System.out.println("Curren displaying drive fragment");
            } else {
                System.out.println("Parent folder is " + DriveFragment.getFolderStack().peek());
                temp.setFOLDER_ID((String)DriveFragment.getFolderStack().peek());
                temp.fetch();
                System.out.println("Curren displaying drive fragment");
            }} else {
                System.out.println("Parent folder is Default onBackPressed");
                super.onBackPressed();
            }
        }else if(fragment instanceof FileInfoFragment) {
            super.onBackPressed();
            Fragment fragment1 = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
            if(fragment instanceof DriveFragment) {
                DriveFragment fragment12 = (DriveFragment)fragment1;
                System.out.println("Parent folder is " + DriveFragment.getFolderStack().peek());
                fragment12.setFOLDER_ID((String)DriveFragment.getFolderStack().peek());
                fragment12.fetch();
                System.out.println("Curren displaying drive fragment");
            }

        } else if(fragment instanceof DiskFragment) {
            super.onBackPressed();
            DriveFragment fragment1 = (DriveFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
            fragment1.setFOLDER_ID((String)DriveFragment.getFolderStack().peek());
            fragment1.fetch();
        } else {
            System.out.println("Parent folder is Default onBackPressed");
            super.onBackPressed();
        }
    }

    public void refreshDriveFragment() {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        RoundedImageView bmImage;

        public DownloadImageTask(RoundedImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class UpdateDriveStatistics extends AsyncTask<Void, Void, String> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private Long usedBytes;
        private Long totalBytes;
        private int usedPerc;

        long GIGABYTE = 1024L * 1024L * 1024L;

        public UpdateDriveStatistics(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Drive API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                About about = mService.about().get().execute();

                System.out.println("Current user name: " + about.getName());
                System.out.println("Root folder ID: " + about.getRootFolderId());
                System.out.println("Total quota (bytes): " + about.getQuotaBytesTotal());
                System.out.println("Used quota (bytes): " + about.getQuotaBytesUsed());
                usedBytes = about.getQuotaBytesUsed();
                totalBytes = about.getQuotaBytesTotal();
                usedBytes = usedBytes / GIGABYTE;
                totalBytes = totalBytes / GIGABYTE;
                usedPerc = (int) (usedBytes * 100L / totalBytes);
                System.out.println("Total quota (GB): " + usedBytes);
                System.out.println("Used quota (GB): " + totalBytes);
                System.out.println("Used % is:" + usedPerc);
                return "something";
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return "something";
            }
        }

        @Override
        protected void onPostExecute(String something) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View mainView =  LayoutInflater.from(getApplicationContext()).inflate(R.layout.progress_layout, null);
            ProgressBar headerView = (ProgressBar) mainView.findViewById(R.id.progressBar);
            TextView usedView = (TextView) mainView.findViewById(R.id.usedValue);
            TextView totalView = (TextView) mainView.findViewById(R.id.totalValue);
            TextView usedPercView = (TextView) mainView.findViewById(R.id.usedPerc);

            headerView.setMax(100);
            headerView.setProgress(usedPerc);
            usedView.setText(usedBytes.toString() + "GB");
            totalView.setText(totalBytes.toString() + "GB");
            usedPercView.setText(""+usedPerc+" %");
            navigationView.addHeaderView(mainView);
        }
    }




}
