package com.morphingcoffee.credit_donut.score_display.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.morphingcoffee.credit_donut.R
import com.morphingcoffee.credit_donut.databinding.FragmentCreditScoreBinding
import com.morphingcoffee.credit_donut.score_display.model.ScoreDisplayState
import com.morphingcoffee.credit_donut.score_display.model.ScoreDisplayUserAction
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Screen displaying credit score information as a circular progress bar
 * & allowing user to retry in the case of score fetching failure.
 **/
class CreditScoreFragment : Fragment() {
    private val creditScoreViewModel: CreditScoreViewModel by viewModel()

    private var _binding: FragmentCreditScoreBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setUpObservers() {
        creditScoreViewModel.state.observe(viewLifecycleOwner) { creditReportState ->
            when (creditReportState) {
                is ScoreDisplayState.Pending -> {
                    displayPending()
                }
                is ScoreDisplayState.Error -> {
                    binding.circularBar.indeterminateMode = false
                    displayError(creditReportState)
                    // Enable users to retry loading credit score by touching the screen
                    setUpSingleRetryGestureListener()
                }
                is ScoreDisplayState.Loaded -> {
                    binding.circularBar.indeterminateMode = false
                    displayScore(creditReportState)
                }
            }
        }
    }

    private fun displayPending() {
        binding.topText.text = ""
        binding.scoreText.text = getString(R.string.question_mark)
        binding.bottomText.text = getString(R.string.getting_credit_score)
        binding.circularBar.apply {
            indeterminateMode = true
            backgroundProgressBarColor = requireContext().getColor(R.color.transparent)
            progressBarColor =
                requireContext().getColor(R.color.circular_bar_gradient_progress_foreground)
        }
    }

    private fun displayScore(state: ScoreDisplayState.Loaded) {
        binding.topText.setText(R.string.your_credit_score_is)
        binding.scoreText.text = state.currentScore.toString()
        binding.bottomText.text = getString(R.string.out_of_score_template, state.totalScore)
        binding.circularBar.apply {
            progressBarColorStart = requireContext().getColor(R.color.circular_bar_gradient_start)
            progressBarColorEnd = requireContext().getColor(R.color.circular_bar_gradient_end)
            backgroundProgressBarColorStart = Color.TRANSPARENT
            backgroundProgressBarColorEnd = Color.TRANSPARENT
        }

        // Animate circular progress bar
        binding.circularBar.setProgressWithAnimation(
            state.percent,
            duration = resources.getInteger(R.integer.circular_bar_animation_duration).toLong(),
            startDelay = resources.getInteger(R.integer.circular_bar_animation_delay).toLong(),
        )
    }

    private fun displayError(state: ScoreDisplayState.Error) {
        // Inform user of the issue
        Toast.makeText(
            requireContext(),
            getString(state.reason.clientFacingErrorMessage),
            Toast.LENGTH_SHORT
        ).show()

        binding.topText.text = getString(R.string.issue_occurred)
        binding.scoreText.text = getString(R.string.exclamation_mark)
        binding.bottomText.text = getString(R.string.try_again_action)
        binding.circularBar.apply {
            progressBarColorStart = requireContext().getColor(R.color.circular_bar_gradient_start)
            progressBarColorEnd = requireContext().getColor(R.color.circular_bar_gradient_end)
            backgroundProgressBarColorStart = Color.TRANSPARENT
            backgroundProgressBarColorEnd = Color.TRANSPARENT
        }
    }

    private fun setUpSingleRetryGestureListener() {
        // Listen user clicks on screen
        binding.content.setOnClickListener(this::handleRetryAndDisposeListener)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleRetryAndDisposeListener(v: View) {
        binding.content.setOnClickListener(null)
        creditScoreViewModel.handleUserAction(ScoreDisplayUserAction.ReloadScore)
    }

}