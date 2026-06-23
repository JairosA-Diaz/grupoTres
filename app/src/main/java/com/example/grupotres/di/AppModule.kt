package com.example.grupotres.di

import com.example.grupotres.repository.ChallengeRepository
import com.example.grupotres.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun provideChallengeRepository(): ChallengeRepository {
        return ChallengeRepository()
    }
}
