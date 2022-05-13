package com.icam.boat

import android.R.attr.*
import android.graphics.Matrix
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private val client = HttpClient(Android) {
        engine {
            // this: AndroidEngineConfig
            connectTimeout = 1000_000
            socketTimeout = 100_000
        }
    }

    // all variable
    private var speedSet =""
    var distance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            //WindowManager.LayoutParams.FLAG_FULLSCREEN,
            //WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        // get reference to button
        val leftButton = findViewById<ImageButton>(R.id.leftButton)
        val rightButton = findViewById<ImageButton>(R.id.rightButton)
        val seekSpeed = findViewById<SeekBar>(R.id.seek_speed)
        val speedSetTex = findViewById<TextView>(R.id.set_speed)
        val rate = findViewById<TextView>(R.id.rate)
        val gite = findViewById<ImageView>(R.id.gite)
        val cap = findViewById<ImageView>(R.id.cap)
        val giteRight = findViewById<TextView>(R.id.giteRight)
        val giteLeft = findViewById<TextView>(R.id.giteLeft)



        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(10)
                /*
                val angle: Float =gites().toFloat() - 2f
                gite.rotation = angle
                giteRight.text= (angle.toInt()).toString()
                giteLeft.text=(angle.toInt()).toString()

                 */

                var str: String =gites()

                var parts = str.split("F")
                var gite1= (parts.elementAt(0)).toInt() - 5
                var cap1= (parts.elementAt(1)).toInt()
                var speed= (parts.elementAt(2)).toFloat() * 36
                //gite
                giteLeft.text = gite1.toString()
                gite.rotation = gite1.toFloat()

                //cat
                cap.rotation=cap1.toFloat()

                //speed
                rate.text= speed.toString() +"km/h"





            }
        }



        //Click right
        rightButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                right()
            }
        }
        //Click left
        leftButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                left()
            }
        }
        //Set Speed
        seekSpeed?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
            }
            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }
            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                CoroutineScope(Dispatchers.Main).launch {
                    speedSet= speed(seek.progress)
                    speedSetTex.text = "Speed: $speedSet"
                }
                /*Toast.makeText(this@MainActivity,
                    "Progress is: " + seek.progress + "%",
                    Toast.LENGTH_SHORT).show()
                 */
            }
        })

    }

    private suspend fun left(): HttpResponse {
        val response: HttpResponse = client.get("http://192.168.0.105/safranLeft"){

            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
                append(HttpHeaders.AcceptEncoding, "gzip, deflate")
                //append(HttpHeaders.Referrer, "http://192.168.0.103/L")
            }
        }
        //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        return response
    }

    private suspend fun right(): HttpResponse {
        val response: HttpResponse = client.get("http://192.168.0.105/safranRight"){
            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
                append(HttpHeaders.AcceptEncoding, "gzip, deflate")
                //append(HttpHeaders.Referrer, "http://192.168.0.103/H")
            }
        }
        //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        return response
    }

    private suspend fun  speed(value: Int): String {
        val response: HttpResponse = client.get("http://192.168.0.105/$value"+"speed"){
            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
                append(HttpHeaders.AcceptEncoding, "gzip, deflate")
                //append(HttpHeaders.Referrer, "http://192.168.0.103")
            }
        }

        //speedSet = response.body()
        return response.body<String>().toString()
    }


    private suspend fun gites() : String{
        val response: HttpResponse = client.get("http://192.168.0.105/data"){
            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
                append(HttpHeaders.AcceptEncoding, "gzip, deflate")
                //append(HttpHeaders.Referrer, "http://192.168.0.103")
            }
        }
        return response.body()
    }






}