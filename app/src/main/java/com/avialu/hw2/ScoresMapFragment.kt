package com.avialu.hw2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ScoresMapFragment : Fragment() {

    private val vm: RecordsViewModel by activityViewModels()
    private var gMap: GoogleMap? = null
    private var pending: ScoreRecord? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_scores_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFrag = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFrag)
            .commit()

        vm.selected.observe(viewLifecycleOwner) { rec ->
            if (rec != null) {
                pending = rec
                renderIfReady()
            }
        }

        mapFrag.getMapAsync { map ->
            gMap = map
            renderIfReady()
        }
    }

    private fun renderIfReady() {
        val map = gMap ?: return
        val rec = pending ?: return
        val pos = LatLng(rec.lat, rec.lng)
        map.clear()
        map.addMarker(MarkerOptions().position(pos))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14f))
    }
}
