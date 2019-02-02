package com.mygate.mahendran.mygatedemo.view;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.exceptions.RealmMigrationNeededException;

public class App extends Application {

    private static App instance;


    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Realm.init(this);

    }

    public Realm getRealmInstance() {
        try {
            return Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            try {
                return Realm.getDefaultInstance();
            } catch (Exception ex) {
                return Realm.getDefaultInstance();
                //No Realm file to remove.
            }
        }
    }
}
