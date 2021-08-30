package com.suthar.todo_location;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static MyApplication singleton;
    public Location des;
    ArrayList<Event> EventToDo;
    private List<Location> myLocations;
    private String idd;

    public static MyApplication getSingleton() {
        return singleton;
    }

    public String getIdd() {
        return idd;
    }

    public void setIdd(String idd) {
        this.idd = idd;
    }

    public ArrayList<Event> getEventToDo() {
        return EventToDo;
    }

    public void setEventToDo(ArrayList<Event> eventToDo) {
        EventToDo = eventToDo;
    }

    public List<Location> getMyLocations() {
        return myLocations;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
        EventToDo = new ArrayList<Event>();
        des = new Location("");
        des.setLatitude(26.4732275);
        des.setLongitude(73.1164138);

    }


}


