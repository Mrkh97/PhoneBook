package com.enigmasolver.phonebook.shared.extensions

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createImageFile(): Uri {
    val timeStamp = System.currentTimeMillis()
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        cacheDir
    )
    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        image
    )
}