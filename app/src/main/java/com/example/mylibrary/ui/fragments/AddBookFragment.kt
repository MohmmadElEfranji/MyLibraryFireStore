package com.example.mylibrary.ui.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.mylibrary.R
import com.example.mylibrary.databinding.FragmentAddBookBinding
import com.example.mylibrary.model.Book
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shasin.notificationbanner.Banner
import java.text.SimpleDateFormat
import java.util.*


class AddBookFragment : Fragment() {
    private val bookCollectionRef = Firebase.firestore.collection("Books")
    private var mRate: Float = 1.5f

    private lateinit var binding: FragmentAddBookBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //region Book review
        binding.rbRatingBar.stepSize = .5f

        binding.rbRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            mRate = rating
        }

        //endregion

        //region Add Button
        binding.btnAddBook.setOnClickListener {
            val idBook = bookCollectionRef.document().id
            val bookName = binding.edBookName.text.toString()
            val bookAuthor = binding.edBookAuthor.text.toString()
            val launchYear = binding.edLaunchYear.text
            val price = binding.edPrice.text.toString()
            val rate = mRate

            if (bookName.isNotEmpty() && bookAuthor.isNotEmpty() && launchYear.isNotEmpty() && price.isNotEmpty()) {
                val dateString = launchYear.toString()
                val formatter = SimpleDateFormat("yyyy", Locale.UK)
                val mLaunchYear = formatter.parse(dateString) as Date
                val book = Book(idBook, bookName, bookAuthor, mLaunchYear, price.toDouble(), rate)
                binding.progressBar.visibility = View.VISIBLE
                addBook(book)
            } else {
                Banner.make(
                    binding.root, requireActivity(), Banner.WARNING,
                    "Fill in all fields !!", Banner.TOP, 3000
                ).show()
            }


        }

        //endregion

        // region Change actionBar color
        val colorDrawable = ColorDrawable(Color.parseColor("#FFB300"))
        (activity as AppCompatActivity?)!!.supportActionBar!!.setBackgroundDrawable(colorDrawable)
        //endregion


    }

    private fun addBook(book: Book) {
        bookCollectionRef.document(book.bookId).set(book).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Banner.make(
                    binding.root, requireActivity(), Banner.SUCCESS,
                    "Addition succeeded :)", Banner.TOP, 3000
                ).show()

                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                findNavController().navigate(
                    R.id.action_addBookFragment_to_homeFragment,
                    null,
                    navOptions
                )
                binding.progressBar.visibility = View.GONE

            } else {
                Banner.make(
                    binding.root, requireActivity(), Banner.ERROR,
                    "Addition failed :(", Banner.TOP, 3000
                ).show()
            }

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
                findNavController().navigate(
                    R.id.action_addBookFragment_to_homeFragment,
                    null,
                    navOptions
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

}