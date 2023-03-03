package th.ac.rmutto.testproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

import org.json.JSONObject
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var editTextUser: EditText = findViewById(R.id.editTextUser)
        var editTextpassword: EditText = findViewById(R.id.editTextpassword)
        var btlogin: Button = findViewById(R.id.btlogin)
        var btregis: Button = findViewById(R.id.btnupdate)
        btregis.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }


        btlogin.setOnClickListener {
            if (editTextUser.text.toString() == "") {
                editTextUser.error = "กรุณาใส่ Username "
                return@setOnClickListener
            }
            if (editTextpassword.text.toString() == "") {
                editTextpassword.error = "กรุณาใส่ Password"
                return@setOnClickListener
            }
            val url = getString(R.string.root_url) + getString(R.string.login_url)
            Log.d("tag", url)

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("username", editTextUser.text.toString())
                .add("password", editTextpassword.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
            try {
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val obj = JSONObject(response.body!!.string())
                    val message = obj["message"].toString()
                    val status = obj["status"].toString()

                    if (status == "true") {

                        val userid = obj["userID"].toString()
                        val username = obj["userName"].toString()

                        //Create shared preference to store user data
                        val sharedPrefer: SharedPreferences =
                            getSharedPreferences("appPrefer", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPrefer.edit()

                        editor.putString("custIDPref", userid)
                        editor.putString("usernamePref", username)
                        editor.commit()

                        //redirect to main page
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        editTextUser.error = "กรุณาระบุรหัสผ่านใหม่อีกครั้ง"
                        return@setOnClickListener

                    }

                } else {
                    response.code
                    Toast.makeText(
                        applicationContext,
                        "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้",
                        Toast.LENGTH_LONG
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}