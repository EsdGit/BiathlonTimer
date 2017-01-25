package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

/**
 * Created by Oleg on 20.01.2017.
 */

public class RealmCompetitionSaver
{
    private Realm realm;
    private AtomicLong keyId;
    public RealmCompetitionSaver(Context context, String databaseName)
    {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().schemaVersion(2).deleteRealmIfMigrationNeeded().name(databaseName+".realm").build();
        realm = Realm.getInstance(config);
        if(realm.where(Competition.class).count() == 0)
        {
            keyId = new AtomicLong(0);
        }
        else
        {
            keyId = new AtomicLong(realm.where(Competition.class).max("id").longValue() + 1);
        }
    }

    public RealmCompetitionSaver()
    {

    }

    public List<Competition> GetAllCompetitions(boolean isAscending)
    {
        Sort sort;
        if(isAscending) sort = Sort.ASCENDING;
        else sort = Sort.DESCENDING;
        return realm.copyFromRealm(realm.where(Competition.class).findAllSorted("name", sort));

    }

    public Competition GetCompetition(String name, String date)
    {
        return realm.copyFromRealm(realm.where(Competition.class).equalTo("name", name).equalTo("date",date).findFirst());
    }

    public void SaveCompetition(Competition competition)
    {
        if(realm.where(Competition.class).equalTo("name",competition.getName()).equalTo("date", competition.getDate()).count() > 0) return;
        competition.setId(keyId.getAndIncrement());
        realm.beginTransaction();
        realm.insert(competition);
        realm.commitTransaction();
    }

    public void DeleteCompetition(Competition competition)
    {
        realm.beginTransaction();
        realm.where(Competition.class).equalTo("name",competition.getName()).equalTo("date", competition.getDate()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
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
