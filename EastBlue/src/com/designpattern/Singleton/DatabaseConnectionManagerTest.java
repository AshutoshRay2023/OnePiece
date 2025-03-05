package com.designpattern.Singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;

public class DatabaseConnectionManagerTest {
    @Test
    public void testSameInstance(){
        DatabaseConnectionManager databaseConnectionManager=DatabaseConnectionManager.getInstance();
        DatabaseConnectionManager databaseConnectionManager1=DatabaseConnectionManager.getInstance();
        assertSame(databaseConnectionManager,databaseConnectionManager1,"Instance are not the same!");
    }

    @Test
    public void testDatabaseOperations(){
        DatabaseConnectionManager databaseConnectionManager=DatabaseConnectionManager.getInstance();
                assertDoesNotThrow(databaseConnectionManager::connect);
                assertDoesNotThrow(databaseConnectionManager::disconnect);
    }
}
