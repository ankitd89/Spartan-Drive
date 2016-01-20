/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mariovinay.android.spartandrive.authentication;

import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.widget.DataBufferAdapter;

/**
 * An activity illustrates how to list file results and infinitely
 * populate the results list view with data if there are more results.
 */
public class AuthenticateActivity extends BaseActivity {

    private ListView mListView;
    private DataBufferAdapter<Metadata> mResultsAdapter;
    private String mNextPageToken;
    private boolean mHasMore;
    private DriveFolder folder;
    private String FOLDER_ID="";
    private String PARENT_FOLDER_ID="";
    private static final int REQUEST_CODE_OPENER = 1;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
    }

    /**
     * Clears the result buffer to avoid memory leaks as soon
     * as the activity is no longer visible by the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        //mResultsAdapter.clear();
    }

    /**
     * Handles the Drive service connection initialization
     * and inits the first listing request.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        System.out.println("Successfully authenticated!");
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

}
