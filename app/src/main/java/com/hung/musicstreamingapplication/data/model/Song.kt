package com.hung.musicstreamingapplication.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
@Entity(tableName = "song")
data class Song(
    val authorIDs: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val genreIDs: List<String> = emptyList(),
    val imageUrl: String = "",
    val play_in_week: Int = 0,
    val play_in_month: Int = 0,
    val link: String = "",
    @PrimaryKey
    var id: String = "",
    val name: String = "",
    val duration: Float = 0.0f,
    val status: Boolean = false,
    var authorName: String? = "",
    val playcount: Int = 0,
    var albumID: String = "",
    val lyrics: String = ""
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
        parcel.readString() ?: "",              //albumid
        parcel.readString() ?: ""               // lyrics
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
        parcel.writeString(lyrics)                      // lyrics
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return """
            Song(
                id='$id',
                authorIDs=$authorIDs,
                createdAt=$createdAt,
                genreIDs=$genreIDs,
                imageUrl='$imageUrl',
                play_in_week=$play_in_week,
                play_in_month=$play_in_month,
                link='$link',
                name='$name',
                duration=$duration,
                status=$status,
                authorName=$authorName,
                playcount=$playcount,
                albumID='$albumID',
                lyrics='$lyrics'
            )
        """.trimIndent()
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
data class YouTubeResponse(
    @SerializedName("items") val items: List<VideoItem>
)

data class VideoItem(
    @SerializedName("snippet") val snippet: VideoSnippet
)

data class VideoSnippet(
    @SerializedName("title") val title: String,
    // Các trường khác bạn cần có thể thêm ở đây
)