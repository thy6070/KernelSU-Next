package com.rifsxd.ksunext.ui.screen

import android.content.Context
import android.net.Uri
import com.topjohnwu.superuser.Shell

/** Path requested by another app (view/send), consumed by the file manager. */
object FileLocate {
    var pendingPath: String? = null
}

/** Resolve a content:// uri back to a real file path (provider, fd, then document id). */
internal fun contentPath(context: Context, uri: Uri): String? {
    if (uri.scheme == "file") return uri.path
    try {
        context.contentResolver.query(uri, arrayOf("_data"), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex("_data")
                if (index >= 0) {
                    val path = cursor.getString(index)
                    if (!path.isNullOrEmpty()) return path
                }
            }
        }
    } catch (t: Throwable) {
    }
    try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            val real = java.io.File("/proc/self/fd/" + pfd.fd).canonicalPath
            if (!real.isNullOrEmpty() && real.startsWith("/") && !real.startsWith("/proc/")) {
                return real
            }
        }
    } catch (t: Throwable) {
    }
    try {
        val docId = android.provider.DocumentsContract.getDocumentId(uri)
        if (!docId.isNullOrEmpty()) {
            val guess = if (docId.startsWith("raw:")) docId.removePrefix("raw:")
            else "/storage/emulated/0/" + docId.substringAfter(':', docId)
            return guess
        }
    } catch (t: Throwable) {
    }
    return null
}

internal fun contentName(context: Context, uri: Uri): String? {
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    val name = cursor.getString(index)
                    if (!name.isNullOrEmpty()) return name
                }
            }
        }
    } catch (t: Throwable) {
    }
    return uri.lastPathSegment?.substringAfterLast('/')
}

/** Resolve with fallbacks: provider path, then a name search across storage. */
internal fun resolveUri(context: Context, uri: Uri): String? {
    val direct = contentPath(context, uri)
    if (direct != null) return direct
    val name = contentName(context, uri) ?: return null
    val atRoot = java.io.File("/storage/emulated/0/" + name)
    if (atRoot.isFile) return atRoot.absolutePath
    return try {
        val safe = name.replace("'", "")
        Shell.cmd("find /storage/emulated/0 -maxdepth 7 -name '$safe' 2>/dev/null | head -n1")
            .exec().out.firstOrNull { it.isNotBlank() }?.trim()
    } catch (t: Throwable) {
        null
    }
}
