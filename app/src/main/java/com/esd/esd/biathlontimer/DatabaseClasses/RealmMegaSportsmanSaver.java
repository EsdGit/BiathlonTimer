package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;

import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

/**
 * Created by Oleg on 26.12.2016.
 */

public class RealmMegaSportsmanSaver
{
    private Realm realm;
    private AtomicLong keyId;
    private Context _localContext;
    public RealmMegaSportsmanSaver(Context context, String databaseName)
    {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().schemaVersion(2).deleteRealmIfMigrationNeeded().name(databaseName+".realm").build();
        realm = Realm.getInstance(config);
        if(realm.where(MegaSportsman.class).count() == 0)
        {
            keyId = new AtomicLong(0);
        }
        else
        {
            keyId = new AtomicLong(realm.where(MegaSportsman.class).max("id").longValue() + 1);
        }
    }

    public List<MegaSportsman> GetSportsmen(String sortBy, boolean sortState)
    {
        if(sortState) return realm.copyFromRealm(realm.where(MegaSportsman.class).findAllSorted(sortBy, Sort.ASCENDING));
        else return realm.copyFromRealm(realm.where(MegaSportsman.class).findAllSorted(sortBy, Sort.DESCENDING));
    }

    public void SaveSportsman(MegaSportsman sportsman)
    {
        if(realm.where(MegaSportsman.class).equalTo("name",sportsman.getName()).equalTo("year", sportsman.getYear()).
                equalTo("country", sportsman.getCountry()).count() > 0) return;
        sportsman.setId(keyId.getAndIncrement());
        // C цветом что-то не то
        realm.beginTransaction();
        realm.insert(sportsman);
        realm.commitTransaction();
    }

    public void SaveSportsmen(List<MegaSportsman> sportsmen)
    {

        for(int i = 0; i < sportsmen.size(); i++)
        {
            SaveSportsman(sportsmen.get(i));
        }

    }

    public void DeleteSportsmen(List<MegaSportsman> sportsmen)
    {
        for(int i = 0; i < sportsmen.size(); i++)
        {
            realm.beginTransaction();
            realm.where(Sportsman.class).equalTo("name",sportsmen.get(i).getName()).equalTo("year", sportsmen.get(i).getYear()).
                    equalTo("country", sportsmen.get(i).getCountry()).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

    public void DeleteSportsman(MegaSportsman sportsman)
    {
        realm.beginTransaction();
        realm.where(Sportsman.class).equalTo("name",sportsman.getName()).equalTo("year", sportsman.getYear()).
                equalTo("country", sportsman.getCountry()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void DeleteTable()
    {
        realm.close();
        RealmConfiguration configuration = realm.getConfiguration();
        Realm.deleteRealm(configuration);
    }
}
