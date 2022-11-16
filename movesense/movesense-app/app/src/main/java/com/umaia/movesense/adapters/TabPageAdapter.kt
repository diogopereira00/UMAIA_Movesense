package com.umaia.movesense

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.umaia.movesense.fragments.Home
import com.umaia.movesense.fragments.Settings
import com.umaia.movesense.fragments.Surveys

class TabPageAdapter(activity: FragmentActivity, private val tabCount: Int) : FragmentStateAdapter(activity){

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when (position)
        {
            0 -> Home()
            1 -> Surveys()
            2 -> Settings()
            else -> Home()
        }
    }
}