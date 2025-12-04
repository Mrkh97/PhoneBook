package com.enigmasolver.phonebook.shared;

import com.enigmasolver.phonebook.features.contacts.domain.ContactRequest
import com.enigmasolver.phonebook.features.contacts.domain.ContactResponse
import com.enigmasolver.phonebook.features.contacts.domain.ListContactResponse
import com.enigmasolver.phonebook.features.contacts.domain.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiServices {

    @POST("api/User")
    suspend fun addContact(
        @Body request: ContactRequest
    ): ApiResponse<ContactResponse>

    @GET("api/User/{id}")
    suspend fun getContactById(@Path("id") id: String): ApiResponse<ContactResponse?>

    @PUT("api/User/{id}")
    suspend fun updateContactById(
        @Path("id") id: String,
        @Body request: ContactRequest
    ): ApiResponse<ContactResponse>

    @DELETE("api/User/{id}")
    suspend fun deleteContactWithId(
        @Path("id") id: String
    ): ApiResponse<Unit>

    @GET("api/User/GetAll")
    suspend fun listContacts(): ApiResponse<ListContactResponse>

    @Multipart
    @POST("api/User/UploadImage")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): ApiResponse<UploadImageResponse>
}
