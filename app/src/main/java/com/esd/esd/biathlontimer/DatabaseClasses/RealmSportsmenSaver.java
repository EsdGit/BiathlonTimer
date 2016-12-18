package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;

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
    public RealmSportsmenSaver(Context context, String databaseName)
    {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().schemaVersion(2).deleteRealmIfMigrationNeeded().name(databaseName+".realm").build();
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
        sportsman.setId(keyId.getAndIncrement());
        realm.beginTransaction();
        realm.insert(sportsman);
        realm.commitTransaction();
    }

    public void DeleteSportsman(Sportsman sportsman)
    {
        realm.beginTransaction();
        realm.where(Sportsman.class).equalTo("name", sportsman.getName()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void DeleteSportsmen(List<Sportsman> sportsmen)
    {
        for(int i = 0; i < sportsmen.size(); i++)
        {
            realm.beginTransaction();
            realm.where(Sportsman.class).equalTo("name", sportsmen.get(i).getName()).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

    public void SaveSportsmen(List<Sportsman> sportsmen)
    {
        for(int i = 0; i < sportsmen.size(); i++)
        {
            sportsmen.get(i).setId(keyId.getAndIncrement());
        }

        realm.beginTransaction();
        realm.insert(sportsmen);
        realm.commitTransaction();
    }
}
