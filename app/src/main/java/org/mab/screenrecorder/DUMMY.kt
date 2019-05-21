/*
package org.mab.screenrecorder

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import org.jcodec.api.android.AndroidSequenceEncoder
import org.jcodec.common.io.FileChannelWrapper
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Rational
import java.io.File
import java.io.FileOutputStream

private fun takeScreenshot() {

    try {
        // create bitmap screen capture
        val v1 = window.decorView.rootView
        v1.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(v1.drawingCache)
//            bitmaps.add(bitmap)
        v1.isDrawingCacheEnabled = false

        */
/*val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        bitmaps.add(BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray())))*//*



        val imageFile = File(mainDirecoty.path + File.separator + System.currentTimeMillis() + ".png")
//
        val outputStream = FileOutputStream(imageFile)
        val quality = 100
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        openScreenshot(imageFile)
    } catch (e: Throwable) {
        // Several error may come out with file handling or DOM
        e.printStackTrace()
    }

}

val mainDirecoty = File(Environment.getExternalStorageDirectory().toString() + File.separator + "ScreenRecord")
private fun createAppDirectry() {

    if (!mainDirecoty.exists()) {
        mainDirecoty.mkdir()
    }
}


private fun openScreenshot(imageFile: File) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    val uri = Uri.fromFile(imageFile)
    intent.setDataAndType(uri, "image/*")
    startActivity(intent)
}

private val FPS_RATE = 25
private var isRecording = false
private val handler = Handler()
private val runnable = object : Runnable {
    override fun run() {
        takeScreenshot()
        handler.postDelayed(this, 40)
    }

}

private fun convertToVideo() {
    progress.visibility = View.VISIBLE
    progress.bringToFront()
    AsyncTask.execute {
        var out: FileChannelWrapper? = null
        val file = File(mainDirecoty, "recorded_${System.currentTimeMillis()}_.mp4")

        try {
            out = NIOUtils.writableFileChannel(file.absolutePath)
            val encoder = AndroidSequenceEncoder(out, Rational.R(FPS_RATE, 1))
            Log.d(TAG, "Frame array size : ${bitmaps.size}")
            bitmaps.forEachIndexed { index, it ->
                encoder.encodeImage(it)
                Log.d(TAG, "Done for $index")
            }
            encoder.finish()
        } finally {
            NIOUtils.closeQuietly(out)
        }
        runOnUiThread {
            progress.visibility = View.GONE
        }
    }


}*/