package com.rmsr.myguard.presentation.ui.homefragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rmsr.myguard.databinding.BreachDetailDialogFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Deprecated("")
@AndroidEntryPoint
class BreachDetailDialogFragment : DialogFragment() {

    private val TAG = "Rob_BreachDetailDialog"

    private lateinit var mDataBinding: BreachDetailDialogFragmentBinding
    private val mViewModel: HomeViewModel by viewModels()
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController


    @Inject
    lateinit var detailAdapter: BreachDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val transition = TransitionInflater.from(requireContext())
//                .inflateTransition(R.transition.shared_image)
//        sharedElementEnterTransition = transition
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDataBinding =
            BreachDetailDialogFragmentBinding.inflate(LayoutInflater.from(context), null, false)
        // DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.breach_detail_dialog_fragment, null, false)

        mDataBinding.detailRecyclerView.adapter = detailAdapter
        toggleViewsVisibility()

        return AlertDialog.Builder(requireActivity())
            .setView(mDataBinding.root)
            //.setTitle("Result")
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog!!.window!!.attributes.height
//        requireParentFragment().requireView().layoutParams.height


        // fix - Implement the new breaches livedata.
//        mViewModel.breachesLeakList.observe(viewLifecycleOwner){ breaches ->
//        detailAdapter.setList(breaches)
//            toggleViewsVisibility(true)
//        }

//        mViewModel.allBreaches.value?.let {
//            detailAdapter.setList(it)
//            toggleViewsVisibility(true)
//        }


        return mDataBinding.root
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "onDismiss: called")
        detailAdapter.setList(null)
        toggleViewsVisibility()
    }

    private fun toggleViewsVisibility(isResultReady: Boolean = false) {
        if (isResultReady) {
            mDataBinding.emailProgressBar.visibility = View.GONE
            mDataBinding.detailRecyclerView.visibility = View.VISIBLE
            mDataBinding.textViewDetail.visibility = View.VISIBLE

//            deal with dialog height
//            val viewHeight = requireActivity().findViewById<FragmentContainerView>(R.id.nav_host_fragment).measuredHeight//.layoutParams.height
//            if (mDataBinding.root.measuredHeight + 50 > viewHeight)
//                dialog!!.window!!.setLayout(dialog!!.window!!.attributes.width, viewHeight)
//            //ViewGroup.MarginLayoutParams(mDataBinding.root.height, viewHeight)

        } else {
            mDataBinding.emailProgressBar.visibility = View.VISIBLE
            mDataBinding.detailRecyclerView.visibility = View.GONE
            mDataBinding.textViewDetail.visibility = View.GONE
        }
    }


//    @RequiresApi(api = Build.VERSION_CODES.N)

//    Breach detailBreach = BreachDetailFragmentArgs.fromBundle(getArguments()).getArgDetailBreach();
//
//    mDataBinding.detailBreachName.setText(detailBreach.getName());
//    mDataBinding.detailBreachDataClasses.setText(detailBreach.getDataClasses());
//    mDataBinding.detailBreachDate.setText(detailBreach.getBreachDate());
//    mDataBinding.detailBreachLogo.setImageResource(R.drawable.ic_launcher_background);
//
//    mDataBinding.detailBreachDescription.setText(Html.fromHtml(detailBreach.getDescription(), Html.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE) );
//    mDataBinding.detailBreachDescription.setMovementMethod(LinkMovementMethod.getInstance());


}


