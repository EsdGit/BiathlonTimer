package com.esd.esd.biathlontimer.Activities;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.esd.esd.biathlontimer.Adapters.CompetitionAdapter;
import com.esd.esd.biathlontimer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIL_RIMS_2 on 27.01.2017.
 */

public class FolderPicker extends AppCompatActivity
{
    private List<String> _arrayDir;
    private FolderAdapter _adapter;

    RecyclerView _recyclerView;
    TextView _pathText;

    String _currentPath;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_diretory);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + getResources().getString(R.string.folder_activity_name) + "</big>" + "</font>")));
        _recyclerView = (RecyclerView) findViewById(R.id.list_file);
        _pathText = (TextView) findViewById(R.id.path_file);

        _currentPath = Environment.getExternalStorageDirectory().getPath();

        _adapter = new FolderAdapter();


        _recyclerView.setAdapter(_adapter);
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getFiles();


    }

    private void getFiles()
    {
        List<String> localList = new ArrayList<String>();
        File[] files = new File(_currentPath).listFiles();
        for(File file:files)
        {
            if(file.isDirectory())
            {
                localList.add(file.getName());
            }
        }

        _adapter.ChangeList(localList);
        _pathText.setText(_currentPath);
    }


    private class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder>
    {
        private List<String> fileNames;
        public FolderAdapter()
        {
            fileNames = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_file_directory, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            String fileName = fileNames.get(position);
            holder.fileNameText.setText(fileName);
        }

        public void ChangeList(List<String> list)
        {
            fileNames.clear();
            fileNames.addAll(list);
            notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            return fileNames.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            private TextView fileNameText;
            public ViewHolder(View itemView) {
                super(itemView);
                fileNameText = (TextView) itemView.findViewById(R.id.file);
            }
        }
    }

}
