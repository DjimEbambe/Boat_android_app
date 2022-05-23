package com.icam.boat

import android.R.attr.*
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Matrix
import android.os.Bundle
import android.text.InputType
import android.view.View
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
        /*
        engine {
            connectTimeout = 1000_000
            socketTimeout = 100_000
        }
         */
    }

    // all variable
    private var speedSet =""
    var distance = 0
    private var ip = "192.168.0.177"


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
        val leftButtonAll = findViewById<ImageButton>(R.id.leftButtonAll)
        val rightButtonAll = findViewById<ImageButton>(R.id.rightButtonAll)
        val seekSpeed = findViewById<SeekBar>(R.id.seek_speed)
        val speedSetTex = findViewById<TextView>(R.id.set_speed)
        val rate = findViewById<TextView>(R.id.rate)
        val gite = findViewById<ImageView>(R.id.gite)
        val cap = findViewById<ImageView>(R.id.cap)
        val giteRight = findViewById<TextView>(R.id.giteRight)
        val giteLeft = findViewById<TextView>(R.id.giteLeft)
        val buttonIp = findViewById<Button>(R.id.buttonIp)
        val loading: ProgressBar = findViewById(R.id.loading)
        val loading_speed: ProgressBar = findViewById(R.id.loading_speed)

        buttonIp.setOnClickListener {
            showdialog(ip)
        }

        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                buttonIp.setText(ip)
                try {
                    val str: String =gites()

                    val parts = str.split("F")
                    val gite1= (parts.elementAt(0)).toInt() - 1
                    val cap1= (parts.elementAt(1)).toInt()
                    val speed= (parts.elementAt(2)).toFloat() * 36
                    //gite
                    giteLeft.text = gite1.toString()
                    gite.rotation = gite1.toFloat()

                    //cat
                    cap.rotation=cap1.toFloat()

                    //speed
                    rate.text= speed.toString() +"km/h"
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "$e", Toast.LENGTH_SHORT).show()
                    delay(3000)
                }
            }
        }

        //Click right all
        rightButtonAll.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    rightButtonAll.isEnabled = false
                    leftButtonAll.isEnabled = false
                    leftButton.isEnabled = false
                    rightButton.isEnabled = false
                    loading.visibility= View.VISIBLE
                    rightAll()
                    rightButtonAll.isEnabled= true
                    leftButtonAll.isEnabled= true
                    leftButton.isEnabled= true
                    rightButton.isEnabled= true
                    loading.visibility= View.GONE
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "$e", Toast.LENGTH_SHORT).show()
                }
            }

        }

        //Click left all
        leftButtonAll.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    leftButtonAll.isEnabled = false
                    rightButtonAll.isEnabled = false
                    leftButton.isEnabled = false
                    rightButton.isEnabled = false
                    loading.visibility= View.VISIBLE
                    leftAll()
                    leftButtonAll.isEnabled= true
                    rightButtonAll.isEnabled= true
                    leftButton.isEnabled= true
                    rightButton.isEnabled= true
                    loading.visibility= View.GONE
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "$e", Toast.LENGTH_SHORT).show()
                }
            }

        }

        //Click right
        rightButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    rightButton.isEnabled = false
                    leftButton.isEnabled = false
                    rightButtonAll.isEnabled = false
                    leftButtonAll.isEnabled = false
                    loading.visibility= View.VISIBLE
                    right()
                    rightButton.isEnabled= true
                    leftButton.isEnabled= true
                    rightButtonAll.isEnabled= true
                    leftButtonAll.isEnabled= true
                    loading.visibility= View.GONE
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "$e", Toast.LENGTH_SHORT).show()
                }
            }

        }

        //Click left
        leftButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    leftButton.isEnabled = false
                    rightButton.isEnabled = false
                    rightButtonAll.isEnabled = false
                    leftButtonAll.isEnabled = false
                    loading.visibility= View.VISIBLE
                    left()
                    leftButton.isEnabled= true
                    rightButton.isEnabled= true
                    rightButtonAll.isEnabled= true
                    leftButtonAll.isEnabled= true
                    loading.visibility= View.GONE
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "$e", Toast.LENGTH_SHORT).show()
                }
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

                    try {
                        loading_speed.visibility= View.VISIBLE
                        seekSpeed.isEnabled=false
                        speedSet= speed(seek.progress)
                        speedSetTex.text= seek.progress.toString()
                        seekSpeed.isEnabled=true
                        loading_speed.visibility= View.GONE
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "$e", Toast.LENGTH_SHORT).show()
                    }
                }

                /*Toast.makeText(this@MainActivity,
                    "Progress is: " + seek.progress + "%",
                    Toast.LENGTH_SHORT).show()
                 */
            }
        })

    }

    private suspend fun left(): HttpResponse {
        val response: HttpResponse = client.get("http://$ip/safranLeft"){

            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
            }
        }
        //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        return response
    }


    private suspend fun leftAll(): HttpResponse {
        val response: HttpResponse = client.get("http://$ip/safranLeftAll"){

            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
            }
        }
        //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        return response
    }

    private suspend fun right(): HttpResponse {
        val response: HttpResponse = client.get("http://$ip/safranRight"){
            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
            }
        }
        //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        return response
    }

    private suspend fun rightAll(): HttpResponse {
        val response: HttpResponse = client.get("http://$ip/safranRightAll"){
            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
            }
        }
        //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        return response
    }

    private suspend fun  speed(value: Int): String {
        val response: HttpResponse = client.get("http://$ip/$value"+"speed"){
            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
            }
        }

        //speedSet = response.body()
        return response.body<String>().toString()
    }


    private suspend fun gites() : String{
        val response: HttpResponse = client.get("http://$ip/data"){
            headers {
                append(HttpHeaders.Accept, "application/x-www-form-urlencoded,application/xhtml+xml,*/*;q=0.8")
                append(HttpHeaders.UserAgent, "ktor client")
            }
        }
        return response.body()


    }



    @SuppressLint("SetTextI18n")
    fun showdialog(i_p: String){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter the ip address from arduino card")
        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setText(i_p)
        input.inputType = InputType.TYPE_CLASS_PHONE
        builder.setView(input)
        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            ip = input.text.toString()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }


}