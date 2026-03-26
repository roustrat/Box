package com.example.box.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import kotlin.math.roundToInt

/* Функция масштабирования с консервативной оценкой
* Функция проверяет размер экрана и уменьшает изображение
* до этого размера. Виджет ImageView, в который загружается
* изображение, всегда меньше размера экрана,
* так что эта оценка весьма консервативна.
*/
fun getScaledBitmap(path: String, activity: Activity): Bitmap {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)
    return getScaledBitmap(path, size.x, size.y)
}

// Функция обработки фото для ручного масштабирования изображения
fun getScaledBitmap(path: String?, destWidth: Int, destHeight: Int): Bitmap {
    // Чтение размеров изображения на диске
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()
    // Выясняем, на сколько нужно уменьшить
    var inSampleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth
        val sampleScale = if (heightScale > widthScale) {
            heightScale
        } else {
            widthScale
        }
        inSampleSize = sampleScale.roundToInt()
    }
    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize
    // Чтение и создание окончательного растрового изображения
    return BitmapFactory.decodeFile(path, options)
}