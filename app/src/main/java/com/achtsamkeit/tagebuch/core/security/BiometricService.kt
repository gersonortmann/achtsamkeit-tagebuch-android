package com.achtsamkeit.tagebuch.core.security

import androidx.fragment.app.FragmentActivity

interface BiometricService {
    fun isAvailable(activity: FragmentActivity): Boolean
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: () -> Unit
    )
}
