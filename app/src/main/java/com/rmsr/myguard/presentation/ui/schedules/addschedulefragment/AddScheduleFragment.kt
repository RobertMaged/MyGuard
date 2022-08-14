package com.rmsr.myguard.presentation.ui.schedules.addschedulefragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.databinding.FragmentAddScheduleBinding
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.presentation.util.UserCommunicate
import com.rmsr.myguard.presentation.util.displayToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddScheduleFragment : Fragment() {

    private var _binding: FragmentAddScheduleBinding? = null
    private val scheduleAddBinding get() = _binding!!

    private val args by navArgs<AddScheduleFragmentArgs>()
    private val viewModel: AddScheduleViewModel by viewModels()

    private var contactPicker: ActivityResultLauncher<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resultContract = createContactPickerContract()

        if (resultContract != null)
            contactPicker = registerForActivityResult(resultContract) { uri ->
                AddScheduleUiEvents.PickContact(requireActivity().contentResolver, uri).send()
            }
        else
            viewModel.setContactsPickerNotAvailable()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)

        viewModel.setEditModeState(args.isInEditMode, lazyScheduleId = { args.scheduleToEditId })

        initScheduleAddInputLayout(
            onActionClicked = scheduleAddBinding.scheduleAddButton::performClick,
            onEndIconClicked = { AddScheduleUiEvents.EndIconClick.send() },
            onTextChange = { AddScheduleUiEvents.SetQuery(it).send() }
        )

        initCategoryGroupButtons(onCategoryChange = { AddScheduleUiEvents.SetCategory(it).send() })

        initAddScheduleButton { AddScheduleUiEvents.SaveSchedule.send() }

        return scheduleAddBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerUiStateObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        contactPicker?.unregister()
    }

    private fun createContactPickerContract(): ActivityResultContract<Unit, Uri?>? {
        val pickerIntent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }

        pickerIntent.resolveActivity(requireActivity().packageManager) ?: return null

        return object : ActivityResultContract<Unit, Uri?>() {
            override fun createIntent(context: Context, input: Unit): Intent = pickerIntent

            override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
                intent.takeIf { resultCode == Activity.RESULT_OK }?.data
        }

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
     * @param onEndIconClicked Called when info button clicked and user need help.
     * @param onActionClicked called when keyboard [EditorInfo.IME_ACTION_GO] clicked.
     */
    private fun initScheduleAddInputLayout(
        binding: FragmentAddScheduleBinding = scheduleAddBinding,
        onActionClicked: () -> Unit = {},
        onEndIconClicked: () -> Unit = {},
        onTextChange: (String) -> Unit = {},
    ) {
        val inputLayout = binding.scheduleAddInputLayout.apply {
            if (BuildConfig.DEBUG.not())
                return@apply
            //test Only.
            editText?.setOnLongClickListener {
                editText?.setTextKeepState("multiple-breaches@hibp-integration-tests.com")

                true
            }

        }


        //Submit search When Enter Button clicked in input Keyboard.
        inputLayout.editText?.setOnEditorActionListener { v, action, _ ->
            if (action != EditorInfo.IME_ACTION_GO) return@setOnEditorActionListener false

            onActionClicked()
            handleSearchClick(v)
            true
        }

        //Hide keyboard when when edit text not in focus.
        inputLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                handleSearchClick(v)
            }
        }

        //Switch End icon between ClearText and Info icons.
        inputLayout.editText?.addTextChangedListener(object : TextWatcher {
            var old = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                old = s?.toString().orEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != old)
                    onTextChange(s?.toString().orEmpty())
            }
        })


        inputLayout.setEndIconOnClickListener {
            onEndIconClicked()
        }
    }

    /**
     * Adding a listener on Add Schedule button to do action when clicked.
     *
     * @param actionClicked Send view back when clicked and invoke needed actions.
     */
    private fun initAddScheduleButton(
        actionClicked: () -> Unit
    ) = scheduleAddBinding.scheduleAddButton.setOnClickListener { actionClicked() }


    /**
     * Adding a listener on checked button in group to do action when changed.
     *
     * @param onCategoryChange Will be called when category changed.
     */
    private fun initCategoryGroupButtons(
        onCategoryChange: (QueryType) -> Unit = {}
    ) =
        scheduleAddBinding.scheduleAddGroupCategory.addOnButtonCheckedListener { _, checkedId, isChecked ->

            when {
                checkedId == scheduleAddBinding.emailScheduleAddGroupItem.id && isChecked -> QueryType.EMAIL
                checkedId == scheduleAddBinding.phoneScheduleAddGroupItem.id && isChecked -> QueryType.Phone
                else -> return@addOnButtonCheckedListener
            }
                .run(onCategoryChange)
        }


    private fun registerUiStateObserver(): Unit = with(lifecycleScope) {

        launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiState.collect {

                    // Since onPause() called when contacts app launch,
                    // when back to onResume() maybe race condition happen between
                    // updating pickerRequired state or collect ui saved state.
                    // so this observed when created.
                    if (it.contactPickerRequired)
                        contactPicker?.launch()
                }
            }
        }

        launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collectLatest {

                    updateCategoryGroup(it.categoryRes)

                    updateQueryInputLayout(it)

                    updateSaveScheduleAbility(it.isValidQuery)

                    it.errorMessages.firstOrNull()?.let { msg ->
                        renderError(msg, onDisplayed = viewModel::userMessageDisplayed)
                    }


                    if (it.scheduleSaved)
                        findNavController().navigateUp()
                }
            }
        }
    }

    /**
     * Update All Schedule Query InputLayout staff.
     */
    private fun updateQueryInputLayout(state: AddScheduleUiState) =
        with(scheduleAddBinding.scheduleAddInputLayout) {
            if (editText.toString() != state.searchQuery.query)
                editText?.setTextKeepState(state.searchQuery.query)

            editText?.imeOptions = state.imeOptions

            hint = state.hintRes.asString(requireContext())
            editText?.inputType = state.keyboardInputType
            endIconDrawable = state.endIcon?.let { AppCompatResources.getDrawable(context, it) }

        }

    private fun updateCategoryGroup(@IdRes buttonRes: Int) {
        scheduleAddBinding.scheduleAddGroupCategory.check(buttonRes)
    }

    private fun updateSaveScheduleAbility(isValidQuery: Boolean) {
        scheduleAddBinding.scheduleAddButton.isEnabled = isValidQuery
    }

    private fun renderError(userMessage: UserCommunicate, onDisplayed: (Long) -> Unit) {
        val context = requireContext()
        when (userMessage) {
            is UserCommunicate.SnackbarMessage -> Snackbar.make(
                scheduleAddBinding.root,
                userMessage.msg.asString(context),
                Snackbar.LENGTH_SHORT
            ).show()
            is UserCommunicate.ToastMessage -> context.displayToast(
                userMessage.msg.asString(context)
            )
        }
        onDisplayed(userMessage.id)
    }

    private fun AddScheduleUiEvents.send() =
        viewModel.sendEvent(this)

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

}