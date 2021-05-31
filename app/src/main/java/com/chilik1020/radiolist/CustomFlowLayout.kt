package com.chilik1020.radiolist

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat

open class CustomFlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    var orientation = Orientation.VERTICAL
    protected var lineSpacing = 0
    protected var itemSpacing = 0
    var singleLine = false
    private var rowCount = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val maxWidth =
            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) width else Int.MAX_VALUE

        var childLeft = paddingLeft
        var childTop = paddingTop
        var childBottom = childTop
        var childRight = childLeft
        var maxChildRight = 0
        val maxRight = maxWidth - paddingRight

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams
            var leftMargin = 0
            var rightMargin = 0
            if (lp is MarginLayoutParams) {
                val marginLp = lp
                leftMargin += marginLp.leftMargin
                rightMargin += marginLp.rightMargin
            }
            childRight = childLeft + leftMargin + child.measuredWidth

            // If the current child's right bound exceeds Flowlayout's max right bound and flowlayout is
            // not confined to a single line, move this child to the next line and reset its left bound to
            // flowlayout's left bound.
            if (childRight > maxRight && !singleLine) {
                childLeft = paddingLeft
                childTop = childBottom + lineSpacing
            }
            childRight = childLeft + leftMargin + child.measuredWidth
            childBottom = childTop + child.measuredHeight

            // Updates Flowlayout's max right bound if current child's right bound exceeds it.
            if (childRight > maxChildRight) {
                maxChildRight = childRight
            }
            childLeft += leftMargin + rightMargin + child.measuredWidth + itemSpacing

            // For all preceding children, the child's right margin is taken into account in the next
            // child's left bound (childLeft). However, childLeft is ignored after the last child so the
            // last child's right margin needs to be explicitly added to Flowlayout's max right bound.
            if (i == childCount - 1) {
                maxChildRight += rightMargin
            }
        }

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val width = child.measuredWidth
            Log.d("___)", "Child $i : $width")
        }

        maxChildRight += paddingRight
        childBottom += paddingBottom
        val finalWidth = getMeasuredDimension(width, widthMode, maxChildRight)
        val finalHeight = getMeasuredDimension(height, heightMode, childBottom)
        setMeasuredDimension(finalWidth, finalHeight)
    }

    private fun getMeasuredDimension(size: Int, mode: Int, childrenEdge: Int): Int {
        return when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> Math.min(childrenEdge, size)
            else -> childrenEdge
        }
    }

    override fun onLayout(sizeChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            // Do not re-layout when there are no children.
            rowCount = 0
            return
        }
        rowCount = 1
        val isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        val paddingStart = if (isRtl) paddingRight else paddingLeft
        val paddingEnd = if (isRtl) paddingLeft else paddingRight
        var childStart = paddingStart
        var childTop = paddingTop
        var childBottom = childTop
        var childEnd: Int
        val maxChildEnd = right - left - paddingEnd
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                child.setTag(R.id.row_index_key, -1)
                continue
            }
            val lp = child.layoutParams
            var startMargin = 0
            var endMargin = 0
            if (lp is MarginLayoutParams) {
                val marginLp = lp
                startMargin = MarginLayoutParamsCompat.getMarginStart(marginLp)
                endMargin = MarginLayoutParamsCompat.getMarginEnd(marginLp)
            }
            childEnd = childStart + startMargin + child.measuredWidth
            if (!singleLine && childEnd > maxChildEnd) {
                childStart = paddingStart
                childTop = childBottom + lineSpacing
                rowCount++
            }
            child.setTag(R.id.row_index_key, rowCount - 1)
            childEnd = childStart + startMargin + child.measuredWidth
            childBottom = childTop + child.measuredHeight
            if (isRtl) {
                child.layout(
                    maxChildEnd - childEnd,
                    childTop,
                    maxChildEnd - childStart - startMargin,
                    childBottom
                )
            } else {
                child.layout(childStart + startMargin, childTop, childEnd, childBottom)
            }
            childStart += startMargin + endMargin + child.measuredWidth + itemSpacing
        }
        Log.d("___)", "onLAYOUT")
    }

    protected fun getRowCount(): Int {
        return rowCount
    }

    /** Gets the row index of the child, primarily for accessibility.    */
    fun getRowIndex(child: View): Int {
        val index = child.getTag(R.id.row_index_key)
        return if (index !is Int) {
            -1
        } else index
    }

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }
}