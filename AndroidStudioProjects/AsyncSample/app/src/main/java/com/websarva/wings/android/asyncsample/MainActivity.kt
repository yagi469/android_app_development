package com.websarva.wings.android.asyncsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter

class MainActivity : AppCompatActivity() {
    // クラス内のprivate定数を宣言するためにcompanion objectブロックとする。
    companion object {
        // ログに記載するタグ用の文字列。
        private const val DEBUG_TAG = "AsyncSample"
        // お天気情報のURL。
        private const val WEATHERINFO_URL = "https://api.openweathermap.org/data/2.5/weather?" +
"lang=ja"
        // お天気APIにアクセスするためのAPIキー。
        private const val APP_ID="6608fab0e1bc8da0d88597d601436c9f"
    }

    // リストビューに表示させるリストデータ。
    private var _list: MutableList<MutableMap<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _list = createList()

        val lvCityList = findViewById<ListView>(R.id.lvCityList)
        val from = arrayOf("name")
        val to = intArrayOf(android.R.id.text1)
        val adapter = SimpleAdapter(this@MainActivity, _list, android.R.layout.
        simple_list_item_1, from, to)
        lvCityList.adapter = adapter
        lvCityList.onItemClickListener = ListItemClickListener()
    }

    // リストビューに表示させる天気ポイントリストデータを生成するメソッド。
    private fun createList(): MutableList<MutableMap<String, String>> {
        var list: MutableList<MutableMap<String, String>> = mutableListOf()

        var city = mutableMapOf("name" to "大阪", "q" to "Osaka")
        list.add(city)
        city = mutableMapOf("name" to "神戸", "q" to "Kobe")
        list.add(city)
        city = mutableMapOf("name" to "京都", "q" to "Kyoto")
        list.add(city)
        city = mutableMapOf("name" to "大津", "q" to "Otsu")
        list.add(city)
        city = mutableMapOf("name" to "奈良", "q" to "Nara")
        list.add(city)
        city = mutableMapOf("name" to "和歌山", "q" to "Wakayama")
        list.add(city)
        city = mutableMapOf("name" to "姫路", "q" to "Himeji")
        list.add(city)

        return list
    }

    // お天気情報の取得処理を行うメソッド。
    private fun receiveWeatherInfo(urlFull: String) {
        // ここに非同期で天気情報を取得する処理を記述する。
    }

    // リストがタップされたときの処理が記述されたリスナクラス。
    private inner class ListItemClickListener: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val item = _list.get(position)
            val q = item.get("q")
            q?.let {
                val urlFull = "$WEATHERINFO_URL&q&appid=$APP_ID"
                receiveWeatherInfo(urlFull)
            }
        }
    }
}