package com.rifsxd.ksunext.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val BG_COLOR = 0xFF2B2B2B.toInt()
private const val HEADER_COLOR = 0xFF1E1E1E.toInt()
private const val PATH_BG = 0xFF232323.toInt()
private const val TEXT_COLOR = 0xFFEDEDED.toInt()
private const val SUB_COLOR = 0xFF9A9A9A.toInt()
private const val FILE_ICON = 0xFF8A8A8A.toInt()

data class FsEntry(val name: String, val isDir: Boolean)

@Composable
fun FileManagerScreen() {
    var leftPath by remember { mutableStateOf("/storage/emulated/0/") }
    var rightPath by remember { mutableStateOf("/data/") }

    Row(modifier = Modifier.fillMaxSize().background(Color(BG_COLOR))) {
        BrowserPane(
            title = "内部存储",
            path = leftPath,
            onNavigate = { leftPath = it },
            modifier = Modifier.weight(1f)
        )
        Column(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF4A4A4A))) {}
        BrowserPane(
            title = "/data",
            path = rightPath,
            onNavigate = { rightPath = it },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BrowserPane(
    title: String,
    path: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var entries by remember(path) { mutableStateOf<List<FsEntry>>(emptyList()) }

    LaunchedEffect(path) {
        entries = readDir(path)
    }

    Column(modifier = modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(HEADER_COLOR))
                .clickable { onNavigate(parentOf(path)) }
                .padding(horizontal = 10.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = null,
                tint = Color(TEXT_COLOR),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "返回上一级", color = Color(TEXT_COLOR), fontSize = 13.sp)
        }

        Text(
            text = path,
            color = Color(SUB_COLOR),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(PATH_BG))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize().background(Color(BG_COLOR))) {
            items(entries) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (entry.isDir) onNavigate(joinPath(path, entry.name))
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (entry.isDir) Icons.Filled.Folder else Icons.Filled.InsertDriveFile,
                        contentDescription = null,
                        tint = if (entry.isDir) Color.Black else Color(FILE_ICON),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.name,
                        color = Color(TEXT_COLOR),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private suspend fun readDir(path: String): List<FsEntry> = withContext(Dispatchers.IO) {
    val direct = runCatching { File(path).listFiles() }.getOrNull()
    val list: List<FsEntry> = if (direct != null) {
        direct.map { FsEntry(it.name, it.isDirectory) }
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
