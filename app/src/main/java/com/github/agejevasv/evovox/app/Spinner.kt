package com.github.agejevasv.evovox.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner

class Spinner : Spinner, OnItemSelectedListener {
    var listener: OnItemSelectedListener? = null
    private var isUserAction = true

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        listener?.onItemSelected(parent, view, position, id, isUserAction)
        isUserAction = true
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        listener?.onNothingSelected(parent)
    }

    interface OnItemSelectedListener {
        fun onNothingSelected(parent: AdapterView<*>?)
        fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long, userSelected: Boolean)
    }

    fun setPosition(pos: Int, animate: Boolean, isUserAction: Boolean) {
        this.isUserAction = isUserAction
        setSelection(pos, animate)
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        this.listener = listener
    }

    constructor(context: Context?) : super(context) {
        super.setOnItemSelectedListener(this)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        super.setOnItemSelectedListener(this)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        super.setOnItemSelectedListener(this)
    }
}