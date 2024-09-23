package com.hung.musicstreamingapplication.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Song(
    val authorIDs: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val genreIDs: List<String> = emptyList(),
    val imageUrl: String = "",
    val play_in_week: Int = 0,
    val play_in_month: Int = 0,
    val link: String = "",
    var id: String = "",
    val name: String = "",
    val duration: Float = 0.0f,
    val status: Boolean = false,
    var authorName: String? = "",
    val playcount: Int = 0,
    var albumID: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList() ?: emptyList(),  // authorIDs
        Timestamp(parcel.readLong(), 0),               // createdAt
        parcel.createStringArrayList() ?: emptyList(), // genreIDs
        parcel.readString() ?: "",                     // imageUrl
        parcel.readInt(),                              // play_in_week
        parcel.readInt(),                              // play_in_month
        parcel.readString() ?: "",                     // link
        parcel.readString() ?: "",                     // id
        parcel.readString() ?: "",                     // name
        parcel.readFloat(),                            // duration
        parcel.readByte() != 0.toByte(),               // status
        parcel.readString(),                           // authorName
        parcel.readInt(),                              // playcount
        parcel.readString() ?: ""                      // albumID
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(authorIDs)                // authorIDs
        parcel.writeLong(createdAt.seconds)              // createdAt
        parcel.writeStringList(genreIDs)                 // genreIDs
        parcel.writeString(imageUrl)                     // imageUrl
        parcel.writeInt(play_in_week)                    // play_in_week
        parcel.writeInt(play_in_month)                   // play_in_month
        parcel.writeString(link)                         // link
        parcel.writeString(id)                           // id
        parcel.writeString(name)                         // name
        parcel.writeFloat(duration)                      // duration
        parcel.writeByte(if (status) 1 else 0)           // status
        parcel.writeString(authorName)                   // authorName
        parcel.writeInt(playcount)                       // playcount
        parcel.writeString(albumID)                      // albumID
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
