package com.example.ilsm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        setup()
    }

    private fun setup(){

    }
    private fun safeCameraOpen(id: Int): Boolean {
        return try {
            releaseCameraAndPreview()
            mCamera = Camera.open(id)
            true
        } catch (e: Exception) {
            Log.e(getString(R.string.app_name), "failed to open Camera")
            e.printStackTrace()
            false
        }
    }

    private fun releaseCameraAndPreview() {
        preview?.setCamera(null)
        mCamera?.also { camera ->
            camera.release()
            mCamera = null
        }
    }
}