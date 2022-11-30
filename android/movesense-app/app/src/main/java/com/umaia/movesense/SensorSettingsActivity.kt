package com.umaia.movesense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umaia.movesense.adapters.SensorSettingsAdapter
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.databinding.ActivitySensorSettingsBinding
import com.umaia.movesense.model.SensorSettingsClass

class SensorSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySensorSettingsBinding
    lateinit var gv: GlobalClass

    private lateinit var userPreferences: UserPreferences

    private var sensorsSettingsList: ArrayList<SensorSettingsClass> = ArrayList()
    private lateinit var sensorsSettingsAdapter: SensorSettingsAdapter
    private lateinit var sensorsSettingssRecyclerView: RecyclerView

    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gv = application as GlobalClass
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        userPreferences = UserPreferences(this)
        gerarLista()
        sensorsSettingsAdapter = SensorSettingsAdapter(this, sensorsSettingsList, this, viewModel)
        sensorsSettingssRecyclerView = binding.recyclerViewDefinicoes
        sensorsSettingssRecyclerView.layoutManager = LinearLayoutManager(this)
        sensorsSettingssRecyclerView.adapter = sensorsSettingsAdapter
    }

    private fun gerarLista() {
        sensorsSettingsList.clear()
        sensorsSettingsList.add(
            SensorSettingsClass(
                id = "Acelerometro",
                name = "Acelerômetro",
                description = "O acelarometro é capaz de fornecer muitos dados úteis sobre movimento, aceleração e choques. Os dados do sensor também podem ser usados para cálculo de posição usando algoritmos AHRS. Para um trabalho eficiente com acelerómetro, deve ser tomado em consideração o consumo de energia e a frequência do acelerómetro.",
                frequency = 0,
                isActive = false
            )
        )
        sensorsSettingsList.add(
            SensorSettingsClass(
                id = "Giroscopio",
                name = "Giroscópio",
                description = "O giroscópio fornece um monte de dados úteis para medir ou manter a orientação e a velocidade angular. Para um trabalho eficiente com giroscópio, o consumo de energia e a frequência do giroscópio devem ser tomados em consideração.",
                frequency = 0,
                isActive = false
            )
        )
        sensorsSettingsList.add(
            SensorSettingsClass(
                id = "Magnetometro",
                name = "Magnetômetro",
                description = "O magnetómetro é capaz de fornecer um monte de dados úteis para medir a direção, a força ou a mudança relativa de um campo magnético num determinado local. Para um trabalho eficiente com o consumo de energia do magnetómetro, deve-se ter em conta a frequência do magnetómetro.",
                frequency = 0,
                isActive = false
            )
        )

        //TODO The Meas/IMU -API provides a synchronized access to accelerometer, gyroscope and magnetometer datastreams for easier processing e.g. for AHRS algorithms.
        // It is more efficient to subscribe to the IMU resource than to subscribe the individual resources separately.
        // /Meas/IMU6: Combined Acc & Gyro
        // /Meas/IMU6m: Combined Acc & Magn
        // /Meas/IMU9: Combined Acc, Gyro & Magn

        sensorsSettingsList.add(
            SensorSettingsClass(
                id = "ECG",
                name = "ECG Eletrocardiograma",
                description = "O sensor Movesense está equipado com uma extremidade frontal analógica capaz de capturar sinais ECG. É aconselhado a utilização da banda do peito para melhores resultados. Para um trabalho eficiente com o consumo de energia durante o ECG deve-se ter em conta a frequência do ECG.",
                frequency = 0,
                isActive = false
            )
        )
        sensorsSettingsList.add(
            SensorSettingsClass(
                id = "HR",
                name = "Frequência Cardiaca",
                description = "O sensor Movesense está equipado com uma extremidade frontal analógica capaz de capturar sinais ECG e calcular o ritmo cardíaco do utilizador.",
                frequency = 0,
                isActive = false
            )
        )
        sensorsSettingsList.add(
            SensorSettingsClass(
                id = "Temperatura",
                name = "Temperatura",
                description = "O sensor Movesense está equipado com sensor de temperatura, que pode ser utilizado para medir a temperatura interna do dispositivo.",
                frequency = 0,
                isActive = false
            )
        )
    }
}