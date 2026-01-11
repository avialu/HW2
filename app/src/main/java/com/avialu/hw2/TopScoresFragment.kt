package com.avialu.hw2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TopScoresFragment : Fragment() {

    private val vm: RecordsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_top_scores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val repo = ScoresRepository(requireContext())
        val items = repo.top10()

        rv.adapter = ScoresAdapter(items) { vm.select(it) }

        if (items.isNotEmpty()) vm.select(items[0])
    }

    private class ScoresAdapter(
        private val items: List<ScoreRecord>,
        private val onClick: (ScoreRecord) -> Unit
    ) : RecyclerView.Adapter<ScoresAdapter.VH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val r = items[position]
            holder.main.text = "Score: ${r.score}   Dist: ${r.distance}"
            holder.sub.text = "Lat: ${"%.5f".format(r.lat)}  Lng: ${"%.5f".format(r.lng)}"
            holder.itemView.setOnClickListener { onClick(r) }
        }

        override fun getItemCount(): Int = items.size

        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val main: TextView = v.findViewById(R.id.txt_main)
            val sub: TextView = v.findViewById(R.id.txt_sub)
        }
    }
}
