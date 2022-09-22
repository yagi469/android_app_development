package com.websarva.wings.android.asyncsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors

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
    @UiThread
    private fun receiveWeatherInfo(urlFull: String) {
        // ここに非同期で天気情報を取得する処理を記述する。
        val handler = HandlerCompat.createAsync(mainLooper)
        val backgroundReceiver = WeatherInfoBackgroundReceiver(handler, urlFull)
        val executeService = Executors.newSingleThreadExecutor()
        executeService.submit(backgroundReceiver)
    }

    // 非同期でお天気情報APIにアクセスするためのクラス。
    private inner class WeatherInfoBackgroundReceiver(handler: Handler, url: String):
Runnable {
        // ハンドラオブジェクト。
        private val _handler = handler
        // お天気情報を取得するURL。
        private val _url = url

        @WorkerThread
        override fun run() {
            //　天気情報サービスから取得したJSON文字列。天気情報が格納されている。
            var result = ""
            // URLオブジェクトを生成。
            val url = URL(_url)
            // URLオブジェクトからHttpURLConnectionオブジェクトを取得。
            val con = url.openConnection() as? HttpURLConnection
            // conがnullじゃないならば…
            con?.let {
                try {
                    // 接続に使ってもよい時間を設定。
                    it.connectTimeout = 1000
                    // データ取得に使ってもよい時間。
                    it.readTimeout = 1000
                    // HTTP接続メソッドをGETに設定。
                    it.requestMethod = "GET"
                    // 接続。
                    it.connect()
                    // HttpURLConnectionオブジェクトからレスポンスデータを取得。
                    val stream = it.inputStream
                    // レスポンスデータであるInputStreamを文字列に変換。
                    result = is2String(stream)
                    // InputStreamオブジェクトを解放。
                    stream.close()
                }
                catch(ex: SocketTimeoutException) {
                    Log.w(DEBUG_TAG, "通信タイムアウト", ex)
                }
                // HttpURLConnectionオブジェクトを解放。
                it.disconnect()
            }
            val postExecutor = WeatherInfoPostExecutor()
            _handler.post(postExecutor)
        }

        private fun is2String(stream: InputStream): String {
            val sb = java.lang.StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()
            while(line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }

    // 非同期でお天気情報を取得した後にUIスレッドでその情報を表示するためのクラス。
    private inner class WeatherInfoPostExecutor(): Runnable {
        @UiThread
        override fun run() {
            // ここにUIスレッドで行う処理コードを記述
        }
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