package com.example.newsline.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsline.R
import com.example.newsline.domain.models.Article
import java.security.PrivateKey
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.Collection

class RecyclerAdapter(
    private val context: Context,
    private val items: MutableList<Article>
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val publishedAtTextView: TextView = itemView.findViewById(R.id.publishedAtTextView)
        val toArticleHyperLinkTextView: TextView =
            itemView.findViewById(R.id.toArticleHyperLinkTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_list_element, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!items[position].urlToImage.isNullOrEmpty()) {
            Glide.with(context)
                .load(items[position].urlToImage)
                .centerCrop()
                .placeholder(androidx.appcompat.R.drawable.abc_ic_menu_selectall_mtrl_alpha)
                .into(holder.imageView)

        } else holder.imageView.visibility = GONE

        holder.titleTextView.text = items[position].title

        holder.authorTextView.text = items[position].author

        if (!items[position].description.isNullOrEmpty()) {
            holder.descriptionTextView.text = items[position].description
        } else holder.descriptionTextView.visibility = GONE

        if (!items[position].publishedAt.isNullOrEmpty()) {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val date: Date = inputDateFormat.parse(items[position].publishedAt)!!
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val outputDateString = outputDateFormat.format(date)
            holder.publishedAtTextView.text = outputDateString
        } else holder.publishedAtTextView.visibility = GONE

        if (!items[position].url.isNullOrEmpty()) {
            holder.toArticleHyperLinkTextView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(items[position].url)
                startActivity(context, intent, null)
            }
        } else holder.toArticleHyperLinkTextView.visibility = GONE
    }

    override fun getItemCount() = items.size
}
