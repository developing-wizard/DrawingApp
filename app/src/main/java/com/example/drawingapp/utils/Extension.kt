package com.example.drawingapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Extension {
    companion object {


        fun getBitmapFromView(view: View?): Bitmap? {
            val bitmap = view?.height?.let { height ->
                view.width.let { width ->
                    Bitmap.createBitmap(
                        width,
                        height, Bitmap.Config.ARGB_8888
                    )
                }
            }
            val canvas = bitmap?.let { Canvas(it) }
            if (canvas != null)
                view.draw(canvas)
            return bitmap
        }

        fun saveImageToGallery(bitmap: Bitmap,context: Context) {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.png"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                val outputStream = resolver.openOutputStream(it)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                outputStream?.close()
            }
            Toast.makeText(context,"Image Saved Successfully",Toast.LENGTH_LONG).show()
        }

        fun saveImageForOlderVersions(bitmap: Bitmap, fileName: String,context: Context) {
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val imageFile = File(directory, "$fileName.png")
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            MediaScannerConnection.scanFile(
                context,
                arrayOf(imageFile.absolutePath),
                arrayOf("image/png"),
                null
            )
            Toast.makeText(context,"Image Saved Successfully Lower Version",Toast.LENGTH_LONG).show()

        }


        fun FragmentActivity.requestNecessaryPermissions(
            permissionsToBeTaken: List<String>,
            message: String,
            granted: () -> Unit
        ) {
            PermissionX.init(this)
                .permissions(permissionsToBeTaken)
                .explainReasonBeforeRequest()
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(deniedList, message, "OK", "Cancel")
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "You need to allow these permissions in Settings manually",
                        "OK",
                        "Cancel"
                    )
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        granted()
//                        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG)
//                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "These permissions are denied: $deniedList",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}