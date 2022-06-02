package com.tapblaze.pizzabus.views

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.tapblaze.pizzabus.R
import com.tapblaze.pizzabus.databinding.GameJokerGirlBinding

class JokerGirlGame : AppCompatActivity() {
    private var _binding: GameJokerGirlBinding? = null
    private val binding get() = _binding!!
    private val imagesMap: Map<Int, Int> = mapOf(
        0 to R.drawable.joker0,
        1 to R.drawable.joker1,
        2 to R.drawable.joker2,
        3 to R.drawable.joker3,
    )
    private val successList: List<Int> = imagesMap.keys.shuffled()
    private val guessList: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = GameJokerGirlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initGame()
        initClickListeners()
        AnimatorSet().apply {
            playSequentially(
                flipCard(binding.back0, binding.iv0),
                flipCard(binding.iv0, binding.back0),
                flipCard(binding.back1, binding.iv1),
                flipCard(binding.iv1, binding.back1),
                flipCard(binding.back2, binding.iv2),
                flipCard(binding.iv2, binding.back2),
                flipCard(binding.back3, binding.iv3),
                flipCard(binding.iv3, binding.back3),
            )
            start()
        }.doOnEnd {
            binding.ivJoker0.isClickable = true
            binding.ivJoker1.isClickable = true
            binding.ivJoker2.isClickable = true
            binding.ivJoker3.isClickable = true
        }

    }

    private fun initClickListeners() {
        listOf(
            binding.ivJoker0,
            binding.ivJoker1,
            binding.ivJoker2,
            binding.ivJoker3,
        ).forEach {
            it.setOnClickListener { guessView ->
                if (guessList.count() < 4) {
                    when (guessView.id) {
                        R.id.ivJoker0 -> {
                            guessList.add(0)
                            imageViewToSet().visibility = View.VISIBLE
                            imageViewToSet().setImageResource(R.drawable.joker0)
                        }
                        R.id.ivJoker1 -> {
                            guessList.add(1)
                            imageViewToSet().visibility = View.VISIBLE
                            imageViewToSet().setImageResource(R.drawable.joker1)
                        }
                        R.id.ivJoker2 -> {
                            guessList.add(2)
                            imageViewToSet().visibility = View.VISIBLE
                            imageViewToSet().setImageResource(R.drawable.joker2)
                        }
                        R.id.ivJoker3 -> {
                            guessList.add(3)
                            imageViewToSet().visibility = View.VISIBLE
                            imageViewToSet().setImageResource(R.drawable.joker3)
                        }
                    }
                }
                if (guessList.count() == 4) finishGame()
            }
        }

        binding.btnTryAgain.setOnClickListener {
            startActivity(Intent(this, JokerGirlGame::class.java))
            finish()
        }
    }

    private fun finishGame() {
        binding.ivJoker0.isClickable = false
        binding.ivJoker1.isClickable = false
        binding.ivJoker2.isClickable = false
        binding.ivJoker3.isClickable = false

        AnimatorSet().apply {
            playTogether(
                flipCard(binding.back0, binding.iv0),
                flipCard(binding.back1, binding.iv1),
                flipCard(binding.back2, binding.iv2),
                flipCard(binding.back3, binding.iv3),
            )
            start()
        }

        if (successList == guessList) {
            Toast.makeText(this, "You won", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "You lose", Toast.LENGTH_LONG).show()
        }

        binding.btnTryAgain.visibility = View.VISIBLE
    }

    private fun imageViewToSet(): ImageView = when (guessList.count()) {
        1 -> binding.ivGuess0
        2 -> binding.ivGuess1
        3 -> binding.ivGuess2
        else -> binding.ivGuess3
    }

    private fun initGame() {
        binding.iv0.setImageResource(imagesMap[successList[0]]!!)
        binding.iv1.setImageResource(imagesMap[successList[1]]!!)
        binding.iv2.setImageResource(imagesMap[successList[2]]!!)
        binding.iv3.setImageResource(imagesMap[successList[3]]!!)

    }

    private fun flipCard(start: ImageView, end: ImageView): AnimatorSet {
        val frontAnim = AnimatorInflater
            .loadAnimator(applicationContext, R.animator.front_animator) as AnimatorSet
        val backAnim = AnimatorInflater
            .loadAnimator(applicationContext, R.animator.back_animator) as AnimatorSet
        frontAnim.setTarget(start)
        backAnim.setTarget(end)
        return AnimatorSet().apply {
            playTogether(frontAnim, backAnim)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}