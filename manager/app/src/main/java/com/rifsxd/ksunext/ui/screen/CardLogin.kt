package com.rifsxd.ksunext.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Weiyan card verification, ported verbatim from the original manager: the parameters travel
 * RC4(hex) encrypted inside data=, the signature is md5 over the sorted params plus APPKEY,
 * and the device code is the Android ID.
 */
object WeiYan {
    private const val URL_BASE = "https://wy.llua.cn/api/?id=kmlogon&app=85515"
    private const val APPKEY = "58JPxzs8FSaGKDx"
    private const val RC4KEY = "MzTjmyDEjedadB5"
    private const val OKCODE = "50953120"

    private fun md5(s: String): String {
        val md = java.security.MessageDigest.getInstance("MD5")
        return md.digest(s.toByteArray()).joinToString("") { String.format("%02x", it) }
    }

    private fun rc4(data: ByteArray, key: String): ByteArray {
        val k = key.toByteArray()
        val s = IntArray(256) { it }
        var j = 0
        for (i in 0 until 256) {
            j = (j + s[i] + (k[i % k.size].toInt() and 0xff)) and 0xff
            val t = s[i]; s[i] = s[j]; s[j] = t
        }
        val out = ByteArray(data.size)
        var x = 0
        var y = 0
        for (i in data.indices) {
            x = (x + 1) and 0xff
            y = (y + s[x]) and 0xff
            val t = s[x]; s[x] = s[y]; s[y] = t
            out[i] = (data[i].toInt() xor s[(s[x] + s[y]) and 0xff]).toByte()
        }
        return out
    }

    private fun hex(b: ByteArray) = b.joinToString("") { String.format("%02x", it) }

    private fun unhex(s: String): ByteArray {
        val t = if (s.length % 2 == 1) "0" + s else s
        return ByteArray(t.length / 2) { t.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
    }

    /** null on success, otherwise the reason to display. */
    fun verify(ctx: android.content.Context, kami: String): String? {
        var last = "无法连接验证服务器"
        for (attempt in 0 until 3) {
            val outcome = try {
                val markcode = android.provider.Settings.System.getString(
                    ctx.contentResolver, android.provider.Settings.System.ANDROID_ID
                ) ?: ""
                val t = System.currentTimeMillis()
                val value = 1 + Math.random() * 10 + t
                val sign = md5("kami=" + kami + "&markcode=" + markcode + "&t=" + t + "&" + APPKEY)
                val plain = "kami=" + kami + "&markcode=" + markcode + "&t=" + t + "&sign=" + sign + "&value=" + value
                val url = URL_BASE + "&data=" + hex(rc4(plain.toByteArray(Charsets.UTF_8), RC4KEY))
                val conn = (java.net.URL(url).openConnection() as java.net.HttpURLConnection)
                conn.requestMethod = "GET"
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                val body = conn.inputStream.bufferedReader().readText().trim()
                val text = try {
                    String(rc4(unhex(body), RC4KEY), Charsets.UTF_8)
                } catch (e: Throwable) {
                    body
                }
                val json = org.json.JSONObject(text)
                if (json.optString("code") == OKCODE) {
                    return null
                }
                val msg = json.optString("msg")
                val definite = msg.contains("卡密") || msg.contains("不存在") ||
                    msg.contains("过期") || msg.contains("使用")
                if (definite) {
                    return msg.ifEmpty { "无效密钥" }
                }
                msg.ifEmpty { "服务器繁忙，请重试" }
            } catch (e: Throwable) {
                "无法连接验证服务器"
            }
            last = outcome
            if (attempt < 2) {
                try {
                    Thread.sleep(400L * (attempt + 1))
                } catch (ignored: InterruptedException) {
                }
            }
        }
        return last
    }
}

@Composable
fun CardLoginScreen(onSuccess: () -> Unit) {
    var key by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF101014))) {
        // blurred app icon as the backdrop
        Image(
            painter = painterResource(id = com.rifsxd.ksunext.R.mipmap.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(60.dp)
        )
        Box(modifier = Modifier.fillMaxSize().background(Color(0x99000000)))

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = com.rifsxd.ksunext.R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(128.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = key,
                    onValueChange = { key = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xCC1C1C1E), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = if (busy) "…" else "进入",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable(enabled = !busy) {
                            val k = key.trim()
                            if (k.isEmpty()) {
                                error = "无效密钥"
                            } else {
                                busy = true
                                error = null
                                scope.launch {
                                    val reason = withContext(Dispatchers.IO) { WeiYan.verify(ctx, k) }
                                    busy = false
                                    if (reason == null) {
                                        ctx.getSharedPreferences("yx_login", 0)
                                            .edit().putString("key", k).apply()
                                        onSuccess()
                                    } else {
                                        error = reason
                                    }
                                }
                            }
                        }
                        .background(Color(0xFF0A84FF), RoundedCornerShape(percent = 50))
                        .padding(horizontal = 18.dp, vertical = 11.dp),
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error!!, color = Color(0xFFFF453A), fontSize = 12.sp)
            }
        }
    }
}
