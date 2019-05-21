package org.mab.screenrecorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var mr: MediaRecorder? = null

    private val STATE_RESULT_CODE = "result_code"
    private val STATE_RESULT_DATA = "result_data"

    private val REQUEST_MEDIA_PROJECTION = 1

    private var mScreenDensity: Int = 0

    private var mResultCode: Int = 0
    private var mResultData: Intent? = null

    private var mSurface: Surface? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mMediaProjectionManager: MediaProjectionManager? = null

    private val TAG = MainActivity::class.java.simpleName
    private var bitmaps = ArrayList<Bitmap>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mButtonToggle.setOnClickListener {
            if (mVirtualDisplay == null) {
                startScreenCapture()
            } else {
                stopScreenCapture()
            }
        }

        mSurface = mSurfaceView.holder.surface

        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE)
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA)
        }
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        initRecorder()

    }

    private fun initRecorder() {
        try {

            mr = MediaRecorder()
            mr!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mr!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mr!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mr!!.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/video.mp4")
            mr!!.setVideoSize(/*mSurfaceView.width, mSurfaceView.height*/720,1280)
            mr!!.setVideoEncodingBitRate(12 * 1000 * 1000)
            mr!!.setVideoFrameRate(60)
            mr!!.prepare()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode)
            outState.putParcelable(STATE_RESULT_DATA, mResultData)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "User cancelled")
                Toast.makeText(this, "user_cancelled", Toast.LENGTH_SHORT).show()
                return
            }
            Log.d(TAG, "Starting screen capture")
            mResultCode = resultCode
            mResultData = data
            setUpMediaProjection()
            setUpVirtualDisplay()
        }
    }

    /*public override fun onPause() {
        super.onPause()
        stopScreenCapture()
    }

    public override fun onDestroy() {
        super.onDestroy()
        tearDownMediaProjection()
    }*/


    private fun setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager?.getMediaProjection(mResultCode, mResultData)
    }

    private fun tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection?.stop()
            mMediaProjection = null
        }
    }

    private fun startScreenCapture() {
        if (mSurface == null) {
            return
        }
        if (mMediaProjection != null) {
            setUpVirtualDisplay()
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection()
            setUpVirtualDisplay()
        } else {
            Log.i(TAG, "Requesting confirmation")
            // This initiates a prompt dialog for the user to confirm screen projection.
            startActivityForResult(
                    mMediaProjectionManager?.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION)
        }
    }

    private fun setUpVirtualDisplay() {
        Log.i(TAG, "Setting up a VirtualDisplay: " +
                mSurfaceView.width + "x" + mSurfaceView.height +
                " (" + mScreenDensity + ")")
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay("ScreenCapture",
                mSurfaceView.width, mSurfaceView.height, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSurface/*mr!!.surface*/, null, null)
        mButtonToggle.text = "Stop"
        mr!!.start()
    }

    private fun stopScreenCapture() {
        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay?.release()
        mVirtualDisplay = null
        mButtonToggle.text = "Start"
        mr!!.stop()
    }
}


