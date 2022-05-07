package com.example.instaclone

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instaclone.databinding.ActivityCreatePostBinding
import com.example.instaclone.models.Post
import com.example.instaclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private lateinit var firestoreDB : FirebaseFirestore
private lateinit var storageReference: StorageReference
private const val TAG = "CreatePostActivity"
private const val PICK_PHOTO_CODE = 1234
private lateinit var binding : ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {

    private var photoUri : Uri? = null
    private var signedInUser : User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // get signed in user
        storageReference = FirebaseStorage.getInstance().reference
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


        //connect submit button to command
        binding.submitBtn.setOnClickListener {
            submitPost()
        }


        // connect image picker button to command
        binding.imgPickerBtn.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            val imgPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imgPickerIntent.type = "image/*"
            // Caller
            getResult.launch(imgPickerIntent)

        }

    }


    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                //val value = it.data?.getStringExtra("input")
                photoUri = it.data?.data
                binding.imageIV.setImageURI(photoUri)
            }
        }



    private fun submitPost(){


        if(photoUri==null){
            Toast.makeText(this, "No photo selected", Toast.LENGTH_LONG).show()
            return
        }

        if(binding.descrET.text.isBlank()){
            Toast.makeText(this, "Description can't be empty", Toast.LENGTH_LONG).show()
            return
        }

        if (signedInUser==null){
            Toast.makeText(this, "No signed in user, please wait", Toast.LENGTH_LONG).show()
            return
        }

        binding.submitBtn.isEnabled = false
        val photoRef = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        // upload image to firebase
        photoRef.putFile(photoUri!!)
            .continueWithTask {photoUploadTask ->
                Log.i(TAG,"uploaded bytes: ${photoUploadTask.result.bytesTransferred}")
                // get url of image
                photoRef.downloadUrl
            }.continueWithTask { downloadUrlTask ->
                // create post object
                val post = Post(
                    binding.descrET.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)
                firestoreDB.collection("posts").add(post)
            }.addOnCompleteListener { postCreationTask ->
                binding.submitBtn.isEnabled = true
                if(!postCreationTask.isSuccessful){
                    Log.i(TAG, "Exceptiond during Firebase operations", postCreationTask.exception)
                    Toast.makeText(this, "Failed to save post.", Toast.LENGTH_LONG).show()
                }

                // else, the post was created successfully
                // clear out the fields
                //binding.imageIV.setImageResource(null!!)
                binding.imageIV.setImageResource(0)
                binding.descrET.text.clear()
                Toast.makeText(this, "Post created successfully", Toast.LENGTH_LONG).show()

                val profileIntent = Intent(this, ProfileActivity::class.java)
                intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                startActivity(profileIntent)
                finish()

            }

    }






}




