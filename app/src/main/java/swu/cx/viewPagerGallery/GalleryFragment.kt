package swu.cx.viewPagerGallery

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*


class GalleryFragment : Fragment() {
    private val galleryViewModel by activityViewModels<GalleryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val galleryAdapter = GalleryAdapter(galleryViewModel)
        mRecycler.apply {
                adapter = galleryAdapter
                layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }
        galleryViewModel.pagedListLiveData.observe(viewLifecycleOwner){
            galleryAdapter.submitList(it)
        }
        swipeLayoutGallery.setOnRefreshListener {
            galleryViewModel.resetQuery()
        }
        galleryViewModel.networkStatus.observe(viewLifecycleOwner){
            galleryAdapter.updateNetworkStatus(it)
            swipeLayoutGallery.isRefreshing = it==NetworkStatus.INITIAL_LOADING
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.swipeIndicator->{
                swipeLayoutGallery.isRefreshing = true
                galleryViewModel.resetQuery()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }
}