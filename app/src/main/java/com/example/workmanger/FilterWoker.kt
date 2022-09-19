package com.example.workmanger

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LightingColorFilter
import android.graphics.Paint
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FilterWoker(
    private val appContext: Context,
    private val params: WorkerParameters
): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val imageFile=params.inputData.getString(Constants.IMAGE_URI)?.toUri()?.toFile()
        delay(5000)
        return imageFile?.let { file ->
            val bitmap=BitmapFactory.decodeFile(file.absolutePath)
            val result=bitmap.copy(bitmap.config,true)
            val paint=Paint()
            paint.colorFilter=LightingColorFilter(0x08FF44,1)
            val canves=Canvas(result)
            canves.drawBitmap(result,0f,0f,paint)
            return withContext(Dispatchers.IO){
                val file= File(appContext.cacheDir,"new_image.jpg")
                val outputStream= FileOutputStream(file)
                val suceessful=result.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
                )
                if (suceessful){
                    Result.success(workDataOf(Constants.FILTER_URI to file.toUri().toString()))
                }else Result.failure()

            }
        }?:Result.failure()
    }
}
