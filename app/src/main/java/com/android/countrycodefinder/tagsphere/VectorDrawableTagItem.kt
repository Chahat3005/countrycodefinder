package com.android.countrycodefinder.tagsphere

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextPaint
import com.magicgoop.tagsphere.item.TagItem
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class VectorDrawableTagItem(drawable: Drawable) : TagItem() {
    private val targetSize = 40
    private val scaledDrawable: Drawable
    init {
        scaledDrawable = drawable.mutate()
        scaledDrawable.setBounds(0, 0, targetSize, targetSize)
    }
    override fun drawSelf(
        x: Float, y: Float, canvas: Canvas, paint: TextPaint, easingFunction: ((t: Float) -> Float)?
    ) {
        canvas.save()
        canvas.translate(x - targetSize / 2f, y - targetSize / 2f)

        val alpha = easingFunction?.let { calc ->
            val ease = calc(getEasingValue())
            if (!ease.isNaN()) max(0, min(255, (255 * ease).roundToInt())) else 0
        } ?: 255
        scaledDrawable.alpha = alpha
        scaledDrawable.draw(canvas)
        canvas.restore()
    }
}