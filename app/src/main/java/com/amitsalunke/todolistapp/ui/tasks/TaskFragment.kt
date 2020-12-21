package com.amitsalunke.todolistapp.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amitsalunke.todolistapp.R
import com.amitsalunke.todolistapp.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

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
    }
}