package com.olrep.gitpulls.ui.main

import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.olrep.gitpulls.R
import com.olrep.gitpulls.callback.ClickListener
import com.olrep.gitpulls.model.Item
import com.olrep.gitpulls.utils.RoundedCorner
import com.olrep.gitpulls.utils.Utils
import com.squareup.picasso.Picasso

class Adapter(private val clickListener: ClickListener) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val TAG = Utils.TAG + "Ad"
    private val list = ArrayList<Item>()

    private val roundedCorner = RoundedCorner(16f)

    fun setData(items: List<Item>) {
        val prevSize = itemCount
        Log.d(TAG, "setData called. prevSize is $prevSize")
        list.addAll(items)

        if (items.isNotEmpty()) {
            Log.d(TAG, "Calling item range changed from $prevSize for items ${items.size}")
            notifyItemRangeInserted(prevSize, items.size)
        }
    }

    fun clear() {
        Log.d(TAG, "Clear called")
        list.clear()
        notifyDataSetChanged()
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

        Picasso.get().load(item.user.avatar_url).transform(roundedCorner).into(holder.avatar)

        holder.user.text = holder.itemView.context.getString(
            R.string.user_placeholder,
            "@",
            item.user.login
        )
        holder.user.paintFlags = holder.user.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.user.setOnClickListener { clickListener.clicked(item.user.html_url, item.user.login) }

        holder.createdAt.text = Utils.getTimeWLabel(item.created_at, true)
        holder.closedAt.text = Utils.getTimeWLabel(item.closed_at, false)

        val repo = Utils.getRepo(item.html_url)
        holder.repo.text = repo
        holder.repo.setOnClickListener { clickListener.clicked(Utils.getRepoUrl(item.html_url), repo) }
        holder.repo.paintFlags = holder.repo.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        val spannableString = SpannableString("#" + item.number + " " + item.title)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Log.d(TAG, "cs clicked")
                clickListener.clicked(item.html_url, item.title)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.typeface = Typeface.DEFAULT_BOLD
            }
        }
        spannableString.setSpan(
            clickableSpan,
            0,
            ((item.number.toString()).length + 1),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.title.text = spannableString
        holder.title.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.iv_author)
        val user: TextView = itemView.findViewById(R.id.tv_user)
        val createdAt: TextView = itemView.findViewById(R.id.tv_created)
        val closedAt: TextView = itemView.findViewById(R.id.tv_closed)
        val repo: TextView = itemView.findViewById(R.id.tv_repo)
        val title: TextView = itemView.findViewById(R.id.tv_title)
    }
}