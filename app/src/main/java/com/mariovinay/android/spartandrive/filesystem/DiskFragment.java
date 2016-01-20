package com.mariovinay.android.spartandrive.filesystem;

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
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.mariovinay.android.spartandrive.MainActivity;
import com.mariovinay.android.spartandrive.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class DiskFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private File currentDir;
    private AbsListView list;
    private boolean isList=true;
    private CustomListFSAdapter adapter;
    private CustomGridFSAdapter adapter2;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Option> dir;
    private MainActivity mainActivity;
    private File fileToUpload;
    private DriveContents contents;
    private DriveFolder pFldr;
    private String titleToUpload;
    private String mimeToUpload;
    public DiskFragment() {

    }

    public DiskFragment(MainActivity mainactivity) {
        // Required empty public constructor
        this.mainActivity = mainactivity;
    }

    public static DiskFragment newInstance(String param1, String param2) {
        DiskFragment fragment = new DiskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_disk, container, false);
        currentDir = new File("/sdcard/");
        fill(currentDir);
        list=(AbsListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FileSystemModel o;
                if (isList)
                    o = adapter.getItem(position);
                else
                    o = adapter2.getItem(position);
                System.out.println("o.getData() is:" + o.getFileType());
                System.out.println("o.getPath() is:" + o.getPath());
                if (o.getFileType().equalsIgnoreCase("folder") || o.getFileType().equalsIgnoreCase("parent directory")) {
                    System.out.println("Inside if");
                    currentDir = new File(o.getPath());
                    fill(currentDir);
                } else {
                    onFileClick(o);
                }

            }
        });
        return view;
    }

    ArrayList<FileSystemModel> fst;

    private void fill(File f)
    {
        File[]dirs = f.listFiles();
        dir = new ArrayList<Option>();
        ArrayList<Option>fls = new ArrayList<Option>();
        try{
            for(File ff: dirs)
            {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else
                {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
            }
        }catch(Exception e)
        {
        }

        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0,new Option("..","Parent Directory",f.getParent()));
        ArrayList<FileSystemModel> temp = convertFS(dir);
        if(fst != null)
            fst.clear();

        if(adapter == null) {
            System.out.println("New adapter created");
            fst = new ArrayList<FileSystemModel>();
            fst.addAll(temp);
            adapter = new CustomListFSAdapter(getActivity(), fst);
            adapter2 = new CustomGridFSAdapter(getActivity(), fst);
        } else {
            System.out.println("Adapter is refreshed");
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

    private ArrayList<FileSystemModel> convertFS(ArrayList<Option> itemoption) {
        ArrayList<FileSystemModel> fi = new ArrayList<FileSystemModel>();

        for(Option op: itemoption) {
            FileSystemModel f = new FileSystemModel();
            f.setName(op.getName());
            f.setFileType(op.getData());
            f.setPath(op.getPath());
            String name = op.getName();
            System.out.println("Name is:" + name);
            if(name.length() > 3)
                name = name.substring(name.length()-3,name.length());
            System.out.println("Extension is:" + name);
            if(op.getData().equals("Folder"))
                f.setIconId(R.drawable.folder);
            else if(op.getName().equals(".."))
                f.setIconId(R.drawable.sign_left);
            else if(!op.getData().equals("Folder") && name.equals("pdf")) {
                f.setIconId(R.drawable.file_pdf);
                f.setMimeType("application/pdf");
            }
            else if(!op.getData().equals("Folder") && (name.equals("doc") || name.equals("docx"))) {
                f.setIconId(R.drawable.file_word);
                f.setMimeType("application/msword");
            }
            else if(!op.getData().equals("Folder") && (name.equals("xlsx") || name.equals("xls"))) {
                f.setIconId(R.drawable.file_excel);
                f.setMimeType("application/vnd.ms-excel");
            }
            else if(!op.getData().equals("Folder") && (name.equals("ppt") || name.equals("pptx"))) {
                f.setIconId(R.drawable.file_powerpoint);
                f.setMimeType("application/vnd.ms-powerpoint");
            }
            else if(!op.getData().equals("Folder") && (name.equals("jpg") || name.equals("jpeg"))) {
                f.setIconId(R.drawable.file_picture);
                f.setMimeType("image/jpeg");
            } else if(!op.getData().equals("Folder") && (name.equals("gif"))) {
                f.setIconId(R.drawable.file_picture);
                f.setMimeType("image/gif");
            } else if(!op.getData().equals("Folder") && (name.equals("png"))) {
                f.setIconId(R.drawable.file_picture);
                f.setMimeType("image/png");
            }
            else {
                f.setIconId(R.drawable.file_text);
                f.setMimeType("text/plain");
            }

            f.setInfoIconId(R.drawable.ic_info_black_24dp);
            fi.add(f);
        }
        return fi;
    }

    public boolean toggleList() {
        isList = !isList;
        return isList;
    }

    FileSystemModel tempToUpload;

    private void onFileClick(FileSystemModel o)
    {
        Toast.makeText(getActivity().getApplicationContext(), "File Uploaded: " + o.getName(), Toast.LENGTH_SHORT).show();
        File file = new File(o.getPath());
        fileToUpload = file;
        String currentFolder = mainActivity.getCurrentFolderId();
        System.out.println("Current folder is:" + currentFolder);
        if(currentFolder=="" || currentFolder==null)
            createFile("root", o.getName(), o.getMimeType(), file);
        else {
            Drive.DriveApi.fetchDriveId(mainActivity.getGoogleApiClient(), mainActivity.getCurrentFolderId())
                    .setResultCallback(idCallbackForUploadFileInFolder);
            tempToUpload = o;
        }
        mainActivity.refreshDriveFragment();
    }

    final ResultCallback<DriveApi.DriveIdResult> idCallbackForUploadFileInFolder = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Cannot find DriveId. Are you authorized to view this file?");
                return;
            }
            DriveId driveId = result.getDriveId();
            File file = new File(tempToUpload.getPath());
            createFile(driveId.toString(), tempToUpload.getName(), tempToUpload.getMimeType(), file);
            /*DriveFolder folder = driveId.asDriveFolder();
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(folderNameToCreate).build();
            folder.createFolder(getGoogleApiClient(), changeSet)
                    .setResultCallback(createFolderCallback);*/
        }
    };

    String createFile(String prnId, String title, String mime, File file) {
        DriveId dId = null;
        GoogleApiClient mGAC = mainActivity.getGoogleApiClient();
        System.out.println(mGAC.isConnected());
        titleToUpload = title;
        mimeToUpload = mime;
        if ( mGAC!= null && mGAC.isConnected() && title != null && mime != null && file != null) try {
            System.out.println("1");
            pFldr = (prnId == null || prnId.equalsIgnoreCase("root")) ?
                    Drive.DriveApi.getRootFolder(mGAC) :
                    Drive.DriveApi.getFolder(mGAC, DriveId.decodeFromString(prnId));
            if (pFldr != null) {
                System.out.println("Uploading file");
               // DriveContents cont = file2Cont(null, file);

                DriveContents cont;
                if (file == null)
                    cont =  null;  //--------------------->>>
              /*  Thread th= new Thread() {

                    @Override
                    public void run() {
                        if (cont == null) {
                            DriveApi.DriveContentsResult r1 = Drive.DriveApi.newDriveContents(mainActivity.getGoogleApiClient()).await();
                            contents = r1 != null && r1.getStatus().isSuccess() ? r1.getDriveContents() : null;
                        }
                    }

                };
                //th.start();*/

                 Drive.DriveApi.newDriveContents(mainActivity.getGoogleApiClient()).setResultCallback(drivContCallback);



            }
        } catch (Exception e) { e.printStackTrace(); }
        return dId == null ? null : dId.encodeToString();
    }


    final private ResultCallback<DriveApi.DriveContentsResult> drivContCallback = new ResultCallback<DriveApi.DriveContentsResult>() {


        @Override
        public void onResult(DriveApi.DriveContentsResult driveContentsResult) {

            DriveContents cont = driveContentsResult.getDriveContents();
            if (cont != null) {
                try {

                    OutputStream oos = cont.getOutputStream();
                    if (oos != null) try {
                        InputStream is = new FileInputStream(fileToUpload);
                        byte[] buf = new byte[4096];
                        int c;
                        while ((c = is.read(buf, 0, buf.length)) > 0) {
                            oos.write(buf, 0, c);
                            oos.flush();
                        }
                    } finally {
                        oos.close();
                    }

                    //ctont =
                    //return cont; //++++++++++++++++++++++++++++++>>>
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }

            }





            MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titleToUpload).setMimeType(mimeToUpload).build();
            pFldr.createFile(mainActivity.getGoogleApiClient(), meta, cont).setResultCallback(createCallback);







        }
    };


    final private ResultCallback<DriveFolder.DriveFileResult> createCallback = new ResultCallback<DriveFolder.DriveFileResult>() {

        DriveId dId;

        @Override
        public void onResult(DriveFolder.DriveFileResult driveFileResult) {
            if (!driveFileResult.getStatus().isSuccess()) {
                showMessage("Create file error");
                return;
            }
            else
            {
                DriveFile dFil = driveFileResult.getDriveFile();
                if (dFil != null) {
                    // DriveResource.MetadataResult r2 = dFil.;
                    // if (r2 != null && r2.getStatus().isSuccess()) {
                    dId = dFil.getDriveId();   //r2.getMetadata().getDriveId();
                    //}
                }
            }
        }
    };

    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
/*
    private DriveContents file2Cont(final DriveContents cont, final File file) {
        if (file == null) return null;  //--------------------->>>
        Thread th= new Thread() {

            @Override
            public void run() {
                if (cont == null) {
                    DriveApi.DriveContentsResult r1 = Drive.DriveApi.newDriveContents(mainActivity.getGoogleApiClient()).await();
                    contents = r1 != null && r1.getStatus().isSuccess() ? r1.getDriveContents() : null;
                }
            }

        };
        //th.start();
        if (cont != null) {
            try {

                OutputStream oos = cont.getOutputStream();
                if (oos != null) try {
                    InputStream is = new FileInputStream(file);
                    byte[] buf = new byte[4096];
                    int c;
                    while ((c = is.read(buf, 0, buf.length)) > 0) {
                        oos.write(buf, 0, c);
                        oos.flush();
                    }
                } finally {
                    oos.close();
                }
                return cont; //++++++++++++++++++++++++++++++>>>
            } catch (Exception ignore) {
                return null;
            }

        }

        else
        {
            return contents;
        }
    }
*/
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void changeTheLayout(MenuItem item);
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


}
