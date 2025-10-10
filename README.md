# Gestor de gastos - Documentación Técnica

## 1. Descripción General

**Gestor de gastos** es una aplicación nativa de Android diseñada para ayudar a los usuarios a registrar y visualizar sus gastos diarios. La aplicación permite a los usuarios añadir gastos, categorizarlos, ver resúmenes visuales en gráficos y filtrar sus gastos por diferentes periodos de tiempo.

La aplicación está construida con una arquitectura moderna y escalable, utilizando las últimas tecnologías recomendadas por Google para el desarrollo de Android.

## 2. Arquitectura del Software

El proyecto sigue los principios de **Clean Architecture**, separando el código en tres capas principales para garantizar que la aplicación sea robusta, escalable y fácil de testear.

<img width="227" height="222" alt="image" src="https://github.com/user-attachments/assets/fd370418-00e0-4d72-9df9-48ec71e01580" />


### 2.1. Capas de la Arquitectura

#### a. Capa de UI (Interfaz de Usuario)
- **Responsabilidad:** Mostrar los datos en la pantalla y manejar la interacción del usuario.
- **Componentes Clave:**
    - **Vistas (Jetpack Compose):** Todos los elementos visuales se construyen de forma declarativa con Composables (`HomeScreen`, `AddOutlayScreen`, etc.).
    - **ViewModels (`HomeViewModel`, `SettingsViewModel`, etc.):** Preparan y gestionan el estado para la UI. Exponen los datos a través de `StateFlow` y reciben los eventos del usuario. No contienen lógica de negocio compleja.
    - **Estado de la UI:** Clases de datos que modelan lo que se debe mostrar en la pantalla.

#### b. Capa de Dominio (Domain)
- **Responsabilidad:** Contener la lógica de negocio principal de la aplicación. Es el núcleo de la app y es independiente de cualquier framework de UI o base de datos.
- **Componentes Clave:**
    - **Modelos (`Expense`, `Category`):** Representaciones de los objetos de negocio principales. Son clases de datos simples (POJOs/POCOs).
    - **Casos de Uso (`GetExpensesUseCase`, `AddExpenseUseCase`, etc.):** Clases que encapsulan una única acción o regla de negocio. Son la única vía por la que la capa de UI puede interactuar con la capa de datos.
    - **Interfaces de Repositorio (`ExpenseRepository`):** Contratos que definen las operaciones de datos que los casos de uso necesitan, pero sin conocer los detalles de la implementación.

#### c. Capa de Datos (Data)
- **Responsabilidad:** Implementar la lógica para acceder a los datos, ya sea desde una base de datos local, una API remota o las preferencias del usuario.
- **Componentes Clave:**
    - **Implementaciones de Repositorio (`ExpenseRepositoryImpl`):** Implementan las interfaces definidas en la capa de dominio. Son los coordinadores que deciden de qué fuente de datos obtener la información.
    - **Fuentes de Datos (Data Sources):**
        - **Room (`ExpenseDao`):** Para la persistencia de datos locales (gastos, categorías).
        - **DataStore (`UserPreferencesRepository`):** Para guardar preferencias simples del usuario (tema, moneda, límite mensual).
    - **Mappers:** Funciones que convierten los modelos de la capa de datos (Entidades) a los modelos de la capa de dominio.

### 2.2. Flujo de Datos
El flujo de datos es unidireccional, lo que hace que la aplicación sea predecible y fácil de depurar:

1.  **UI Event:** El usuario realiza una acción (ej: hace clic en "Guardar Gasto").
2.  **ViewModel:** La vista notifica al `ViewModel` sobre la acción.
3.  **Use Case:** El `ViewModel` llama al caso de uso correspondiente (ej: `AddExpenseUseCase`).
4.  **Repository:** El caso de uso utiliza la interfaz del repositorio para solicitar la operación de datos.
5.  **Data Source:** La implementación del repositorio llama a la fuente de datos apropiada (ej: `ExpenseDao` para insertar en la base de datos).
6.  **Actualización de Estado:** La base de datos (a través de `Flow`) notifica al repositorio que los datos han cambiado. Este flujo sube a través del caso de uso y el `ViewModel`, que actualiza el `StateFlow` de la UI.
7.  **Recomposición:** Jetpack Compose detecta el cambio de estado y redibuja automáticamente las partes necesarias de la pantalla.

### 2.3. Inyección de Dependencias (Hilt)
Utilizamos **Hilt** para gestionar las dependencias en toda la aplicación. Hilt crea y proporciona automáticamente las instancias necesarias (como repositorios y casos de uso) a las clases que las necesitan (como los ViewModels), facilitando el desacoplamiento y el testing.

## 3. Características Principales

- **Registro de Gastos:** Pantalla para añadir nuevos gastos con descripción, monto, fecha y categoría.
- **Gestión de Categorías:** Creación y edición de categorías personalizadas con asignación de color.
- **Dashboard Interactivo (`HomeScreen`):**
    - Gráfico de dona (`MPAndroidChart`) que visualiza la distribución de gastos por categoría.
    - Filtrado dinámico por Día, Mes, Año y Periodo.
    - Resumen del gasto total frente al límite mensual establecido.
- **Historial de Gastos (`OutlayScreen`):**
    - Lista de todos los gastos agrupados por fecha.
    - Funcionalidad de búsqueda por descripción.
    - Opciones para editar y eliminar cada gasto individual.
- **Configuración Personalizada (`SettingsScreen`):**
    - Selección de moneda.
    - Establecimiento de un límite de gasto mensual.
    - Borrado de todos los datos de la aplicación.
- **Product Flavors (Free vs. Pro):**
    - **`free`:** Versión gratuita con el tema de la aplicación bloqueado en modo claro.
    - **`pro`:** Versión de pago que permite al usuario elegir entre tema claro, oscuro o el predeterminado del sistema.

## 4. Estructura del Proyecto

El código fuente principal se encuentra en `app/src/main/java/com/aleon/proyectocellcli/`.

-   `.../di/`: Módulos de Hilt para la inyección de dependencias y la configuración de flavors.
-   `.../data/`: Contiene la capa de datos.
    -   `.../local/`: Clases de Room (Base de datos, DAO, Entidades).
    -   `.../repository/`: Implementaciones de las interfaces de repositorio.
-   `.../domain/`: Contiene la capa de dominio.
    -   `.../model/`: Los modelos de negocio principales.
    -   `.../repository/`: Las interfaces de los repositorios.
    -   `.../use_case/`: Todas las clases de casos de uso.
-   `.../ui/`: Contiene la capa de UI.
    -   `.../screens/`: Los Composables que definen cada pantalla.
    -   `.../viewmodel/`: Los ViewModels de Android.
    -   `.../navigation/`: Lógica de navegación con Jetpack Navigation Compose.
    -   `.../theme/`: Definición del tema de la aplicación (colores, tipografía).
-   `app/src/androidTest/`: Contiene los tests de instrumentación (UI Tests).
-   `app/src/free/` y `app/src/pro/`: Directorios de código fuente para cada Product Flavor.

## 5. Tecnologías y Librerías

- **Lenguaje:** [Kotlin](https://kotlinlang.org/) 100%.
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) para una UI declarativa y moderna.
- **Arquitectura:** Clean Architecture, MVVM.
- **Inyección de Dependencias:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android).
- **Base de Datos:** [Room](https://developer.android.com/training/data-storage/room) para la persistencia de datos SQL.
- **Preferencias:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) para guardar preferencias clave-valor.
- **Navegación:** [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation).
- **Asincronía:** Corutinas de Kotlin y Flow.
- **Gráficos:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart).
- **Testing:** [JUnit4](https://junit.org/junit4/), [Compose Test](https://developer.android.com/jetpack/compose/testing), [Hilt Testing](https://developer.android.com/training/dependency-injection/hilt-testing).

## 6. Configuración y Compilación

### 6.1. Requisitos Previos
- Android Studio Iguana o superior.
- JDK 11 o superior.

### 6.2. Product Flavors (Free vs. Pro)
Para compilar o ejecutar una versión específica:
1.  Abre el panel **Build Variants** en Android Studio (`View` > `Tool Windows` > `Build Variants`).
2.  En la columna "Active Build Variant" para el módulo `:app`, selecciona la variante deseada (ej: `proDebug` o `freeDebug`).
3.  Ejecuta la aplicación.

### 6.3. Compilación desde Línea de Comandos
- **Versión Pro (Debug):** `./gradlew assembleProDebug`
- **Versión Free (Debug):** `./gradlew assembleFreeDebug`
- **Versión Pro (Release):** `./gradlew assembleProRelease`
- **Versión Free (Release):** `./gradlew assembleFreeRelease`

## 7. Testing

El proyecto incluye tests de UI para verificar el comportamiento de los Composables y ViewModels.

- **Estrategia:** Los tests utilizan **repositorios falsos (Fakes)** que se inyectan con Hilt para simular la capa de datos. Esto permite ejecutar tests de UI rápidos, fiables y sin depender de la base de datos real.
- **Ubicación:** Los tests se encuentran en `app/src/androidTest/`.

### 7.1. Ejecutar Tests
- **Desde Android Studio:** Abre un archivo de test (ej: `HomeScreenTest.kt`) y haz clic en el icono de "play" junto al nombre de la clase o de un método de test.
- **Desde Línea de Comandos:** `./gradlew connectedAndroidTest` (ejecutará los tests para la variante seleccionada).
  - Para una variante específica: `./gradlew connectedProDebugAndroidTest`
