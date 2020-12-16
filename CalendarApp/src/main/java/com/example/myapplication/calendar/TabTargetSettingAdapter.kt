package com.example.myapplication.calendar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by yichao,hu on 2020/09/18 10:18.
 */
class TabTargetSettingAdapter(
        private val fragments: List<Fragment>,
        private val strings: List<String>, fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return strings.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return strings[position]
    }

}