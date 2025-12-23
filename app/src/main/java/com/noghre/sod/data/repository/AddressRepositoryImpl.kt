package com.noghre.sod.data.repository

import com.noghre.sod.data.dto.AddressDto
import com.noghre.sod.data.dto.request.UpdateAddressRequest
import com.noghre.sod.data.local.dao.AddressDao
import com.noghre.sod.data.mapper.AddressMapper
import com.noghre.sod.data.remote.api.NoghreSodApiService
import com.noghre.sod.data.remote.exception.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class AddressRepositoryImpl(
    private val apiService: NoghreSodApiService,
    private val addressDao: AddressDao,
    private val mapper: AddressMapper
) : AddressRepository {

    override fun getAddresses(): Flow<Result<List<AddressDto>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getAddresses()
            if (response.success && response.data != null) {
                addressDao.insertAddresses(mapper.toEntities(response.data))
                emit(Result.Success(response.data))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch addresses")
            emitCached()
        }
    }

    override suspend fun createAddress(request: UpdateAddressRequest): Result<AddressDto> {
        return try {
            val response = apiService.createAddress(request)
            if (response.success && response.data != null) {
                addressDao.insertAddress(mapper.toEntity(response.data))
                Result.Success(response.data)
            } else {
                Result.Error(ApiException.ServerError())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    override suspend fun updateAddress(
        id: String,
        request: UpdateAddressRequest
    ): Result<AddressDto> {
        return try {
            val response = apiService.updateAddress(id, request)
            if (response.success && response.data != null) {
                addressDao.updateAddress(mapper.toEntity(response.data))
                Result.Success(response.data)
            } else {
                Result.Error(ApiException.ServerError())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    override suspend fun deleteAddress(id: String): Result<Unit> {
        return try {
            apiService.deleteAddress(id)
            addressDao.deleteAddressById(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private suspend fun emitCached() {
        addressDao.getAllAddresses().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(mapper.toDtos(entities)))
            }
        }
    }

    private fun handleException(e: Exception): ApiException {
        return when (e) {
            is ApiException -> e
            else -> ApiException.UnknownError(e.message ?: "Unknown error")
        }
    }
}

interface AddressRepository {
    fun getAddresses(): Flow<Result<List<AddressDto>>>
    suspend fun createAddress(request: UpdateAddressRequest): Result<AddressDto>
    suspend fun updateAddress(id: String, request: UpdateAddressRequest): Result<AddressDto>
    suspend fun deleteAddress(id: String): Result<Unit>
}