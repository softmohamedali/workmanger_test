package com.example.workmanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.work.*
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.workmanger.ui.theme.WorkMangerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val downloadRequest= OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        val filterRequest= OneTimeWorkRequestBuilder<FilterWoker>().build()
        val workMangwer=WorkManager.getInstance(applicationContext)


        setContent {
            WorkMangerTheme {
                val workInfo=workMangwer.getWorkInfosForUniqueWorkLiveData("download")
                    .observeAsState().value
                val downloadInfo= remember(key1 = workInfo) {
                    workInfo?.find { it.id==downloadRequest.id }
                }
                val filterInfo= remember(key1 = workInfo) {
                    workInfo?.find { it.id==filterRequest.id }
                }
                val imageUri by derivedStateOf {
                    val downloadUri=downloadInfo?.outputData?.getString(Constants.IMAGE_URI)?.toUri()
                    val filterUri=filterInfo?.outputData?.getString(Constants.FILTER_URI)?.toUri()
                    filterUri?:downloadUri
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageUri?.let {
                        Image(
                            painter = rememberImagePainter(it ),
                            contentDescription = "null",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Button(
                        onClick = {
                            workMangwer.beginUniqueWork(
                                "download",
                                ExistingWorkPolicy.KEEP,
                                downloadRequest
                            )
                                .then(filterRequest)
                                .enqueue()
                        },
                        enabled = downloadInfo?.state!=WorkInfo.State.RUNNING,
                        content = {
                            Text(text = "Start Download")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "downlaod ${downloadInfo?.state?.name}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "downlaod ${filterInfo?.state?.name}")
                    Spacer(modifier = Modifier.height(16.dp))
                }

            }
        }
    }
}

