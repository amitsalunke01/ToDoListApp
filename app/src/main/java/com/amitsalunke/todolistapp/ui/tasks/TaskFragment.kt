package com.amitsalunke.todolistapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amitsalunke.todolistapp.R
import com.amitsalunke.todolistapp.data.SortOrder
import com.amitsalunke.todolistapp.databinding.FragmentTasksBinding
import com.amitsalunke.todolistapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks) {
    private val viewModel: TaskViewModel by viewModels()//also called as property delegate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTasksBinding.bind(view)
        val taskAdapter = TasksAdapter()

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager =
                    LinearLayoutManager(requireContext()) // requireContext() is fragment method
                setHasFixedSize(true)
            }
        }

        //for fragment we use viewLifeCycleOwner as there are multiple fragment and so view hiracy is destroid but instance is there in backstack
        //if we get update for that view and that view hirarcy is not present then the app is crashed 
        viewModel.tasks.observe(viewLifecycleOwner) {
            //submit list is function of list adapter
            taskAdapter.submitList(it)
            //once data is given to submit list then diffUtil takes care after this
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            //update search query
            viewModel.searchQuery.value = it
        }

        //to read the preferences

        //coroutine scope which leaves as the view of the fragment lives
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                    //as we want to read the a value only one time i.e starting we use first instead of collect
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                //viewModel.sortOrder.value = SortOrder.BY_NAME
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.action_sort_by_date_created -> {
                //viewModel.sortOrder.value = SortOrder.BY_DATE
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                //viewModel.hideCompleted.value = item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }

            R.id.action_delete_all_completed_tasks -> {
                true
            }
            else -> super.onOptionsItemSelected(item) //return false internally
        }
    }
}