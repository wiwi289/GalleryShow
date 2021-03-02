package swu.cx.viewPagerGallery

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Pixabay(
    val totalHits:Int,
    val hits:Array<PhotoItem>,
    val total:Int
)
@Parcelize data class PhotoItem(
    val webformatURL:String,
    val id:Int,
    val largeImageURL:String,
    val webformatHeight:Int,
    val user:String,
    val likes:Int,
    val favorites:Int
):Parcelable