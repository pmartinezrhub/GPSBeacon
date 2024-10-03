package com.example.gpsbeacon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;

public class FileListFragment extends Fragment {

    private ListView listView;
    private TextView titleTextView;
    private Button shareButton;
    private String selectedFile;  // Almacena el nombre del archivo seleccionado
    private Button backButton;
    private Button deleteButton;

    public FileListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_files, container, false);

        // Obtener las referencias de las vistas
        listView = view.findViewById(R.id.lv_files);
        shareButton = view.findViewById(R.id.button_share);
        backButton = view.findViewById(R.id.button_back);
        deleteButton = view.findViewById(R.id.button_delete);

        // Deshabilitar los botones hasta que se seleccione un archivo
        shareButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Cargar la lista de archivos y mostrarla
        displayFileList();

        // Configurar el botón de compartir
        shareButton.setOnClickListener(v -> shareFile(getActivity(), selectedFile));

        // Configurar el botón de eliminar
        deleteButton.setOnClickListener(v -> {
            if (selectedFile != null) {
                deleteFile(selectedFile);
            } else {
                Toast.makeText(getActivity(), "No file selected to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón de regresar
        backButton.setOnClickListener(view1 -> NavHostFragment.findNavController(FileListFragment.this)
                .navigate(R.id.action_FileListFragment_to_FirstFragment));

        return view;
    }

    private void displayFileList() {
        // Obtener el contexto para acceder al almacenamiento interno de la aplicación
        Context context = getActivity();
        if (context == null) {
            return;
        }

        // Obtener la lista de archivos guardados en el almacenamiento interno
        String[] files = context.fileList();

        // Comprobar si hay archivos disponibles
        if (files.length > 0) {
            // Mostrar la lista de archivos usando un ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, files);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  // Permite seleccionar solo un archivo

            // Manejar los clics en los elementos de la lista
            listView.setOnItemClickListener((parent, view, position, id) -> {
                selectedFile = adapter.getItem(position);  // Guardar el archivo seleccionado
                shareButton.setEnabled(true);  // Habilitar el botón de compartir
                deleteButton.setEnabled(true);  // Habilitar el botón de eliminar
                Toast.makeText(context, "Selected file: " + selectedFile, Toast.LENGTH_SHORT).show();
            });
        } else {
            // Si no hay archivos, mostrar un mensaje al usuario
            Toast.makeText(context, "No files found", Toast.LENGTH_SHORT).show();
            Log.d("FileListFragment", "No files found in the internal storage");
        }
    }

    private void shareFile(Context context, String filename) {
        if (filename == null || filename.isEmpty()) {
            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Crear un archivo en el almacenamiento interno
            File file = new File(context.getFilesDir(), filename);
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.gpsbeacon.fileprovider", file);

            // Intent para compartir el archivo
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Iniciar la actividad de compartir
            startActivity(Intent.createChooser(shareIntent, "Share file via"));
        } catch (Exception e) {
            Log.e("FileListFragment", "Error sharing file: " + e.getMessage());
            Toast.makeText(context, "Error sharing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFile(String fileName) {
        // Obtener el contexto de la actividad
        Context context = getActivity();
        if (context == null) {
            return;
        }

        // Eliminar el archivo del almacenamiento interno
        File file = new File(context.getFilesDir(), fileName);
        boolean deleted = file.delete();

        // Verificar si el archivo fue eliminado exitosamente
        if (deleted) {
            Toast.makeText(context, "File deleted: " + fileName, Toast.LENGTH_SHORT).show();
            selectedFile = null;  // Reiniciar el archivo seleccionado
            shareButton.setEnabled(false);  // Deshabilitar el botón de compartir
            deleteButton.setEnabled(false);  // Deshabilitar el botón de eliminar
            // Actualizar la lista de archivos en la vista
            displayFileList();
        } else {
            Toast.makeText(context, "Failed to delete file: " + fileName, Toast.LENGTH_SHORT).show();
        }
    }
}

