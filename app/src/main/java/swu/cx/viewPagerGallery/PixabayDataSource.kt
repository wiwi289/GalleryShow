package swu.cx.viewPagerGallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
enum class NetworkStatus{
    LOADING,
    FAILED,
    COMPLETED,
    INITIAL_LOADING,
    LOADED
}
class PixabayDataSource(private val mContext:Context):PageKeyedDataSource<Int,PhotoItem>() {
    var retry:(()->Unit) ?= null
    private val _networkStatus = MutableLiveData<NetworkStatus>()
    val networkStatus:LiveData<NetworkStatus> = _networkStatus
    private val queryKey = arrayOf("cat","dog","car","beauty","phone","computer","flower","animal").random()
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        retry = null
        _networkStatus.postValue(NetworkStatus.INITIAL_LOADING)
        val url = "https://pixabay.com/api/?key=20130160-5126d62d4db8fcd58173c7f09&q=${queryKey}&per_page=50"
        StringRequest(
            Request.Method.GET,
            url,{
                val dataList = Gson().fromJson(it,Pixabay::class.java).hits.toList()
                callback.onResult(dataList,null,2)
                _networkStatus.postValue(NetworkStatus.LOADED)
            },{
                _networkStatus.postValue(NetworkStatus.FAILED)
                retry = {loadInitial(params,callback)}
            }
        ).also { VolleySingleton.getInstance(mContext).requestQueue.add(it) }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        retry = null
        _networkStatus.postValue(NetworkStatus.LOADING)
        val url = "https://pixabay.com/api/?key=20130160-5126d62d4db8fcd58173c7f09&q=${queryKey}&per_page=50&page=${params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            {
                val dataList = Gson().fromJson(it,Pixabay::class.java).hits.toList()
                callback.onResult(dataList,params.key+1)
                _networkStatus.postValue(NetworkStatus.LOADED)
            },
            {
                if (it.toString()=="com.android.volley.ClientError"){
                    _networkStatus.postValue(NetworkStatus.COMPLETED)
                }else{
                    _networkStatus.postValue(NetworkStatus.FAILED)
                    retry = {loadAfter(params,callback)}
                }
            }
        ).also { VolleySingleton.getInstance(mContext).requestQueue.add(it) }
    }
}