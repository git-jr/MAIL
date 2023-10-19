package com.alura.mail.di

import com.alura.mail.FileUtil
import com.alura.mail.mlkit.TextTranslate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TextTranslateModule {
    @Provides
    fun provideTextTranslate(fileUtil: FileUtil): TextTranslate {
        return TextTranslate(fileUtil)
    }
}