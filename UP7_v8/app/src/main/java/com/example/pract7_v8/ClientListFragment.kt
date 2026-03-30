package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.UserDao
import com.example.pract7_v8.db.User
import com.example.pract7_v8.db.UserRole
import com.example.pract7_v8.databinding.FragmentClientListBinding
import com.example.pract7_v8.databinding.ItemClientBinding
import com.example.pract7_v8.AuthViewModel
import com.example.pract7_v8.AuthViewModelFactory
import com.example.pract7_v8.AuthRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController





import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext







import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.isActive


class ClientListFragment : Fragment() {

    private var _binding: FragmentClientListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ClientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        // Кнопка назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }



        // RecyclerView
        adapter = ClientAdapter(
            onEditClick = { client ->
                val bundle = Bundle()
                bundle.putInt("clientId", client.id)
                findNavController().navigate(R.id.clientEditorFragment, bundle)
            },
            onBlacklistClick = { client -> toggleBlacklist(client) }
        )
        binding.recyclerViewClients.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewClients.adapter = adapter

        loadClients()

        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    private fun loadClients() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val clients = withContext(Dispatchers.IO) {
                    db.userDao().getClientsList()
                }

                if (!isActive || !isAdded || _binding == null) return@launch

                adapter.submitList(clients ?: emptyList())

                val count = clients?.size ?: 0
                binding.tvTotalClients.text = "👥 Всего клиентов: $count"

                if (clients.isNullOrEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.recyclerViewClients.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.recyclerViewClients.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "❌ Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ✅ ЧЁРНЫЙ СПИСОК — добавить/убрать
    private fun toggleBlacklist(client: User) {
        val action = if (client.isBlacklisted) {
            "разблокировать"
        } else {
            "добавить в чёрный список"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Подтверждение")
            .setMessage("Вы действительно хотите $action клиента \"${client.name}\"?")
            .setPositiveButton("Да") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val db = AppDatabase.getDatabase(requireContext())
                        withContext(Dispatchers.IO) {
                            db.userDao().setBlacklisted(client.id, !client.isBlacklisted)
                        }
                        if (isActive && isAdded) {
                            val message = if (client.isBlacklisted) {
                                "✅ Клиент разблокирован"
                            } else {
                                "🚫 Клиент в чёрном списке"
                            }
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            loadClients()
                        }
                    } catch (e: Exception) {
                        if (isActive && isAdded) {
                            Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}