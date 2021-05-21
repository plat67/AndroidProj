package com.android.study.hanselandphotograph

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.study.hanselandphotograph.databinding.ActivityMainBinding
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: ArrayList<Story>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        initDB()
        init()
    }

    private fun initDB() {
        // 스토리 이름(id), 스토리 한줄평, 이동한 좌표, 사진 찍은 좌표
        val comment = "첫번째 스토리"
        val route = arrayListOf<LatLng>(LatLng(37.53,126.96),
                LatLng(37.54,126.97),
                LatLng(37.55,126.96),
                LatLng(37.56,126.97),
                LatLng(37.57,126.96),
                LatLng(37.58,126.97))
        val picture = arrayListOf<LatLng>(LatLng(37.56,126.97))
        db = arrayListOf(Story(0, "firstStory", comment, route, picture))
    }

    private fun init() {
        binding.apply {
            mainInsertBtn.setOnClickListener {
                // new story
                val intent = Intent(this@MainActivity, NewStoryActivity::class.java)
                startActivity(intent)
                // initDB() // 새로운 스토리 생성 후 데베 배열 다시 초기화
            }
            mainRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            val adapter = StoryAdapter(db)
            adapter.onStoryClickListener = object: StoryAdapter.OnStoryClickListener {
                override fun onStoryClick(holder: StoryAdapter.ViewHolder, story: Story) {
                    // show story
                    val intent = Intent(this@MainActivity, ShowStoryActivity::class.java)
                    intent.putExtra("story", story)
                    startActivity(intent)
                }

            }
            mainRecyclerView.adapter = adapter
        }
    }
}