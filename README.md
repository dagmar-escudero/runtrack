# RunTrack - Monitor Deportivo (Gestion de Sensores)

Aplicacion Android para corredores que mide el movimiento y los cambios de
direccion usando el **acelerometro** y el **giroscopio**. Captura y filtra las
lecturas en tiempo real para evitar lecturas excesivas o duplicadas y cuidar la
bateria del dispositivo.

## Caracteristicas
- Verifica la disponibilidad fisica/virtual de los sensores.
- Habilita los listeners solo mientras la pantalla esta visible (ciclo de vida).
- Filtra las lecturas por tiempo (intervalo minimo) y por umbral (evita duplicadas).
- Muestra los valores de cada sensor en tiempo real en la interfaz.

## Estructura
- `app/src/main/java/com/runtrack/GestorSensores.kt` - manejo y filtrado de sensores.
- `app/src/main/java/com/runtrack/MainActivity.kt` - pantalla principal y ciclo de vida.
- `app/src/main/res/layout/activity_main.xml` - interfaz.

Asignatura: Programacion de Aplicaciones Moviles - AVS.03.03.01 Gestion de sensores.
