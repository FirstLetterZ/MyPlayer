package com.zpf.myplayer.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
open class VideoBaseInfo(
    var name: String?,
    var path: String?,
    var duration: Long = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(path)
        parcel.writeLong(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "{name=$name, path=$path, duration=$duration}"
    }

    companion object CREATOR : Parcelable.Creator<VideoBaseInfo> {
        override fun createFromParcel(parcel: Parcel): VideoBaseInfo {
            return VideoBaseInfo(parcel)
        }

        override fun newArray(size: Int): Array<VideoBaseInfo?> {
            return arrayOfNulls(size)
        }
    }

}