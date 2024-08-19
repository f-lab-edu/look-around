package kky.flab.lookaround.feature.record

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.feature.record.databinding.FragmentRecordBinding
import kky.flab.lookaround.feature.record.model.RecordUiState
import kky.flab.lookaround.feature.recording.ModifyRecordActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecordFragment : Fragment(), RecordListAdapter.ButtonListener {

    private lateinit var binding: FragmentRecordBinding

    private val viewModel: RecordViewModel by viewModels()

    private val recordAdapter = RecordListAdapter(this)

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
                    when (it) {
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

    override fun onModify(record: Record) {
        startActivity(
            Intent(
                requireContext(),
                ModifyRecordActivity::class.java
            ).putExtra(ModifyRecordActivity.EXTRA_RECORD_ID, record.id)
        )
    }

    override fun onDelete(record: Record) {
        AlertDialog.Builder(requireContext())
            .setMessage("기록을 삭제 하시겠습니까?")
            .setPositiveButton("삭제") { dialog, _ ->
                viewModel.delete(record)
                dialog.dismiss()
            }.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(true)
            .show()
    }
}
