package com.chilik1020.radiolist

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import kotlin.math.max
import kotlin.math.min

open class AutoAlignedGridLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    var orientation = Orientation.VERTICAL
    var lineSpacing = 0
    var itemSpacing = 0

    private var rowNumber = 0
    private var columnNumber = 0

    private var maxChildWidth = 0
    private var maxChildHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val maxWidth =
            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) width else Int.MAX_VALUE

        val parentWidth = maxWidth - paddingRight - paddingLeft

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams
            var leftMargin = 0
            var rightMargin = 0
            var topMargin = 0
            var bottomMargin = 0

            if (lp is MarginLayoutParams) {
                leftMargin += lp.leftMargin
                rightMargin += lp.rightMargin
                topMargin += lp.topMargin
                bottomMargin += lp.bottomMargin
            }
            val width = leftMargin + rightMargin + child.measuredWidth + itemSpacing
            val height = topMargin + bottomMargin + child.measuredHeight + lineSpacing
            maxChildWidth = max(maxChildWidth, width)
            maxChildHeight = max(maxChildHeight, height)
        }

        calcRowsNumber(parentWidth, maxChildWidth)
        calcColumnNumber()

        val parentHeight = paddingTop + maxChildHeight * rowNumber + paddingBottom
        val finalWidth = getMeasuredDimension(width, widthMode, parentWidth)
        val finalHeight = getMeasuredDimension(height, heightMode, parentHeight)
        setMeasuredDimension(finalWidth, finalHeight)
    }

    private fun calcRowsNumber(parentWidth: Int, childWidth: Int) {
        if (orientation == Orientation.VERTICAL) {
            rowNumber = childCount
            return
        }
        val childrenFittedInParent = parentWidth / childWidth
        rowNumber = if (childrenFittedInParent > childCount) {
            1
        } else {
            (childCount + childrenFittedInParent - 1) / childrenFittedInParent
        }
    }

    private fun calcColumnNumber() {
        columnNumber = (childCount + rowNumber - 1) / rowNumber
    }

    private fun getMeasuredDimension(size: Int, mode: Int, childrenEdge: Int): Int {
        return when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> min(childrenEdge, size)
            else -> childrenEdge
        }
    }

    override fun onLayout(sizeChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            // Do not re-layout when there are no children.
            return
        }
        val isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        val paddingStart = if (isRtl) paddingRight else paddingLeft
        val paddingEnd = if (isRtl) paddingLeft else paddingRight
        var childStart = paddingStart
        var childTop = paddingTop
        val maxChildEnd = right - left - paddingEnd

        val iterator = children.iterator()
        for (row in 0 until rowNumber) {
            for (column in 0 until columnNumber) {
                val child = if (iterator.hasNext()) iterator.next() else null
                if (child == null || child.visibility == GONE) {
                    continue
                }

                val lp = child.layoutParams
                var startMargin = 0
                if (lp is MarginLayoutParams) {
                    startMargin = MarginLayoutParamsCompat.getMarginStart(lp)
                }
                val startPos = childStart + maxChildWidth * column
                val endPos = startPos + startMargin + child.measuredWidth
                val topPos = childTop + maxChildHeight * row
                val bottomPos = topPos + child.measuredHeight

                if (isRtl) {
                    child.layout(
                        maxChildEnd - endPos,
                        topPos,
                        maxChildEnd - startPos - startMargin,
                        bottomPos
                    )
                } else {
                    child.layout(startPos, topPos, endPos, bottomPos)
                }
            }
        }
    }

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }
}