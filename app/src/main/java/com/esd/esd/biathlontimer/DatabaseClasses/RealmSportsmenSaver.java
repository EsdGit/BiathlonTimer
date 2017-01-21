package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;
import android.graphics.Color;

import com.esd.esd.biathlontimer.Sportsman;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
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

    public List<Sportsman> GetSportsmen(String sortBy, boolean sortState)
    {
        if(sortState) return realm.copyFromRealm(realm.where(Sportsman.class).findAllSorted(sortBy, Sort.ASCENDING));
        else return realm.copyFromRealm(realm.where(Sportsman.class).findAllSorted(sortBy, Sort.DESCENDING));
    }

    public void SaveSportsman(Sportsman sportsman)
    {
        if(realm.where(Sportsman.class).equalTo("name",sportsman.getName()).equalTo("year", sportsman.getYear()).
            equalTo("country", sportsman.getCountry()).count() > 0) return;
        sportsman.setId(keyId.getAndIncrement());
        // C цветом что-то не то
        realm.beginTransaction();
        realm.insert(sportsman);
        realm.commitTransaction();
    }

    public void DeleteSportsman(Sportsman sportsman)
    {
        realm.beginTransaction();
        realm.where(Sportsman.class).equalTo("name",sportsman.getName()).equalTo("year", sportsman.getYear()).
                equalTo("country", sportsman.getCountry()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void DeleteSportsmen(List<Sportsman> sportsmen)
    {
        realm.beginTransaction();
        for(int i = 0; i < sportsmen.size(); i++)
        {
            realm.where(Sportsman.class).equalTo("name",sportsmen.get(i).getName()).equalTo("year", sportsmen.get(i).getYear()).
                    equalTo("country", sportsmen.get(i).getCountry()).findAll().deleteAllFromRealm();
        }
        realm.commitTransaction();
    }

    public void SaveSportsmen(List<Sportsman> sportsmen)
    {

        for(int i = 0; i < sportsmen.size(); i++)
        {
            SaveSportsman(sportsmen.get(i));
        }

    }

    public List<Sportsman> SortByGroup(ArrayList<String> group)
    {
        List<Sportsman> localList = realm.copyFromRealm(realm.where(Sportsman.class).in("group", group.toArray(new String[group.size()])).findAll());
        return localList;
    }

    public List<Sportsman> SortBy(String sortBy, boolean state, ArrayList<String> group)
    {
        Sort sort;
        if(state) sort = Sort.ASCENDING;
        else sort = Sort.DESCENDING;
        List<Sportsman> results = realm.copyFromRealm(realm.where(Sportsman.class).in("group", group.toArray(new String[group.size()])).findAllSorted(sortBy, sort));
        return results;
    }

    public void Dispose()
    {
        realm.close();
    }

    public void DeleteTable()
    {
        RealmConfiguration configuration = realm.getConfiguration();
        realm.close();
        Realm.deleteRealm(configuration);
    }
}
