package com.mariovinay.android.spartandrive.cloud;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.mariovinay.android.spartandrive.MainActivity;
import com.mariovinay.android.spartandrive.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FileInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String driveId;
    private String fileType;
    String title;
    //String driveId;
    String extension;
    private OnFragmentInteractionListener mListener;

    public FileInfoFragment() {
        // Required empty public constructor
    }
    private MainActivity mainActivity;

    public FileInfoFragment(String _driveId, String _fileType, MainActivity mainActivity) {
        // Required empty public constructor
        this.driveId = _driveId;
        this.fileType = _fileType;
        this.mainActivity = mainActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FileInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileInfoFragment newInstance(String param1, String param2) {
        FileInfoFragment fragment = new FileInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_file_info, container, false);
        View deleteView = view.findViewById(R.id.deleteHolder);

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drive.DriveApi.fetchDriveId(mainActivity.getGoogleApiClient(), driveId)
                        .setResultCallback(idCallbackForDelete);
            }
        });

        View emailView = view.findViewById(R.id.emailHolder);
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drive.DriveApi.fetchDriveId(mainActivity.getGoogleApiClient(), driveId)
                        .setResultCallback(idCallbackForEmail);
            }
        });

        return view;
    }

    final private ResultCallback<DriveApi.DriveIdResult> idCallbackForEmail = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            new RetrieveDriveFileContentsAsyncTask(
                    mainActivity).execute(result.getDriveId());
        }
    };

    final class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {

            String contents = null;
            DriveFile file = params[0].asDriveFile();
            file.getMetadata(mainActivity.getGoogleApiClient()).setResultCallback(downloadCallBack);
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(mainActivity.getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            InputStream inputStream = driveContents.getInputStream();
            try {
                //System.out.println("Title and extension  "+title+" "+extension);
                //Document document = new Document();
                //PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream((String.format("/storage/emulated/0/Download/%s.%s",title,"pdf"))));

                System.out.println("Extention is :"+extension);
                System.out.println("System property: " + Environment.getExternalStorageDirectory());

                OutputStream outputStream = new FileOutputStream(new File(String.format("/storage/emulated/0/Downloaded/%s",title)));
                int read =0 ;
                byte[] bytes = new byte[1024];

                while((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes,0, read);

                }
            } catch (IOException  e) {
                e.printStackTrace();
            }
            /*
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(driveContents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                contents = builder.toString();
            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
            }
            */
            try {
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            driveContents.discard(mainActivity.getGoogleApiClient());
            return "File Downloaded";
        }

        final private ResultCallback<DriveResource.MetadataResult> downloadCallBack = new
                ResultCallback<DriveResource.MetadataResult>() {
                    //String title;
                    //String driveId;
                    //String extension;

                    // Boolean folderTrue ;
                    @Override
                    public void onResult(DriveResource.MetadataResult result) {
                        if (result.getStatus().isSuccess()) {
                            //MetadataBuffer mdb = null;
                            try {
                                title = result.getMetadata().getTitle();
                                //driveId = md.getDriveId().encodeToString();

                                extension = result.getMetadata().getFileExtension();
                                //result.getMetadata().getFileExtension();
                                System.out.println("downloaded a file with : " + title);
                                System.out.println("extension " + extension);
                                System.out.println( "Alt link " +result.getMetadata().getAlternateLink());
                                System.out.println( "Embed link " +result.getMetadata().getEmbedLink());
                                System.out.println( "Web Content link " +result.getMetadata().getWebContentLink());
                                System.out.println( "Web View link " +result.getMetadata().getWebViewLink());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                };

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                showMessage("Error while reading from the file");
                return;
            }
            showMessage("File contents: " + result);
        }
    }

    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    final ResultCallback<DriveApi.DriveIdResult> idCallbackForDelete =
            new ResultCallback<DriveApi.DriveIdResult>() {
                @Override
                public void onResult(DriveApi.DriveIdResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Cannot find DriveId. Are you authorized to view this file?");
                        return;
                    }
                    DriveId driveId = result.getDriveId();

                    if(fileType.equalsIgnoreCase("folder")) {
                        DriveFolder folder = driveId.asDriveFolder();
                        folder.delete(mainActivity.getGoogleApiClient()).setResultCallback(deleteCallback);
                    } else {
                        DriveFile file = driveId.asDriveFile();
                        file.delete(mainActivity.getGoogleApiClient()).setResultCallback(deleteCallback);
                    }

                    //DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(),
                    //      DriveId.decodeFromString("1Otj6MFnHR55VZYXUPLyMZUQEKXXr7ATduXTV9iwsoFo"));

                }
            };

    final ResultCallback deleteCallback = new ResultCallback() {
        @Override
        public void onResult(Result result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Problem while trying to delete.");
                return;
            }
            showMessage("successfully deleted.");
        }


    };

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
    }
}
