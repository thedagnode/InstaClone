package com.example.instaclone

import android.R
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instaclone.databinding.ItemPostBinding
import com.example.instaclone.models.Post


class PostsAdapter(val context: Context, private val posts : List<Post>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>()
{



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val view = ItemPostBinding.inflate(LayoutInflater.from(context) )

        // I had trouble getting my cardview to stretch to the width of the screen. I opted to put
        // the cardview in a relative layout which fixed it.
        // Otherwise, I can get the parent width of the screen like this and stretch it to fit.
        //val cardWidth = parent.width
        //view.postIV.layoutParams.width = cardWidth

        return ViewHolder(view)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }


    override fun getItemCount() = posts.size











    inner class ViewHolder(private val itemPostBinding: ItemPostBinding)
        : RecyclerView.ViewHolder(itemPostBinding.root) {

        fun bind(post: Post){
            itemPostBinding.usernameTV.text = post.user?.username
            itemPostBinding.descrTV.text = post.description

            val imageView: ImageView = itemPostBinding.postIV


            Glide.with(context).load(post.imageURL).into(itemPostBinding.postIV)

            itemPostBinding.timestampTV.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)


        }

    }




}