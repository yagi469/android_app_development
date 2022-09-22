package com.websarva.wings.android.asynccoroutinesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

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

    // リストがタップされたときの処理が記述されたリスナクラス。
    private inner class ListItemClickListener: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val item = _list.get(position)
            val q = item.get("q")
            q?.let {
                val urlFull = "$WEATHERINFO_URL&q=$q&appid=$APP_ID"
                receiveWeatherInfo(urlFull)
            }
        }
    }

    @UiThread
    private fun receiveWeatherInfo(urlFull: String) {
        // ここにコルーチンに関するコードを記述
        lifecycleScope.launch {
            val result = weatherInfoBackgroundRunner(urlFull)
            weatherInfoPostRunner(result)
        }
    }

    @WorkerThread
    private suspend fun weatherInfoBackgroundRunner(url: String): String {
        val returnVal = withContext(Dispatchers.IO) {
            var result = ""
            val url = URL(url)
            val con = url.openConnection() as? HttpURLConnection
            con?.let {
                try {
                    it.connectTimeout = 1000
                    it.readTimeout = 1000
                    it.requestMethod = "GET"
                    it.connect()
                    val stream = it.inputStream
                    result = is2String(stream)
                    stream.close()
                }
                catch(ex: SocketTimeoutException) {
                    Log.w(DEBUG_TAG, "通信タイムアウト", ex)
                }
                it.disconnect()
            }
            result
        }
        return returnVal
    }

    private fun is2String(stream: InputStream): String {
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while(line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

    @UiThread
    private fun weatherInfoPostRunner(result: String) {
        val rootJSON = JSONObject(result)
        val cityName = rootJSON.getString("name")
        val coordJSON = rootJSON.getJSONObject("coord")
        val latitude = coordJSON.getString("lat")
        val longitude = coordJSON.getString("lon")
        val weatherJSONArray = rootJSON.getJSONArray("weather")
        val weatherJSON = weatherJSONArray.getJSONObject(0)
        val weather = weatherJSON.getString("description")
        val telop = "${cityName}の天気"
        val desc = "現在は${weather}です。\n緯度は${latitude}度で経度は${longitude}度です。"
        val tvWeatherTelop = findViewById<TextView>(R.id.tvWeatherTelop)
        val tvWeatherDesc = findViewById<TextView>(R.id.tvWeatherDesc)
        tvWeatherTelop.text = telop
        tvWeatherDesc.text = desc
    }
}