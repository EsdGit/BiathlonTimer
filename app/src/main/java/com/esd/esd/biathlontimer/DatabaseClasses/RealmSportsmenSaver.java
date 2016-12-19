package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;
import android.graphics.Color;

import com.esd.esd.biathlontimer.Sportsman;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Oleg on 18.12.2016.
 */

public class RealmSportsmenSaver
{
    private Realm realm;
    private AtomicLong keyId;
    private boolean isMainDb = false;
    public RealmSportsmenSaver(Context context, String databaseName)
    {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().schemaVersion(2).deleteRealmIfMigrationNeeded().name(databaseName+".realm").build();
        if(databaseName.equals("MAIN")) isMainDb = true;
        realm = Realm.getInstance(config);
        if(realm.where(Sportsman.class).count() == 0)
        {
            keyId = new AtomicLong(0);
        }
        else
        {
            keyId = new AtomicLong(realm.where(Sportsman.class).max("id").longValue() + 1);
        }
    }

    public List<Sportsman> GetSportsmen()
    {
        RealmResults<Sportsman> results = realm.where(Sportsman.class).findAll();
        return realm.copyFromRealm(results);
    }

    public List<Sportsman> GetSortedSportsmen(String sortBy, boolean sortState)
    {

        Sort localSort;
        if(sortState) localSort = Sort.ASCENDING;
        else localSort = Sort.DESCENDING;

        RealmResults<Sportsman> results = realm.where(Sportsman.class).findAllSorted(sortBy, localSort);
        return realm.copyFromRealm(results);
    }

    public void SaveSportsman(Sportsman sportsman)
    {
        if(realm.where(Sportsman.class).equalTo("name",sportsman.getName()).equalTo("year", sportsman.getYear()).
            equalTo("country", sportsman.getCountry()).count() > 0) return;
        sportsman.setId(keyId.getAndIncrement());
        // C цветом что-то не то
        if(isMainDb) sportsman.setColor(Color.BLACK);
        realm.beginTransaction();
        realm.insert(sportsman);
        realm.commitTransaction();
    }

    public void DeleteSportsman(Sportsman sportsman)
    {
        realm.beginTransaction();
        realm.where(Sportsman.class).equalTo("name", sportsman.getName()).equalTo("year", sportsman.getYear()).
                equalTo("country",sportsman.getCountry()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void DeleteSportsmen(List<Sportsman> sportsmen)
    {
        for(int i = 0; i < sportsmen.size(); i++)
        {
            realm.beginTransaction();
            realm.where(Sportsman.class).equalTo("name", sportsmen.get(i).getName()).equalTo("year", sportsmen.get(i).getYear()).
                    equalTo("country",sportsmen.get(i).getCountry()).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

    public void SaveSportsmen(List<Sportsman> sportsmen)
    {

        for(int i = 0; i < sportsmen.size(); i++)
        {
            SaveSportsman(sportsmen.get(i));
        }

    }
}
