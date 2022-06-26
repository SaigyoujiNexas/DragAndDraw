package com.saigyouji.draganddraw

import android.graphics.PointF
import kotlin.math.atan
import kotlin.math.atan2

class Line(val p1: PointF, val p2: PointF){
    private val angle:Float
    get() = atan2(p2.y - p1.y, p2.x - p1.x)
    fun computeAngle(other: Line): Double{
        var angle =  Math.toDegrees((other.angle - this.angle).toDouble()) % 360
        if(angle < -180.0) angle += 360.0
        if(angle > 180.0) angle -= 360.0
        return angle
    }
}