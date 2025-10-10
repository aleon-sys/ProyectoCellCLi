# Aplicación de Gestor De Gastos

Esta es una aplicación nativa de Android para el seguimiento de gastos personales. Este documento proporciona las instrucciones necesarias para configurar, compilar y ejecutar el proyecto.

## 1. Requisitos Previos

-   **Android Studio:** Se recomienda la versión Iguana o superior.
-   **JDK:** JDK 11 o superior.
-   **Fuentes:** Asegúrate de haber colocado los archivos de la fuente "Advent Pro" (`adventpro_regular.ttf`, `adventpro_medium.ttf`, `adventpro_bold.ttf`) en el directorio `app/src/main/res/font/`.

## 2. Configuración del Proyecto

1.  Clona el repositorio en tu máquina local.
2.  Abre el proyecto con Android Studio.
3.  Espera a que Gradle se sincronice y descargue todas las dependencias.

## 3. Cómo Compilar y Ejecutar

La aplicación está configurada con dos **Product Flavors**: `free` y `pro`.

### 3.1. Ejecutar desde Android Studio

1.  Abre el panel **Build Variants** (`View` > `Tool Windows` > `Build Variants`).
2.  En la columna **Active Build Variant** para el módulo `:app`, selecciona la versión que deseas compilar (ej: `proDebug` para la versión Pro de depuración).
3.  Haz clic en el botón **Run 'app'** (el icono de play verde).

### 3.2. Compilar desde la Línea de Comandos

Puedes usar los siguientes comandos de Gradle desde la raíz del proyecto para generar los APKs:

-   **Versión Pro (Debug):** `./gradlew assembleProDebug`
-   **Versión Free (Debug):** `./gradlew assembleFreeDebug`
-   **Versión Pro (Release):** `./gradlew assembleProRelease`
-   **Versión Free (Release):** `./gradlew assembleFreeRelease`

Los APKs generados se encontrarán en `app/build/outputs/apk/`.

## 4. Cómo Ejecutar los Tests

El proyecto incluye una suite de tests de instrumentación para verificar la funcionalidad de la UI.

### 4.1. Ejecutar desde Android Studio

1.  Abre un archivo de test ubicado en `app/src/androidTest/java/com/aleon/proyectocellcli/`.
2.  Haz clic en el icono de "play" que aparece junto al nombre de la clase de test o de un método de test individual.
3.  Selecciona el dispositivo o emulador donde se ejecutarán los tests.

### 4.2. Ejecutar desde la Línea de Comandos

El siguiente comando ejecutará todos los tests de instrumentación en la variante de build que esté seleccionada por defecto (normalmente `debug`):

```bash
./gradlew connectedAndroidTest
```

Para ejecutar los tests para una variante específica, usa el comando correspondiente:

```bash
./gradlew connectedProDebugAndroidTest
```
