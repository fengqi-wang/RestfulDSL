package com.ktfan.retrodsl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktfan.retrodsl.net.Item
import com.ktfan.retrodsl.net.restyCall
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        restyCall<Item>(this) {
            request = { fetchItems("myToken") }
            onSuccess = {
                data_view.text = it?.name
            }
            onError = {
                error_view.text = it.message
            }
            onException = {
                exception_view.text = it.message
            }
        }
    }
}
