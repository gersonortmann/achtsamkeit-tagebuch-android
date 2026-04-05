package com.achtsamkeit.tagebuch.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.achtsamkeit.tagebuch.core.security.BiometricAuthenticator
import com.achtsamkeit.tagebuch.core.security.BiometricService
import com.achtsamkeit.tagebuch.core.utils.Constants
import com.achtsamkeit.tagebuch.data.local.database.AppDatabase.Companion.MIGRATION_3_4
import com.achtsamkeit.tagebuch.data.local.dao.GuidedQuestionDao
import com.achtsamkeit.tagebuch.data.local.dao.JournalEntryDao
import com.achtsamkeit.tagebuch.data.local.database.AppDatabase
import com.achtsamkeit.tagebuch.data.repository.AchtsamkeitRepositoryImpl
import com.achtsamkeit.tagebuch.data.repository.JournalRepositoryImpl
import com.achtsamkeit.tagebuch.data.repository.SecurityRepositoryImpl
import com.achtsamkeit.tagebuch.domain.repository.AchtsamkeitRepository
import com.achtsamkeit.tagebuch.domain.repository.JournalRepository
import com.achtsamkeit.tagebuch.domain.repository.SecurityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
        .addMigrations(MIGRATION_3_4)
        .fallbackToDestructiveMigration(false)
        .build()
    }

    @Provides
    @Singleton
    fun provideJournalEntryDao(db: AppDatabase): JournalEntryDao {
        return db.journalEntryDao
    }

    @Provides
    @Singleton
    fun provideGuidedQuestionDao(db: AppDatabase): GuidedQuestionDao {
        return db.guidedQuestionDao
    }

    @Provides
    @Singleton
    fun provideJournalRepository(
        journalDao: JournalEntryDao,
        questionDao: GuidedQuestionDao
    ): JournalRepository {
        return JournalRepositoryImpl(journalDao, questionDao)
    }

    @Provides
    @Singleton
    fun provideSecurityRepository(dataStore: DataStore<Preferences>): SecurityRepository {
        return SecurityRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideAchtsamkeitRepository(dataStore: DataStore<Preferences>): AchtsamkeitRepository {
        return AchtsamkeitRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideBiometricService(impl: BiometricAuthenticator): BiometricService = impl
}
