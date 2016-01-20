package com.mariovinay.android.spartandrive.cloud;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.mariovinay.android.spartandrive.MainActivity;
import com.mariovinay.android.spartandrive.R;

import java.util.ArrayList;
import java.util.Stack;


public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Stack folderStack = new Stack();

    public static Stack getFolderStack() {
        return folderStack;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    AbsListView list;
    CloudFilesModel[] item = new CloudFilesModel[4];


    Integer[] imgid={
            R.drawable.me_linkedin,
            R.drawable.me_linkedin1,
            R.drawable.me_linkedin2,
            R.drawable.me_linkedin3
    };

    private boolean isList=true;
    private String currentDriveId;

    public String getFOLDER_ID() {
        return FOLDER_ID;
    }

    private String FOLDER_ID="";
    private MainActivity mainActivity;
    public boolean toggleList() {
        isList = !isList;
        return isList;
    }
    ArrayList<CloudFilesModel> fst;

    CustomListAdapter adapter;
    CustomGridAdapter adapter2;
    String searchQuery;
    static final String TITL = "titl";
    static final String GDID = "gdid";
    static final String MIME = "mime";
    static final String DESC = "desc";

    public SearchFragment() {

    }

    public SearchFragment(MainActivity mainactivity, String searchQuery) {
        this.mainActivity = mainactivity;
        this.searchQuery = searchQuery;
        //ActivityCompat.requestPermissions(mainactivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        search("root",searchQuery);
    }

    public void setFOLDER_ID(String _FOLDER_ID) {
        this.FOLDER_ID = _FOLDER_ID;
    }

    public void fetch() {

        if(FOLDER_ID ==""||FOLDER_ID==null) {
            DriveFolder drivFolder = Drive.DriveApi.getRootFolder(mainActivity.getGoogleApiClient());
            FOLDER_ID = drivFolder.getDriveId().getResourceId();
            folderStack.push(FOLDER_ID);
        }
        System.out.println("FOLDER_ID finally is " + FOLDER_ID);
        //if(!folderStack.isEmpty() && !folderStack.peek().equals(FOLDER_ID))
          // folderStack.push(FOLDER_ID);
        Drive.DriveApi.fetchDriveId(mainActivity.getGoogleApiClient(), FOLDER_ID)
                .setResultCallback(idCallback);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

    } /* End of onRequestPermissionsResult() */

    public void search(String prnId, String titl) {
        GoogleApiClient mGAC ;
        System.out.println("In Search function");
        mGAC = mainActivity.getGoogleApiClient();
        ArrayList<ContentValues> gfs = new ArrayList<>();
        //if (mGAC != null && mGAC.isConnected()) try {
        if (mGAC != null) try {
            System.out.println("Authenticating");
            if(!mGAC.isConnected())
                mGAC.connect();
            // add query conditions, build query
            ArrayList<com.google.android.gms.drive.query.Filter> fltrs = new ArrayList<>();
            if (prnId != null) {
                System.out.println("ParentId is not null" +prnId);
                /*if (prnId.equalsIgnoreCase("root")) {
                    fltrs.add(Filters.in(SearchableField.PARENTS, Drive.DriveApi.getRootFolder(mGAC).getDriveId()));
                } else if (prnId.equalsIgnoreCase("appfolder")) {
                    fltrs.add( Filters.in(SearchableField.PARENTS, Drive.DriveApi.getAppFolder(mGAC).getDriveId()));
                } else {
                    fltrs.add( Filters.in(SearchableField.PARENTS, DriveId.decodeFromString(prnId)));
                }*/
            }
            if (titl != null) {
                fltrs.add( Filters.eq(SearchableField.TITLE, titl));
                //fltrs.add( Filters.sharedWithMe());
            }

            // if (mime != null) fltrs.add(Filters.eq(SearchableField.MIME_TYPE, mime));
            Query qry = new Query.Builder().addFilter(Filters.and(fltrs)).build();
            System.out.println("set the call back function");
            Drive.DriveApi.query(mGAC, qry)
                    .setResultCallback(metadataResult);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    final private ResultCallback<DriveApi.MetadataBufferResult> searchCallBack = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                String title;
                String driveId ;
                Boolean folderTrue ;
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (result.getStatus().isSuccess()) {
                        MetadataBuffer mdb = null;
                        try {
                            mdb = result.getMetadataBuffer();
                            if(mdb !=null){
                                System.out.println("mdb is not null");
                                System.out.println(mdb.getCount() + "Count");
                                for (int i= 0; i<mdb.getCount(); i++) {
                                    Metadata md = mdb.get(i);
                                    System.out.println("In for loop");
                                    if (md == null || !md.isDataValid() || md.isTrashed()){
                                        System.out.println("md is null");
                                        continue;
                                    }
                                    title = md.getTitle();
                                    driveId = md.getDriveId().encodeToString();
                                    folderTrue = md.isFolder();
                                    System.out.println("searched a file : " + title + " " + driveId + " " + folderTrue);
                                }
                            }
                        } finally { if (mdb != null) mdb.close(); }
                    }
                    //showMessage("searched a file : " + title + " " + driveId + " " + folderTrue);
                }
            };

    final private ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Cannot find DriveId. Are you authorized to view this file?");
                return;
            }
            DriveId driveId = result.getDriveId();
            DriveFolder folder = driveId.asDriveFolder();

            folder.listChildren(mainActivity.getGoogleApiClient())
                    .setResultCallback(metadataResult);
        }
    };

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataResult = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }
                    //mResultsAdapter.clear();
                    //mResultsAdapter.append(result.getMetadataBuffer());
                    //showMessage("Successfully listed files.");
                    ArrayList<CloudFilesModel> temp = convertFS(result);
                    //ArrayList<CloudFilesModel> temp = new ArrayList<CloudFilesModel>();

                    if(fst != null)
                        fst.clear();

                    if(adapter == null) {
                        System.out.println("New adapter created for search");
                        fst = new ArrayList<CloudFilesModel>();
                        fst.addAll(temp);
                        adapter = new CustomListAdapter(getActivity(), fst, mainActivity);
                        adapter2 = new CustomGridAdapter(getActivity(), fst, mainActivity);
                        adapter.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    } else {
                        System.out.println("Adapter is refreshed for search");
                        fst.clear();
                        fst.addAll(temp);
                        //adapter.clear();
                        //adapter.notifyDataSetChanged();
                        //adapter2.clear();
                        //adapter2.notifyDataSetChanged();
                        //adapter.addAll(fst);
                        //adapter2.addAll(fst);
                        adapter.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                    }
                }
            };

    private ArrayList<CloudFilesModel> convertFS(DriveApi.MetadataBufferResult result) {
        ArrayList<CloudFilesModel> fi = new ArrayList<CloudFilesModel>();
        System.out.println("Inside convertFS for search");
        if (result != null && result.getStatus().isSuccess()) {
            MetadataBuffer mdb = null;
            try {
                mdb = result.getMetadataBuffer();
                System.out.println("Total search results are: "+mdb.getCount());
                if (mdb != null ) for (Metadata md : mdb) {
                    System.out.println("Inside for metadata md");
                    if (md == null || !md.isDataValid()) {
                        System.out.println("md is null or invalid");
                        continue;
                    }
                    CloudFilesModel CFM = new CloudFilesModel();
                    CFM.setName(md.getTitle());
                    CFM.setDriveId(md.getDriveId().getResourceId());
                    if(md.isFolder())
                        CFM.setFileType("Folder");
                    else
                        CFM.setFileType("File");
                    CFM.setPath(md.getCreatedDate().toString());
                    CFM.setInfoIconId(R.drawable.ic_info_black_24dp);

                    String name = md.getTitle();
                    System.out.println("Search Name is:" + name);
                    if(name.length() > 3)
                        name = name.substring(name.length()-3,name.length());
                    System.out.println("Search Extension is:" + name);
                    if(md.isFolder())
                        CFM.setIconId(R.drawable.folder);
                    else if(md.getTitle().equals(".."))
                        CFM.setIconId(R.drawable.sign_left);
                    else if(!md.isFolder() && name.equals("pdf")) {
                        CFM.setIconId(R.drawable.file_pdf);
                        CFM.setMimeType("application/pdf");
                    }
                    else if(!md.isFolder() && (name.equals("doc") || name.equals("docx"))) {
                        CFM.setIconId(R.drawable.file_word);
                        CFM.setMimeType("application/msword");
                    }
                    else if(!md.isFolder() && (name.equals("xlsx") || name.equals("xls"))) {
                        CFM.setIconId(R.drawable.file_excel);
                        CFM.setMimeType("application/vnd.ms-excel");
                    }
                    else if(!md.isFolder() && (name.equals("ppt") || name.equals("pptx"))) {
                        CFM.setIconId(R.drawable.file_powerpoint);
                        CFM.setMimeType("application/vnd.ms-powerpoint");
                    }
                    else if(!md.isFolder() && (name.equals("jpg") || name.equals("jpeg"))) {
                        CFM.setIconId(R.drawable.file_picture);
                        CFM.setMimeType("image/jpeg");
                    } else if(!md.isFolder() && (name.equals("gif"))) {
                        CFM.setIconId(R.drawable.file_picture);
                        CFM.setMimeType("image/gif");
                    } else if(!md.isFolder() && (name.equals("png"))) {
                        CFM.setIconId(R.drawable.file_picture);
                        CFM.setMimeType("image/png");
                    }
                    else {
                        CFM.setIconId(R.drawable.file_text);
                        CFM.setMimeType("text/plain");
                    }

                    CFM.setInfoIconId(R.drawable.ic_info_black_24dp);
                    fi.add(CFM);
                }
            } finally { if (mdb != null) mdb.close(); }
        }
        return fi;
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
/*
    public static DriveFragment newInstance(String param1, String param2) {
        DriveFragment fragment = new DriveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
/*
        item[0] = new CloudFilesModel();
        item[0].setName("Android");
        item[0].setIconId(R.drawable.folder);
        item[0].setInfoIconId(R.drawable.ic_info_black_24dp);
        item[0].setModifiedDate("10 Oct 2015");

        item[1] = new CloudFilesModel();
        item[1].setName("Bluetooth");
        item[1].setIconId(R.drawable.folder);
        item[1].setInfoIconId(R.drawable.ic_info_black_24dp);
        item[1].setModifiedDate("10 Oct 2015");

        item[2] = new CloudFilesModel();
        item[2].setName("DCIM");
        item[2].setIconId(R.drawable.folder);
        item[2].setInfoIconId(R.drawable.ic_info_black_24dp);
        item[2].setModifiedDate("10 Oct 2015");

        item[3] = new CloudFilesModel();
        item[3].setName("Whatsapp");
        item[3].setIconId(R.drawable.folder);
        item[3].setInfoIconId(R.drawable.ic_info_black_24dp);
        item[3].setModifiedDate("10 Oct 2015");
        adapter =new CustomListAdapter(getActivity(), item);
        adapter2 = new CustomGridAdapter(getActivity(),item);
  */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        list=(AbsListView)view.findViewById(R.id.list);
        fst = new ArrayList<CloudFilesModel>();
        adapter = new CustomListAdapter(getActivity(), fst, mainActivity);
        adapter2 = new CustomGridAdapter(getActivity(), fst, mainActivity);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //System.out.println("Clicked view: "+view.toString());
                //System.out.println("And id is "+id);
                CloudFilesModel o;
                if(isList)
                    o = adapter.getItem(position);
                else
                    o = adapter2.getItem(position);
                if(o.getFileType().equalsIgnoreCase("folder")) {
                  FOLDER_ID = o.getDriveId();
                    System.out.println("Current FOLDER_ID is" + FOLDER_ID);
                    if(!folderStack.isEmpty() && !folderStack.peek().equals(FOLDER_ID))
                        folderStack.push(FOLDER_ID);
                    fetch();
                }
                //String Slecteditem = item[+position].getName();
                //Toast.makeText(getActivity().getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }

    public void changeLayout(MenuItem item) {
        list=(AbsListView)getActivity().findViewById(R.id.list);
        if(toggleList()) {

            list.setAdapter(adapter);
            item.setIcon(getResources().getDrawable(R.drawable.ic_view_list_white_48dp));
            GridView gv = (GridView)getActivity().findViewById(R.id.list);
            gv.setNumColumns(1);
        }
        else {
            list.setAdapter(adapter2);
            item.setIcon(getResources().getDrawable(R.drawable.ic_view_module_white_48dp));
            GridView gv = (GridView)getActivity().findViewById(R.id.list);
            gv.setNumColumns(2);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void changeTheLayout(MenuItem item);
        String getCurrentFolderId();
    }
}
