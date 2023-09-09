package com.bignerdranch.example.nerdlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

private const val TAG = "NerdLaunchActivity"
class NerdLauncherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()
    }
    private fun setupAdapter(){             //生成应用列表
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(startupIntent,0)  //返回所有符合的activity的ResolveInfo信息
        activities.sortWith(Comparator{a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(), //对ResolveInfo对象中的activity标签按首字母排序
                b.loadLabel(packageManager).toString()
            )
        })

        Log.i(TAG,"Found ${activities.size} activities")        //记录下PackageManager返回的activity总数
        recyclerView.adapter = ActivityAdapter(activities)
    }

    private class ActivityHolder(itemView: View): RecyclerView.ViewHolder(itemView),View.OnClickListener{                  //显示activity标签名
        private val nameTextView = itemView.findViewById(R.id.txtView_txt) as TextView
        private val nameIcon = itemView.findViewById(R.id.imgView_img) as ImageView
        private lateinit var resolveInfo: ResolveInfo

        init {
            nameTextView.setOnClickListener(this)
            nameIcon.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo){
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val appIcon = resolveInfo.loadIcon(packageManager)
            nameTextView.text = appName
            nameIcon.setImageDrawable(appIcon)

        }

        override fun onClick(v: View?) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)     //控制每个activity仅创建一个任务。
            }
            val context = v?.context
            context?.startActivity(intent)
        }
    }

    private class ActivityAdapter(val activities: List<ResolveInfo>): RecyclerView.Adapter<ActivityHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_list,parent,false)
            return ActivityHolder(view)
        }


        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }


    }
}