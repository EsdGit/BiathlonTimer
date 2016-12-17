package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;

import com.esd.esd.biathlontimer.MyParticipant;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Oleg on 18.12.2016.
 */

public class RealmProvider
{
    private Realm _realm;
    RealmResults<MyParticipant> participants;
    public RealmProvider(Context context)
    {
        Realm.init(context);
        _realm = Realm.getDefaultInstance();
        _realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm)
            {
               participants = realm.where(MyParticipant.class).findAll();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        });
    }

    public ArrayList<MyParticipant> getList()
    {
        return (ArrayList<MyParticipant>) participants.subList(0, participants.size()-1);
    }

    public void addParticipant(String name, int year, String country, int number, String group)
    {
        _realm.beginTransaction();
        MyParticipant myParticipant = _realm.createObject(MyParticipant.class);
        myParticipant.setData(name,year,country,number,group);
        _realm.commitTransaction();
    }


}
