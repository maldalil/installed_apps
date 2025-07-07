package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import java.util.zip.ZipFile

class BuiltWithUtil {
    companion object {
        fun getPlatform(applicationInfo: ApplicationInfo?): String {
            // Vérification sécurisée de applicationInfo et sourceDir
            if (applicationInfo == null) {
                return "native_or_others"
            }
            
            val sourceDir = applicationInfo.sourceDir
            if (sourceDir == null) {
                return "native_or_others"
            }
            
            return try {
                val zipFile = ZipFile(sourceDir)
                val entries = zipFile.entries().toList().map { it.name }
                
                when {
                    isFlutterApp(entries) -> "flutter"
                    isReactNativeApp(entries) -> "react_native"
                    isXamarinApp(entries) -> "xamarin"
                    isIonicApp(entries) -> "ionic"
                    else -> "native_or_others"
                }
            } catch (e: Exception) {
                "native_or_others" // Fallback en cas d'erreur
            }
        }
        
        private fun isFlutterApp(entries: List<String>): Boolean {
            return contains(entries, "/flutter_assets/")
        }
        
        private fun isReactNativeApp(entries: List<String>): Boolean {
            return contains(entries, "react_native_routes.json")
                    || contains(entries, "libs_reactnativecore_components")
                    || contains(entries, "node_modules_reactnative")
        }
        
        private fun isXamarinApp(entries: List<String>): Boolean {
            return contains(entries, "libaot-Xamarin")
        }
        
        private fun isIonicApp(entries: List<String>): Boolean {
            return contains(entries, "node_modules_ionic")
        }
        
        private fun contains(entries: List<String>, value: String): Boolean {
            return entries.firstOrNull { entry -> entry.contains(value) } != null
        }
        
        fun getAppNameFromPackage(context: Context, packageInfo: PackageInfo): String {
            // Ligne 57 corrigée - vérification sécurisée
            val appInfo = packageInfo.applicationInfo
            return if (appInfo != null) {
                appInfo.loadLabel(context.packageManager)?.toString() ?: "Unknown"
            } else {
                "Unknown"
            }
        }
    }
}