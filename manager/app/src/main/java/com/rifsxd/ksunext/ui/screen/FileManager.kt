package com.rifsxd.ksunext.ui.screen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
private const val FILE_ICON = 0xFF8A8A8A.toInt()

data class FsEntry(val name: String, val isDir: Boolean, val modified: Long = 0L)

@Composable
fun FileManagerScreen() {
    var leftPath by remember { mutableStateOf("/storage/emulated/0/") }
    var rightPath by remember { mutableStateOf("/data/") }

    Row(modifier = Modifier.fillMaxSize().background(Color(BG_COLOR))) {
        BrowserPane(
            path = leftPath,
            onNavigate = { leftPath = it },
            modifier = Modifier.weight(1f)
        )
        Column(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF4A4A4A))) {}
        BrowserPane(
            path = rightPath,
            onNavigate = { rightPath = it },
            modifier = Modifier.weight(1f)
        )
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
        entries = readDir(path)
    }

    LazyColumn(modifier = modifier.fillMaxSize().background(Color(BG_COLOR))) {
        item {
            FsRow(
                name = "..",
                isDir = true,
                modified = 0L,
                onClick = { onNavigate(parentOf(path)) }
            )
        }
        items(entries) { entry ->
            FsRow(
                name = entry.name,
                isDir = entry.isDir,
                modified = entry.modified,
                onClick = { if (entry.isDir) onNavigate(joinPath(path, entry.name)) }
            )
        }
    }
}

@Composable
private fun FsRow(name: String, isDir: Boolean, modified: Long, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(TILE_COLOR)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDir) Icons.Filled.Folder else Icons.Filled.InsertDriveFile,
                contentDescription = null,
                tint = if (isDir) Color(FOLDER_ICON) else Color(FILE_ICON),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color(TEXT_COLOR),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (modified > 0L) {
                Text(
                    text = formatTime(modified),
                    color = Color(SUB_COLOR),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatTime(millis: Long): String =
    SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault()).format(Date(millis))

private suspend fun readDir(path: String): List<FsEntry> = withContext(Dispatchers.IO) {
    val direct = runCatching { File(path).listFiles() }.getOrNull()
    val list: List<FsEntry> = if (direct != null) {
        direct.map { FsEntry(it.name, it.isDirectory, it.lastModified()) }
    } else {
        val out = runCatching {
            Shell.cmd("ls -1p \"$path\" 2>/dev/null").exec().out
        }.getOrDefault(emptyList())
        out.filter { it.isNotBlank() }.map { line ->
            val trimmed = line.trim()
            FsEntry(trimmed.removeSuffix("/"), trimmed.endsWith("/"))
        }
    }
    list.sortedWith(compareByDescending<FsEntry> { it.isDir }.thenBy { it.name.lowercase() })
}

private fun parentOf(path: String): String {
    val trimmed = path.trimEnd('/')
    val idx = trimmed.lastIndexOf('/')
    return if (idx <= 0) "/" else trimmed.substring(0, idx + 1)
}

private fun joinPath(base: String, name: String): String =
    if (base.endsWith("/")) "$base$name/" else "$base/$name/"
