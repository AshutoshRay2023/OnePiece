package com.designpattern.Singleton;

public class DatabaseConnectionManager {
    private static DatabaseConnectionManager instance;

    private DatabaseConnectionManager() {
        System.out.println("Database Connection Initialized");
    }

    public static synchronized DatabaseConnectionManager getInstance(){
        if(instance==null){
            instance=new DatabaseConnectionManager();
            System.out.println("Initial Database Connection Creation!");
        }
        return instance;
    }

    public void connect(){
        System.out.println("Connected to database");
    }
    public void disconnect(){
        System.out.println("Disconnected to database");
    }

}
