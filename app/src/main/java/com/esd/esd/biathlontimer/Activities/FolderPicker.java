package com.esd.esd.biathlontimer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private MenuItem _accept;
    private MenuItem _cancel;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_file_directory, menu);
        _accept = (MenuItem) menu.findItem(R.id.file_directory_accept);
        _cancel = (MenuItem) menu.findItem(R.id.file_directory_cancel);
        _accept.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        _cancel.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.file_directory_accept:
                Toast.makeText(getApplicationContext(),"принять",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("url", _currentPath);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.file_directory_cancel:
                Toast.makeText(getApplicationContext(),"отмена",Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == (KeyEvent.KEYCODE_BACK))
        {
           // if(_currentPath.equals(Environment.getExternalStorageDirectory().getPath())) return false;
            String[] splits = _currentPath.split("/");
            if(splits.length > 2)
            {
                _currentPath = splits[0];
                for (int i = 1; i < splits.length - 1; i++) {
                    _currentPath += "/" + splits[i];
                }
                getFiles();
            }
        }
        return false;
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
