package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;

import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.ArrayList;
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

    public void SaveSportsman(final MegaSportsman sportsman)
    {
        if(realm.where(MegaSportsman.class).equalTo("name",sportsman.getName()).equalTo("year", sportsman.getYear()).
                equalTo("country", sportsman.getCountry()).count() > 0) return;
        sportsman.setId(keyId.getAndIncrement());
        realm.beginTransaction();
        realm.insert(sportsman);
        realm.commitTransaction();
    }

    public void SaveSportsmen(final List<MegaSportsman> sportsmen)
    {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm)
            {
                for(int i = 0; i < sportsmen.size(); i++)
                {
                    sportsmen.get(i).setId(keyId.getAndIncrement());
                }
                realm.insert(sportsmen);
            }
        });

    }
    public int GetPositionInList(int number)
    {
        List<MegaSportsman> sportsmen = realm.where(MegaSportsman.class).findAllSorted("number",Sort.ASCENDING);
        for(int i = 0; i < sportsmen.size(); i++)
        {
            if(sportsmen.get(i).getNumber() == number)
            {
                return i;
            }
        }
        return 0;
    }

    public List<MegaSportsman> SortByGroup(ArrayList<String> group)
    {
        List<MegaSportsman> localList = realm.copyFromRealm(realm.where(MegaSportsman.class).in("group", group.toArray(new String[group.size()])).findAll());
        return localList;
    }

    public void DeleteSportsmen(List<MegaSportsman> sportsmen)
    {
        realm.beginTransaction();
        for(int i = 0; i < sportsmen.size(); i++)
        {

            realm.where(MegaSportsman.class).equalTo("name",sportsmen.get(i).getName()).equalTo("year", sportsmen.get(i).getYear()).
                    equalTo("country", sportsmen.get(i).getCountry()).findAll().deleteAllFromRealm();

        }
        realm.commitTransaction();
    }

    public MegaSportsman getMegaSportsman(int number)
    {
        MegaSportsman sportsman = realm.copyFromRealm(realm.where(MegaSportsman.class).equalTo("number", number).findFirst());
        return sportsman;
    }

    public void DeleteSportsman(MegaSportsman sportsman)
    {
        realm.beginTransaction();
        realm.where(MegaSportsman.class).equalTo("name",sportsman.getName()).equalTo("year", sportsman.getYear()).
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
