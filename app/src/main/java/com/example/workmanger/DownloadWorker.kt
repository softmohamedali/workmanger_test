package com.example.workmanger

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.random.Random

/*
........ it baisecly a class responsible about define taskes need to work by work manger

*/
class DownloadWorker(
    private val appContext: Context,
    private val params: WorkerParameters
):CoroutineWorker(appContext, params){
    override suspend fun doWork(): Result {
        startForgoundServices()
        delay(5000L)
        val response=ImageApi.instance.downloadImage()
        Log.d("moali","response body : ${response.body()}")
        response.body()?.let { body ->
            return withContext(Dispatchers.IO){
                val file=File(appContext.cacheDir,"image.jpg")
                val outputStream=FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        stream.write(body.bytes())
                    }catch (e:IOException){
                        return@withContext Result.failure(
                            workDataOf(Constants.ERROR_MSG to e.localizedMessage)
                        )
                    }
                }
                Result.success(
                    workDataOf(Constants.IMAGE_URI to file.toUri().toString())
                )
            }
        }
        if (!response.isSuccessful){
            if(response.code().toString().startsWith("5")){
                Result.retry()
            }
            return Result.failure(
                workDataOf(Constants.ERROR_MSG to "Server Error")
            )
        }
        return Result.failure(
            workDataOf(Constants.ERROR_MSG to "Unkown Error")
        )
    }

    private suspend fun startForgoundServices(){
        setForeground(
            foregroundInfo = ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(appContext, MyApplication.NOTIFICATION_CHANNEL_ID_DOWNLOAD)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("notification progress")
                    .setContentTitle("notification")
                    .build()
            )
        )
    }

}