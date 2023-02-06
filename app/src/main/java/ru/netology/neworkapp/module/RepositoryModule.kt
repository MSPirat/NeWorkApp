package ru.netology.neworkapp.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.neworkapp.repository.PostRepository
import ru.netology.neworkapp.repository.PostRepositoryImpl
import ru.netology.neworkapp.repository.WallRepository
import ru.netology.neworkapp.repository.WallRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsWallRepository(impl: WallRepositoryImpl): WallRepository
}