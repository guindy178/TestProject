package th.ac.rmutto.testproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import th.ac.rmutto.testproject.MainActivity
import th.ac.rmutto.testproject.R
import th.ac.rmutto.testproject.RealPathUtil
import java.io.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*



class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)



        var txtfirstname: EditText = findViewById(R.id.txtfirstname)
        var txtlastname: EditText = findViewById(R.id.txtlastname)
        var txtusername: EditText = findViewById(R.id.txtusername)
        var txtpassword: EditText = findViewById(R.id.txtpassword)
        var buttongo: Button = findViewById(R.id.buttongo)

        var rbman : RadioButton = findViewById(R.id.rbman)
        var rbwomam : RadioButton = findViewById(R.id.rbwoman)
        var gender = 0


        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.data!!
                    val path = RealPathUtil.getRealPath(applicationContext, uri)
                    var file = File(path.toString())

                }
            }




        buttongo.setOnClickListener {

            if (txtfirstname.text.toString() == "") {
                txtfirstname.error = "กรุณาใส่ Username "
                return@setOnClickListener
            }
            if (txtlastname.text.toString() == "") {
                txtlastname.error = "กรุณาใส่ Lastnamw"
                return@setOnClickListener
            }
            if (txtusername.text.toString() == "") {
                txtusername.error = "กรุณาใส่ Username"
            }
            if (txtpassword.text.toString() == "") {
                txtpassword.error = "กรุณาใส่ Password"
            }
            if (rbman.isChecked == true){
                gender = 1

            }else if (rbwomam.isChecked == true){
                gender = 2
            }
            if (rbman.isChecked == false && rbwomam.isChecked == false){
                rbwomam.error ="เลือก เพศก่อน"
                return@setOnClickListener
            }


            val url = getString(R.string.root_url) + getString(R.string.register_url)
            Log.d("tag", url)

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("firstName", txtfirstname.text.toString())
                .add("lastName", txtlastname.text.toString())
                .add("Username", txtusername.text.toString())
                .add("Password", txtpassword.text.toString())
                .add("gender", gender.toString())

                .build()
            Log.d("tag", "x2")
            val request: Request = Request.Builder()

                .url(url)
                .post(formBody)
                .build()
            Log.d("tag", "x5")
            try {
                Log.d("tag", "x88")
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("tag", "goif")
                    val obj = JSONObject(response.body!!.string())
                    val message = obj["message"].toString()
                    val status = obj["status"].toString()
                    Log.d("tag", "x6")
                    if (status == "true") {
                        Log.d("tag", "x7")
                        //redirect to main page
                        val intent = Intent(this, MainActivity ::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("tag", "aa")
                        txtusername.error = "กรุณาระบุรหัสผ่านใหม่อีกครั้ง"
                        return@setOnClickListener

                    }

                }else{
                    response.code
                    Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG)
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}