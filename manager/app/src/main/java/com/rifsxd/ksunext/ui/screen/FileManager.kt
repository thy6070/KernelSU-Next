package com.rifsxd.ksunext.ui.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

private const val BG_COLOR = 0xFF2B2B2B.toInt()
private const val TILE_COLOR = 0xFF3A3A3A.toInt()
private const val TEXT_COLOR = 0xFFEDEDED.toInt()
private const val SUB_COLOR = 0xFF8A8A8A.toInt()
private const val FOLDER_ICON = 0xFFD6D6D6.toInt()
private const val FILE_ICON = 0xFF9A9A9A.toInt()

private val apkIconCache = ConcurrentHashMap<String, ImageBitmap?>()
private val apkIconMutex = Mutex()

data class FsEntry(val name: String, val isDir: Boolean, val modified: Long = 0L)

@Composable
fun FileManagerScreen() {
    var leftPath by remember { mutableStateOf("/storage/emulated/0") }
    var rightPath by remember { mutableStateOf("/data") }

    Row(modifier = Modifier.fillMaxSize().background(Color(BG_COLOR))) {
        key(leftPath) {
            BrowserPane(
                path = leftPath,
                onNavigate = { leftPath = it },
                modifier = Modifier.weight(1f)
            )
        }
        Column(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF4A4A4A))) {}
        key(rightPath) {
            BrowserPane(
                path = rightPath,
                onNavigate = { rightPath = it },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BrowserPane(
    path: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var entries by remember(path) { mutableStateOf<List<FsEntry>>(emptyList()) }

    LaunchedEffect(path) {
        entries = listDir(path)
    }

    val parent = parentOf(path)

    LazyColumn(modifier = modifier.fillMaxSize().background(Color(BG_COLOR))) {
        item {
            FsRow(
                name = "..",
                isDir = true,
                modified = 0L,
                fullPath = "",
                isParent = true,
                onClick = { if (parent != path) onNavigate(parent) }
            )
        }
        items(entries) { entry ->
            FsRow(
                name = entry.name,
                isDir = entry.isDir,
                modified = entry.modified,
                fullPath = joinPath(path, entry.name),
                isParent = false,
                onClick = { if (entry.isDir) onNavigate(joinPath(path, entry.name)) }
            )
        }
    }
}

@Composable
private fun FsRow(
    name: String,
    isDir: Boolean,
    modified: Long,
    fullPath: String,
    isParent: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(isDir = isDir, name = name, fullPath = fullPath, isParent = isParent)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color(TEXT_COLOR),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (modified > 0L) {
                Text(
                    text = formatTime(modified),
                    color = Color(SUB_COLOR),
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RowIcon(isDir: Boolean, name: String, fullPath: String, isParent: Boolean) {
    val ctx = LocalContext.current

    val apkIcon by produceState<ImageBitmap?>(apkIconCache[fullPath], fullPath) {
        if (value != null) return@produceState
        if (isDir || isParent || !isApk(name)) return@produceState
        value = withContext(Dispatchers.IO) {
            apkIconMutex.withLock {
                apkIconCache.getOrPut(fullPath) { loadApkIcon(ctx, fullPath) }
            }
        }
    }

    when {
        isDir -> Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(TILE_COLOR)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                tint = Color(FOLDER_ICON),
                modifier = Modifier.size(17.dp)
            )
        }

        apkIcon != null -> Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
            Image(
                bitmap = apkIcon!!,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }

        else -> Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.InsertDriveFile,
                contentDescription = null,
                tint = Color(FILE_ICON),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

private fun loadApkIcon(ctx: Context, path: String): ImageBitmap? {
    return try {
        val pm = ctx.packageManager
        var usedPath = path
        var info = pm.getPackageArchiveInfo(path, 0)

        if (info == null) {
            val probe = ctx.cacheDir.absolutePath + "/icon_probe_" + Math.abs(path.hashCode()) + ".apk"
            copyOutForIcon(path, probe)
            usedPath = probe
            info = pm.getPackageArchiveInfo(probe, 0)
        }

        val app = info?.applicationInfo
        if (app == null) null else {
            app.sourceDir = usedPath
            app.publicSourceDir = usedPath
            try {
                app.loadIcon(pm)?.toBitmap()?.asImageBitmap()
            } catch (t: Throwable) {
                null
            }
        }
    } catch (t: Throwable) {
        null
    }
}

private fun copyOutForIcon(src: String, dest: String) {
    try {
        val p = Runtime.getRuntime().exec(
            arrayOf("su", "-c", "cp " + shq(src) + " " + shq(dest) + " && chmod 644 " + shq(dest))
        )
        p.waitFor()
    } catch (t: Throwable) {
    }
}

private fun shq(s: String): String = "'" + s.replace("'", "'\\''") + "'"

internal fun isApk(n: String): Boolean {
    val lower = n.lowercase()
    if (lower.endsWith(".apk")) return true
    val tail = lower.substringAfterLast(".apk.", "")
    return tail.isNotEmpty() && tail.all { it.isDigit() }
}

private fun formatTime(millis: Long): String =
    SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault()).format(Date(millis))

private suspend fun listDir(path: String): List<FsEntry> = withContext(Dispatchers.IO) {
    val norm = if (path.endsWith("/")) path else "$path/"

    val shellOut = runCatching {
        Shell.cmd("ls -1p \"$norm\" 2>/dev/null").exec().out
    }.getOrDefault(emptyList())

    val fromShell = shellOut
        .filter { it.isNotBlank() }
        .map { line ->
            val trimmed = line.trim()
            FsEntry(trimmed.removeSuffix("/"), trimmed.endsWith("/"))
        }

    val timeMap: Map<String, Long> = runCatching {
        File(norm).listFiles()?.associate { it.name to it.lastModified() } ?: emptyMap()
    }.getOrDefault(emptyMap())

    val merged = if (fromShell.isNotEmpty()) {
        fromShell.map { it.copy(modified = timeMap[it.name] ?: 0L) }
    } else {
        runCatching { File(norm).listFiles() }.getOrNull()
            ?.map { FsEntry(it.name, it.isDirectory, it.lastModified()) }
            ?: emptyList()
    }

    merged.sortedWith(compareByDescending<FsEntry> { it.isDir }.thenBy { it.name.lowercase() })
}

private fun parentOf(path: String): String {
    val p = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    val i = p.lastIndexOf('/')
    return if (i <= 0) "/" else p.substring(0, i)
}

private fun joinPath(base: String, name: String): String =
    if (base.endsWith("/")) "$base$name" else "$base/$name"
