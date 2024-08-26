package kky.flab.lookaround.core.ui.util

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.LruCache
import android.widget.ImageView
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImageLoader {
    private val cached: LruCache<String, Bitmap>

    init {
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize = (maxMemory / 8).toInt()
        cached = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    fun ImageView.loadUri(
        uri: Uri,
        maxWidth: Int = 1000,
        maxHeight: Int = 1000,
    ) {
        val key = uri.toString().substringAfterLast("/")
        val cachedBitmap = getBitmapCached(key)

        if (cachedBitmap != null) {
            this.setImageBitmap(cachedBitmap)
            return
        }

        doOnAttach {
            val lifecycleOwner = findViewTreeLifecycleOwner() ?: throw IllegalStateException("View가 attach 되지 않았습니다.")
            val imageView = it as ImageView

            lifecycleOwner.lifecycleScope.launch {
                val context = it.context
                val bitmap = withContext(Dispatchers.IO) {
                    decodeBitmap(
                        source = ImageDecoder.createSource(context.contentResolver, uri),
                        maxWidth = maxWidth,
                        maxHeight = maxHeight,
                    )
                }

                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun decodeBitmap(source: ImageDecoder.Source, maxWidth: Int, maxHeight: Int): Bitmap {
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val (originalWidth, originalHeight) = info.size.width to info.size.height

            if (originalWidth > maxWidth || originalHeight > maxHeight) {
                val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

                if (originalWidth > originalHeight) {
                    val scaledHeight = (maxWidth / aspectRatio).toInt()
                    decoder.setTargetSize(maxWidth, scaledHeight)
                } else {
                    val scaledWidth = (maxHeight * aspectRatio).toInt()
                    decoder.setTargetSize(scaledWidth, maxHeight)
                }
            }
        }
    }

    private fun getBitmapCached(key: String): Bitmap? = cached.get(key)
}