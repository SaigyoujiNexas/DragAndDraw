package com.saigyouji.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.math.PI

private const val TAG = "BoxDrawingView"
private const val INVALID_POINTER_ID = -1
class BoxDrawingView(context: Context, attrs: AttributeSet? = null): View(context, attrs){

    private var currentBox: Box? = null
    private val boxen = mutableListOf<Box>()
    private var currentLine: Line? = null
    private var angle: Double = 0.0
    private var prevAngle = 0.0
    private val boxPaint = Paint().apply {
        color = 0x22ff0000
    }
    private val backgroundPaint = Paint().apply {
        color = Color.argb(0xff, 0xf8, 0xef, 0xe0)
    }
    private var ptrID1: Int = INVALID_POINTER_ID
    private var ptrID2: Int = INVALID_POINTER_ID
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        when(event.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                ptrID1 = event.getPointerId(event.actionIndex)
                currentBox = Box(current).also {
                    boxen.add(it)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN->{
                boxen.removeAt(boxen.size - 1)
                currentBox = null
                ptrID2 = event.getPointerId(event.actionIndex)
                val p1 = PointF(
                    event.getX(event.findPointerIndex(ptrID1)),
                event.getY(event.findPointerIndex(ptrID1)))
                val p2 = PointF(
                    event.getX(event.findPointerIndex(ptrID2)),
                    event.getY(event.findPointerIndex(ptrID2)))
                currentLine = Line(p1, p2)
            }
            MotionEvent.ACTION_MOVE ->{
                if(ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                    val p1 = PointF(
                        event.getX(event.findPointerIndex(ptrID1)),
                        event.getY(event.findPointerIndex(ptrID1)))
                    val p2 = PointF(
                        event.getX(event.findPointerIndex(ptrID2)),
                        event.getY(event.findPointerIndex(ptrID2)))
                    val newLine = Line(p1, p2)
                    angle = currentLine!!.computeAngle(newLine)
                    updateCanvas()
                }
                else if(event.pointerCount == 1) {
                    action = "ACTION_MOVE"
                    updateCurrentBox(current)
                }
            }
            MotionEvent.ACTION_UP->{
                action = "ACTION_UP"
                ptrID1 = INVALID_POINTER_ID
                updateCurrentBox(current)
                currentBox = null

                currentLine = null
            }
            MotionEvent.ACTION_POINTER_UP->{
                ptrID2 = INVALID_POINTER_ID

                prevAngle += angle % 360
            }
            MotionEvent.ACTION_CANCEL->{
                ptrID1 = INVALID_POINTER_ID
                ptrID2 = INVALID_POINTER_ID
                currentLine = null
                action = "ACTION_CANCEL"
                currentBox = null
            }
        }
        Log.i(TAG, "$action at x = ${current.x}, y = ${current.y}")
        return true
    }

    private fun updateCurrentBox(current: PointF)
    {
        currentBox?.let{
            it.end = current
            invalidate()
        }
    }
    private fun updateCanvas()
    {
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)
        currentLine?.let {
            canvas.rotate((prevAngle + angle).toFloat(),
                (currentLine!!.p1.x + currentLine!!.p2.x) / 2.0f,
                (currentLine!!.p1.y + currentLine!!.p2.y) / 2.0f)
        }
            boxen.forEach{box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
        }
        Log.i(TAG, "computed degree: ${(angle/ PI * 180).toFloat()}")
    }

    override fun onSaveInstanceState(): Parcelable? {
        Log.e(TAG, "onSaveInstanceState: save state")
        return Bundle().apply {
            putParcelable("superState", super.onSaveInstanceState())
            putParcelableArray(TAG, boxen.toTypedArray())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var superState: Parcelable? = state
        if(state is Bundle){
            state.apply {
                getParcelableArray(TAG)?.forEach {
                    boxen.add(it as Box)
                }
                superState = state.getParcelable("superState")
            }
        }
        super.onRestoreInstanceState(superState)

    }
    companion object{

    }
}