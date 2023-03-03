package th.ac.rmutto.testproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    var imgViewFile: ImageView? = null
    var txtUsername: TextView? = null
    var txtFirstName: TextView? = null
    var txtLastName: TextView? = null
    var txtEmail: TextView? = null
    var txtGender: TextView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        //For an synchronous task
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)



        val sharedPrefer = requireContext().getSharedPreferences(
            "appPrefer", Context.MODE_PRIVATE)
        var custID = sharedPrefer?.getString("custIDPref", null)
        //var username = sharedPrefer?.getString("usernamePref", null)

        imgViewFile = root.findViewById(R.id.imgFile)
        txtUsername = root.findViewById(R.id.txtUsername)
        txtFirstName = root.findViewById(R.id.txtFirstName)
        txtLastName = root.findViewById(R.id.txtLastName)
        txtGender = root.findViewById(R.id.txtGender)
        val buttonUpdate: Button = root.findViewById(R.id.buttonUpdate)
        val buttonLogout: Button = root.findViewById(R.id.buttonLogout)

        buttonUpdate.setOnClickListener {

//            val fragmentTransaction = requireActivity().
//            supportFragmentManager.beginTransaction()
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, DashboardFragment())
//            fragmentTransaction.commit()
            val intent = Intent(context, CustomerUpdateActivity::class.java)
            startActivity(intent)

        }

        buttonLogout.setOnClickListener {
//            val editor = sharedPrefer.edit()
//            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences
//            editor.commit() // ยืนยันการแก้ไข preferences
//
//            activity?.runOnUiThread {
//                Toast.makeText(
//                    requireContext(), "ออกจากระบบเรียบร้อยแล้ว",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//            //return to login page
//            val intent = Intent(context, Login::class.java)
//            startActivity(intent)
        }


        viewUser(custID!!)

        return root
    }

    fun viewUser(custID: String)
    {

        var url: String = getString(R.string.root_url) + getString(R.string.profile_url) + custID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            Log.d("tag", "x2")
            val response = okHttpClient.newCall(request).execute()
            Log.d("tag", url)
            if (response.isSuccessful) {
                Log.d("tag", "x3")
                try {
                    val data = JSONObject(response.body!!.string())
                    Log.d("tag", "x4")
                    if (data.length() > 0) {
                        var imageFile = data.getString("imageFile")
                        var username = data.getString("userName")
                        var firstName = data.getString("firstName")
                        var lastName = data.getString("lastName")
                        var email = data.getString("email")
                        var gender = data.getString("gender")

                        if (!imageFile.equals("null") && !imageFile.equals("")){
                            val image_url = getString(R.string.root_url) +
                                    getString(R.string.customer_image_url) + imageFile
                            Log.d("tag", "x10")
                            Picasso.get().load(image_url).into(imgViewFile)
                            Log.d("tag", "x11")
                        }

                        if(username.equals("null"))username = ""
                        txtUsername?.text = username

                        if(firstName.equals("null"))firstName = ""
                        txtFirstName?.text = firstName

                        if(lastName.equals("null"))lastName = ""
                        txtLastName?.text = lastName

                        if(email.equals("null"))email = ""
                        txtEmail?.text = email

                        if(gender.equals("null")){
                            txtGender?.text = ""
                        }else if(gender.equals("0")){
                            txtGender?.text = "ชาย"
                        }else if(gender.equals("1")){
                            txtGender?.text = "หญิง"
                        }

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}