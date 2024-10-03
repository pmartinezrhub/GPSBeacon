package com.example.gpsbeacon;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gpsbeacon.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private URLManager urlManager;
    private String deviceID;
    private ServiceBeacon serviceBeacon;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa URLManager
        urlManager = new URLManager(requireContext());

        // Configura el botón para navegar al segundo fragmento
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceID = getDeviceID();
                deviceID = deviceID + "_SOS";
                // Envía el nuevo deviceID al servicio
                Intent intent = new Intent(requireContext(), ServiceBeacon.class);
                intent.setAction(ServiceBeacon.ACTION_UPDATE_DEVICE_ID);
                intent.putExtra(ServiceBeacon.EXTRA_DEVICE_ID, deviceID);
                requireContext().startService(intent);
                System.out.println("changed to " + deviceID);
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        binding.buttonRecordRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), ServiceBeacon.class);
                intent.setAction(ServiceBeacon.ACTION_START_SAVING);
                requireContext().startService(intent);
                Toast.makeText(getContext(), "Inicio ruta", Toast.LENGTH_SHORT).show();
                System.out.println("start recordin route");
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_RecordFragment);
            }
        });

        // Configura el botón para guardar la URL
        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Accede al EditText utilizando el binding
                EditText editText = binding.serverUrl; // 'serverUrl' debe ser el ID de tu EditText en el XML
                String serverUrl = editText.getText().toString();

                // Guarda la URL utilizando URLManager
                urlManager.saveURL(serverUrl);

                // Opcionalmente, muestra un mensaje para confirmar el guardado
                System.out.println("URL guardada: " + serverUrl);
            }
        });

        binding.buttonFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_FileListFragment);
            }
        });
    }
    private String getDeviceID() {
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceID;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
