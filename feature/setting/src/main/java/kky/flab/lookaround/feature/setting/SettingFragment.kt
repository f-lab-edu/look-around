package kky.flab.lookaround.feature.setting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.feature.setting.databinding.FragmentSettingBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentSettingBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.swTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.setDarkMode(checked)
        }

        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.configState.collect {
                Log.d("SettingFragment", it.toString())
                binding.swTheme.isChecked = it.darkTheme
            }
        }
    }
}
