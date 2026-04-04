package com.achtsamkeit.tagebuch.domain.repository

import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
    val isBiometricEnabled: Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)
}
