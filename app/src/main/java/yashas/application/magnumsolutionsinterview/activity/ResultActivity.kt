package yashas.application.magnumsolutionsinterview.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import yashas.application.magnumsolutionsinterview.R
import yashas.application.magnumsolutionsinterview.ResultRecyclerAdapter
import yashas.application.magnumsolutionsinterview.model.Data
import yashas.application.magnumsolutionsinterview.util.ConnectionManager

class ResultActivity : AppCompatActivity() {

    lateinit var btnSearchResult: Button
    val searchData = arrayListOf<Data>()
    var loading: Boolean =true
    var count: Int = 1
    var position: Int = 0
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: ResultRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var txtEnd: TextView
    lateinit var noResults: TextView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        loading = true
        count = 1
        position = 0
        searchData.clear()
        sharedPreferences = getSharedPreferences("search", Context.MODE_PRIVATE)
        btnSearchResult = findViewById(R.id.btnSearchResult)
        recyclerView = findViewById(R.id.recyclerResult)
        layoutManager = LinearLayoutManager(this@ResultActivity)
        txtEnd = findViewById(R.id.txtEnd)
        progressBar = findViewById(R.id.progress_circular)
        noResults = findViewById(R.id.noResults)
        txtEnd.visibility = View.INVISIBLE
        noResults.visibility = View.INVISIBLE


        btnSearchResult.text = sharedPreferences.getString("name", null)!!

        btnSearchResult.setOnClickListener {

            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finishActivity(1)
        }


        val queue = Volley.newRequestQueue(this@ResultActivity)
        val repo = sharedPreferences.getString("repos", "")
        val followers = sharedPreferences.getString("followers", "")
        val name = sharedPreferences.getString("name", "")

        var url = "https://api.github.com/search/users?q=$name&page=$count"
        url = if(repo!=""&&followers!=""){
            "https://api.github.com/search/users?q=$name+repos:%3e$repo+followers:%3e$followers&page=$count"
        } else if(repo!=""){
            "https://api.github.com/search/users?q=$name+repos:%3e$repo&page=$count"
        } else if(followers!=""){
            "https://api.github.com/search/users?q=$name+followers:%3e$followers&page=$count"
        } else{
            "https://api.github.com/search/users?q=$name&page=$count"
        }

        if (ConnectionManager().checkConnectivity(this@ResultActivity)){
            val jsonObjectRequest = object: JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {
                    val data = it.getJSONArray("items")
                    if(data.isNull(0)){
                        noResults.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        progressBar.visibility = View.INVISIBLE
                    }
                    else {
                        noResults.visibility = View.INVISIBLE
                        progressBar.visibility = View.GONE
                        for (i in 0 until data.length()) {
                            val dataJsonObject = data.getJSONObject(i)
                            val dataObject =
                                Data(
                                    dataJsonObject.getString("id"),
                                    dataJsonObject.getString("login"),
                                    dataJsonObject.getString("avatar_url"),
                                    dataJsonObject.getString("html_url")
                                )
                            searchData.add(dataObject)
                            recyclerAdapter =
                                ResultRecyclerAdapter(
                                    this@ResultActivity,
                                    searchData
                                )
                            recyclerView.adapter = recyclerAdapter
                            recyclerView.layoutManager = layoutManager
                        }
                        position+=searchData.size
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this@ResultActivity, "Error", Toast.LENGTH_LONG).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return HashMap()
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            val dialog = AlertDialog.Builder(this@ResultActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not Found")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                finishAffinity()
            }
            dialog.create()
            dialog.show()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager =
                    LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached = lastVisible + 1 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached) {
                    if (loading){
                        loading=false
                        progressBar.visibility = View.VISIBLE
                        txtEnd.visibility = View.INVISIBLE
                        count+=1
                        url = if(repo!=""&&followers!=""){
                            "https://api.github.com/search/users?q=$name+repos:%3e$repo+followers:%3e$followers&page=$count"
                        } else if(repo!=""){
                            "https://api.github.com/search/users?q=$name+repos:%3e$repo&page=$count"
                        } else if(followers!=""){
                            "https://api.github.com/search/users?q=$name+followers:%3e$followers&page=$count"
                        } else{
                            "https://api.github.com/search/users?q=$name&page=$count"
                        }
                        Handler().postDelayed({
                            if(!loadMore(url)){
                                txtEnd.visibility = View.VISIBLE
                                progressBar.visibility = View.INVISIBLE
                                loading = false
                            }
                        },2000)
                    }
                }
            }
        })
    }

    fun loadMore(url: String): Boolean{
        val queue = Volley.newRequestQueue(this@ResultActivity)
        progressBar.visibility = View.INVISIBLE
        var more = true
        val extraData = arrayListOf<Data>()
        println("Request sent $count")
        if (ConnectionManager().checkConnectivity(this@ResultActivity)){
            val jsonObjectRequest = object: JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {
                    val data = it.getJSONArray("items")
                    if(data.length()==0) {
                        more = false
                        loading = false
                    }
                    else {
                        for (i in 0 until data.length()) {
                            val dataJsonObject = data.getJSONObject(i)
                            val dataObject =
                                Data(
                                    dataJsonObject.getString("id"),
                                    dataJsonObject.getString("login"),
                                    dataJsonObject.getString("avatar_url"),
                                    dataJsonObject.getString("html_url")
                                )
                            searchData.add(dataObject)
                            recyclerAdapter =
                                ResultRecyclerAdapter(
                                    this@ResultActivity,
                                    searchData
                                )
                            recyclerView.adapter = recyclerAdapter
                            recyclerView.layoutManager = layoutManager
                            recyclerAdapter.notifyItemInserted(position)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        position=searchData.size
                        more = true
                        loading = true
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this@ResultActivity, "Error", Toast.LENGTH_LONG).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return HashMap()
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            val dialog = AlertDialog.Builder(this@ResultActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not Found")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                finishAffinity()
            }
            dialog.create()
            dialog.show()
        }
        return more
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishActivity(1)
    }
}