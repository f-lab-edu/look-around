package kky.flab.lookaround.feature.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import kky.flab.lookaround.feature.main.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavHost()
    }

    private fun initNavHost() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as? NavHostFragment

        navHostFragment?.navController?.let {
            binding.mainBottomNavigation.setupWithNavController(it)
        }
    }
}