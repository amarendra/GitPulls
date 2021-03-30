package com.olrep.gitpulls.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.olrep.gitpulls.R
import com.olrep.gitpulls.model.Item
import com.squareup.picasso.Picasso

class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val list = ArrayList<Item>()

    fun setData(items: List<Item>) {
        val prevSize = itemCount
        list.addAll(items)

        if (items.isNotEmpty()) {
            notifyItemRangeInserted(prevSize, items.size)
        }
    }

    fun clear() {
        val prevSize = itemCount
        list.clear()
        notifyItemRangeRemoved(0, prevSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_pull,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        Picasso.get().load(item.user.avatar_url)
            .into(holder.avatar)

        holder.author.text = item.user.login
        holder.title.text = item.title
        holder.createdAt.text = item.created_at
        holder.closedAt.text = item.closed_at
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.iv_author)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val author: TextView = itemView.findViewById(R.id.tv_athour)
        val createdAt: TextView = itemView.findViewById(R.id.tv_created)
        val closedAt: TextView = itemView.findViewById(R.id.tv_closed)
    }
}