package com.enigmasolver.phonebook.features.contacts.data

import android.content.Context
import android.net.Uri
import com.enigmasolver.phonebook.features.contacts.domain.ContactRequest
import com.enigmasolver.phonebook.features.contacts.domain.ContactResponse
import com.enigmasolver.phonebook.shared.ApiServices
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class RemoteContactRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ApiServices
) {

    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val tempFile = File.createTempFile("upload_source_", ".jpg", context.cacheDir)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return Result.failure(Exception("Could not open file from Uri"))

            val fileSizeLimit = 500_000L

            val finalFile = if (tempFile.length() > fileSizeLimit) {
                Compressor.compress(context, tempFile) {
                    resolution(1280, 720)
                    quality(80)
                    size(fileSizeLimit)
                }
            } else {
                tempFile
            }

            val requestFile = finalFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody =
                MultipartBody.Part.createFormData("image", "profile.jpg", requestFile)
            val response = api.uploadImage(multipartBody)

            try {
                if (tempFile.exists()) tempFile.delete()
                if (finalFile != tempFile && finalFile.exists()) finalFile.delete()
            } catch (e: Exception) {
            }

            if (response.success) {
                Result.success(response.data.imageUrl)
            } else {
                Result.failure(Exception(response.messages?.firstOrNull() ?: "Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addContact(
        name: String,
        surname: String,
        phone: String,
        imageUrl: String?
    ): Result<ContactResponse> {
        val response = api.addContact(
            ContactRequest(
                firstName = name,
                lastName = surname,
                phoneNumber = phone,
                profileImageUrl = imageUrl
            )
        );
        if (response.success) {
            return Result.success(response.data)
        }
        return Result.failure(Exception(response.messages?.joinToString(separator = ",")))
    }

    suspend fun updateContact(
        id: String,
        name: String,
        surname: String,
        phone: String,
        imageUrl: String?
    ): Result<ContactResponse> {
        val response = api.updateContactById(
            id,
            ContactRequest(
                firstName = name,
                lastName = surname,
                phoneNumber = phone,
                profileImageUrl = imageUrl
            )
        );
        if (response.success) {
            return Result.success(response.data)
        }
        return Result.failure(Exception(response.messages?.joinToString(separator = ",")))
    }

    suspend fun getContactById(contactId: String): Result<ContactResponse> {
        val response = api.getContactById(contactId);
        if (response.success) {
            return Result.success(response.data as ContactResponse)
        }
        return Result.failure(Exception(response.messages?.joinToString(separator = ",")))
    }

    suspend fun listContacts(): Result<List<ContactResponse>> {
        val response = api.listContacts()
        if (response.success) {
            return Result.success(response.data.users)
        }
        return Result.failure(Exception(response.messages?.joinToString(separator = ",")))
    }

    suspend fun deleteContactWithId(id: String): Result<Unit> {
        val response = api.deleteContactWithId(id)
        if (response.success) {
            return Result.success(Unit)
        }
        return Result.failure(Exception(response.messages?.joinToString(separator = ",")))
    }
}