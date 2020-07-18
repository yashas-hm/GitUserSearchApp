package yashas.application.magnumsolutionsinterview

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import yashas.application.magnumsolutionsinterview.activity.ResultActivity

class SearchFragment : Fragment() {

    lateinit var etName: EditText
    lateinit var etFollowers: EditText
    lateinit var etRepos: EditText
    lateinit var btnSearch: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        sharedPreferences = context!!.getSharedPreferences("search", Context.MODE_PRIVATE)
        etName = view.findViewById(R.id.etName)
        etFollowers = view.findViewById(R.id.etFollowers)
        etRepos = view.findViewById(R.id.etRepos)
        btnSearch = view.findViewById(R.id.btnSearch)

        etName.text.clear()
        etRepos.text.clear()
        etFollowers.text.clear()

        btnSearch.setOnClickListener {
            if(etName.text.toString()=="") {
                Toast.makeText(
                    context,
                    "User name empty! Cannot search",
                    Toast.LENGTH_SHORT
                )
                .show()
            }
            else{

                sharedPreferences.edit().putString("name", etName.text.toString()).apply()
                if(etFollowers.text.toString()!=""){
                    sharedPreferences.edit().putString("followers", etFollowers.text.toString()).apply()
                }
                if(etRepos.text.toString()!=""){
                    sharedPreferences.edit().putString("repos", etRepos.text.toString()).apply()
                }
                startActivity(Intent(context, ResultActivity::class.java))
            }
        }
        return view
    }

    override fun onPause() {
        super.onPause()
        etName.text.clear()
        etRepos.text.clear()
        etFollowers.text.clear()
    }
}