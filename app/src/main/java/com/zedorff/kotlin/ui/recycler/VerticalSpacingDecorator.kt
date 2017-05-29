package com.zedorff.kotlin.ui.recycler

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


class VerticalSpacingDecorator(private var verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        outRect.bottom = verticalSpaceHeight
    }
}