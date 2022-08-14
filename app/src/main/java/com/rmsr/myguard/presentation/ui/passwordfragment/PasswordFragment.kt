package com.rmsr.myguard.presentation.ui.passwordfragment

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.rmsr.myguard.R
import com.rmsr.myguard.databinding.FragmentPasswordBinding
import com.rmsr.myguard.presentation.util.displayToast
import com.rmsr.myguard.presentation.util.getHTMLFormatText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PasswordFragment : Fragment() {

    private var _binding: FragmentPasswordBinding? = null
    private val passwordBinding get() = _binding!!


    private val viewModel: PasswordViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)

        return passwordBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        passwordBinding.passwordSearchInputLayout.editText?.setText(viewModel.passwordQuery)

        initSearchInputLayout(
            handleViewsAfterClick = { editView -> handleSearchClick(editView) },
            textWatcher = { viewModel.passwordQuery = it },
            actionClicked = {
                passwordBinding.passwordSubmitSearchButton.performClick()
            }
        )


        initSubmitSearchButton(
            actionClicked = { button ->
                viewModel.searchForPassword()

                handleSearchClick(button)
                handleSearchClick(passwordBinding.passwordSearchInputLayout)
            }
        )


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {


                viewModel.uiState.collect {
                    it.errorMessages.firstOrNull()?.let {
                        displayError(it.msg.asString(requireContext()))
                        viewModel.userMessageShown(it.id)
                    }

                    refreshProgressbarState(it.isLoading)
                    if (it.isResultReady) {
                        displayResultFromObserver(
                            resultText = makeResultText(it.passwordLeaksCount),
                            resultBackgroundDrawable = makeResultBackground(it.passwordLeaksCount),
                            resultBackgroundColor = makeResultBackgroundTint(it.passwordLeaksCount)
                        )
                    }
                }

            }
        }
    }


    /**
     * Initialize SearchInputLayout by handle focus changes.
     *
     * Handle Enter button click in keyboard.
     *
     * @param searchInputLayout Search InputLayout to initialize.
     * @param handleViewsAfterClick Called to handle focus of views.
     * @param actionClicked Send view back when clicked and invoke needed actions.
     */
    private fun initSearchInputLayout(
        searchInputLayout: TextInputLayout = passwordBinding.passwordSearchInputLayout,
        handleViewsAfterClick: (View) -> Unit = {},
        textWatcher: (String) -> Unit = {},
        actionClicked: (View) -> Unit,
    ) {

        //Submit search When Enter Button clicked in input Keyboard
        searchInputLayout.editText?.setOnEditorActionListener { editView, _, _ ->

            actionClicked(editView)
            handleViewsAfterClick(editView)
            true
        }

        //Hide keyboard when when edit text not in focus.
        searchInputLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                handleViewsAfterClick(v)
        }

        searchInputLayout.editText?.addTextChangedListener {
            textWatcher(it.toString())
        }
    }

    /**
     * Adding a listener on submit button to do action when clicked.
     *
     * @param submitButton Submit materialButton view to initialize.
     * @param actionClicked Send view back when clicked and invoke needed actions.
     */
    private fun initSubmitSearchButton(
        submitButton: MaterialButton = passwordBinding.passwordSubmitSearchButton,
        actionClicked: (View) -> Unit
    ) = submitButton.setOnClickListener {
        actionClicked(it)
    }


    /**
     * Show or hide progress bar on Submit button.
     *
     * @param show `true` to show, `false` to hide.
     * @param progressBar The progressbar view to initialize.
     * @param submitButton SubmitButton view to handle progress animations on it.
     */
    private fun refreshProgressbarState(
        show: Boolean,
        progressBar: ProgressBar = passwordBinding.passwordProgressBar,
        submitButton: MaterialButton = passwordBinding.passwordSubmitSearchButton
    ) {
        if (show) {
            progressBar.visibility = View.VISIBLE
            submitButton.setTextColor(submitButton.solidColor)
        } else {
            progressBar.visibility = View.GONE
            submitButton.setTextColor(
                MaterialColors.getColor(requireView(), R.attr.colorOnPrimary)
            )
        }
    }

    /**
     * Display the result came from view model and do some animations in ui.
     *
     * @param resultTextView Where the result will by displayed and get styled.
     * @param resultText Result massage to display.
     * @param resultBackgroundDrawable Background drawable to use.
     *
     * @suppress Will be updated to decouple animations code.
     */
    private fun displayResultFromObserver(
        resultTextView: MaterialTextView = passwordBinding.passwordResultTextview,
        resultText: String,
        resultBackgroundDrawable: Drawable,
        resultBackgroundColor: Int = -1,
    ) {

        resultTextView.apply {
            text = getHTMLFormatText(resultText)
            background = resultBackgroundDrawable
            animateResult()
        }
    }


    /**
     * Get the result massage resource depending on Result
     */
    private fun makeResultText(leaksNumber: Int): String {
        return if (leaksNumber > 0) {
            String.format(getString(R.string.password_leaked_massage), leaksNumber)
        } else {
            String.format(getString(R.string.password_safe_massage), leaksNumber)
        }
    }

    /**
     * Return a drawable for background depending on number of leaks.
     *
     * @param leaksNumber Number of leaks found.
     * @return Drawable based on result.
     */
    private fun makeResultBackground(leaksNumber: Int): Drawable {
        val drawable = if (leaksNumber > 0) {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.leaks_found_background,
                requireActivity().theme
            )
        } else {
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.no_leaks_found_background,
                requireActivity().theme
            )
        }
        return drawable!!
    }

    /**
     * Return a color for background depending on number of leaks.
     *
     * @param leaksNumber Number of leaks found.
     * @return Color based on result.
     */
    private fun makeResultBackgroundTint(leaksNumber: Int): Int {

        return if (leaksNumber > 0) {
            ResourcesCompat.getColor(resources, R.color.error_red_color, requireContext().theme)
        } else {
            ResourcesCompat.getColor(resources, R.color.success_green_color, requireContext().theme)
        }
    }

    /**
     * Show toast with an error message.
     *
     * @see refreshErrorArea
     * @suppress May be updated later.
     */
    private fun displayError(
        message: String,
        textInputLayout: TextInputLayout = passwordBinding.passwordSearchInputLayout
    ) {
        context?.displayToast(message)
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

        inputManger.hideSoftInputFromWindow(
            v.windowToken,
            if (showKeyboard) InputMethodManager.SHOW_IMPLICIT else InputMethodManager.RESULT_UNCHANGED_SHOWN
        )
    }

    /**
     * Animate the result massage after any changes observed from view model.
     */
    private fun MaterialTextView.animateResult() {
        val startPivot = pivotY
        ObjectAnimator.ofFloat(this, "translationY", startPivot, 100f)
            .apply {
                duration = 2000
                start()
            }.doOnEnd { pivotY = startPivot }

        AlphaAnimation(0.0f, 1.0f).apply {
            visibility = View.VISIBLE
            duration = 2000
            startOffset = 0
            fillAfter = true
            startAnimation(this)
        }

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
        textInputLayout: TextInputLayout = passwordBinding.passwordSearchInputLayout
    ) {
        var error: String? = message


        error?.let {
            context?.displayToast(it)
            error = "* $it"
        }
        textInputLayout.error = error
    }


    companion object {
        private const val TAG = "Rob_PasswordFragment"
    }
}

