package com.rifsxd.ksunext.ui.screen

import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val BG_COLOR = 0xFF2B2B2B.toInt()
private const val TILE_COLOR = 0xFF3A3A3A.toInt()
private const val TEXT_COLOR = 0xFFEDEDED.toInt()
private const val SUB_COLOR = 0xFF8A8A8A.toInt()
private const val FOLDER_ICON = 0xFFD6D6D6.toInt()
private const val FILE_ICON = 0xFF9A9A9A.toInt()

data class FsEntry(val name: String, val isDir: Boolean, val modified: Long = 0L)

@Composable
fun FileManagerScreen() {
    var leftPath by remember { mutableStateOf("/storage/emulated/0/") }
    var rightPath by remember { mutableStateOf("/data/") }

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

    LazyColumn(modifier = modifier.fillMaxSize().background(Color(BG_COLOR))) {
        item {
            FsRow(
                name = "..",
                isDir = true,
                modified = 0L,
                fullPath = "",
                onClick = {
                    val up = parentOf(path)
                    if (up != path) onNavigate(up)
                }
            )
        }
        items(entries) { entry ->
            FsRow(
                name = entry.name,
                isDir = entry.isDir,
                modified = entry.modified,
                fullPath = joinPath(path, entry.name),
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
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(isDir = isDir, name = name, fullPath = fullPath, isParent = fullPath.isEmpty())
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
    val context = LocalContext.current
    var apkIcon by remember(fullPath) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(fullPath) {
        if (!isDir && !isParent && name.endsWith(".apk", ignoreCase = true)) {
            apkIcon = withContext(Dispatchers.IO) { loadApkIcon(context, fullPath) }
        }
    }

    when {
        isDir -> Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(TILE_COLOR)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                tint = Color(FOLDER_ICON),
                modifier = Modifier.size(18.dp)
            )
        }

        apkIcon != null -> Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
            Image(
                bitmap = apkIcon!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }

        else -> Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.InsertDriveFile,
                contentDescription = null,
                tint = Color(FILE_ICON),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun loadApkIcon(context: Context, path: String): Bitmap? = runCatching {
    val pm = context.packageManager
    val info = pm.getPackageArchiveInfo(path, 0) ?: return null
    val appInfo = info.applicationInfo ?: return null
    appInfo.sourceDir = path
    appInfo.publicSourceDir = path
    (pm.getApplicationIcon(appInfo) as? android.graphics.drawable.BitmapDrawable)?.bitmap
        ?: runCatching { (pm.getApplicationIcon(appInfo) as android.graphics.drawable.BitmapDrawable).bitmap }.getOrNull()
}.getOrNull()

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
    val trimmed = path.trimEnd('/')
    if (trimmed.isEmpty()) return "/"
    val idx = trimmed.lastIndexOf('/')
    return if (idx <= 0) "/" else trimmed.substring(0, idx + 1)
}

private fun joinPath(base: String, name: String): String =
    if (base.endsWith("/")) "$base$name/" else "$base/$name/"
