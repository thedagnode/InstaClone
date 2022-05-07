package com.example.instaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instaclone.databinding.ActivityPostsBinding
import com.example.instaclone.models.Post
import com.example.instaclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val TAG = "PostsActivity"
const val EXTRA_USERNAME = "EXTRA_USERNAME"

open class PostsActivity : AppCompatActivity() {

    private var signedInUser : User? = null
    private lateinit var binding : ActivityPostsBinding
    private lateinit var firestoreDB : FirebaseFirestore
    private lateinit var posts : MutableList<Post>
    private lateinit var adapter: PostsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        posts = mutableListOf()
        adapter = PostsAdapter(this, posts)

        binding.postsRV.adapter = adapter

        binding.postsRV.setHasFixedSize(true)
        binding.postsRV.layoutManager = LinearLayoutManager(this)


        firestoreDB = FirebaseFirestore.getInstance()
        firestoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "Signed in user: ${signedInUser?.username}")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "failed fetching signed in user ", exception)
            }

        // make a query to firestore to retrieve data

        var postsReference = firestoreDB
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        if(username != null){
            supportActionBar?.title = username
            postsReference = postsReference.whereEqualTo("user.username", username)
        }

        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception!=null || snapshot==null){
                Log.e(TAG, "exception when querying posts", exception)
                return@addSnapshotListener
            }

            else{
                val postList = snapshot.toObjects(Post::class.java)
                posts.clear()
                posts.addAll(postList)
                adapter.notifyDataSetChanged()
                for(post in postList){
                    Log.i(TAG, "Post: $post")
                }
            }


        }




        binding.createFAB.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }



}