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
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SuperUserScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ModuleScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingScreenDestination
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Terminal
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

data class ModItem(val id: String, val name: String, val version: String, val desc: String, val disabled: Boolean)

data class SuApp(val pkg: String, val label: String, val uid: Int, val allowed: Boolean, val icon: android.graphics.Bitmap? = null)

data class FsEntry(val name: String, val isDir: Boolean)

@Composable
fun FileManagerScreen(navigator: DestinationsNavigator) {
    val appCtx = LocalContext.current
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
    var showTerminal by remember { mutableStateOf(false) }
    var termInput by remember { mutableStateOf("") }
    var termText by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var refreshTick by remember { mutableStateOf(0) }
    var leftHighlight by remember { mutableStateOf<String?>(null) }
    var longPressPath by remember { mutableStateOf<String?>(null) }
    var longPressIsDir by remember { mutableStateOf(false) }
    var longPressTick by remember { mutableStateOf(0) }
    var longPressFromRight by remember { mutableStateOf(false) }
    var editPath by remember { mutableStateOf<String?>(null) }
    var editText by remember { mutableStateOf("") }
    LaunchedEffect(FileLocate.tick) {
        val target = FileLocate.path ?: return@LaunchedEffect
        val dir = target.substringBeforeLast('/', "/")
        if (leftPath == dir) refreshTick += 1 else leftPath = dir
        leftHighlight = target.substringAfterLast('/')
    }

    var runMessage by remember { mutableStateOf<String?>(null) }
    var runOk by remember { mutableStateOf(true) }
    var running by remember { mutableStateOf(false) }
    var activeProc by remember { mutableStateOf<Process?>(null) }
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
                onLongPress = { lp ->
                    longPressIsDir = runCatching { java.io.File(lp).isDirectory }.getOrDefault(false) || !lp.substringAfterLast("/").contains(".")
                    longPressFromRight = false
                    longPressPath = lp
                    longPressTick += 1
                },
                onFileClick = { picked ->
                    if (longPressPath != null) longPressPath = null
                    if (selectedFile != null && selectedFile != picked) {
                        selectedFile = null
                        uiScope.launch {
                            kotlinx.coroutines.delay(280)
                            selectedFile = picked
                        }
                    } else {
                        selectedFile = picked
                    }
                },
                refreshTick = refreshTick,
                selectedPath = selectedFile,
                highlightName = leftHighlight,
                modifier = Modifier.weight(1f)
            )
        }
        Column(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF4A4A4A))) {}
        key(rightPath) {
            BrowserPane(
                path = rightPath,
                onNavigate = { rightPath = it },
                onLongPress = { lp ->
                    longPressIsDir = runCatching { java.io.File(lp).isDirectory }.getOrDefault(false) || !lp.substringAfterLast("/").contains(".")
                    longPressFromRight = true
                    longPressPath = lp
                    longPressTick += 1
                },
                onFileClick = { picked ->
                    if (longPressPath != null) longPressPath = null
                    if (selectedFile != null && selectedFile != picked) {
                        selectedFile = null
                        uiScope.launch {
                            kotlinx.coroutines.delay(280)
                            selectedFile = picked
                        }
                    } else {
                        selectedFile = picked
                    }
                },
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
        onGoLeft = { pth -> leftPath = pth },
        onGoRight = { pth -> rightPath = pth },
        onExtractApp = { pkg, label ->
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val safe = label.replace("/", "_").replace("\"", "_")
                        Shell.cmd(
                            "P=\$(pm path $pkg | head -1 | sed 's/package://'); " +
                                "mkdir -p \"/storage/emulated/0/小鱼儿\"; " +
                                "cp \"\$P\" \"/storage/emulated/0/小鱼儿/$safe.apk\" && " +
                                "chmod 644 \"/storage/emulated/0/小鱼儿/$safe.apk\""
                        ).exec()
                    } catch (t: Throwable) {
                    }
                }
                refreshTick += 1
            }
        },
        onOpenStorage = { leftPath = "/storage/emulated/0" },
        onOpenData = { rightPath = "/data" },
        onExtract = {
            val from = selectedFile
            if (from != null) {
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            Shell.cmd("cp -rf \"$from\" \"$leftPath/\" 2>/dev/null").exec()
                        } catch (t: Throwable) {
                        }
                    }
                    refreshTick += 1
                }
            }
        },
        onDelete = {
            val p = selectedFile
            if (p != null) uiScope.launch {
                withContext(Dispatchers.IO) { Shell.cmd("rm -rf \"$p\"").exec() }
                selectedFile = null
                refreshTick += 1
            }
        },
        runMessage = runMessage,
        runOk = runOk,
        running = running,
        terminalMode = showTerminal,
        termText = termText,
        termInput = termInput,
        onTermInput = { termInput = it },
        onTermRun = {
            val cmd = termInput
            if (cmd.isNotBlank()) {
                termInput = ""
                val alive = activeProc
                if (alive != null && alive.isAlive) {
                    try {
                        alive.outputStream.write((cmd + "\n").toByteArray())
                        alive.outputStream.flush()
                        termText = termText + cmd + "\n"
                    } catch (t: Throwable) {
                        termText = termText + "写入失败: " + (t.message ?: "") + "\n"
                    }
                } else {
                    termText = termText + "\n$ " + cmd + "\n"
                    uiScope.launch {
                        val out = withContext(Dispatchers.IO) { Shell.cmd(cmd).exec() }
                        termText = termText + out.out.joinToString("\n") + out.err.joinToString("\n") + "\n"
                    }
                }
            }
        },
        onTermClose = { showTerminal = false },
        onArchiveEnter = {
            val p = longPressPath ?: selectedFile
            if (p != null) {
                uiScope.launch {
                    running = true
                    showTerminal = true
                    termText = ""
                    runMessage = null
                    withContext(Dispatchers.IO) {
                        try {
                            val q = "'" + p.replace("'", "'\''") + "'"
                            Shell.cmd(
                                "rm -rf /data/local/tmp/yx_arch && mkdir -p /data/local/tmp/yx_arch && " +
                                    "( unzip -o -q " + q + " -d /data/local/tmp/yx_arch 2>&1 || " +
                                    "tar -xf " + q + " -C /data/local/tmp/yx_arch 2>&1 )"
                            ).exec()
                        } catch (t: Throwable) {
                        }
                    }
                    running = false
                    showTerminal = false
                    leftPath = "/data/local/tmp/yx_arch"
                    refreshTick += 1
                }
            }
        },
        longPressPath = longPressPath,
        longPressIsDir = longPressIsDir,
        longPressTick = longPressTick,
        onClearLongPress = { longPressPath = null },
        oppositePath = if (longPressFromRight) leftPath else rightPath,
        onFileOp = { op, arg ->
            val srcPath = longPressPath ?: selectedFile
            if (srcPath != null) {
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            fun q(s: String) = "'" + s.replace("'", "'\''") + "'"
                            val dir = srcPath.substringBeforeLast('/', "/")
                            val name = srcPath.substringAfterLast('/')
                            val cmd = when (op) {
                                "move" -> "mv -f " + q(srcPath) + " " + q(arg)
                                "copy" -> "cp -rf " + q(srcPath) + " " + q(arg)
                                "zip" -> "cd " + q(dir) + " && tar -czf " + q(name + ".tar.gz") + " " + q(name)
                                "unzip" -> "unzip -o " + q(srcPath) + " -d " + q(arg)
                                "rename" -> "mv -f " + q(srcPath) + " " + q(dir + "/" + arg)
                                "delete" -> "rm -rf " + q(srcPath)
                                "install" -> "pm install -r " + q(srcPath)
                                "edit" -> "chmod 644 " + q(srcPath)
                                else -> ""
                            }
                            var ok = true
                            if (cmd.isNotEmpty()) {
                                val res = Shell.cmd(cmd).exec()
                                ok = res.isSuccess
                            }
                            if (op == "share") {
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    appCtx,
                                    appCtx.packageName + ".fileprovider",
                                    java.io.File(srcPath)
                                )
                                withContext(Dispatchers.Main) {
                                    val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "*/*"
                                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    appCtx.startActivity(
                                        android.content.Intent.createChooser(send, "分享")
                                    )
                                }
                            }
                            if (op == "edit") {
                                val content = try {
                                    java.io.File(srcPath).readText()
                                } catch (t1: Throwable) {
                                    try {
                                        Shell.cmd("cat " + q(srcPath)).exec().out.joinToString("\n")
                                    } catch (t: Throwable) {
                                        ""
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    editPath = srcPath
                                    editText = content
                                }
                            }
                        } catch (t: Throwable) {
                        }
                    }
                    try {
                        Shell.getShell().close()
                    } catch (t: Throwable) {
                    }
                    refreshTick += 1
                    longPressPath = null
                    runOk = true
                    runMessage = if (op == "share") null else ("已完成")
                }
            }
        },
        onArchiveMount = {
            val p = selectedFile
            if (p != null) {
                uiScope.launch {
                    running = true
                    termText = ""
                    runMessage = null
                    showTerminal = true
                    val ksud = appCtx.applicationInfo.nativeLibraryDir + "/libksud.so"
                    val q = "'" + p.replace("'", "'\''") + "'"
                    val cmd = "export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                        "export KSU_VER=1.0.0 ; export KSU_VER_CODE=12100 ; export KSU_KERNEL_VER_CODE=12100 ; " +
                        "'" + ksud + "' module install " + q + " 2>&1"
                    try {
                        val bb = listOf(
                            "/data/adb/ksu/bin/busybox",
                            "/data/adb/magisk/busybox",
                            "/system/bin/busybox",
                            "/system/xbin/busybox",
                            "/data/adb/modules/busybox-ndk/system/bin/busybox"
                        ).firstOrNull { java.io.File(it).isFile }
                        val qq = { s: String -> "'" + s.replace("'", "'\''") + "'" }
                        val wrapped = if (bb != null) {
                            "exec " + qq(bb) + " script -q /dev/null -c " + qq(cmd + " 2>&1")
                        } else {
                            cmd + " 2>&1"
                        }
                        withContext(Dispatchers.IO) {
                            val pb = ProcessBuilder("/system/bin/sh", "-c", "su -c " + qq(wrapped))
                            pb.redirectErrorStream(true)
                            val proc = pb.start()
                            activeProc = proc
                            proc.inputStream.bufferedReader().useLines { lines ->
                                lines.forEach { line ->
                                    withContext(Dispatchers.Main) {
                                        termText = (termText + line + "\n").takeLast(20000)
                                    }
                                }
                            }
                            proc.waitFor()
                        }
                    } catch (t: Throwable) {
                        termText += "挂载失败: " + (t.message ?: "") + "\n"
                    }
                    activeProc = null
                    running = false
                    try {
                        Shell.getShell().close()
                    } catch (t: Throwable) {
                    }
                }
            }
        },
        onAction = { soKey ->
            val p = longPressPath ?: selectedFile
            if (p != null) {
                val lower = p.lowercase()
                uiScope.launch {
                    running = true
                    termText = ""
                    runMessage = null
                    showTerminal = true
                    try {
                    withContext(Dispatchers.IO) {
                        try {
                            val dir = p.substringBeforeLast('/', "/")
                            val file = p.substringAfterLast('/')
                            val cmd = when {
                                lower.endsWith(".apk") -> "pm install -r \"$p\""
                                lower.endsWith(".so") -> {
                                    val key = soKey.trim()
                                    "cd \"$dir\" ; export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                                        "chmod 755 \"$p\" 2>/dev/null ; " +
                                        (if (key.isEmpty()) "\"$p\"" else "\"$p\" -k \"$key\"") + " 2>&1"
                                }
                                else -> "chmod 777 \"$file\" 2>/dev/null; exec sh \"$file\""
                            }
                            val pb = ProcessBuilder("/system/bin/su", "-c", cmd)
                            pb.redirectErrorStream(true)
                            pb.directory(java.io.File(dir))
                            pb.environment()["PATH"] = "/system/bin:/system/xbin:/sbin:/vendor/bin"
                            pb.environment()["HOME"] = "/data"
                            pb.environment()["TMPDIR"] = "/data/local/tmp"
                            val proc = pb.start()
                            withContext(Dispatchers.Main) { activeProc = proc }
                            proc.inputStream.bufferedReader().useLines { seq ->
                                seq.forEach { line ->
                                    withContext(Dispatchers.Main) {
                                        termText = termText + line + "\n"
                                    }
                                }
                            }
                            proc.waitFor()
                        } catch (t: Throwable) {
                            withContext(Dispatchers.Main) {
                                termText = termText + "执行失败: " + (t.message ?: "") + "\n"
                            }
                        }
                    }
                    } finally {
                        running = false
                    }
                    refreshTick += 1
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
                        transformOrigin = TransformOrigin(0.735f, 0.075f)
                    }
            ) {
                ModePanel(
                    onRunExec = { path ->
                        uiScope.launch {
                            running = true
                            termText = ""
                            runMessage = null
                            showTerminal = true
                            withContext(Dispatchers.IO) {
                                try {
                                    fun q3(s: String) = "'" + s.replace("'", "'\\''") + "'"
                                    val cmd = "export HOME=/data/local/tmp; export TMPDIR=/data/local/tmp; " +
                                        "cd " + q3(java.io.File(path).parent ?: "/") + " 2>/dev/null; " +
                                        "chmod 755 " + q3(path) + " 2>/dev/null; " +
                                        "sh " + q3(path) + " 2>&1"
                                    val pr = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
                                    activeProc = pr
                                    pr.inputStream.bufferedReader().useLines { ls ->
                                        ls.forEach { line ->
                                            withContext(Dispatchers.Main) {
                                                termText = (termText + line + "\n").takeLast(20000)
                                            }
                                        }
                                    }
                                    pr.waitFor()
                                } catch (t: Throwable) {
                                    withContext(Dispatchers.Main) {
                                        termText += "执行失败: " + (t.message ?: "") + "\n"
                                    }
                                }
                            }
                            activeProc = null
                            running = false
                        }
                    },
                    onDismiss = {
                    uiScope.launch {
                        panelP.animateTo(0f, tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing))
                        panel = false
                    }
                })
            }
        }
    if (editPath != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "编辑 " + (editPath ?: "").substringAfterLast('/'),
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.foundation.text.BasicTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF141418), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        IslandActionButton("取消", Color(0xFF3A3A3C)) {
                            editPath = null
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        IslandActionButton("保存", Color(0xFF0A84FF)) {
                            val target = editPath
                            if (target != null) {
                                uiScope.launch {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val b64 = android.util.Base64.encodeToString(
                                                editText.toByteArray(),
                                                android.util.Base64.NO_WRAP
                                            )
                                            Shell.cmd(
                                                "echo '" + b64 + "' | base64 -d > '" +
                                                    target.replace("'", "'\''") + "'"
                                            ).exec()
                                        } catch (t: Throwable) {
                                        }
                                    }
                                    editPath = null
                                    refreshTick += 1
                                }
                            }
                        }
                    }
                }
            }
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
    runMessage: String?,
    runOk: Boolean,
    running: Boolean,
    terminalMode: Boolean,
    termText: String,
    termInput: String,
    onTermInput: (String) -> Unit,
    onTermRun: () -> Unit,
    onTermClose: () -> Unit,
    onClearSelection: () -> Unit,
    onGoLeft: (String) -> Unit,
    onGoRight: (String) -> Unit,
    onOpenStorage: () -> Unit,
    onOpenData: () -> Unit,
    onExtract: () -> Unit,
    onExtractApp: (String, String) -> Unit,
    onDelete: () -> Unit,
    onAction: (String) -> Unit,
    onArchiveEnter: () -> Unit,
    onArchiveMount: () -> Unit,
    longPressPath: String?,
    longPressIsDir: Boolean,
    longPressTick: Int,
    onClearLongPress: () -> Unit,
    oppositePath: String,
    onFileOp: (String, String) -> Unit,
    onOpen: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showWave by remember { mutableStateOf(false) }
    var navMode by remember { mutableStateOf(false) }
    var navTarget by remember { mutableStateOf<String?>(null) }
    var navClosing by remember { mutableStateOf(false) }
    var jumpStep by remember { mutableStateOf(0) }
    var jumpSide by remember { mutableStateOf(0) }
    var jumpInput by remember { mutableStateOf("") }
    var renameInput by remember { mutableStateOf("") }
    val ctxNav = androidx.compose.ui.platform.LocalContext.current
    var susfsVer by remember { mutableStateOf("") }
    var susfsVariant by remember { mutableStateOf("") }
    var susfsSupp by remember { mutableStateOf(false) }
    var zygiskOn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        susfsSupp = try {
            com.rifsxd.ksunext.ui.util.getSuSFS() == "Supported"
        } catch (t: Throwable) {
            false
        }
        susfsVer = try {
            com.rifsxd.ksunext.ui.util.getSuSFSVersion()
        } catch (t: Throwable) {
            ""
        }
        susfsVariant = try {
            com.rifsxd.ksunext.ui.util.getSuSFSVariant()
        } catch (t: Throwable) {
            ""
        }
        zygiskOn = try {
            com.rifsxd.ksunext.Natives.isZygiskEnabled()
        } catch (t: Throwable) {
            false
        }
    }
    var paneTarget by remember { mutableStateOf("/storage/emulated/0/") }
    var createIsDir by remember { mutableStateOf(true) }
    var extractDone by remember { mutableStateOf(false) }
    var modules by remember { mutableStateOf<List<ModItem>>(emptyList()) }
    var modSel by remember { mutableStateOf(-1) }
    var suApps by remember { mutableStateOf<List<SuApp>>(emptyList()) }
    var revokedPkgs by remember { mutableStateOf<Set<String>>(emptySet()) }
    var suSel by remember { mutableStateOf(-1) }
    val navCtx = LocalContext.current
    val navScope = rememberCoroutineScope()
    LaunchedEffect(navTarget, jumpStep) {
        modSel = -1
        suSel = -1
        if (navTarget == "module") {
            modules = withContext(Dispatchers.IO) {
                try {
                    val ids = Shell.cmd("ls -1 /data/adb/modules 2>/dev/null").exec().out
                        .filter { it.isNotBlank() }
                    ids.mapNotNull { id ->
                        val prop = Shell.cmd("cat /data/adb/modules/$id/module.prop 2>/dev/null").exec().out
                        if (prop.isEmpty()) null else {
                            val map = prop.mapNotNull { line ->
                                val i = line.indexOf('=')
                                if (i > 0) line.substring(0, i).trim() to line.substring(i + 1).trim() else null
                            }.toMap()
                            val disabled = Shell.cmd("test -e /data/adb/modules/$id/disable && echo yes").exec()
                                .out.joinToString("").contains("yes")
                            ModItem(
                                id = id,
                                name = map["name"] ?: id,
                                version = map["version"] ?: "",
                                desc = map["description"] ?: "",
                                disabled = disabled
                            )
                        }
                    }
                } catch (t: Throwable) {
                    emptyList()
                }
            }
        }
        if (navTarget == "su" || (navTarget == "home" && jumpStep == 7)) {
            val base = withContext(Dispatchers.IO) {
                try {
                    val pm = navCtx.packageManager
                    val userPkgs = Shell.cmd("pm list packages -3").exec().out
                        .map { it.removePrefix("package:").trim() }
                        .filter { it.isNotEmpty() && it.contains('.') }
                    userPkgs
                        .mapNotNull { pkg ->
                            try {
                                pm.getApplicationInfo(pkg, 0)
                            } catch (t: Throwable) {
                                null
                            }
                        }
                        .map { ai ->
                            val allowed = false
                            val icon = try {
                                val d = pm.getApplicationIcon(ai)
                                val w = d.intrinsicWidth.coerceAtLeast(1)
                                val h = d.intrinsicHeight.coerceAtLeast(1)
                                val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                                val canvas = android.graphics.Canvas(bmp)
                                d.setBounds(0, 0, w, h)
                                d.draw(canvas)
                                bmp
                            } catch (t: Throwable) {
                                null
                            }
                            SuApp(ai.packageName, pm.getApplicationLabel(ai).toString(), ai.uid, allowed, icon)
                        }
                        .take(200)
                } catch (t: Throwable) {
                    emptyList()
                }
            }
            suApps = base.map { a ->
                val allowed = try {
                    com.rifsxd.ksunext.Natives.getAppProfile(a.pkg, a.uid)?.allowSu == true
                } catch (t: Throwable) {
                    false
                }
                a.copy(allowed = allowed)
            }.sortedByDescending { it.allowed }
        }
    }
    var soInput by remember { mutableStateOf("") }
    var soPanel by remember { mutableStateOf(false) }
    LaunchedEffect(selectedFile) {
        if (selectedFile != null) navMode = false
        soPanel = false
    }
    LaunchedEffect(extractDone) {
        if (extractDone) {
            kotlinx.coroutines.delay(2200)
            extractDone = false
        }
    }
    LaunchedEffect(running) {
        if (running) {
            showWave = true
        } else {
            kotlinx.coroutines.delay(650)
            showWave = false
        }
    }
    val reopenScope = rememberCoroutineScope()
    fun foldAndReopen() {
        reopenScope.launch {
            expanded = false
            kotlinx.coroutines.delay(280)
            expanded = true
        }
    }

    fun closeIsland() {
        reopenScope.launch {
            navClosing = true
            navMode = false
            expanded = false
            onClearLongPress()
            kotlinx.coroutines.delay(180)
            navTarget = null
            jumpStep = 0
            jumpInput = ""
            navClosing = false
        }
    }

    fun switchStep(target: Int) {
        reopenScope.launch {
            navClosing = true
            kotlinx.coroutines.delay(110)
            jumpStep = target
            navClosing = false
            expanded = true
        }
    }

    LaunchedEffect(longPressPath) {
        // a cleared long press (file tapped elsewhere) must fold the menu
        if (longPressPath == null && jumpStep == 9) {
            closeIsland()
        }
    }
    LaunchedEffect(longPressTick) {
        if (longPressTick <= 0) return@LaunchedEffect
        val lp = longPressPath ?: return@LaunchedEffect
        if (lp.isNotEmpty()) {
            navClosing = true
            expanded = false
            jumpStep = 0
            kotlinx.coroutines.delay(110)
            navMode = true
            navTarget = "home"
            jumpStep = 9
            renameInput = lp.substringAfterLast('/')
            navClosing = false
            expanded = true
        }
    }

    fun openArchive() {
        reopenScope.launch {
            navClosing = true
            kotlinx.coroutines.delay(110)
            navMode = true
            navTarget = "home"
            jumpStep = 8
            navClosing = false
            expanded = true
        }
    }

    fun openNav(target: String?) {
        reopenScope.launch {
            navClosing = true
            kotlinx.coroutines.delay(150)
            navMode = true
            navTarget = target
            navClosing = false
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
    val wingPress = remember { Animatable(0f) }
    LaunchedEffect(panelOpen) {
        if (panelOpen) {
            wingPress.animateTo(1f, tween(220, easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)))
        } else {
            wingPress.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 420f))
        }
    }
    val ball = remember { Animatable(1f) }
    val wingHidden = selectedFile != null || navMode || terminalMode
    LaunchedEffect(wingHidden) {
        if (wingHidden) {
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
        lowerSel.endsWith(".sh") || lowerSel.endsWith(".rc") -> "执行"
        lowerSel.endsWith(".zip") || lowerSel.endsWith(".tar") || lowerSel.endsWith(".gz") ||
            lowerSel.endsWith(".7z") || lowerSel.endsWith(".rar") || lowerSel.endsWith(".xz") -> "更多"
        else -> "编辑"
    }
    val actionColor = Color(0xFF0A84FF)

    val zyEase = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)


    Box(
        modifier = Modifier.fillMaxWidth().offset(y = 10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // wing sits UNDER the pill and slides out of it
        Row(
            modifier = Modifier
                .offset(x = (pillW.value / 2f + 30f).dp, y = 0.dp)
                .width(52.dp)
                .height(42.dp)
                .graphicsLayer {
                    val travel = with(density) { (pillW.value / 2f + 3f).dp.toPx() }
                    val press = 1f - 0.16f * wingPress.value
                    translationX = (ballShift - 1f) * travel
                    scaleX = ballScale * bump * press
                    scaleY = ballScale * bump * press
                    alpha = (ballShift * 3f).coerceIn(0f, 1f)
                }
                .clip(RoundedCornerShape(21.dp))
                .background(Color.Black)
                .border(0.5.dp, Color(0x47FFFFFF), RoundedCornerShape(21.dp))
                .clickable { onOpen() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_modules_foreground),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(30.dp)
            )
        }

        // main pill on top
        Column(
            modifier = Modifier
                .zIndex(1f)
                .width(pillW)
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
                    onClick = {
                        if (terminalMode) onTermClose()
                        // let the panels shrink before the pill settles, so closing keeps its animation
                        val hadNav = navMode || navTarget != null
                        navMode = false
                        expanded = false
                        onClearLongPress()
                        if (hadNav) {
                            navClosing = true
                            reopenScope.launch {
                                kotlinx.coroutines.delay(180)
                                navTarget = null
                                jumpStep = 0
                                jumpInput = ""
                                navClosing = false
                            }
                        }
                        soPanel = false
                        if (sel != null) onClearSelection()
                        expanded = false
                        reopenScope.launch {
                            kotlinx.coroutines.delay(300)
                            jumpStep = 0
                            jumpInput = ""
                        }
                    },
                    onLongClick = { openNav(null) }
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
                if (extractDone) {
                    Text(
                        text = "已完成",
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF4CAF50)
                    )
                }
                if (terminalMode || (running && expanded)) {
                    YukiWave(
                        color = Color(0xFFFFB6C8),
                        running = running,
                        modifier = Modifier.weight(1f)
                    )
                } else if (sel != null || longPressPath != null) {
                    Text(
                        text = if (longPressPath != null) longPressPath.substringAfterLast('/') else selName,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = if (runMessage != null && !runOk) Icons.Outlined.Lock else Icons.Outlined.InsertDriveFile,
                        contentDescription = null,
                        tint = if (runMessage != null && !runOk) Color(0xFFFF3B30) else accent,
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
                visible = (navMode || navTarget != null) && !navClosing,
                enter = expandVertically(animationSpec = tween(260, easing = zyEase), expandFrom = Alignment.Bottom) + fadeIn(tween(160)),
                exit = shrinkVertically(animationSpec = tween(260, easing = zyEase), shrinkTowards = Alignment.Bottom) + fadeOut(tween(40))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = tween(260, easing = zyEase))
                ) {
                if (navTarget == null) Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavIcon(Icons.Outlined.InsertDriveFile) { openNav("home") }
                    NavIcon(Icons.Outlined.AdminPanelSettings) { openNav("su") }
                    NavIcon(Icons.Outlined.Layers) { openNav("module") }
                    NavIcon(Icons.Outlined.Settings) {
                        expanded = false
                        navMode = false
                        navTarget = null
                        navigator.navigate(SettingScreenDestination)
                    }
                }
            }

                if (navTarget != null) Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (navTarget) {
                        "module" -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(Color(0xFF0E0E10), RoundedCornerShape(12.dp))
                                    .padding(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (modules.isEmpty()) {
                                        Text(text = "没有添加模块", fontSize = 11.sp, color = Color(0xFFBDBDBD))
                                    } else {
                                        modules.forEachIndexed { mi, m ->
                                            val chosen = modSel == mi
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 6.dp)
                                                    .background(
                                                        if (chosen) Color(0xFF00BCD4) else Color(0x33FFFFFF),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .padding(1.dp)
                                                    .background(Color(0xFF16161A), RoundedCornerShape(10.dp))
                                                    .clickable { modSel = if (chosen) -1 else mi }
                                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = m.name,
                                                        fontSize = 12.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        color = Color.White,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        text = if (m.disabled) "已禁用" else "启用",
                                                        fontSize = 10.sp,
                                                        color = if (m.disabled) Color(0xFFFF9800) else Color(0xFF4CAF50)
                                                    )
                                                }
                                                if (m.version.isNotEmpty()) {
                                                    Text(text = m.version, fontSize = 10.sp, color = Color(0xFF9E9E9E))
                                                }
                                                if (m.desc.isNotEmpty()) {
                                                    Text(
                                                        text = m.desc,
                                                        fontSize = 10.sp,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis,
                                                        color = Color(0xFF9E9E9E)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (modSel >= 0 && modSel < modules.size) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        IslandActionButton("删除", Color(0xFFFF3B30)) {
                                            val mod = modules.getOrNull(modSel)
                                            if (mod != null) {
                                                navScope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        try {
                                                            Shell.cmd("rm -rf \"/data/adb/modules/${mod.id}\"").exec()
                                                        } catch (t: Throwable) {
                                                        }
                                                    }
                                                    modules = modules.filterNot { it.id == mod.id }
                                                    modSel = -1
                                                }
                                            }
                                        }
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        IslandActionButton("操作", Color(0xFF0A84FF)) {
                                            val mod = modules.getOrNull(modSel)
                                            if (mod != null) {
                                                navScope.launch {
                                                    val hasWeb = withContext(Dispatchers.IO) {
                                                        Shell.cmd("test -d \"/data/adb/modules/${mod.id}/webroot\" && echo yes")
                                                            .exec().out.joinToString("").contains("yes")
                                                    }
                                                    if (hasWeb) {
                                                        try {
                                                            val it = android.content.Intent(
                                                                navCtx,
                                                                com.rifsxd.ksunext.ui.webui.WebUIActivity::class.java
                                                            ).apply {
                                                                putExtra("is_internal_transition", true)
                                                                setData(android.net.Uri.parse("kernelsu://webui/${mod.id}"))
                                                                putExtra("id", mod.id)
                                                                putExtra("name", mod.name)
                                                            }
                                                            navCtx.startActivity(it)
                                                        } catch (t: Throwable) {
                                                        }
                                                    } else {
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                Shell.cmd("sh \"/data/adb/modules/${mod.id}/action.sh\" 2>/dev/null")
                                                                    .exec()
                                                            } catch (t: Throwable) {
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "su" -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(Color(0xFF0E0E10), RoundedCornerShape(12.dp))
                                    .padding(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (suApps.isEmpty()) {
                                        Text(text = "没有应用", fontSize = 11.sp, color = Color(0xFFBDBDBD))
                                    } else {
                                        suApps.forEachIndexed { si, app ->
                                            val chosen = suSel == si
                                            val ring = when {
                                                chosen -> Color(0xFF00BCD4)
                                                app.allowed -> Color(0xFFFF6FA8)
                                                else -> Color(0x33FFFFFF)
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 6.dp)
                                                    .background(ring, RoundedCornerShape(10.dp))
                                                    .padding(1.dp)
                                                    .background(Color(0xFF16161A), RoundedCornerShape(10.dp))
                                                    .clickable { suSel = if (chosen) -1 else si }
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val iconBmp = app.icon
                                                if (iconBmp != null) {
                                                    Image(
                                                        bitmap = iconBmp.asImageBitmap(),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                                Text(
                                                    text = app.label,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = Color.White,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                if (app.allowed) {
                                                    Text(text = "已寄生", fontSize = 10.sp, color = Color(0xFFFF6FA8))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (suSel >= 0 && suSel < suApps.size) {
                                val app = suApps[suSel]
                                IslandActionButton(
                                    if (app.allowed) "撤销" else "寄生",
                                    if (app.allowed) Color(0xFFFF3B30) else Color(0xFF0A84FF)
                                ) {
                                    navScope.launch {
                                        withContext(Dispatchers.IO) {
                                            try {
                                                // same path as the AppProfile screen: read the live
                                                // profile, flip allowSu, write it back
                                                val current = com.rifsxd.ksunext.Natives.getAppProfile(app.pkg, app.uid)
                                                val next = current?.copy(allowSu = !app.allowed)
                                                    ?: com.rifsxd.ksunext.Natives.Profile(
                                                        name = app.pkg,
                                                        currentUid = app.uid,
                                                        allowSu = !app.allowed
                                                    )
                                                com.rifsxd.ksunext.Natives.setAppProfile(next)
                                            } catch (t: Throwable) {
                                            }
                                        }
                                        revokedPkgs = if (app.allowed) {
                                            revokedPkgs + app.pkg
                                        } else {
                                            revokedPkgs - app.pkg
                                        }
                                        suApps = suApps.mapIndexed { i, a ->
                                            if (i == suSel) a.copy(allowed = !a.allowed) else a
                                        }.sortedByDescending { it.allowed }
                                        suSel = -1
                                    }
                                }
                            }
                        }
                        "home" -> {
                            when (jumpStep) {
                                8 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("进入", Color(0xFF0A84FF)) {
                                                onArchiveEnter()
                                                closeIsland()
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("挂载", Color(0xFFFFB300)) {
                                                onArchiveMount()
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                9 -> {
                                    val lp = longPressPath ?: ""
                                    val lowerLp = lp.lowercase()
                                    val isArchive = lowerLp.endsWith(".zip") || lowerLp.endsWith(".tar") ||
                                        lowerLp.endsWith(".gz") || lowerLp.endsWith(".7z") ||
                                        lowerLp.endsWith(".rar") || lowerLp.endsWith(".xz")
                                    val isApk = lowerLp.endsWith(".apk")
                                    val isExec = lowerLp.endsWith(".sh") || lowerLp.endsWith(".so") ||
                                        lowerLp.endsWith(".rc")
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        IslandActionButton("移动/复制", Color(0xFF0A84FF)) {
                                            switchStep(10)
                                        }
                                        IslandActionButton(
                                            if (isArchive) "解压" else "压缩",
                                            Color(0xFF0A84FF)
                                        ) {
                                            if (isArchive) onFileOp("unzip", oppositePath)
                                            else onFileOp("zip", "")
                                            closeIsland()
                                        }
                                        IslandActionButton("重命名", Color(0xFF0A84FF)) {
                                            switchStep(11)
                                        }
                                        IslandActionButton("复制路径", Color(0xFF0A84FF)) {
                                            try {
                                                val cm = ctxNav.getSystemService(
                                                    android.content.Context.CLIPBOARD_SERVICE
                                                ) as android.content.ClipboardManager
                                                cm.setPrimaryClip(
                                                    android.content.ClipData.newPlainText("path", lp)
                                                )
                                            } catch (t: Throwable) {
                                            }
                                            closeIsland()
                                        }
                                        if (longPressIsDir) {
                                            IslandActionButton("删除", Color(0xFFFF3B30)) {
                                                switchStep(12)
                                            }
                                        } else {
                                            IslandActionButton("分享", Color(0xFF0A84FF)) {
                                                onFileOp("share", "")
                                                closeIsland()
                                            }
                                        }
                                        if (!longPressIsDir) {
                                            IslandActionButton(
                                                when {
                                                    isApk -> "安装"
                                                    isArchive -> "进入"
                                                    isExec -> "编辑"
                                                    else -> "执行"
                                                },
                                                Color(0xFF0A84FF)
                                            ) {
                                                when {
                                                    isApk -> onFileOp("install", "")
                                                    isArchive -> onArchiveEnter()
                                                    isExec -> onFileOp("edit", "")
                                                    else -> onAction("")
                                                }
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                10 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("移动", Color(0xFF3A3A3C)) {
                                                onFileOp("move", oppositePath)
                                                closeIsland()
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("复制", Color(0xFF0A84FF)) {
                                                onFileOp("copy", oppositePath)
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                11 -> {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = renameInput,
                                        onValueChange = { renameInput = it },
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = Color.White,
                                            fontSize = 12.sp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("确定", Color(0xFF0A84FF)) {
                                                if (renameInput.isNotBlank()) {
                                                    onFileOp("rename", renameInput.trim())
                                                }
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                12 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("确认删除", Color(0xFFFF3B30)) {
                                                onFileOp("delete", "")
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                0 -> {
                                    GradientActionButton("提取", Color(0xFF0A84FF)) { switchStep(7) }
                                    GradientActionButton("data/储存", Color(0xFF0A84FF)) { switchStep(2) }
                                    GradientActionButton("文件/文件夹", Color(0xFF0A84FF)) { switchStep(1) }
                                    GradientActionButton("跳转", Color(0xFF0A84FF)) { switchStep(4) }
                                }
                                1 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("创建文件", Color(0xFF3A3A3C)) {
                                                createIsDir = false
                                                switchStep(6)
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("创建文件夹", Color(0xFF0A84FF)) {
                                                createIsDir = true
                                                switchStep(6)
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("data", Color(0xFF3A3A3C)) {
                                                paneTarget = "/data/"
                                                switchStep(3)
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("储存", Color(0xFF0A84FF)) {
                                                paneTarget = "/storage/emulated/0/"
                                                switchStep(3)
                                            }
                                        }
                                    }
                                }
                                3 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向左", Color(0xFF3A3A3C)) {
                                                onGoLeft(paneTarget)
                                                closeIsland()
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向右", Color(0xFF0A84FF)) {
                                                onGoRight(paneTarget)
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                4 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向左", Color(0xFF3A3A3C)) {
                                                jumpSide = 1
                                                switchStep(5)
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向右", Color(0xFF0A84FF)) {
                                                jumpSide = 2
                                                switchStep(5)
                                            }
                                        }
                                    }
                                }
                                5 -> {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = jumpInput,
                                        onValueChange = { jumpInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = Color.White,
                                            fontSize = 12.sp
                                        ),
                                        singleLine = true
                                    )
                                    IslandActionButton("确定", Color(0xFF0A84FF)) {
                                        if (jumpSide == 1) onGoLeft(jumpInput) else onGoRight(jumpInput)
                                        closeIsland()
                                    }
                                }
                                7 -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .background(Color(0xFF0E0E10), RoundedCornerShape(12.dp))
                                            .padding(6.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .verticalScroll(rememberScrollState())
                                        ) {
                                            if (suApps.isEmpty()) {
                                                Text(text = "没有应用", fontSize = 11.sp, color = Color(0xFFBDBDBD))
                                            } else {
                                                suApps.forEach { app ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(bottom = 6.dp)
                                                            .background(Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                                                            .padding(1.dp)
                                                            .background(Color(0xFF16161A), RoundedCornerShape(10.dp))
                                                            .clickable {
                                                                onExtractApp(app.pkg, app.label)
                                                                jumpStep = 0
                                                                navTarget = null
                                                                navMode = false
                                                                expanded = false
                                                                extractDone = true
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        val iconBmp = app.icon
                                                        if (iconBmp != null) {
                                                            Image(
                                                                bitmap = iconBmp.asImageBitmap(),
                                                                contentDescription = null,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                        }
                                                        Text(
                                                            text = app.label,
                                                            fontSize = 12.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            color = Color.White,
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = jumpInput,
                                        onValueChange = { jumpInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = Color.White,
                                            fontSize = 12.sp
                                        ),
                                        singleLine = true
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向左", Color(0xFF3A3A3C)) {
                                                val nm = jumpInput.trim()
                                                if (nm.isNotEmpty()) {
                                                    navScope.launch {
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                if (createIsDir) {
                                                                    Shell.cmd("mkdir -p \"$paneTarget$nm\"").exec()
                                                                } else {
                                                                    Shell.cmd("touch \"$paneTarget$nm\"").exec()
                                                                }
                                                            } catch (t: Throwable) {
                                                            }
                                                        }
                                                        onGoLeft(paneTarget)
                                                        closeIsland()
                                                    }
                                                }
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向右", Color(0xFF0A84FF)) {
                                                val nm = jumpInput.trim()
                                                if (nm.isNotEmpty()) {
                                                    navScope.launch {
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                if (createIsDir) {
                                                                    Shell.cmd("mkdir -p \"$paneTarget$nm\"").exec()
                                                                } else {
                                                                    Shell.cmd("touch \"$paneTarget$nm\"").exec()
                                                                }
                                                            } catch (t: Throwable) {
                                                            }
                                                        }
                                                        onGoRight(paneTarget)
                                                        closeIsland()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = terminalMode,
                enter = expandVertically(animationSpec = tween(360, easing = zyEase), expandFrom = Alignment.CenterVertically) + fadeIn(tween(200)),
                exit = shrinkVertically(animationSpec = tween(260, easing = zyEase), shrinkTowards = Alignment.CenterVertically) + fadeOut(tween(160))
            ) {
                val termScroll = rememberScrollState()
                    LaunchedEffect(termText) {
                        termScroll.animateScrollTo(termScroll.maxValue)
                    }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = termText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .verticalScroll(termScroll),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = termInput,
                            onValueChange = onTermInput,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF0E0E10), RoundedCornerShape(6.dp))
                                .padding(4.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 11.sp
                            ),
                            singleLine = true
                        )
                        Text(
                            "发送",
                            modifier = Modifier
                                .clickable { onTermRun() }
                                .padding(start = 10.dp)
                                .background(Color(0xFF0A84FF), RoundedCornerShape(percent = 50))
                                .padding(horizontal = 10.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            color = Color.White
                        )

                    }
                }
            }

            AnimatedVisibility(
                visible = sel != null && !terminalMode && !navMode && !soPanel,
                enter = expandVertically(
                    animationSpec = tween(360, easing = zyEase),
                    expandFrom = Alignment.CenterVertically
                ) + fadeIn(tween(200)),
                exit = shrinkVertically(
                    animationSpec = tween(260, easing = zyEase),
                    shrinkTowards = Alignment.CenterVertically
                ) + fadeOut(tween(160))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            IslandActionButton("删除", Color(0xFFFF3B30)) { onDelete() }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            IslandActionButton(actionLabel, actionColor) {
                                if (actionLabel == "更多") openArchive()
                                else if (actionLabel == "编辑") onFileOp("edit", "")
                                else if (lowerSel.endsWith(".so")) soPanel = true
                                else onAction("")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = soPanel && sel != null && !terminalMode && !navMode,
                enter = expandVertically(
                    animationSpec = tween(320, easing = zyEase),
                    expandFrom = Alignment.CenterVertically
                ) + fadeIn(tween(180)),
                exit = shrinkVertically(
                    animationSpec = tween(240, easing = zyEase),
                    shrinkTowards = Alignment.CenterVertically
                ) + fadeOut(tween(140))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = soInput,
                        onValueChange = { soInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        ),
                        singleLine = true
                    )
                    IslandActionButton("启动", Color(0xFF0A84FF)) {
                        onAction(soInput)
                        soPanel = false
                    }
                }
            }
        }
    }
}

@Composable
private fun GradientActionButton(label: String, color: Color, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(color, RoundedCornerShape(percent = 50))
            .padding(vertical = 9.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun DarkActionButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF16161A), RoundedCornerShape(percent = 50))
            .border(0.5.dp, Color(0xFF3A3A3C), RoundedCornerShape(percent = 50))
            .padding(vertical = 9.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun FileBigButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF16161A), RoundedCornerShape(26.dp))
            .border(0.5.dp, Color(0xFF3A3A3C), RoundedCornerShape(26.dp))
            .padding(vertical = 15.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun NavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(percent = 50))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun IslandActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(color, RoundedCornerShape(percent = 50))
            .padding(vertical = 9.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun TerminalOverlay(onExit: () -> Unit) {
    var input by remember { mutableStateOf("") }
    val lines = remember { androidx.compose.runtime.mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }

    fun run(cmd: String) {
        if (cmd.isBlank()) return
        lines.add("> " + cmd)
        busy = true
        scope.launch {
            val out = withContext(Dispatchers.IO) { Shell.cmd(cmd).exec() }
            out.out.forEach { if (it.isNotBlank()) lines.add(it) }
            if (out.err.isNotEmpty()) out.err.forEach { if (it.isNotBlank()) lines.add(it) }
            busy = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xF2000000)).padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "终端", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "关闭",
                color = Color(0xFF0A84FF),
                fontSize = 13.sp,
                modifier = Modifier.clickable { onExit() }
            )
        }
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(lines) { line ->
                Text(
                    text = line,
                    color = Color(0xFFDDDDDD),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "\$", color = Color(0xFF4CAF50), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            androidx.compose.foundation.text.BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Go
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onGo = {
                        run(input)
                        input = ""
                    }
                )
            )
            Text(
                text = if (busy) "…" else "运行",
                color = Color(0xFF0A84FF),
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    run(input)
                    input = ""
                }
            )
        }
    }
}

@Composable
private fun YukiWave(color: Color, running: Boolean, modifier: Modifier = Modifier) {
    val phase = remember { Animatable(0f) }
    val fill = remember { Animatable(0f) }
    val finishing = remember { mutableStateOf(false) }
    val round = remember { mutableStateOf(0) }
    LaunchedEffect(running) {
        if (running) {
            fill.snapTo(0f)
            while (true) {
                phase.snapTo(0f)
                phase.animateTo(1f, tween(900, easing = LinearEasing))
                round.value = round.value + 1
            }
        } else {
            finishing.value = true
            phase.animateTo(1f, tween(450, easing = LinearEasing))
            phase.snapTo(0f)
            phase.animateTo(1f, tween(320, easing = LinearEasing))
            round.value = round.value + 1
            finishing.value = false
            fill.animateTo(1f, tween(340, easing = LinearEasing))
        }
    }
    Canvas(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(12.dp)
            .clipToBounds()
    ) {
        val cy = size.height / 2f
        drawLine(
            color = Color.White.copy(alpha = 0.45f),
            start = Offset(0f, cy),
            end = Offset(size.width, cy),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        if (running || finishing.value) {
            val seg = size.width * (0.30f + 0.10f * kotlin.math.min(round.value, 4).toFloat())
            val amp = 2.6.dp.toPx()
            for (k in 0 until 2) {
                val p = (phase.value + k * 0.5f) % 1f
                val x0 = -seg + p * (size.width + seg)
                val path = Path()
                var x = 0f
                while (x <= seg) {
                    val px = x0 + x
                    val py = cy + amp * kotlin.math.sin(x / 26.dp.toPx() * 6.2831855f - phase.value * 6.2831855f)
                    if (x <= 0f) path.moveTo(px, py) else path.lineTo(px, py)
                    x += 1f
                }
                val ribbon = Brush.horizontalGradient(
                    0f to color.copy(alpha = 0.05f),
                    0.14f to color,
                    0.86f to color,
                    1f to color.copy(alpha = 0.05f)
                )
                drawPath(
                    path = path,
                    brush = ribbon,
                    alpha = 0.18f,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = path,
                    brush = ribbon,
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
        if (fill.value > 0.001f) {
            drawLine(
                color = color,
                start = Offset(0f, cy),
                end = Offset(size.width * fill.value.coerceIn(0f, 1f), cy),
                strokeWidth = 2.4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ModePanel(onDismiss: () -> Unit, onRunExec: (String) -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize().clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModeButton(Icons.Outlined.DarkMode, "放开通行")
            ModeButton(Icons.Outlined.AcUnit, "性能模式")
            ModeButton(Icons.Outlined.Person, "隐藏BL")
            ModeButton(Icons.Outlined.Smartphone, "刷入RT驱动")
            ModeButton(Icons.Outlined.Folder, "执行此文件", onRun = {
                onDismiss()
                onRunExec(it)
            })
        }
    }
}

@Composable
private fun ModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onRun: ((String) -> Unit)? = null
) {
    var on by remember { mutableStateOf(false) }
    var latencyMsg by remember { mutableStateOf<String?>(null) }
    val ctx3 = androidx.compose.ui.platform.LocalContext.current
    val scope2 = rememberCoroutineScope()
    var execPath by remember { mutableStateOf<String?>(null) }
    var showPathDialog by remember { mutableStateOf(false) }
    var showKeyDialog by remember { mutableStateOf(false) }
    var pathInput by remember { mutableStateOf("") }
    var keyInput by remember { mutableStateOf("") }
    suspend fun refreshLatency() {
        latencyMsg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    fun measure(p: String): Double {
                        var best = Double.MAX_VALUE
                        for (r in 0 until 3) {
                            val iter = 8000
                            val t0 = System.nanoTime()
                            for (i in 0 until iter) {
                                try {
                                    android.system.Os.stat(p)
                                } catch (e: Throwable) {
                                }
                            }
                            val per = (System.nanoTime() - t0).toDouble() / iter
                            if (per < best) best = per
                        }
                        return best
                    }
                    val den = (measure("/data/system") + measure("/data/misc")) / 2.0
                    val mod = measure("/data/adb/modules")
                    if (mod - den > 500.0) "会拉闸" else "不会拉闸"
                } catch (t: Throwable) {
                    null
                }
            }
    }
    LaunchedEffect(title) {
        if (title == "性能模式") {
            on = withContext(kotlinx.coroutines.Dispatchers.IO) {
                java.io.File("/data/adb/service.d/xy_perf.sh").exists()
            }
        }
        if (title == "执行此文件") {
            execPath = ctx3.getSharedPreferences("yx_exec", 0).getString("path", null)
        }
        if (title == "隐藏BL") {
            on = withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    fun q(c: String): String {
                        val pr = Runtime.getRuntime().exec(arrayOf("su", "-c", c))
                        val o = pr.inputStream.bufferedReader().readText()
                        pr.waitFor()
                        return o
                    }
                    val mod = q("grep -l '小鱼儿附属' /data/adb/modules/*/module.prop 2>/dev/null | head -n1").trim()
                    val vs = q("getprop ro.boot.verifiedbootstate 2>/dev/null").trim()
                    mod.isNotEmpty() || vs == "green"
                } catch (t: Throwable) {
                    false
                }
            }
        }
        if (title == "放开通行") {
            on = withContext(kotlinx.coroutines.Dispatchers.IO) {
                java.io.File("/data/adb/service.d/xy_open_access.sh").exists()
            }
            refreshLatency()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xE60E0E10))
            .clickable {
                on = !on
                if (title == "刷入RT驱动") {
                    latencyMsg = "刷入中…"
                    scope2.launch {
                        val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su2(c: String): String {
                                    val pr = Runtime.getRuntime().exec(arrayOf("su", "-c", c))
                                    val o = pr.inputStream.bufferedReader().readText()
                                    pr.waitFor()
                                    return o
                                }
                                val rtDir = java.io.File(ctx3.filesDir, "rtdrv")
                                try {
                                    if ((rtDir.listFiles()?.size ?: 0) < 5) {
                                        rtDir.mkdirs()
                                        su2("cp -rf '/storage/emulated/0/床上面的/RT dev驱动/.' '" +
                                            rtDir.absolutePath.replace("'", "'\\''") + "/' 2>/dev/null; true")
                                    }
                                } catch (t: Throwable) {
                                }
                                val rel = su2("uname -r 2>/dev/null").trim()
                                val ver = rel.substringBefore('-')
                                val mm = ver.split(".").take(2).joinToString(".")
                                val allNames = rtDir.listFiles()
                                    ?.filter { it.name.endsWith(".sh") }
                                    ?.map { it.name }
                                    ?: emptyList()
                                fun baseOf(n: String): String {
                                    var b = n.removeSuffix(".sh")
                                    while (b.isNotEmpty() && (b.last() == 'b' || b.last() == 'c')) {
                                        b = b.dropLast(1)
                                    }
                                    return b
                                }
                                val exactName = allNames.firstOrNull { baseOf(it) == ver }
                                val sameMinor = allNames.filter {
                                    baseOf(it).split(".").take(2).joinToString(".") == mm
                                }
                                val chosenName = exactName ?: sameMinor.firstOrNull()
                                val cands = if (chosenName != null) {
                                    listOf(chosenName)
                                } else {
                                    listOf(mm + ".sh")
                                }
                                val root = rtDir.absolutePath
                                val script = cands.map { "$root/$it" }.firstOrNull { java.io.File(it).isFile }
                                if (script == null) {
                                    "无匹配驱动(" + rel + ")"
                                } else {
                                    val out = su2("export HOME=/data/local/tmp; export TMPDIR=/data/local/tmp; " +
                                        "cp '" + script.replace("'", "'\\''") + "' /data/local/tmp/rt.sh; " +
                                        "chmod 755 /data/local/tmp/rt.sh; sh /data/local/tmp/rt.sh 2>&1 | tail -n 3")
                                    val ok = out.contains("成功") || out.contains("ok") || out.contains("OK") ||
                                        out.contains("done") || out.isEmpty()
                                    (if (ok) "已刷入 " else "刷入失败 ") + java.io.File(script).name
                                }
                            } catch (t: Throwable) {
                                "刷入异常"
                            }
                        }
                        latencyMsg = msg
                    }
                }
                if (title == "执行此文件" && onRun != null) {
                    val savedPath = ctx3.getSharedPreferences("yx_exec", 0).getString("path", null)
                    if (savedPath.isNullOrBlank()) {
                        latencyMsg = "暂无设置"
                    } else if (savedPath.endsWith(".so")) {
                        keyInput = ""
                        showKeyDialog = true
                    } else {
                        onRun(savedPath)
                    }
                } else if (title == "执行此文件") {
                    val path = ctx3.getSharedPreferences("yx_exec", 0).getString("path", null)
                    if (path.isNullOrBlank()) {
                        latencyMsg = "暂无设置"
                    } else if (path.endsWith(".so")) {
                        keyInput = ""
                        showKeyDialog = true
                    } else {
                        latencyMsg = "执行中…"
                        scope2.launch {
                            val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    fun su2(c: String): String {
                                        val pr = Runtime.getRuntime().exec(arrayOf("su", "-c", c))
                                        val o = pr.inputStream.bufferedReader().readText()
                                        pr.waitFor()
                                        return o
                                    }
                                    val q = "'" + path.replace("'", "'\\''") + "'"
                                    val out = su2("export HOME=/data/local/tmp; export TMPDIR=/data/local/tmp; " +
                                        "cd '" + java.io.File(path).parent + "' 2>/dev/null; " +
                                        "chmod 755 $q 2>/dev/null; sh $q 2>&1 | tail -n 2")
                                    (if (out.contains("fail") || out.contains("error")) "执行失败" else "已执行")
                                } catch (t: Throwable) {
                                    "执行异常"
                                }
                            }
                            latencyMsg = msg
                        }
                    }
                }
                if (title == "隐藏BL") {
                    val enabling = on
                    latencyMsg = "处理中…"
                    scope2.launch {
                        val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su(c: String): String {
                                    val p = Runtime.getRuntime().exec(arrayOf("su", "-c", c))
                                    val out = p.inputStream.bufferedReader().readText()
                                    p.waitFor()
                                    return out
                                }
                                // resetprop is what makes the props invisible to detection
                                if (enabling) {
                                    su("resetprop ro.boot.verifiedbootstate green 2>/dev/null; " +
                                        "resetprop ro.boot.flash.locked 1 2>/dev/null; " +
                                        "resetprop ro.boot.veritymode enforcing 2>/dev/null; " +
                                        "resetprop ro.secureboot.lockstate locked 2>/dev/null; " +
                                        "resetprop ro.boot.warranty_bit 0 2>/dev/null; true")
                                    // download through the app (Android has no curl), then hand it to ksud
                                    su("mkdir -p /data/local/tmp")
                                    try {
                                        val cacheZip = java.io.File(ctx3.cacheDir, "as.zip")
                                        if (!cacheZip.exists() || cacheZip.length() < 10000) {
                                            val conn = java.net.URL(
                                                "https://gitlab.com/api/v4/projects/87739512/packages/generic/AlwaysStrong/v1.0.5-r2/AlwaysStrong-v1.0.5.zip"
                                            ).openConnection() as java.net.HttpURLConnection
                                            conn.connectTimeout = 20000
                                            conn.readTimeout = 60000
                                            conn.instanceFollowRedirects = true
                                            conn.inputStream.use { input ->
                                                cacheZip.outputStream().use { out -> input.copyTo(out) }
                                            }
                                        }
                                        su("cp '" + cacheZip.absolutePath.replace("'", "'\\''") + "' /data/local/tmp/as.zip")
                                    } catch (t: Throwable) {
                                    }
                                    val ksud = ctx3.applicationInfo.nativeLibraryDir + "/libksud.so"
                                    su("'" + ksud.replace("'", "'\''") + "' module install /data/local/tmp/as.zip 2>&1")
                                    // rename it so the module list shows 小鱼儿附属
                                    su("for d in /data/adb/modules/*/; do " +
                                        "if [ -f \"\${d}module.prop\" ] && grep -qi 'alwaysstrong' \"\${d}module.prop\" 2>/dev/null; then " +
                                        "sed -i 's/^name=.*/name=小鱼儿附属/' \"\${d}module.prop\" 2>/dev/null; " +
                                        "sed -i 's/^description=.*/description=小鱼儿bl控制器/' \"\${d}module.prop\" 2>/dev/null; " +
                                        "fi; done; true")
val modDir = su("grep -l '小鱼儿附属' /data/adb/modules/*/module.prop 2>/dev/null | head -n1").trim()
                                    val vs = su("getprop ro.boot.verifiedbootstate 2>/dev/null").trim()
                                    val lk = su("getprop ro.boot.flash.locked 2>/dev/null").trim()
                                    val propOk = vs == "green" && lk == "1"
                                    val modOk = modDir.isNotEmpty()
                                    (if (modOk) "小鱼儿附属已装·重启" else "模块安装失败") +
                                        (if (propOk) " · BL✓" else " · BL✗")
                                } else {
                                    su("resetprop --delete ro.boot.verifiedbootstate 2>/dev/null; " +
                                        "resetprop --delete ro.boot.flash.locked 2>/dev/null; " +
                                        "resetprop --delete ro.boot.veritymode 2>/dev/null; " +
                                        "resetprop --delete ro.secureboot.lockstate 2>/dev/null; " +
                                        "resetprop --delete ro.boot.warranty_bit 2>/dev/null; true")
                                    su("for m in /data/adb/modules/AlwaysStrong /data/adb/modules/alwaysstrong; do [ -d \"\$m\" ] && touch \"\$m/remove\"; done 2>/dev/null; true")
                                    "已恢复 BL 状态"
                                }
                            } catch (t: Throwable) {
                                "操作失败"
                            }
                        }
                        latencyMsg = msg
                    }
                }
                if (title == "性能模式") {
                    val enabling = on
                    latencyMsg = "处理中…"
                    scope2.launch {
                        val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su(c: String): String {
                                    val p = Runtime.getRuntime().exec(arrayOf("su", "-c", c))
                                    val out = p.inputStream.bufferedReader().readText()
                                    p.waitFor()
                                    return out
                                }
                                val path = "/data/adb/service.d/xy_perf.sh"
                                if (enabling) {
                                    val perfLines = listOf(
                                        "#!/system/bin/sh",
                                        "sleep 20",
                                        "for c in /sys/devices/system/cpu/cpu*/cpufreq; do",
                                        "  echo performance > \"\$c/scaling_governor\" 2>/dev/null",
                                        "  m=\$(cat \"\$c/cpuinfo_max_freq\" 2>/dev/null)",
                                        "  [ -n \"\$m\" ] && echo \"\$m\" > \"\$c/scaling_min_freq\" 2>/dev/null",
                                        "done",
                                        "for f in /dev/cpuset/top-app/cpus /sys/fs/cgroup/top-app/cpuset.cpus; do [ -f \"\$f\" ] && echo 4-7 > \"\$f\" 2>/dev/null; done",
                                        "for f in /dev/cpuset/background/cpus /dev/cpuset/system-background/cpus /sys/fs/cgroup/background/cpuset.cpus; do [ -f \"\$f\" ] && echo 0-1 > \"\$f\" 2>/dev/null; done",
                                        "for f in /dev/cpuset/top-app/uclamp.min /sys/fs/cgroup/top-app/uclamp.min; do [ -f \"\$f\" ] && echo 1024 > \"\$f\" 2>/dev/null; done",
                                        "echo 1024 > /proc/sys/kernel/sched_util_clamp_min 2>/dev/null",
                                        "for g in /sys/class/kgsl/kgsl-3d0/devfreq /sys/class/devfreq/*; do [ -f \"\$g/max_freq\" ] && cat \"\$g/max_freq\" > \"\$g/min_freq\" 2>/dev/null; done",
                                        "for z in /sys/class/thermal/thermal_zone*/mode; do echo disabled > \"\$z\" 2>/dev/null; done",
                                        "settings put system peak_refresh_rate 120 2>/dev/null",
                                        "settings put system min_refresh_rate 120 2>/dev/null"
                                    )
                                    val scriptText = perfLines.joinToString("\n")
                                    su("mkdir -p /data/adb/service.d")
                                    su("printf '%s' '" + scriptText.replace("'", "'\\''") + "' > " + path + " ; chmod 755 " + path)
                                    su("sed 's/^sleep 20$/sleep 1/' " + path + " > /data/local/tmp/xy_now.sh ; chmod 755 /data/local/tmp/xy_now.sh ; sh /data/local/tmp/xy_now.sh >/dev/null 2>&1")
                                    val gov = su("cat /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor 2>/dev/null || cat /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor 2>/dev/null").trim()
                                    val fps = su("settings get system peak_refresh_rate 2>/dev/null").trim()
                                    val th = su("cat /sys/class/thermal/thermal_zone0/mode 2>/dev/null").trim()
                                    val topCpus = su("cat /dev/cpuset/top-app/cpus 2>/dev/null || cat /sys/fs/cgroup/top-app/cpuset.cpus 2>/dev/null").trim()
                                    val govOk = gov == "performance"
                                    val fpsOk = fps.toIntOrNull()?.let { it >= 120 } == true
                                    val thOk = th == "disabled"
                                    val cpuSetOk = topCpus.contains("4-7") || topCpus == "4,5,6,7"
                                    "FPS" + fps.ifEmpty { "?" } + (if (fpsOk) "✓" else "✗") +
                                        " 温控" + (if (thOk) "✓" else "✗") +
                                        " CPU" + (if (govOk) "✓" else "✗") +
                                        " 前台核" + topCpus.ifEmpty { "?" } + (if (cpuSetOk) "✓" else "✗")
                                } else {
                                    su("rm -f $path")
                                    su("for c in /sys/devices/system/cpu/cpu*/cpufreq; do [ -f \"\$c/scaling_governor\" ] && grep -q schedutil \"\$c/scaling_available_governors\" 2>/dev/null && echo schedutil > \"\$c/scaling_governor\" 2>/dev/null; done; settings delete system peak_refresh_rate 2>/dev/null; settings delete system min_refresh_rate 2>/dev/null; true")
                                    val gov = su("cat /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor 2>/dev/null").trim()
                                    val fps = su("settings get system peak_refresh_rate 2>/dev/null").trim()
                                    "已恢复 · " + (gov.ifEmpty { "?" }) +
                                        " · FPS" + (fps.ifEmpty { "默认" })
                                }
                            } catch (t: Throwable) {
                                "操作失败"
                            }
                        }
                        latencyMsg = msg
                    }
                }
                if (title == "放开通行") {
                    val enabling = on
                    scope2.launch {
                        withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su(c: String): String {
                                    val p = Runtime.getRuntime().exec(arrayOf("su", "-c", c))
                                    val out = p.inputStream.bufferedReader().readText()
                                    p.waitFor()
                                    return out
                                }
                                if (enabling) {
                                    // install a boot script so the state survives reboots
                                    try {
                                        val bootDir = "/data/adb/service.d"
                                        su("mkdir -p $bootDir")
                                        val boot = "#!/system/bin/sh\n" +
                                            "chmod 771 /data/adb\n" +
                                            "chmod 755 /data/adb/modules\n" +
                                            "chmod 755 /data/adb/ksu\n" +
                                            "chmod 755 /data/adb/ksud\n" +
                                            "printf 'allow untrusted_app adb_data_file dir { open read search getattr }\\n' > /data/local/tmp/xy_allow.txt\n" +
                                            "printf 'allow untrusted_app adb_data_file file { open read getattr }\\n' >> /data/local/tmp/xy_allow.txt\n" +
                                            "for c in /data/adb/ksud /data/adb/ksu/bin/ksud; do [ -x \"\$c\" ] && \"\$c\" sepolicy apply /data/local/tmp/xy_allow.txt; done\n"
                                        su("printf '%s' '" + boot.replace("'", "'\''") + "' > $bootDir/xy_open_access.sh ; chmod 755 $bootDir/xy_open_access.sh")
                                    } catch (t: Throwable) {
                                    }
                                    for ((path, mode) in listOf(
                                        "/data/adb" to "771",
                                        "/data/adb/modules" to "755",
                                        "/data/adb/ksu" to "755",
                                        "/data/adb/ksud" to "755"
                                    )) {
                                        su("chmod $mode '" + path.replace("'", "'\''") + "' 2>/dev/null; true")
                                    }
                                    val rules = "allow untrusted_app adb_data_file dir { open read search getattr }\n" +
                                        "allow untrusted_app adb_data_file file { open read getattr }\n" +
                                        "allow untrusted_app adb_data_file lnk_file read\n" +
                                        "allow untrusted_app proc_modules file { open read getattr }\n"
                                    val rf = "/data/local/tmp/sepolicy_rules.txt"
                                    su("printf '%s' '" + rules.replace("'", "'\''") + "' > $rf ; chmod 644 $rf ; true")
                                    val ks = su("for c in /data/adb/ksud /data/adb/ksu/bin/ksud; do [ -x \"\$c\" ] && echo \"\$c\" && break; done 2>/dev/null").trim()
                                    if (ks.isNotEmpty()) {
                                        su("'" + ks + "' sepolicy apply $rf 2>&1; true")
                                    }
                                } else {
                                    try {
                                        su("rm -f /data/adb/service.d/xy_open_access.sh")
                                    } catch (t: Throwable) {
                                    }
                                    for (path in listOf(
                                        "/data/adb", "/data/adb/modules",
                                        "/data/adb/ksu", "/data/adb/ksud"
                                    )) {
                                        su("chmod 700 '" + path.replace("'", "'\''") + "' 2>/dev/null; true")
                                    }
                                    val rules = "deny untrusted_app adb_data_file dir { open read search getattr }\n" +
                                        "deny untrusted_app adb_data_file file { open read getattr }\n" +
                                        "deny untrusted_app adb_data_file lnk_file read\n" +
                                        "deny untrusted_app proc_modules file { open read getattr }\n"
                                    val rf = "/data/local/tmp/sepolicy_deny.txt"
                                    su("printf '%s' '" + rules.replace("'", "'\''") + "' > $rf ; chmod 644 $rf ; true")
                                    val ks = su("for c in /data/adb/ksud /data/adb/ksu/bin/ksud; do [ -x \"\$c\" ] && echo \"\$c\" && break; done 2>/dev/null").trim()
                                    if (ks.isNotEmpty()) {
                                        su("'" + ks + "' sepolicy apply $rf 2>&1; true")
                                    }
                                }
                            } catch (t: Throwable) {
                            }
                        }
                        kotlinx.coroutines.delay(600)
                        refreshLatency()
                    }
                }
            }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = subtitle ?: latencyMsg
                    ?: (if (title == "执行此文件") (execPath ?: "暂无设置") else ""),
                color = Color(0x66FFFFFF),
                fontSize = 9.sp,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .then(
                        if (title == "执行此文件") {
                            Modifier.clickable {
                                pathInput = execPath ?: ""
                                showPathDialog = true
                            }
                        } else Modifier
                    )
            )
        }
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
    if (showPathDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPathDialog = false },
            title = { androidx.compose.material3.Text("执行文件路径") },
            text = {
                androidx.compose.foundation.text.BasicTextField(
                    value = pathInput,
                    onValueChange = { pathInput = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val v = pathInput.trim()
                    ctx3.getSharedPreferences("yx_exec", 0).edit().putString("path", v).apply()
                    execPath = v
                    showPathDialog = false
                }) { androidx.compose.material3.Text("确定") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showPathDialog = false }) {
                    androidx.compose.material3.Text("取消")
                }
            }
        )
    }
    if (showKeyDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showKeyDialog = false },
            title = { androidx.compose.material3.Text("so 密钥") },
            text = {
                androidx.compose.foundation.text.BasicTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val p2 = ctx3.getSharedPreferences("yx_exec", 0).getString("path", null)
                    showKeyDialog = false
                    if (!p2.isNullOrBlank()) {
                        latencyMsg = "执行中…"
                        scope2.launch {
                            val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val pr = Runtime.getRuntime().exec(arrayOf("su", "-c",
                                        "export HOME=/data/local/tmp; export TMPDIR=/data/local/tmp; " +
                                            "cd '" + java.io.File(p2).parent + "' 2>/dev/null; " +
                                            "chmod 755 '" + p2.replace("'", "'\\''") + "' 2>/dev/null; " +
                                            "'" + p2.replace("'", "'\\''") + "' -k '" + keyInput.replace("'", "'\\''") + "' 2>&1 | tail -n 2"))
                                    val o = pr.inputStream.bufferedReader().readText()
                                    pr.waitFor()
                                    if (o.contains("fail") || o.contains("error")) "执行失败" else "已执行 so"
                                } catch (t: Throwable) {
                                    "执行异常"
                                }
                            }
                            latencyMsg = msg
                        }
                    }
                }) { androidx.compose.material3.Text("执行") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showKeyDialog = false }) {
                    androidx.compose.material3.Text("取消")
                }
            }
        )
    }
}

@Composable
private fun BrowserPane(
    path: String,
    onNavigate: (String) -> Unit,
    onFileClick: (String) -> Unit,
    refreshTick: Int = 0,
    selectedPath: String? = null,
    highlightName: String? = null,
    onLongPress: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var entries by remember { mutableStateOf<List<FsEntry>>(emptyList()) }

    LaunchedEffect(path, refreshTick) {
        entries = listDir(path)
    }

    val parent = parentOf(path)
    val listState = rememberLazyListState()
    LaunchedEffect(highlightName, entries) {
        val idx = entries.indexOfFirst { it.name == highlightName }
        if (idx >= 0) {
            listState.animateScrollToItem(idx + 1)
        }
    }

        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize().background(Color(BG_COLOR)),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
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
                highlight = highlightName,
                onClick = {
                    if (entry.isDir) onNavigate(joinPath(path, entry.name)) else onFileClick(joinPath(path, entry.name))
                },
                onLongClick = { onLongPress(rowPath) }
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
    highlight: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    // ZySU row: rounded 14dp, no tile behind the glyph, 12sp monospace, outline on the row
    val accent = if (isDir) null else fileAccent(name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(
                if (highlight != null && highlight == name) Color(0x3300BCD4)
                else Color.Transparent
            )
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

private fun shq(s: String): String = "'" + s.replace("'", "'\''") + "'"

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
