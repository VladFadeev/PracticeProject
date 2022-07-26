package com.example.practiceproject.utils

import android.content.Context
import com.example.practiceproject.data.places.Sight
import org.json.JSONArray
import java.io.InputStream

object JsonUtils {

    fun readJsonFromAssets(context: Context): String {
        var stream: InputStream? = null
        return try {
            stream = context.assets.open("sights.json")
            val size: Int = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer, Charsets.UTF_8)
        } finally {
            stream?.close()
        }
    }

    fun createSightsList(str: String): List<Sight> {
        val array = JSONArray(str)
        val list = ArrayList<Sight>()
        for (i in 0 until array.length()) {
            list.add(Sight(array.getJSONObject(i).getString("name"),
                array.getJSONObject(i).getDouble("latitude").toFloat(),
                array.getJSONObject(i).getDouble("longitude").toFloat()))
        }
        return list
    }
}