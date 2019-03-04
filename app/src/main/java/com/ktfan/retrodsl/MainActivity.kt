package com.ktfan.retrodsl

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ktfan.retrodsl.domain.demoAction
import com.ktfan.retrodsl.model.onError
import com.ktfan.retrodsl.model.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        launch {
            demoAction()
                .onSuccess {
                    Toast.makeText(this@MainActivity, "Successfully Fetched ${it?.size} Items", Toast.LENGTH_LONG)
                        .show()
                }
                .onError {
                    Toast.makeText(this@MainActivity, "Failed!! -- $it", Toast.LENGTH_LONG).show()
                }
        }
    }

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
