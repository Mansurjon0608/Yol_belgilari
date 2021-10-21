package com.example.yolbelgilari

import Adapters.AdapterCategory
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.yolbelgilari.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.tab_item.view.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var binding: FragmentHomeBinding
    lateinit var categoryAdapter: AdapterCategory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(context))

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("OnStart", "HomeFragment")
        categoryAdapter = AdapterCategory(activity!!)
        binding.viewPager.adapter = categoryAdapter
        binding.viewPager.isUserInputEnabled = false

        val tabList = ArrayList<Int>()
        tabList.add(R.drawable.ic_baseline_home_24)
        tabList.add(R.drawable.ic_baseline_favorite_24)
        tabList.add(R.drawable.ic_baseline_info)
        TabLayoutMediator(binding.tabLayout, binding.viewPager
        ) { tab, position ->
            val tabView = LayoutInflater.from(context).inflate(R.layout.tab_item, null, false)

            tab.customView = tabView

            tabView.tab_item_image.setImageResource(tabList[position])

            binding.viewPager.setCurrentItem(tab.position, false)
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().navigate(R.id.addFragment, bundleOf("key" to -1))
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}