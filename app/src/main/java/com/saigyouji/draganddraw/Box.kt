package com.saigyouji.draganddraw

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable
import kotlin.math.max
import kotlin.math.min

class Box(val start: PointF): Parcelable{

    var end: PointF = start

    val left: Float
    get() = min(start.x, end.x)

    val right:Float
    get() = max(start.x, end.x)

    val top: Float
    get() = min(start.y, end.y)

    val bottom:Float
    get() = max(start.y, end.y)

    constructor(parcel: Parcel) : this((parcel.readParcelable(PointF::class.java.classLoader))!!) {
        end = parcel.readParcelable(PointF::class.java.classLoader)!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(start, 0)
        dest.writeParcelable(end, 0)
    }

    companion object CREATOR : Parcelable.Creator<Box> {
        override fun createFromParcel(parcel: Parcel): Box {
            return Box(parcel)
        }

        override fun newArray(size: Int): Array<Box?> {
            return arrayOfNulls(size)
        }
    }

}