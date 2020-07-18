package yashas.application.magnumsolutionsinterview

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import yashas.application.magnumsolutionsinterview.model.Data

import java.util.*


class ResultRecyclerAdapter(val context: Context, val data: ArrayList<Data>): RecyclerView.Adapter<ResultRecyclerAdapter.ResultViewHolder>(){
    class ResultViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtId: TextView = view.findViewById(R.id.txtId)
        val rlContent: RelativeLayout = view.findViewById(R.id.rlContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val d = data[position]
        holder.txtName.text = d.name
        holder.txtId.text = d.id
        android.os.Handler().postDelayed({
            Glide.with(holder.imgAvatar.context)
                .load(d.avatar)
                .error(R.drawable.name)
                .into(holder.imgAvatar)
        }, 500)



        holder.rlContent.setOnClickListener {
            val url = d.url
            try {
                val i = Intent("android.intent.action.MAIN")
                i.component =
                    ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main")
                i.addCategory("android.intent.category.LAUNCHER")
                i.data = Uri.parse(url)
                context.startActivity(i)
            } catch (e: ActivityNotFoundException) {
                // Chrome is not installed
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(i)
            }
        }
    }
}