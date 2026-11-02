package com.runtrack

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla principal de RunTrack.
 * Muestra el estado de los sensores y las lecturas del acelerometro y el
 * giroscopio en tiempo real. Los listeners se registran solo mientras la
 * pantalla esta visible, para cuidar la bateria del dispositivo.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var gestor: GestorSensores
    private lateinit var txtEstado: TextView
    private lateinit var txtAcel: TextView
    private lateinit var txtGiro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtEstado = findViewById(R.id.txtEstado)
        txtAcel = findViewById(R.id.txtAcel)
        txtGiro = findViewById(R.id.txtGiro)

        // El gestor avisa a la interfaz cada vez que hay una lectura valida
        gestor = GestorSensores(this) { tipo, v ->
            runOnUiThread {
                when (tipo) {
                    TipoSensor.ACELEROMETRO ->
                        txtAcel.text = "Acelerometro (m/s2)\nX: %.2f   Y: %.2f   Z: %.2f".format(v[0], v[1], v[2])
                    TipoSensor.GIROSCOPIO ->
                        txtGiro.text = "Giroscopio (rad/s)\nX: %.2f   Y: %.2f   Z: %.2f".format(v[0], v[1], v[2])
                }
            }
        }

        mostrarDisponibilidad()
    }

    /** Verifica y muestra si el dispositivo cuenta con los sensores. */
    private fun mostrarDisponibilidad() {
        val acel = if (gestor.acelerometroDisponible()) "disponible" else "NO disponible"
        val giro = if (gestor.giroscopioDisponible()) "disponible" else "NO disponible"
        txtEstado.text = "Estado de los sensores\nAcelerometro: $acel\nGiroscopio: $giro"
    }

    // Registra los sensores cuando la app vuelve al frente
    override fun onResume() {
        super.onResume()
        gestor.iniciar()
    }

    // Libera los sensores cuando la app pasa a segundo plano (ahorro de bateria)
    override fun onPause() {
        super.onPause()
        gestor.detener()
    }
}
