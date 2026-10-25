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
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.zIndex
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Folder
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
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
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var refreshTick by remember { mutableStateOf(0) }
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
                onFileClick = { selectedFile = it },
                refreshTick = refreshTick,
                selectedPath = selectedFile,
                modifier = Modifier.weight(1f)
            )
        }
        Column(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF4A4A4A))) {}
        key(rightPath) {
            BrowserPane(
                path = rightPath,
                onNavigate = { rightPath = it },
                onFileClick = { selectedFile = it },
                refreshTick = refreshTick,
                selectedPath = selectedFile,
                modifier = Modifier.weight(1f)
            )
        }
    }

    val uiCtx = androidx.compose.ui.platform.LocalContext.current
    ProMaxIsland(
        navigator = navigator,
        panelOpen = panel,
        selectedFile = selectedFile,
        onClearSelection = { selectedFile = null },
        onDelete = {
            val p = selectedFile
            if (p != null) uiScope.launch {
                withContext(Dispatchers.IO) { Shell.cmd("rm -rf \"$p\"").exec() }
                selectedFile = null
                refreshTick += 1
            }
        },
        onAction = {
            val p = selectedFile
            if (p != null) {
                val lower = p.lowercase()
                uiScope.launch {
                    val out = withContext(Dispatchers.IO) {
                        when {
                            lower.endsWith(".apk") -> Shell.cmd("pm install -r \"$p\"").exec()
                            lower.endsWith(".sh") || lower.endsWith(".rc") -> Shell.cmd("sh \"$p\"").exec()
                            lower.endsWith(".so") -> Shell.cmd("chmod 755 \"$p\"").exec()
                            else -> Shell.cmd("echo mount-not-implemented").exec()
                        }
                    }
                    val msg = out.out.joinToString("\n").ifBlank { out.code.toString() }
                    Toast.makeText(uiCtx, msg.take(120), Toast.LENGTH_SHORT).show()
                }
            }
        }
    ) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProMaxIsland(
    navigator: DestinationsNavigator,
    panelOpen: Boolean,
    selectedFile: String?,
    onClearSelection: () -> Unit,
    onDelete: () -> Unit,
    onAction: () -> Unit,
    onOpen: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val reopenScope = rememberCoroutineScope()
    fun foldAndReopen() {
        reopenScope.launch {
            expanded = false
            kotlinx.coroutines.delay(260)
            expanded = true
        }
    }

    // Island.kt motion: 1s bump/sway/lift envelope
    val bounce = remember { Animatable(0f) }
    LaunchedEffect(expanded, selectedFile) {
        bounce.snapTo(0f)
        bounce.animateTo(1f, tween(1000))
    }
    val bt = bounce.value
    val bump = 1f + 0.06f * sin(bt * PI.toFloat())
    val sway = 6f * sin(bt * 9.424778f) * (1f - bt)
    val lift = -4f * sin(bt * PI.toFloat())

    // Island.kt ball choreography
    val ball = remember { Animatable(1f) }
    LaunchedEffect(selectedFile != null) {
        if (selectedFile != null) {
            ball.animateTo(0f, spring(dampingRatio = 0.72f, stiffness = 120f))
        } else {
            ball.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 130f))
        }
    }
    val ballShift = ball.value
    val ballScale = 0.55f + 0.45f * ballShift

    val config = LocalConfiguration.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val pillW = (config.screenWidthDp * 0.36f).dp

    val sel = selectedFile
    val selName = sel?.substringAfterLast('/') ?: ""
    val accent = sel?.let { fileAccent(it) } ?: Color(0xFFEDEDED)
    val lowerSel = sel?.lowercase() ?: ""
    val actionLabel = when {
        sel == null -> ""
        lowerSel.endsWith(".apk") -> "安装"
        lowerSel.endsWith(".so") -> "启动"
        lowerSel.endsWith(".zip") || lowerSel.endsWith(".tar") || lowerSel.endsWith(".gz") ||
            lowerSel.endsWith(".7z") || lowerSel.endsWith(".rar") || lowerSel.endsWith(".xz") -> "挂载"
        else -> "执行"
    }
    val actionColor = if (actionLabel == "挂载") Color(0xFFFFCA24) else Color(0xFF0A84FF)

    val zyEase = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    val pillH by animateDpAsState(if (sel != null) 132.dp else 42.dp, tween(360, easing = zyEase), label = "ph")


    Box(
        modifier = Modifier.fillMaxWidth().height(160.dp).offset(y = 10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // ball sits UNDER the pill and slides out of it
        Box(
            modifier = Modifier
                .offset(x = (pillW.value / 2f + 27f).dp, y = 0.dp)
                .size(42.dp)
                .graphicsLayer {
                    val travel = with(density) { (pillW.value / 2f + 3f).dp.toPx() }
                    translationX = (ballShift - 1f) * travel
                    scaleX = ballScale * bump
                    scaleY = ballScale * bump
                    alpha = (ballShift * 3f).coerceIn(0f, 1f)
                }
                .clip(RoundedCornerShape(percent = 50))
                .background(Color.Black)
                .border(0.5.dp, Color(0x47FFFFFF), RoundedCornerShape(percent = 50))
                .clickable { onOpen() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color(0xFF0A84FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "＋", color = Color.White, fontSize = 17.sp)
            }
        }

        // main pill on top
        Column(
            modifier = Modifier
                .zIndex(1f)
                .width(pillW)
                .height(pillH)
                .graphicsLayer {
                    scaleX = bump
                    scaleY = bump
                    translationX = with(density) { sway.dp.toPx() }
                    translationY = with(density) { lift.dp.toPx() }
                }
                .clip(RoundedCornerShape(34.dp))
                .background(Color.Black)
                .border(0.5.dp, Color(0x47FFFFFF), RoundedCornerShape(34.dp))
                .combinedClickable(
                    onClick = { if (sel == null) expanded = !expanded else onClearSelection() },
                    onLongClick = { foldAndReopen() }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(start = 12.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val logoCtx = LocalContext.current
                val logoBmp = remember(logoCtx) {
                    val d = logoCtx.applicationInfo.loadIcon(logoCtx.packageManager)
                    val w = d.intrinsicWidth.coerceAtLeast(1)
                    val h = d.intrinsicHeight.coerceAtLeast(1)
                    val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bmp)
                    d.setBounds(0, 0, w, h)
                    d.draw(canvas)
                    bmp.asImageBitmap()
                }
                Image(
                    bitmap = logoBmp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(percent = 22))
                )
                if (sel != null) {
                    Text(
                        text = selName,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = Icons.Outlined.InsertDriveFile,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp).clickable { onClearSelection() }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Outlined.TaskAlt,
                        contentDescription = "install",
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { navigator.navigate(InstallScreenDestination) }
                    )
                }
            }

            AnimatedVisibility(
                visible = sel != null,
                enter = expandVertically(
                    animationSpec = tween(360, easing = zyEase),
                    expandFrom = Alignment.CenterVertically
                ) + fadeIn(tween(200)),
                exit = shrinkVertically(
                    animationSpec = tween(260, easing = zyEase),
                    shrinkTowards = Alignment.CenterVertically
                ) + fadeOut(tween(160))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionPill(
                            text = "删除",
                            color = Color(0xFFFF453A),
                            modifier = Modifier.weight(1f)
                        ) { onDelete() }
                        ActionPill(
                            text = actionLabel,
                            color = actionColor,
                            modifier = Modifier.weight(1f)
                        ) { onAction() }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun ActionPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // exactly the ZySU IslandActionButton look: solid capsule, 9dp vertical padding, 14sp white
    Text(
        text = text,
        modifier = modifier
            .clickable(onClick = onClick)
            .background(color, RoundedCornerShape(percent = 50))
            .padding(vertical = 9.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
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
    onFileClick: (String) -> Unit,
    refreshTick: Int = 0,
    selectedPath: String? = null,
    modifier: Modifier = Modifier
) {
    var entries by remember { mutableStateOf<List<FsEntry>>(emptyList()) }

    LaunchedEffect(path, refreshTick) {
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
                        imageVector = Icons.Outlined.Folder,
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
            val rowPath = joinPath(path, entry.name)
            FsRow(
                name = entry.name,
                isDir = entry.isDir,
                fullPath = rowPath,
                selected = selectedPath == rowPath,
                onClick = {
                    if (entry.isDir) onNavigate(joinPath(path, entry.name)) else onFileClick(joinPath(path, entry.name))
                }
            )
        }
    }
}

@Composable
private fun FsRow(
    name: String,
    isDir: Boolean,
    fullPath: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    // ZySU row: rounded 14dp, no tile behind the glyph, 12sp monospace, outline on the row
    val accent = if (isDir) null else fileAccent(name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(if (selected) Color(0x3300BCD4) else Color.Transparent)
            .then(
                if (accent != null) {
                    Modifier.border(1.dp, accent.copy(alpha = 0.38f), RoundedCornerShape(14.dp))
                } else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(isDir = isDir, name = name, fullPath = fullPath)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = FontFamily.Monospace,
            color = Color(TEXT_COLOR)
        )
    }
}

private fun fileAccent(name: String): Color? {
    val l = name.lowercase()
    return when {
        l.endsWith(".apk") -> Color(0xFFFFB6C8)
        l.endsWith(".so") -> Color(0xFF4A9EFF)
        l.endsWith(".sh") || l.endsWith(".rc") -> Color(0xFF26C6DA)
        l.endsWith(".zip") || l.endsWith(".tar") || l.endsWith(".gz") ||
            l.endsWith(".7z") || l.endsWith(".rar") || l.endsWith(".xz") -> Color(0xFFFFCA24)
        else -> null
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

    val apkIconNow = apkIcon
    if (apkIconNow != null) {
        Image(
            bitmap = apkIconNow,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    } else {
        Icon(
            imageVector = if (isDir) Icons.Outlined.Folder else Icons.Outlined.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isDir) Color(FOLDER_ICON) else (fileAccent(name) ?: Color(FILE_ICON))
        )
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
