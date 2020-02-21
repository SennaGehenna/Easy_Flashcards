package io.github.tormundsmember.easyflashcards.ui.util

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.AnimationListener
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


operator fun <T> MutableLiveData<T>.plusAssign(value: T?) {
    postValue(value)
}


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

@Suppress("UNUSED_PARAMETER")
fun View.invisible(animate: Boolean = false) {
    visibility = View.INVISIBLE
}


fun View.animateVisible() {
    if (visibility != View.VISIBLE) {
        alpha = 0F
        visible()
        animate()
            .alpha(1F)
            .setDuration(300)
            .resetListener()
            .start()
    }
}

@Suppress("unused")
fun View.animateInvisible() {
    if (visibility != View.INVISIBLE) {
        alpha = 1F
        animate()
            .alpha(0F)
            .setDuration(300)
            .setListener(
                onAnimationEnd = {
                    invisible()
                }
            )
            .start()
    }
}

fun View.animateGone() {
    if (visibility != View.GONE) {
        alpha = 1F
        animate()
            .alpha(0F)
            .setDuration(300)
            .setListener(
                onAnimationEnd = {
                    gone()
                }
            )
            .start()
    }
}

fun String.isNotEmptyOrBlank() = isNotEmpty() && isNotBlank()


fun EditText.putCursorInTextview(selectAll: Boolean = false) {
    post {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        requestFocusFromTouch()

        if (selectAll) {
            setSelection(0, text.length)
        } else {
            setSelection(text.length)
        }

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

fun openUrlInCustomTabs(context: Context, data: Uri) {
    val intent = CustomTabsIntent.Builder()
        .setToolbarColor(ContextCompat.getColor(context, R.color.colorAccent))
        .build()

    if (intent.intent.resolveActivity(context.packageManager) != null) {
        intent.launchUrl(context, data)
    } else {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = data
            context.startActivity(i)
        } catch (e: Exception) {
            context.showGeneralErrorMessage()
        }
    }
}

fun Context.showGeneralErrorMessage() {
    Toast.makeText(this, R.string.generalError, Toast.LENGTH_SHORT).show()
}


fun CharSequence.prepareLinkText(context: Context): SpannableStringBuilder {
    fun makeLinkClickable(context: Context, strBuilder: SpannableStringBuilder, span: URLSpan) {
        val start = strBuilder.getSpanStart(span)
        val end = strBuilder.getSpanEnd(span)
        val flags = strBuilder.getSpanFlags(span)

        val clickable = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                openUrlInCustomTabs(context, Uri.parse(span.url))
            }
        }

        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    @Suppress("DEPRECATION") //what's the alternative? the other ones require SDK 28
    val seq = Html.fromHtml(this.toString())
    val stringBuilder = SpannableStringBuilder(seq)
    val urls = stringBuilder.getSpans(0, seq.length, URLSpan::class.java)

    urls.forEach {
        makeLinkClickable(context, stringBuilder, it)
    }

    return stringBuilder
}


fun Activity.hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/*
 * you could just use a custom getter, which functions the same way. this is simply a delegate to emulate DI-methods
 */
@Suppress("ClassName")//this is a delegated property, they're all lowercase
class factory<T>(val factory: () -> T) : ReadOnlyProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return factory()
    }

}

typealias Action = () -> Unit

fun ViewPropertyAnimator.resetListener(): ViewPropertyAnimator = setListener(null)

fun ViewPropertyAnimator.setListener(
    onAnimationRepeat: Action = {},
    onAnimationCancel: Action = {},
    onAnimationStart: Action = {},
    onAnimationEnd: Action = {}

): ViewPropertyAnimator = setListener(object : AnimationListener() {
    override fun onAnimationRepeat(animation: Animator?) {
        onAnimationRepeat()
    }

    override fun onAnimationCancel(animation: Animator?) {
        onAnimationCancel()
    }

    override fun onAnimationStart(animation: Animator?) {
        onAnimationStart()
    }

    override fun onAnimationEnd(animation: Animator?) {
        onAnimationEnd()
    }
})

fun View.width(function: (Int) -> Boolean) {
    if (width == 0)
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                function(width)
            }
        })
    else function(width)
}