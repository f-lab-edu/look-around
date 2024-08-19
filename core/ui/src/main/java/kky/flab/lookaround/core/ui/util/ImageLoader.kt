package kky.flab.lookaround.core.ui.util

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImageLoader {
    private val cached: LruCache<String, Bitmap>
    private val ioScope = CoroutineScope(Dispatchers.IO)

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

        ioScope.launch {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap =
                ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
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

            cached.put(uri.toString().substringAfterLast("/"), bitmap)

            withContext(Dispatchers.Main) {
                this@loadUri.setImageBitmap(bitmap)
            }
        }
    }

    private fun getBitmapCached(key: String): Bitmap? = cached.get(key)
}