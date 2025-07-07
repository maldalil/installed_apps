package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import java.io.File

class Util {
    companion object {
        fun convertAppToMap(
            packageManager: PackageManager,
            app: ApplicationInfo?,
            withIcon: Boolean
        ): HashMap<String, Any?> {
            val map = HashMap<String, Any?>()
            
            app?.let { safeApp ->
                map["name"] = packageManager.getApplicationLabel(safeApp)
                map["package_name"] = safeApp.packageName
                map["icon"] = if (withIcon) DrawableUtil.drawableToByteArray(safeApp.loadIcon(packageManager)) else ByteArray(0)
                
                try {
                    val packageInfo = packageManager.getPackageInfo(safeApp.packageName, 0)
                    map["version_name"] = packageInfo.versionName
                    map["version_code"] = getVersionCode(packageInfo)
                    map["built_with"] = BuiltWithUtil.getPlatform(packageInfo.applicationInfo)
                    
                    // Ligne 32 corrigée - gestion ultra-sécurisée
                    val appInfo = packageInfo.applicationInfo
                    if (appInfo != null) {
                        val sourceDir = appInfo.sourceDir
                        map["installed_timestamp"] = if (sourceDir != null) {
                            File(sourceDir).lastModified()
                        } else {
                            0L
                        }
                    } else {
                        map["installed_timestamp"] = 0L
                    }
                    
                } catch (e: PackageManager.NameNotFoundException) {
                    // Gérer l'erreur si le package n'existe pas
                    map["version_name"] = null
                    map["version_code"] = null
                    map["built_with"] = null
                    map["installed_timestamp"] = null
                }
            }
            
            return map
        }
        
        fun getPackageManager(context: Context): PackageManager {
            return context.packageManager
        }
        
        @Suppress("DEPRECATION")
        private fun getVersionCode(packageInfo: PackageInfo): Long {
            return if (SDK_INT < P) packageInfo.versionCode.toLong()
            else packageInfo.longVersionCode
        }
    }
}
