package com.github.burachevsky.mqtthub.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val context: Context,
) : FileRepository {

    override suspend fun saveContent(content: String, uri: Uri) {
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use {
                it.write(content.toByteArray())
            }
        }
    }

    override suspend fun getContent(uri: Uri): String {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri).use {
                it?.bufferedReader()
                    ?.readText()
                    .orEmpty()
            }
        }
    }
}