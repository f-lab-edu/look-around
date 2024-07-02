package kky.flab.lookaround.feature.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.feature.home.databinding.FragmentHomeBinding
import kky.flab.lookaround.feature.home.model.Error
import kky.flab.lookaround.feature.home.model.Loading
import kky.flab.lookaround.feature.home.model.Result
import kky.flab.lookaround.feature.home.model.ShowEndRecordingMessage
import kky.flab.lookaround.feature.home.model.ShowStartRecordingMessage
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observe()
    }

    private fun initView() {
        binding.statusCard.setOnClickListener {
            viewModel.showRecordingMessage()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    when(it) {
                        Loading -> {}
                        is Result -> {
                            val message = if (it.recording) R.string.status_on_message else R.string.status_off_message
                            val subMessage = if (it.recording) R.string.status_on_sub_message else R.string.status_off_sub_message

                            binding.tvMessage.setText(message)
                            binding.tvSubMessage.setText(subMessage)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.effect.collect {
                    when(it) {
                        is ShowEndRecordingMessage,
                        is ShowStartRecordingMessage -> {
                            showRecordingDialog(it.message) { _, _ ->
                                viewModel.toggleRecording()
                            }
                        }

                        is Error -> {
                            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showRecordingDialog(message: String, onPositiveButtonListener: (DialogInterface, Int) -> Unit) {
        val dialog = AlertDialog.Builder(requireActivity()).apply {
            setMessage(message)
            setPositiveButton("확인", onPositiveButtonListener)
            setNegativeButton("취소") { dialog, _ -> dialog.dismiss()}
        }

        dialog.show()
    }
}