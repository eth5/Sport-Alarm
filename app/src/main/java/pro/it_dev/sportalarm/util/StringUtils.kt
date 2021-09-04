package pro.it_dev.sportalarm.util

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.compose.ui.graphics.Color

fun String.fromHtml():Spanned =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    else
        Html.fromHtml(this)

fun String.convertToColorInt() = android.graphics.Color.parseColor("#$this")
fun String.convertToColor() = Color(android.graphics.Color.parseColor("#$this"))
