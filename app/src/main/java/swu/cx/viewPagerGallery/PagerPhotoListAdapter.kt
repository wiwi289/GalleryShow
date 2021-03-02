package swu.cx.viewPagerGallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.pager_photo_view.view.*

class PagerPhotoListAdapter: ListAdapter<PhotoItem, PagerPhotoListAdapter.PagerPhotoViewHolder>(DiffCallBack) {
    object DiffCallBack:DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.id == newItem.id
        }

    }
    class PagerPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view,parent,false).also {
            return PagerPhotoViewHolder(it)
        }

    }

    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(getItem(position).largeImageURL)
            .placeholder(R.drawable.photo_placeholder)
            .into(holder.itemView.pagerPhoto)
    }
}