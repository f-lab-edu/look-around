package kky.flab.lookaround.feature.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.feature.record.databinding.FragmentRecordBinding
import kky.flab.lookaround.feature.record.model.RecordUiState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding

    private val viewModel: RecordViewModel by viewModels()

    private val recordAdapter = RecordListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentRecordBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    private fun initView() {
        binding.rvRecord.adapter = recordAdapter
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    when(it) {
                        RecordUiState.Loading -> {
                            binding.rvRecord.isVisible = false
                            binding.progress.isVisible = true
                        }
                        is RecordUiState.Result -> {
                            binding.rvRecord.isVisible = true
                            binding.progress.isVisible = false
                            recordAdapter.submitList(it.records)
                        }
                    }
                }
            }
        }
    }
}
