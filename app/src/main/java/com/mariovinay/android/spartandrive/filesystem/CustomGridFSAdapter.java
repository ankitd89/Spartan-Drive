package com.mariovinay.android.spartandrive.filesystem;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mariovinay.android.spartandrive.R;
import com.mariovinay.android.spartandrive.cloud.FileInfoFragment;

import java.util.ArrayList;

public class CustomGridFSAdapter extends ArrayAdapter<FileSystemModel> {

    private final FragmentActivity context;
    private ArrayList<FileSystemModel> item;

    public CustomGridFSAdapter(FragmentActivity context, ArrayList<FileSystemModel> item) {
        super(context, R.layout.files_grid_view, item);
        this.context=context;
        this.item = item;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.files_grid_view, null, true);

        TextView fileName = (TextView) rowView.findViewById(R.id.filename);
        ImageView fileIcon = (ImageView) rowView.findViewById(R.id.fileicon);
        ImageView infoIcon = (ImageView) rowView.findViewById(R.id.infoicon);
        TextView modifiedTime = (TextView) rowView.findViewById(R.id.modifiedTime);
        fileName.setText(getItem(position).getName());
        fileIcon.setImageResource(getItem(position).getIconId());
        infoIcon.setImageResource(getItem(position).getInfoIconId());
        //modifiedTime.setText(getItem(position).getFileType());

        View vi = rowView.findViewById(R.id.infoIconHolder);
        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInfoFragment fragment = new FileInfoFragment();
                FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_placeholder, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return rowView;
    };
}
