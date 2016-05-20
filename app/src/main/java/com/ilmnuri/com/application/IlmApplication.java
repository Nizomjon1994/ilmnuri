package com.ilmnuri.com.application;

import android.app.Application;

import javax.inject.Inject;

import dagger.ObjectGraph;
import com.ilmnuri.com.api.IlmApi;
import com.ilmnuri.com.module.MainModule;

/**
 * Created by User on 18.05.2016.
 */
public class IlmApplication extends Application {


    private ObjectGraph mObjectGraph;
    @Inject
    IlmApi mApi;
    public boolean isViewed;

    @Override
    public void onCreate() {
        super.onCreate();
        mObjectGraph = ObjectGraph.create(new MainModule(this));
        inject(this);
//
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .resetViewBeforeLoading(true)
//                .build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .defaultDisplayImageOptions(defaultOptions)
//
//                .build();
//        ImageLoader.getInstance().init(config);
    }


    public void inject(Object object) {
        try {
            mObjectGraph.inject(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }
}
