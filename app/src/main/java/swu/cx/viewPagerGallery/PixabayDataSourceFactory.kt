package swu.cx.viewPagerGallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


class PixabayDataSourceFactory(private val mContext:Context): DataSource.Factory<Int,PhotoItem>() {
   private val _pixabayDataSource = MutableLiveData<PixabayDataSource>()
    val pixabayDataSource:LiveData<PixabayDataSource> = _pixabayDataSource
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(mContext).also { _pixabayDataSource.postValue(it)}
    }
}