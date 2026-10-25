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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.ColorFilter
import com.ramcosta.composedestinations.generated.destinations.InstallScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.TransformOrigin
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlin.math.PI
import kotlin.math.sin
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
fun FileManagerScreen(navigator: DestinationsNavigator) {
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

    var panel by remember { mutableStateOf(false) }
    val panelP = remember { Animatable(0f) }
    val uiScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color(BG_COLOR))) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(BG_COLOR))
                .blur(if (panel) 30.dp else 0.dp)
        ) {
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

    ProMaxIsland(navigator, panel) {
        uiScope.launch {
            panelP.snapTo(0f)
            panel = true
            panelP.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
        }
    }
        }

        if (panel) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val s = 0.25f + 0.75f * panelP.value
                        scaleX = s
                        scaleY = s
                        alpha = panelP.value
                        transformOrigin = TransformOrigin(0.62f, 0.055f)
                    }
            ) {
                ModePanel(onDismiss = {
                    uiScope.launch {
                        panelP.animateTo(0f, tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing))
                        panel = false
                    }
                })
            }
        }
    }
}

@Composable
private fun ProMaxIsland(navigator: DestinationsNavigator, panelOpen: Boolean, onOpen: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val dotPulse = remember { Animatable(0f) }
    LaunchedEffect(panelOpen) {
        if (panelOpen) {
            dotPulse.animateTo(1f, tween(340, easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)))
        } else {
            dotPulse.animateTo(0f, tween(280, easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)))
        }
    }

    val curve = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    val p = remember { Animatable(0f) }
    LaunchedEffect(expanded) {
        if (expanded) {
            p.animateTo(1f, tween(1650, easing = curve))
        } else {
            p.animateTo(0f, tween(1450, easing = curve))
        }
    }

    fun seg(v: Float, a: Float, b: Float): Float = ((v - a) / (b - a)).coerceIn(0f, 1f)
    fun smooth(t: Float): Float = (1f - kotlin.math.cos(t * PI.toFloat())) / 2f

    val v = p.value
    val iconAlpha = (1f - smooth(seg(v, 0f, 0.14f)) + smooth(seg(v, 0.74f, 1f))).coerceIn(0f, 1f)
    val iconScale = 0.92f + 0.08f * smooth(seg(v, 0.74f, 1f))
    val iconShift = -2.5f * (smooth(seg(v, 0f, 0.3f)) - smooth(seg(v, 0.7f, 1f)))
    val gather = smooth(seg(v, 0f, 0.26f)) - smooth(seg(v, 0.74f, 1f))
    val overshoot = 4f * sin(seg(v, 0f, 0.42f) * PI.toFloat()) * (1f - seg(v, 0.30f, 0.5f))
    val pillW = 188f + (138f - 188f) * gather + overshoot
    val pillX = 25f * gather
    val radius = 22f
    val scale = if (v < 0.32f) {
        1f + 0.28f * smooth(seg(v, 0f, 0.30f))
    } else {
        1.28f - 0.28f * smooth(seg(v, 0.32f, 0.64f))
    }
    val keyline = 0.30f + 0.14f * smooth(seg(v, 0f, 0.5f)) * (1f - smooth(seg(v, 0.6f, 1f)))
    val ball = smooth(seg(v, 0.76f, 1f))
    val ballScale = 0.55f + 0.45f * ball
    val density = androidx.compose.ui.platform.LocalDensity.current

    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(modifier = Modifier.width(250.dp).height(52.dp)) {

            Row(
                modifier = Modifier
                    .offset(x = (pillW + 4f).dp, y = 4.dp)
                    .width(52.dp)
                    .height(44.dp)
                    .graphicsLayer {
                        val travel = with(density) { 64.dp.toPx() }
                        translationX = (ball - 1f) * travel
                        val pulse = 1f - 0.18f * dotPulse.value
                        scaleX = ballScale * pulse
                        scaleY = ballScale * pulse
                        alpha = (ball * 3f).coerceIn(0f, 1f) * (1f - 0.45f * dotPulse.value)
                    }
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.Black)
                    .border(0.5.dp, Color(0xFFE6E6E6).copy(alpha = keyline), RoundedCornerShape(22.dp))
                    .clickable { onOpen() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_modules_foreground),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(38.dp)
                )
            }

            Row(
                modifier = Modifier
                    .offset(x = pillX.dp)
                    .width(pillW.dp)
                    .height(44.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(radius.dp))
                    .background(Color.Black)
                    .border(0.5.dp, Color(0xFFE6E6E6).copy(alpha = keyline), RoundedCornerShape(radius.dp))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconAlpha > 0.01f) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .size(38.dp)
                            .graphicsLayer {
                                alpha = iconAlpha
                                scaleX = iconScale
                                scaleY = iconScale
                                translationX = with(density) { iconShift.dp.toPx() }
                            }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { navigator.navigate(InstallScreenDestination) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "install",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }


}

@Composable
private fun ModePanel(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModeButton(Icons.Outlined.DarkMode, "勿扰模式")
            ModeButton(Icons.Outlined.AcUnit, "小鱼儿")
            ModeButton(Icons.Outlined.Person, "个人")
            ModeButton(Icons.Outlined.Smartphone, "工作")
            ModeButton(Icons.Outlined.Hotel, "睡眠")
        }
    }
}

@Composable
private fun ModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    var on by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xE60E0E10))
            .clickable { on = !on }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = title, color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "开",
                color = if (on) Color.White else Color(0xFF5A5A5E),
                fontSize = 15.sp,
                fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(text = " | ", color = Color(0xFF6A6A6E), fontSize = 15.sp)
            Text(
                text = "关",
                color = if (!on) Color.White else Color(0xFF5A5A5E),
                fontSize = 15.sp,
                fontWeight = if (!on) FontWeight.SemiBold else FontWeight.Normal
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

        else -> {
            val lower = name.lowercase()
            val accent = when {
                lower.endsWith(".sh") || lower.endsWith(".rc") || lower.endsWith(".so") -> Color(0xFF26C6DA)
                lower.endsWith(".zip") || lower.endsWith(".tar") || lower.endsWith(".gz") ||
                    lower.endsWith(".7z") || lower.endsWith(".rar") || lower.endsWith(".xz") -> Color(0xFFFFCA28)
                else -> null
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .then(
                        if (accent != null) {
                            Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .border(1.dp, accent.copy(alpha = 0.85f), RoundedCornerShape(7.dp))
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.InsertDriveFile,
                    contentDescription = null,
                    tint = if (accent != null) accent else Color(FILE_ICON),
                    modifier = Modifier.size(20.dp)
                )
            }
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
