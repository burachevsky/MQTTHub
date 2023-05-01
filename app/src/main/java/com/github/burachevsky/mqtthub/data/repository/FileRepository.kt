package com.github.burachevsky.mqtthub.data.repository

import android.net.Uri

interface FileRepository {

    suspend fun saveContent(content: String, uri: Uri)

    suspend fun getContent(uri: Uri): String
}