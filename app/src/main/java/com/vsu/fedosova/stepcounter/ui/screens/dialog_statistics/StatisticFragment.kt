package com.vsu.fedosova.stepcounter.ui.screens.dialog_statistics

import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.vsu.fedosova.stepcounter.databinding.FragmentStatisticBinding
import com.vsu.fedosova.stepcounter.ui.screens.dialog_statistics.model.DataForChart
import im.dacer.androidcharts.LineView
import kotlinx.coroutines.launch

class StatisticFragment : Fragment() {

    private val viewModel: DialogStatisticsViewModel by activityViewModels()
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleButton.addOnButtonCheckedListener(toggleGroupListener)
        observeUiState()
    }

    override fun onDestroyView() {
        binding.toggleButton.removeOnButtonCheckedListener(toggleGroupListener)
        _binding = null
        super.onDestroyView()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { uiState -> applyUiState(uiState) }
        }
    }

    private fun applyUiState(uiState: DialogStatisticsState) {
        when (uiState) {
            is DialogStatisticsState.NoData -> {
                binding.tvNoData.visibility = View.VISIBLE
            }
            is DialogStatisticsState.YesData -> {
                binding.tvNoData.visibility = View.GONE
                binding.statisticsAllTime.renderPrams(uiState.data.dataSummary)
                binding.statistikaSrednemZaDen.renderPrams(uiState.data.dataOnAveragePerDay)
                drawGrafika(uiState.data.dataForChart)
            }
        }
    }

    private fun drawGrafika(data: DataForChart) {
        binding.grafic.apply {
            setDrawDotLine(false)
            setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY)
            setColorArray(intArrayOf(Color.BLACK))
            setBottomTextList(data.listDate)
            setDataList(arrayListOf(data.listStep))
        }
    }

    private val toggleGroupListener =
        MaterialButtonToggleGroup.OnButtonCheckedListener { toggle, checkedId, isChecked ->
            toggle.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (isChecked) {
                val days = when (checkedId) {
                    binding.bt7day.id -> 7
                    binding.bt30day.id -> 30
                    else -> 90
                }
                viewModel.getDataForSpecificTime(days)
            }
        }

    companion object {
        private const val TAG = "DialogStatistics"
        fun show(manager: FragmentManager) {
            val dialog = DialogStatistics()
            manager.beginTransaction()
                .add(dialog, TAG)
                .commit()
        }
    }

}