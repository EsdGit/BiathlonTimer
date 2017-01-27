package com.esd.esd.biathlontimer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private static FolderAdapter _adapter;

    RecyclerView _recyclerView;
    static TextView _pathText;
    Button _acceptButton;
    Button _declineButton;

    static String _currentPath;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_diretory);
        _recyclerView = (RecyclerView) findViewById(R.id.list_file);
        _pathText = (TextView) findViewById(R.id.path_file);
        _acceptButton = (Button) findViewById(R.id.positive_button);
        _declineButton = (Button) findViewById(R.id.negative_button);

        _currentPath = Environment.getExternalStorageDirectory().getPath();

        _adapter = new FolderAdapter();


        _recyclerView.setAdapter(_adapter);
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getFiles();

        _acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("url", _currentPath);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        _declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    this.finalize();
                }catch (Throwable ex)
                {

                }
            }
        });


    }

    public static void getFiles()
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
            holder.clickListener.setFileName(fileName);

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
            private ClickListener clickListener;
            public ViewHolder(View itemView) {
                super(itemView);
                fileNameText = (TextView) itemView.findViewById(R.id.file);
                clickListener = new ClickListener();
                itemView.setOnClickListener(clickListener);
            }

            private class ClickListener implements View.OnClickListener
            {
                private String fileName;
                @Override
                public void onClick(View view)
                {
                    _currentPath += "/"+fileName;
                    FolderPicker.getFiles();

                }
                public void setFileName(String fileName){this.fileName = fileName;}
            }
        }
    }

}
