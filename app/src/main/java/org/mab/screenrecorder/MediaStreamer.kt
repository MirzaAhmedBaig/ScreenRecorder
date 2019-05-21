package org.mab.screenrecorder

import android.system.Os.accept
import android.net.LocalSocketAddress
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.media.MediaRecorder
import java.io.IOException
import java.io.InputStream


class MediaStreamer : MediaRecorder() {

    private var localServerSocket: LocalServerSocket? = null
    private var receiver: LocalSocket? = null
    private var sender: LocalSocket? = null

    val inputStream: InputStream?
        get() {

            var out: InputStream? = null

            try {
                out = receiver!!.inputStream
            } catch (e: IOException) {
            }

            return out

        }

    @Throws(IllegalStateException::class, IOException::class)
    override fun prepare() {

        receiver = LocalSocket()
        try {
            localServerSocket = LocalServerSocket("<your_socket_addr>")
            receiver!!.connect(LocalSocketAddress("<your_socket_addr>"))
            receiver!!.receiveBufferSize = 4096
            receiver!!.sendBufferSize = 4096
            sender = localServerSocket!!.accept()
            sender!!.receiveBufferSize = 4096
            sender!!.sendBufferSize = 4096
        } catch (e1: IOException) {
            throw IOException("Can't create local socket !")
        }

        setOutputFile(sender!!.fileDescriptor)

        try {
            super.prepare()
        } catch (e: Exception) {
            closeSockets()
            throw e
        }

    }


    override fun stop() {
        closeSockets()
        super.stop()
    }

    private fun closeSockets() {
        if (localServerSocket != null) {
            try {
                localServerSocket!!.close()
                sender!!.close()
                receiver!!.close()
            } catch (e: IOException) {

            }

            localServerSocket = null
            sender = null
            receiver = null
        }
    }
}