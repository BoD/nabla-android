package com.nabla.sdk.core.data.init

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.startup.Initializer
import com.nabla.sdk.core.Configuration

internal class NablaCoreInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Configuration.defaultAppContext = context
        val metaData: Bundle? = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        ).metaData
        val publicApiKey = metaData?.getString("com.nabla.sdk.PUBLIC_API_KEY")
        publicApiKey?.let { Configuration.defaultPublicApiKey = it }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
