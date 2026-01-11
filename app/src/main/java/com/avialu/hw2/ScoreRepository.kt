package com.avialu.hw2

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ScoresRepository(context: Context) {

    private val prefs = context.getSharedPreferences("scores_prefs", Context.MODE_PRIVATE)

    fun add(record: ScoreRecord) {
        val all = getAll().toMutableList()
        all.add(record)
        saveAll(all)
    }

    fun top10(): List<ScoreRecord> {
        return getAll()
            .sortedWith(compareByDescending<ScoreRecord> { it.score }.thenByDescending { it.distance })
            .take(10)
    }

    private fun getAll(): List<ScoreRecord> {
        val raw = prefs.getString("scores_json", "[]") ?: "[]"
        val arr = JSONArray(raw)
        val out = ArrayList<ScoreRecord>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(
                ScoreRecord(
                    score = o.getInt("score"),
                    distance = o.getInt("distance"),
                    timestamp = o.getLong("timestamp"),
                    lat = o.getDouble("lat"),
                    lng = o.getDouble("lng")
                )
            )
        }
        return out
    }

    private fun saveAll(list: List<ScoreRecord>) {
        val arr = JSONArray()
        for (r in list) {
            val o = JSONObject()
            o.put("score", r.score)
            o.put("distance", r.distance)
            o.put("timestamp", r.timestamp)
            o.put("lat", r.lat)
            o.put("lng", r.lng)
            arr.put(o)
        }
        prefs.edit().putString("scores_json", arr.toString()).apply()
    }
}
