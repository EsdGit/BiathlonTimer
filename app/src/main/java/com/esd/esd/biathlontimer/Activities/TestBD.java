package com.esd.esd.biathlontimer.Activities;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.icu.util.TimeUnit;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;
import com.esd.esd.biathlontimer.MySimpleCursorAdapter;
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;

/**
 * Created by NIL_RIMS_2 on 15.12.2016.
 */

public class TestBD extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    //SimpleCursorAdapter scAdapter;
    MySimpleCursorAdapter scAdapter;
    GridView gv;
    ParticipantSaver ps;
    String name;
    View row;
    TableRow _row;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        name = getIntent().getStringExtra("test");
        ps = new ParticipantSaver(this);

        String[] from = new String[]{DatabaseProvider.DbParticipant.COLUMN_NUMBER, DatabaseProvider.DbParticipant.COLUMN_NAME, DatabaseProvider.DbParticipant.COLUMN_YEAR,
                DatabaseProvider.DbParticipant.COLUMN_GROUP, DatabaseProvider.DbParticipant.COLUMN_COUNTRY};
        int[] to = new int[]{R.id.number, R.id.fio, R.id.year, R.id.group, R.id.country};

        //scAdapter = new SimpleCursorAdapter(this, R.layout.test_row, null, from, to, 0);
        scAdapter = new MySimpleCursorAdapter(this, R.layout.test_row, null, from, to, 0);
        gv = (GridView) findViewById(R.id.gv);
        gv.setAdapter(scAdapter);
        getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MyCursorLoader(this, ps, name);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    static class MyCursorLoader extends CursorLoader
    {
        ParticipantSaver db;
        String name;
        public MyCursorLoader(Context context, ParticipantSaver db, String name)
        {
            super(context);
            this.db = db;
            this.name = name;
        }

        @Override
        public Cursor loadInBackground()
        {
            Cursor cursor = db.getAllData(name, DatabaseProvider.DbParticipant.COLUMN_NAME+" ASC");
            return cursor;
        }
    }
}

