package com.ksolutions.whatNeed.models

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    val id: String = "",
    val name: String = "",
    val mobile: String = "",
    val email: String = "",
    val dob: String = "",
    val image: String = "",
    val fcmToken: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(mobile)
        writeString(email)
        writeString(dob)
        writeString(image)
        writeString(fcmToken)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserModel> = object : Parcelable.Creator<UserModel> {
            override fun createFromParcel(source: Parcel): UserModel = UserModel(source)
            override fun newArray(size: Int): Array<UserModel?> = arrayOfNulls(size)
        }
    }
}