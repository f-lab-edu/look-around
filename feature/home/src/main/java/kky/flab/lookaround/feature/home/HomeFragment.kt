package kky.flab.lookaround.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kky.flab.lookaround.feature.home.databinding.FragmentHomeBinding
import kky.flab.lookaround.feature.home.model.Loading
import kky.flab.lookaround.feature.home.model.Result
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    private var start: Boolean = false
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
            val dialog = AlertDialog.Builder(requireActivity()).apply {
                val message = if(start) "산책을 종료할까요?" else "산책을 시작해볼까요?"
                setMessage(message)
                setPositiveButton("확인") { _, _ ->
                    viewModel.toggleWalk()
                }
                setNegativeButton("취소") { dialog, _ -> dialog.dismiss()}
            }

            dialog.show()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    when(it) {
                        Loading -> {}
                        is Result -> {
                            start = it.start
                        }
                    }
                }
            }
        }
    }
}