/*
* Copyright (C) 2020 Stanislav Georgiev
* https://github.com/slaviboy
*
*  NOTICE:  All information contained herein is, and remains the property
*  of Stanislav Georgiev and its suppliers, if any. The intellectual and
*  technical concepts contained herein are proprietary to Stanislav Georgiev
*  and its suppliers and may be covered by U.S. and Foreign Patents, patents
*  in process, and are protected by trade secret or copyright law. Dissemination
*  of this information or reproduction of this material is strictly forbidden
*  unless prior written permission is obtained from Stanislav Georgiev.
*/
package com.slaviboy.toolbar

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Simple class for generating drag shadow, this is the image object that
 * will be displayed as a shadow of the element that is drag from the user.
 *
 * @param element the ImageButton view that is being drag
 * @param backgroundColor the background color for the shadow image
 * @param iconColor the icon color for the shadow image
 * @param previousIconColor the icon color for the shadow image, before the changed used to restore the color for the drawable
 * @param cornerRadius array with corner radii for all four corners of the shadow image
 * @param alpha the opacity of the shadow image
 */
class DragShadowBuilder(
    var element: ToolbarElement,
    var backgroundColor: Int = Color.GRAY,
    var iconColor: Int = Color.WHITE,
    var previousIconColor: Int = Color.WHITE,
    var cornerRadius: FloatArray = floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f),
    var alpha: Float = 0.8f
) : View.DragShadowBuilder(element) {

    // bitmap icon for the shadow image
    val bitmap = drawableToBitmap((element as ImageButton).drawable)
    val path = Path()

    val paint = Paint().apply {
        isAntiAlias = true
        color = this@DragShadowBuilder.iconColor
        alpha = (this@DragShadowBuilder.alpha * 255).toInt()
    }

    /**
     * Convert the drawable to bitmap
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        bitmap = if (element.width <= 0 || element.height <= 0) {
            // single color bitmap will be created of 1x1 pixel
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )
        } else {
            Bitmap.createBitmap(element.width, element.height, Bitmap.Config.ARGB_8888)
        }

        val drawableWrap = DrawableCompat.wrap(drawable)
        val canvas = Canvas(bitmap)
        val previousBound = Rect(drawable.copyBounds())

        // change drawable color and bound
        DrawableCompat.setTint(drawableWrap, iconColor)
        drawableWrap.setBounds(
            element.paddingLeft,
            element.paddingTop,
            element.width - element.paddingRight,
            element.height - element.paddingBottom
        )
        drawableWrap.draw(canvas)

        // restore bound and color for the drawable
        drawable.bounds = previousBound
        DrawableCompat.setTint(drawable, previousIconColor)

        return bitmap
    }

    /**
     * Make the shadow image centered around the finger.
     */
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        size.set(element.width, element.height)
        touch.set(element.width / 2, element.height / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {

        path.reset()
        path.addRoundRect(
            RectF(0f, 0f, element.width * 1f, element.height * 1f),
            cornerRadius, Path.Direction.CW
        )
        // draw the background color for the image shadow
        paint.color = backgroundColor
        paint.alpha = (alpha * 255).toInt()
        canvas.drawPath(path, paint)

        // draw the icon for the image shadow
        paint.color = iconColor
        paint.alpha = (alpha * 255).toInt()
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }
}