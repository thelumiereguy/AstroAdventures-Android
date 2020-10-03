package com.thelumierguy.galagatest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.ui.EnemiesView
import com.thelumierguy.galagatest.ui.EnemyShipView

class EnemiesAdapter(private val enemyList: List<EnemiesView.Enemy>) :
    RecyclerView.Adapter<EnemiesAdapter.EnemyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnemyViewHolder {
        return EnemyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_enemy_ship, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EnemyViewHolder, position: Int) {
        holder.enemyView.isVisible = enemyList[position].isVisible
    }

    override fun getItemCount(): Int {
        return enemyList.size
    }

    class EnemyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val enemyView: EnemyShipView = view.findViewById(R.id.enemy_view)
    }
}
