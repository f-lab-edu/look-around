package kky.flab.lookaround.feature.recording

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.map.style.sources.ImageSource
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.feature.recording.databinding.ActivityModifyRecordBinding
import kky.flab.lookaround.feature.recording.model.ModifyRecordEffect
import kky.flab.lookaround.feature.recording.model.ModifyRecordUiState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModifyRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyRecordBinding

    private val viewModel: ModifyRecordViewModel by viewModels()

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let { uri ->
                photoUri = uri
                val bitmap =
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                binding.ivPhoto.setImageBitmap(bitmap)
                binding.cardImage.isVisible = true
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.contains(true)) {
                imagePermissionGranted = true
                startPhotoPicker()
            }
        }

    private var imagePermissionGranted = false

    private var hasRequestedPermission = false

    private var sdkVersion: Int = 0

    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityModifyRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sdkVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            LATER_SDK_34
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LATER_SDK_33
        } else {
            LATER_SDK_28
        }

        val id = intent.getLongExtra(EXTRA_RECORD_ID, 0)
        viewModel.getRecord(id)

        observe()
        initView()
    }

    override fun onResume() {
        super.onResume()
        checkImagePermission()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { uiState ->
                    when (uiState) {
                        is ModifyRecordUiState.Result -> {
                            binding.etMemo.setText(uiState.record.memo)
                            val uri = uiState.record.imageUri
                            if (uri.isNotEmpty()) {
                                val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, Uri.parse(uri)))
                                binding.ivPhoto.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.config.collect {
                hasRequestedPermission = it.requestReadStorage
            }
        }

        lifecycleScope.launch {
            viewModel.effect.collect {
                when (it) {
                    is ModifyRecordEffect.Error -> Toast.makeText(
                        this@ModifyRecordActivity,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    ModifyRecordEffect.SaveRecord -> {
                        Toast.makeText(this@ModifyRecordActivity, "저장되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.llAddPhoto.setOnClickListener {
            startPhotoPicker()
        }

        binding.tvSave.setOnClickListener {
            viewModel.save(
                binding.etMemo.text.toString(),
                photoUri
            )
        }

        binding.ivDelete.setOnClickListener {
            binding.ivPhoto.setImageBitmap(null)
            binding.cardImage.isVisible = false
        }
    }

    private fun startPhotoPicker() {
        if (imagePermissionGranted) {
            photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            requestPermission()
        }
    }

    private fun checkImagePermission() {
        imagePermissionGranted =
            if (sdkVersion == LATER_SDK_33 && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else if (sdkVersion == LATER_SDK_34 && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                false
            }
    }

    @SuppressLint("InlinedApi")
    private fun requestPermission() {
        if (hasRequestedPermission
            && imagePermissionGranted.not()
            && !checkShouldShowRationaleMessage()
        ) {

            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${packageName}")
            )
            startActivity(intent)
        }

        when (sdkVersion) {
            LATER_SDK_34 -> {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            }

            LATER_SDK_33 -> {
                permissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
            }

            else -> {
                permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun checkShouldShowRationaleMessage(): Boolean {
        return when (sdkVersion) {
            LATER_SDK_34 -> {
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            }

            LATER_SDK_33 -> {
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
            }

            else -> {
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    companion object {
        const val EXTRA_RECORD_ID = "extraRecordId"
        const val LATER_SDK_34 = 34
        const val LATER_SDK_33 = 33
        const val LATER_SDK_28 = 32
    }
}
