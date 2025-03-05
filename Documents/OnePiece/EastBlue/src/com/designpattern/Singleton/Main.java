package com.designpattern.Singleton;

public class Main {



    public static void main(String[] args) {
       //Singleton
        DatabaseConnectionManager databaseConnectionManager= DatabaseConnectionManager.getInstance();
        databaseConnectionManager.connect();
        System.out.println(databaseConnectionManager);
        databaseConnectionManager.disconnect();
        DatabaseConnectionManager databaseConnectionManager1=DatabaseConnectionManager.getInstance();
        System.out.println(databaseConnectionManager1);
        databaseConnectionManager1.connect();
    }

}
