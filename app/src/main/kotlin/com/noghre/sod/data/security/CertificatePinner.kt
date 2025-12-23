package com.noghre.sod.data.security

import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.CertificatePinner
import javax.inject.Inject

@ViewModelScoped
class CertificatePinnerProvider @Inject constructor() {
    fun get(): CertificatePinner {
        return CertificatePinner.Builder()
            .add("api.noghresod.com", "sha256/CERTIFICATE_PIN_HERE")
            .build()
    }
}
