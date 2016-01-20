package com.mariovinay.android.spartandrive.cloud;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mariovinay.android.spartandrive.MainActivity;
import com.mariovinay.android.spartandrive.R;

import java.util.ArrayList;

/**
 * Created by mario on 12/2/2015.
 */
public class CustomListAdapter extends ArrayAdapter<CloudFilesModel> {

    private final FragmentActivity context;
    private final ArrayList<CloudFilesModel> item;
    private MainActivity mainActivity;

    public CustomListAdapter(FragmentActivity context, ArrayList<CloudFilesModel> item, MainActivity mainActivity) {
        super(context, R.layout.files_list_view, item);
        this.context=context;
        this.item=item;
        this.mainActivity = mainActivity;
    }

    public View getView(final int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.files_list_view, null, true);

        TextView fileName = (TextView) rowView.findViewById(R.id.filename);
        ImageView fileIcon = (ImageView) rowView.findViewById(R.id.fileicon);
        ImageView infoIcon = (ImageView) rowView.findViewById(R.id.infoicon);
        TextView modifiedTime = (TextView) rowView.findViewById(R.id.modifiedTime);

        fileName.setText(getItem(position).getName());
        fileIcon.setImageResource(getItem(position).getIconId());
        infoIcon.setImageResource(getItem(position).getInfoIconId());
        modifiedTime.setText(getItem(position).getPath());

        /*View v = rowView.findViewById(R.id.clickHolder);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Slecteditem = getItem(position).getName();
                Toast.makeText(context.getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });*/

        View vi = rowView.findViewById(R.id.infoIconHolder);
        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInfoFragment fragment = new FileInfoFragment(getItem(position).getDriveId(),getItem(position).getFileType(), mainActivity);
                FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_placeholder, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return rowView;
    };
}
