package th.ac.rmutto.testproject

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    private var display = ArrayList<Data>()
    private val data = ArrayList<Data>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        recyclerView = root.findViewById(R.id.recyclerView)
        showDataList()

        return root

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.search_nav_menu, menu)

        val item = menu?.findItem(R.id.action_search)

        if (item != null) {
            val searchView = item?.actionView as SearchView
            //searchView.isIconifiedByDefault = false
            searchView.queryHint = "ค้นหา"

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    // task HERE
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {

                    if (newText.isNotEmpty()) {
                        val searchText = newText!!.toLowerCase(Locale.getDefault())
                        //Log.e("tag", searchText)
                        //Log.e("tag","x")
                        display.clear()
                        data.forEach {
                            if (it.firstName.toLowerCase(Locale.getDefault())
                                    .contains(searchText)
                            ) {
                                display.add(it)
                            }
                        }
                        recyclerView!!.adapter!!.notifyDataSetChanged()
                    } else {
                        Log.e("tag","y")
                        display.clear()
                        display.addAll(data)
                        recyclerView!!.adapter!!.notifyDataSetChanged()
                    }

                    return false
                }


            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showDataList() {
        val url: String = getString(R.string.root_url) + getString(R.string.doc_url)
        Log.e("tag","x")
        val okHttpClient = OkHttpClient()
        Log.e("tag","x")
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            Log.e("tag","x")
                            data.add( Data(
                                item.getString("userID"),
                                item.getString("firstName"),
                                item.getString("department"),
                                item.getString("docdepartment"),
                                item.getString("imageFile")
                            )
                            )
                        }

                        display.addAll(data)
                        recyclerView!!.adapter = DataAdapter(display)
                    } else {
                        Toast.makeText(context, "ไม่สามารถแสดงข้อมูลได้",
                            Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    internal class Data(
        var userID: String, var firstName: String,var department: String,
        var docdepartment: String,
        var file: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.medical_list,
                parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
//            var url = getString(R.string.root_url) +
//                    getString(R.string.doctor_image_url) + data.file
//            Picasso.get().load(url).into(holder.file)
            holder.Namedoc.text = data.firstName
            holder.department.text = data.department
            holder.docdepartment.text = data.docdepartment
//
//
//            holder.file.setOnClickListener {
//                val intent = Intent(context, ProductActivity::class.java)
//                intent.putExtra("userID", data.userID)
//                startActivity(intent)
//            }

        }


        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var data: Data? = null


            var Namedoc: TextView = itemView.findViewById(R.id.txtNamedoc)
            var department: TextView = itemView.findViewById(R.id.txtdepartment)
            var docdepartment: TextView = itemView.findViewById(R.id.txtdepartment1)
            var pricedoc: TextView = itemView.findViewById(R.id.txtpricedoc)
//            var file: ImageView = itemView.findViewById(R.id.imgFile)


        }

    }


}