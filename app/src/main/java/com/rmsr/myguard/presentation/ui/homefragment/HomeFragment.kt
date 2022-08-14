package com.rmsr.myguard.presentation.ui.homefragment

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.PopupWindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputLayout
import com.rmsr.myguard.R
import com.rmsr.myguard.databinding.FragmentHomeBinding
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.presentation.util.BreachSortType
import com.rmsr.myguard.presentation.util.UiText
import com.rmsr.myguard.presentation.util.displayToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeBinding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private var _resultAdapter: HomeResultAdapter? = null
    private val mResultAdapter get() = _resultAdapter!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _resultAdapter = HomeResultAdapter()


        return homeBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _resultAdapter = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //disable any interactions before submitting a search
        homeBinding.homeMotionLayout.isInteractionEnabled = false

        initResultRecyclerView(
            onScrollChange = { v ->
                handleSearchClick(homeBinding.homeSearchInputLayout)
                handleSearchClick(v)
            }
        )


        initCategoryGroupButtons(
            onCategoryChange = viewModel::setCategory
        )


        initSearchInputLayout(
            handleViewsAfterClick = { v -> handleSearchClick(v) },
            onEndIconClicked = { v: View ->
                viewModel.endIconClicked(
                    onInfoRequired = { infoMessage: UiText ->
                        displayInfoPopupWindow(v, infoMessage.asString(requireContext()))
                    }
                )
            },
            onTextChange = { viewModel.query = it }
        )


        initSubmitSearchButton(
            actionClicked = { button ->

                homeBinding.homeMotionLayout.transitionToStart()
                viewModel.search()

                handleSearchClick(button)
                handleSearchClick(homeBinding.homeSearchInputLayout)
            }
        )

        initSortByGroupButtons(onSortChange = viewModel::setSort)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val context = requireContext()
                launch {
                    viewModel.uiState.onEach {
                        refreshLoadingViews(it.isLoading)
                        it.errorMessages.firstOrNull()?.apply {
                            displayError(msg.asString(context))
                            viewModel.userMessageShown(id)
                        }

                        if (it.resultReady)
                            mResultAdapter.setList(it.breachesItems)

                        if (it.isPwned && it.resultReady) {
                            homeBinding.homeResultRecyclerView.scrollToPosition(0)
                            homeBinding.homeMotionLayout.isInteractionEnabled = true
                            homeBinding.homeMotionLayout.transitionToEnd()
                            homeBinding.homeDataProviderNote.visibility = View.VISIBLE
                        }
                    }
                        .catch { displayError(it.message.orEmpty()) }
                        .collect()
                }

                launch {
                    viewModel.componentsState.collectLatest {
                        homeBinding.homeSearchInputLayout.editText?.setTextKeepState(it.query)

                        homeBinding.materialGroupCategory.check(it.categoryRes)
                        homeBinding.materialGroupSort.check(it.sortRes)

                        homeBinding.homeSearchInputLayout.endIconDrawable =
                            AppCompatResources.getDrawable(context, it.endIcon)

                        updateCategoryHintsAndInfo(
                            it.hintRes.asString(context),
                            it.keyboardInputType,
                            it.infoRes.asString(context)
                        )
                    }
                }
            }
        }
    }

    /**
     * Show or hide progress bar on Submit button.
     *
     * @param isLoading `true` to show, `false` to hide.
     * @param progressBar The progressbar view to initialize.
     * @param submitButton SubmitButton view to handle progress animations on it.
     */
    private fun refreshLoadingViews(
        isLoading: Boolean,
        progressBar: ProgressBar = homeBinding.homeProgressBar,
        submitButton: MaterialButton = homeBinding.homeSubmitSearchButton
    ) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            submitButton.isClickable = false
            submitButton.setTextColor(submitButton.solidColor)
        } else {
            progressBar.visibility = View.GONE
            submitButton.isClickable = true
            submitButton.setTextColor(
                MaterialColors.getColor(requireView(), R.attr.colorOnPrimary)
            )
        }
    }

    /**
     * Initialize recycler view and listen to its scroll state changes.
     *
     * @param recycler Recycler view to initialize.
     * @param adapter To bound it to the recycler.
     * @param onScrollChange Called when scroll state changed (can be ignored).
     */
    private fun initResultRecyclerView(
        recycler: RecyclerView = homeBinding.homeResultRecyclerView,
        adapter: HomeResultAdapter = mResultAdapter,
        onScrollChange: (View) -> Unit = {}
    ) {
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter

        recycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                onScrollChange(recyclerView)
            }
        })

    }

    /**
     * Initialize SearchInputLayout by handle view touch
     * or focus change to do some ui motions.
     *
     * Handle Enter button click in keyboard.
     *
     * Switch between End icons and handle click in it.
     *
     * @param binding HomeFragmentBinding
     * @param handleViewsAfterClick Called to handle focus of views.
     * @param onEndIconClicked Called when info button clicked and user need help.
     * @param actionClicked Send view back when clicked and invoke needed actions.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchInputLayout(
        binding: FragmentHomeBinding = homeBinding,
        handleViewsAfterClick: (View) -> Unit = {},
        onEndIconClicked: (View) -> Unit = {},
        onTextChange: (String) -> Unit = {},
    ) {
        val inputLayout = binding.homeSearchInputLayout

        //Handle motion when edit text touched, make edit text go down.
        inputLayout.setOnTouchListener { _, _ ->
            binding.homeMotionLayout.transitionToStart()
            return@setOnTouchListener false
        }

        //Submit search When Enter Button clicked in input Keyboard.
        inputLayout.editText?.setOnEditorActionListener { v, action, _ ->
            if (action != EditorInfo.IME_ACTION_SEARCH) return@setOnEditorActionListener true

            homeBinding.homeSubmitSearchButton.performClick()
            handleViewsAfterClick(v)
            true
        }

        //Hide keyboard when when edit text not in focus.
        inputLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                handleViewsAfterClick(v)
            }
        }

        //Switch End icon between ClearText and Info icons.
        inputLayout.editText?.addTextChangedListener {
            onTextChange(it.toString())
        }


        inputLayout.setEndIconOnClickListener {
            onEndIconClicked(it)
        }
    }


    /**
     * Adding a listener on submit button to do action when clicked.
     *
     * @param submitButton Submit materialButton view to initialize.
     * @param actionClicked Send view back when clicked and invoke needed actions.
     */
    private fun initSubmitSearchButton(
        submitButton: MaterialButton = homeBinding.homeSubmitSearchButton,
        actionClicked: (View) -> Unit
    ) = submitButton.setOnClickListener {
        actionClicked(it)
    }


    /**
     * Adding a listener on checked button in group to do action when changed.
     *
     * @param categoryGroup Category material group view to initialize.
     * @param updateHintsAndInfo Will be called when category changed.
     */
    private fun initCategoryGroupButtons(
        categoryGroup: MaterialButtonToggleGroup = homeBinding.materialGroupCategory,
        onCategoryChange: (QueryType) -> Unit = {}
    ) = categoryGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->

        when {
            checkedId == homeBinding.emailMaterialGroupItem.id && isChecked -> QueryType.EMAIL
            checkedId == homeBinding.phoneMaterialGroupItem.id && isChecked -> QueryType.Phone
            checkedId == homeBinding.domainMaterialGroupItem.id && isChecked -> QueryType.DOMAIN
            else -> return@addOnButtonCheckedListener
        }
            .run(onCategoryChange)

    }


    /**
     * Adding a listener on checked button in group to do action when changed.
     *
     * @param sortMaterialGroup Sort by material group view to initialize.
     * @param onSortChange Will be called when sort by changed.
     */
    private fun initSortByGroupButtons(
        sortMaterialGroup: MaterialButtonToggleGroup = homeBinding.materialGroupSort,
        onSortChange: (BreachSortType) -> Unit
    ) = sortMaterialGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
        when {
            checkedId == homeBinding.dateSortMaterialGroupItem.id && isChecked -> BreachSortType.DATE_DESCENDING
            checkedId == homeBinding.nameSortMaterialGroupItem.id && isChecked -> BreachSortType.TITLE_ASCENDING
            checkedId == homeBinding.pwnedCountSortMaterialGroupItem.id && isChecked -> BreachSortType.PWN_COUNT_DESCENDING
            else -> return@addOnButtonCheckedListener
        }
            .run(onSortChange)
    }


    /**
     * Update All hints and info massages when user select different category.
     *
     * @param editTextHint Hint that will be displayed in editText.
     * @param inputType Specify input type for editText.
     * @param infoText Info massage that will displayed if user click info button.
     */
    private fun updateCategoryHintsAndInfo(editTextHint: String, inputType: Int, infoText: String) {
        homeBinding.homeSearchInputLayout.hint = editTextHint
        homeBinding.homeSearchInputLayout.editText?.inputType = inputType

    }

    /**
     * Show toast with an error message.
     *
     * @see refreshErrorArea
     * @suppress May be updated later.
     */
    private fun displayError(
        message: String,
        textInputLayout: TextInputLayout = homeBinding.homeSearchInputLayout
    ) {
        context?.displayToast(message)
    }


    /**
     * Show Popup window with some info to help the user.
     *
     * This window will close when user click anywhere out of the info window.
     *
     * @param anchorView display top end of pop window layout on bottom left of that view.
     * @param infoMessage massage to display in pop window
     */
    private fun displayInfoPopupWindow(
        anchorView: View,
        infoMessage: String,
    ) {
        val inflater =
            anchorView.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val wrapContent = ConstraintLayout.LayoutParams.WRAP_CONTENT

        val popupInfoView = inflater.inflate(R.layout.info_popup_window, null, false)

        val popupWindow = PopupWindow(popupInfoView, wrapContent, wrapContent, true)
        popupWindow.elevation = .3f

        popupInfoView.findViewById<TextView>(R.id.popup_info_textview).text = infoMessage


//get View measures to calculate popup view location
        popupWindow.contentView.measure(0, 0)
        val widthOffset = -popupWindow.contentView.measuredWidth + anchorView.width / 2
        val heightOffset = -anchorView.height / 2

        PopupWindowCompat.showAsDropDown(
            popupWindow,
            anchorView,
            widthOffset,
            heightOffset,
            Gravity.BOTTOM or Gravity.START
        )
    }


    /**
     * Request to clear focus on specific view and hide keyboard.
     *
     * Used when user hit any Button or hit Enter key in keyBoard.
     * @param v Clear focus from that View.
     * @param showKeyboard Default is false (hidden) .
     */
    private fun handleSearchClick(v: View, showKeyboard: Boolean = false) {
        v.clearFocus()
        val inputManger =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // if (getActivity().getCurrentFocus() != null)
        inputManger.hideSoftInputFromWindow(
            v.windowToken,
            if (showKeyboard) InputMethodManager.SHOW_IMPLICIT else InputMethodManager.RESULT_UNCHANGED_SHOWN
        )

    }

    /**
     * Display an error message below TextInputLayout area, or null to hide any errors.
     *
     * @param message Message to display, or null to hide it.
     * @param textInputLayout TextInputLayout view that shows error.
     * @see displayError
     * @suppress Will be used later.
     */
    private fun refreshErrorArea(
        message: String?,
        textInputLayout: TextInputLayout = homeBinding.homeSearchInputLayout
    ) {
        var error: String? = message


        error?.let {
            context?.displayToast(it)
            error = "* $it"
        }
        textInputLayout.error = error
    }

    companion object {
        private const val TAG = "Rob_EmailFragment"
    }
}