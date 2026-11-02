package com.runtrack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

/** Tipo de sensor que notifica el gestor hacia la interfaz. */
enum class TipoSensor { ACELEROMETRO, GIROSCOPIO }

/**
 * Gestiona el acelerometro y el giroscopio del dispositivo para la app RunTrack.
 * Verifica la disponibilidad de los sensores, habilita la escucha (listeners)
 * y filtra las lecturas para no capturar datos en exceso ni duplicados,
 * evitando asi un consumo innecesario de bateria.
 */
class GestorSensores(
    context: Context,
    private val alActualizar: (TipoSensor, FloatArray) -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Verificacion de disponibilidad: getDefaultSensor devuelve null si no existe
    private val acelerometro: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val giroscopio: Sensor?   = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // --- Parametros de filtrado ---
    private val intervaloMinMs = 100L   // tiempo minimo entre lecturas procesadas
    private val umbral = 0.15f          // cambio minimo para considerar una lectura "nueva"
    private var ultimoAcelMs = 0L
    private var ultimoGiroMs = 0L
    private var ultimoValorAcel = FloatArray(3)

    fun acelerometroDisponible(): Boolean = acelerometro != null
    fun giroscopioDisponible(): Boolean = giroscopio != null

    /** Habilita (registra) los sensores con una frecuencia adecuada para correr. */
    fun iniciar() {
        acelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        giroscopio?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    /** Libera los sensores para no seguir gastando bateria cuando no se usan. */
    fun detener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val ahora = System.currentTimeMillis()
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (ahora - ultimoAcelMs < intervaloMinMs) return      // filtra exceso de lecturas
                if (!cambioSignificativo(event.values)) return         // filtra lecturas duplicadas
                ultimoAcelMs = ahora
                ultimoValorAcel = event.values.clone()
                alActualizar(TipoSensor.ACELEROMETRO, event.values)
            }
            Sensor.TYPE_GYROSCOPE -> {
                if (ahora - ultimoGiroMs < intervaloMinMs) return
                ultimoGiroMs = ahora
                alActualizar(TipoSensor.GIROSCOPIO, event.values)
            }
        }
    }

    /** Devuelve true solo si la lectura cambio mas que el umbral (no es duplicada). */
    private fun cambioSignificativo(nuevo: FloatArray): Boolean {
        for (i in nuevo.indices) {
            if (abs(nuevo[i] - ultimoValorAcel[i]) > umbral) return true
        }
        return false
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) { /* sin uso */ }
}
