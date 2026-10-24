package com.rifsxd.ksunext.ui.screen

import android.app.Activity
import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import com.rifsxd.ksunext.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
private const val FOLDER_ICON = 0xFFD6D6D6.toInt()
private const val FILE_ICON = 0xFF9A9A9A.toInt()

private val apkIconCache = ConcurrentHashMap<String, ImageBitmap?>()
private val apkIconMutex = Mutex()

data class FsEntry(val name: String, val isDir: Boolean)

@Composable
fun FileManagerScreen() {
    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    var leftPath by remember { mutableStateOf("/storage/emulated/0") }
    var rightPath by remember { mutableStateOf("/data") }

    Box(modifier = Modifier.fillMaxSize().background(Color(BG_COLOR))) {
    Row(modifier = Modifier.fillMaxSize()) {
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

    DynamicIsland()
    }
}

@Composable
private fun DynamicIsland() {
    var expanded by remember { mutableStateOf(false) }
    val spec = spring<androidx.compose.ui.unit.Dp>(dampingRatio = 0.82f, stiffness = 380f)
    val specF = spring<Float>(dampingRatio = 0.85f, stiffness = 380f)

    val tr = updateTransition(expanded, label = "island")
    val mainW by tr.animateDp(transitionSpec = { spec }) { if (it) 244.dp else 152.dp }
    val mainH by tr.animateDp(transitionSpec = { spec }) { if (it) 92.dp else 44.dp }
    val radius by tr.animateDp(transitionSpec = { spec }) { if (it) 30.dp else 22.dp }
    val gap by tr.animateDp(transitionSpec = { spec }) { if (it) 0.dp else 5.dp }
    val subW by tr.animateDp(transitionSpec = { spec }) { if (it) 0.dp else 52.dp }
    val subH by tr.animateDp(transitionSpec = { spec }) { if (it) 0.dp else 40.dp }
    val borderAlpha by tr.animateFloat(transitionSpec = { specF }) { if (it) 0.10f else 0.22f }
    val contentAlpha by tr.animateFloat(transitionSpec = { tween(140) }) { if (it) 1f else 0f }

    var blurTarget by remember { mutableStateOf(0.dp) }
    LaunchedEffect(expanded) {
        blurTarget = 2.5.dp
        delay(110)
        blurTarget = 0.dp
    }
    val blurPx by animateDpAsState(blurTarget, tween(150), label = "blur")

    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(gap)
        ) {
            Row(
                modifier = Modifier
                    .width(mainW)
                    .height(mainH)
                    .clip(RoundedCornerShape(radius))
                    .background(Color.Black)
                    .border(1.dp, Color(0xFFFFFFFF).copy(alpha = borderAlpha), RoundedCornerShape(radius))
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(38.dp)
                        .padding(start = 3.dp)
                        .blur(blurPx * 0.2f)
                )
                if (contentAlpha > 0.01f) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.alpha(contentAlpha).blur(blurPx)) {
                        Text(text = "小鱼儿", color = Color.White, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "文件管理器", color = Color(0xFF9A9A9A), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            if (subW > 0.5f) {
                Box(
                    modifier = Modifier
                        .width(subW)
                        .height(subH)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.Black)
                        .border(1.dp, Color(0xFFFFFFFF).copy(alpha = borderAlpha), RoundedCornerShape(percent = 50))
                        .clickable { expanded = true }
                )
            }
        }
    }
}

private fun currentClock(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

@Composable
private fun BrowserPane(
    path: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var entries by remember { mutableStateOf<List<FsEntry>>(emptyList()) }

    LaunchedEffect(path) {
        entries = listDir(path)
    }

    val parent = parentOf(path)

    LazyColumn(modifier = modifier.fillMaxSize().background(Color(BG_COLOR))) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = parent != path) { onNavigate(parent) }
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(TILE_COLOR)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Folder,
                        contentDescription = null,
                        tint = Color(FOLDER_ICON),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "..", color = Color(TEXT_COLOR), fontSize = 15.sp)
            }
        }
        items(entries) { entry ->
            FsRow(
                name = entry.name,
                isDir = entry.isDir,
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
    fullPath: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(isDir = isDir, name = name, fullPath = fullPath)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            color = Color(TEXT_COLOR),
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RowIcon(isDir: Boolean, name: String, fullPath: String) {
    val ctx = LocalContext.current

    val apkIcon by produceState<ImageBitmap?>(apkIconCache[fullPath], fullPath) {
        if (value != null) return@produceState
        if (isDir || !isApk(name)) return@produceState
        value = withContext(Dispatchers.IO) {
            apkIconMutex.withLock {
                apkIconCache.getOrPut(fullPath) { loadApkIcon(ctx, fullPath) }
            }
        }
    }

    when {
        isDir -> Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(TILE_COLOR)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                tint = Color(FOLDER_ICON),
                modifier = Modifier.size(20.dp)
            )
        }

        apkIcon != null -> Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Image(
                bitmap = apkIcon!!,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
        }

        else -> Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.InsertDriveFile,
                contentDescription = null,
                tint = Color(FILE_ICON),
                modifier = Modifier.size(20.dp)
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
                val drawable = app.loadIcon(pm)
                val bmp = if (drawable is android.graphics.drawable.BitmapDrawable && drawable.bitmap != null) {
                    drawable.bitmap
                } else {
                    val w = drawable.intrinsicWidth.coerceAtLeast(1)
                    val h = drawable.intrinsicHeight.coerceAtLeast(1)
                    val b = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(b)
                    drawable.setBounds(0, 0, w, h)
                    drawable.draw(canvas)
                    b
                }
                bmp.asImageBitmap()
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

    if (fromShell.isNotEmpty()) {
        fromShell.sortedWith(compareByDescending<FsEntry> { it.isDir }.thenBy { it.name.lowercase() })
    } else {
        runCatching { File(norm).listFiles() }.getOrNull()
            ?.map { FsEntry(it.name, it.isDirectory) }
            ?.sortedWith(compareByDescending<FsEntry> { it.isDir }.thenBy { it.name.lowercase() })
            ?: emptyList()
    }
}

private fun parentOf(path: String): String {
    val p = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    val i = p.lastIndexOf('/')
    return if (i <= 0) "/" else p.substring(0, i)
}

private fun joinPath(base: String, name: String): String =
    if (base.endsWith("/")) "$base$name" else "$base/$name"
