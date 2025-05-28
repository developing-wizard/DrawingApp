package com.example.drawingapp

import android.Manifest
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.drawingapp.databinding.ActivityMainBinding
import com.example.drawingapp.databinding.DialogBrushSizeBinding
import com.example.drawingapp.utils.Extension.Companion.getBitmapFromView
import com.example.drawingapp.utils.Extension.Companion.requestNecessaryPermissions
import com.example.drawingapp.utils.Extension.Companion.saveImageForOlderVersions
import com.example.drawingapp.utils.Extension.Companion.saveImageToGallery
import yuku.ambilwarna.AmbilWarnaDialog


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var lastColor: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.changeBrushSize(24f)
        lastColor = Color.CYAN
        setUpListender()
    }

    private fun setUpListender() {
        binding.changeBrushSizeBtn.setOnClickListener(this)
        binding.undoBtn.setOnClickListener(this)
        binding.saveBtn.setOnClickListener(this)
        binding.uploadPhotoBtn.setOnClickListener(this)
        binding.colorPicker.setOnClickListener(this)
        binding.redColor.setOnClickListener(this)
        binding.orangeColor.setOnClickListener(this)
        binding.blueColor.setOnClickListener(this)
        binding.prupleColor.setOnClickListener(this)
        binding.greenColor.setOnClickListener(this)
    }


    private fun dialogBrushSize() {
        val brushDialog = Dialog(this)
        val dialogBinding = DialogBrushSizeBinding.inflate(layoutInflater)
        brushDialog.setContentView(dialogBinding.root)
        brushDialog.setCancelable(true)
        brushDialog.getWindow()?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogBinding.seekbarProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, position: Int, p2: Boolean) {
                seekBar?.progress?.toFloat()?.let {
                    binding.drawingView.changeBrushSize(it)
                    dialogBinding.sizeTextView.text = "${it.toInt()}/100"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        brushDialog.show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.change_brush_size_btn -> {
                dialogBrushSize()
            }

            R.id.upload_photo_btn -> {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            }

            R.id.save_btn -> {
                val bitmap = getBitmapFromView(binding.view)
                val fileName = "MyImage_${System.currentTimeMillis()}"
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    this.requestNecessaryPermissions(
                        listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        "To Save Photo Following Permission is Required"
                    ) {
                        saveImageForOlderVersions(bitmap!!, fileName, this)
                    }
                } else {
                    if (bitmap != null) {
                        saveImageToGallery(bitmap, this)
                    }
                }
            }

            R.id.color_picker -> {
                showColorPickerDialog()
            }

            R.id.undo_btn -> {
                binding.drawingView.undoDrawing()
            }

            R.id.red_color -> {
                binding.drawingView.changeBrushColor("#80FD0000")
            }

            R.id.blue_color -> {
                binding.drawingView.changeBrushColor("#80003FFD")

            }

            R.id.green_color -> {
                binding.drawingView.changeBrushColor("#8000FD1E")

            }

            R.id.orange_color -> {
                binding.drawingView.changeBrushColor("#80FF5722")

            }

            R.id.pruple_color -> {
                binding.drawingView.changeBrushColor("#80BA11DB")
            }

        }
    }

    private fun showColorPickerDialog() {
        val dialog = AmbilWarnaDialog(this, lastColor, object :
            AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog?) {

            }

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                binding.drawingView.changeBrushColor(color)
                lastColor = color

            }

        })
        dialog.show()
    }

    val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.pickedIv.setImageURI(uri)
            } else {
                Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }


}