package com.example.pract7_v8

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.FurnitureTypeRepository
import com.example.pract7_v8.databinding.FragmentFurnitureEditorBinding
import com.example.pract7_v8.FurnitureTypeViewModel
import com.example.pract7_v8.FurnitureTypeViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream

class FurnitureTypeEditorFragment : Fragment() {

    private var _binding: FragmentFurnitureEditorBinding? = null
    private val binding get() = _binding!!

    private val furnitureTypeId: Int by lazy {
        arguments?.getInt("furnitureTypeId", -1) ?: -1
    }

    private var selectedImageUri: Uri? = null
    private var savedImagePath: String? = null

    // ✅ Лаунчер для выбора изображения
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Показываем превью
            Picasso.get()
                .load(it)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .fit()
                .into(binding.ivPreview)

            // Сохраняем путь для БД
            savedImagePath = saveImageToStorage(it)
            binding.etImageUrl.setText(savedImagePath ?: "")
        }
    }

    private val viewModel: FurnitureTypeViewModel by viewModels {
        FurnitureTypeViewModelFactory(
            FurnitureTypeRepository(AppDatabase.getDatabase(requireContext()).furnitureTypeDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFurnitureEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        // Кнопка назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Загрузка данных если редактируем
        if (furnitureTypeId > 0) {
            binding.toolbar.title = "✏️ Редактировать тип"
            loadFurnitureType(furnitureTypeId)
        } else {
            binding.toolbar.title = "➕ Новый тип мебели"
        }

        // Кнопка: выбрать изображение из галереи
        binding.btnPickImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Кнопка: сохранить
        binding.btnSave.setOnClickListener {
            saveFurnitureType()
        }

        // Кнопка: отмена
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    // ✅ Сохранение изображения в хранилище приложения
    private fun saveImageToStorage(uri: Uri): String? {
        return try {
            val fileName = "furniture_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, fileName)

            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath  // Возвращаем путь к файлу
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ✅ Загрузка изображения из пути
    private fun loadImageFromPath(path: String?) {
        if (!path.isNullOrBlank()) {
            val file = File(path)
            if (file.exists()) {
                Picasso.get()
                    .load(file)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .fit()
                    .into(binding.ivPreview)
            }
        }
    }

    private fun loadFurnitureType(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val ft = db.furnitureTypeDao().getFurnitureTypeById(id)

            withContext(Dispatchers.Main) {
                if (ft != null && isAdded) {
                    binding.etName.setText(ft.name)
                    binding.etDescription.setText(ft.description)
                    binding.etBasePrice.setText(ft.basePrice.toString())

                    // Загружаем картинку из пути (не URL)
                    savedImagePath = ft.imageUrl
                    loadImageFromPath(savedImagePath)
                }
            }
        }
    }

    private fun saveFurnitureType() {
        val name = binding.etName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val basePrice = binding.etBasePrice.text.toString().toDoubleOrNull() ?: 0.0

        if (name.isEmpty()) {
            binding.etName.error = "Введите название"
            return
        }

        if (basePrice < 0) {
            binding.etBasePrice.error = "Неверная цена"
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())

                if (furnitureTypeId <= 0) {
                    // Добавление нового
                    db.furnitureTypeDao().insertFurnitureType(
                        com.example.pract7_v8.db.FurnitureType(
                            0, name, description.ifBlank { null }, basePrice, savedImagePath
                        )
                    )
                    withContext(Dispatchers.Main) {
                        if (isAdded) Toast.makeText(requireContext(), "✅ Тип добавлен", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Редактирование
                    db.furnitureTypeDao().updateFurnitureType(
                        com.example.pract7_v8.db.FurnitureType(
                            furnitureTypeId, name, description.ifBlank { null }, basePrice, savedImagePath
                        )
                    )
                    withContext(Dispatchers.Main) {
                        if (isAdded) Toast.makeText(requireContext(), "✅ Изменения сохранены", Toast.LENGTH_SHORT).show()
                    }
                }

                withContext(Dispatchers.Main) {
                    if (isAdded) findNavController().navigateUp()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}