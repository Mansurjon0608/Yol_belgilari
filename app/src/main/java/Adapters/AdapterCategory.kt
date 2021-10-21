package Adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.yolbelgilari.InfoFragment
import com.example.yolbelgilari.LikedFragment
import com.example.yolbelgilari.MyHomeFragment

class AdapterCategory(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity!!) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        Log.d("onPosition", "$position")
        return when(position){
            0 -> MyHomeFragment()
            1 -> LikedFragment()
            2 -> InfoFragment()
            else -> MyHomeFragment()
        }
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        Log.d("onBindPosition", "$position")
    }
}