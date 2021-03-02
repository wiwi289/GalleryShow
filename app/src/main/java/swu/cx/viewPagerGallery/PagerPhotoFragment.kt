package swu.cx.viewPagerGallery

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val REQUEST_WRITE_EXTERNAL_STORAGE = 1

class PagerPhotoFragment : Fragment() {
    val galleryViewModel by activityViewModels<GalleryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pager_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = PagerPhotoListAdapter()
        viewPager2.adapter = adapter
        galleryViewModel.pagedListLiveData.observe(viewLifecycleOwner){
            adapter.submitList(it)
            viewPager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION") ?: 0, false)
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = getString(R.string.photoTag, position + 1, galleryViewModel.pagedListLiveData.value?.size)
            }
        })

        saveBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            } else {
               viewLifecycleOwner.lifecycleScope.launch {
                   savePhoto()
               }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        savePhoto()
                    }
                } else {
                    Toast.makeText(requireContext(), "没有权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun savePhoto() {
        val holder =
            (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoListAdapter.PagerPhotoViewHolder
        val bitmap = holder.itemView.pagerPhoto.drawable.toBitmap()
        val saveUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        ) ?: kotlin.run {
            Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
            return
        }
        val isSuccess = withContext(Dispatchers.IO) {
            requireContext().contentResolver.openOutputStream(saveUri).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
            }
        }
                if (isSuccess) {
                    Toast.makeText(requireContext(), "存储成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "存储失败", Toast.LENGTH_SHORT).show()
                }
    }
}