package com.noghre.sod.data.local.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.noghre.sod.data.local.NoghreSodDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Database migration tests.
 * Ensures smooth upgrades between database schema versions.
 * 
 * @since 1.0.0
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    
    private val testDb = "test_database"
    
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        NoghreSodDatabase::class.java,
        listOf(),  // migrations
        FrameworkSQLiteOpenHelperFactory()
    )
    
    @Test
    fun migrate_1_to_2() {
        // Create database with version 1
        val db1 = helper.createDatabase(testDb, 1)
        db1.execSQL(
            "CREATE TABLE products " +
            "(id TEXT PRIMARY KEY, name TEXT NOT NULL, price REAL NOT NULL)"
        )
        db1.close()
        
        // Migrate to version 2
        val db2 = helper.runMigrationsAndValidate(
            testDb,
            2,
            true,
            // Add migration here: MIGRATION_1_2
        )
        
        // Verify new schema
        val cursor = db2.query("PRAGMA table_info(products)")
        assert(cursor.count > 3)  // Should have more columns in v2
        cursor.close()
        db2.close()
    }
    
    @Test
    fun database_opens_successfully_with_latest_schema() {
        // This should not throw any exceptions
        helper.runMigrationsAndValidate(
            testDb,
            NoghreSodDatabase.DATABASE_VERSION,
            true,
            // Add all migrations here
        )
    }
}
