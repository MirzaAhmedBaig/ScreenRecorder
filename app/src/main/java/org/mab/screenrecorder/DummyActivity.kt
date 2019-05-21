package org.mab.screenrecorder

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaRecorder
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.content.Intent
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.media.projection.MediaProjectionManager
import android.media.projection.MediaProjection
import android.os.Environment
import java.io.IOException


class DummyActivity : AppCompatActivity() {

    var mr: MediaRecorder? = null
    var mp: MediaProjection? = null
    var dw = 720
    var dh = 1280
    var mpm: MediaProjectionManager? = null
    var metrics: DisplayMetrics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dummy)
        mr = MediaRecorder()
        initRecorder()
        mpm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mpm!!.createScreenCaptureIntent(), 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mp = mpm!!.getMediaProjection(resultCode, data!!)
        mp!!.createVirtualDisplay(javaClass.name, dw, dh, metrics!!.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mr!!.surface, null, null)
        mr!!.start()
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        mr!!.stop()
    }

    private fun initRecorder() {
        try {
                mr!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
                mr!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mr!!.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/video.mp4")
                mr!!.setVideoSize(dw, dh)
                mr!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                mr!!.setVideoEncodingBitRate(12 * 1000 * 1000)
                mr!!.setVideoFrameRate(60)
                mr!!.prepare()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
