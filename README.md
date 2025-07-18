 ![video 1](/app/docs/gif1.gif) ![video 2](/app/docs/gif2.gif)

📄 [Descargar Mobile Challenge (PDF)](/app/docs/Mobile%20Challenge%20-%20Engineer%20-%20v0.8.pdf)


# 📍 Lista de Ciudades

Aplicación Android desarrollada con **Jetpack Compose**, que permite:

* Descargar una lista de ciudades desde un JSON remoto.
* Guardarlas en una base de datos local SQLite.
* Navegar, buscar, marcar como favoritas y consultar información desde Wikipedia.
* Todo implementado con **MVVM**, principios **SOLID**, y un enfoque basado en **Clean Architecture**.

---

## ✨ ENFOQUE GENERAL

* Se descarga un archivo JSON de ciudades.
* Se convierte directamente en entidades Room (`CiudadEntity`).
* Se guarda en SQLite usando DAO.
* Se accede a los datos con `StateFlow`, permitiendo búsquedas y filtros en tiempo real.
* Se aplica MVVM + Clean Architecture con una sola capa de repositorio.
* Se hace inyección de dependencias con Koin.
* Se utiliza Jetpack Compose para la interfaz, con test de UI automatizados.

---

## 🔹 DESCARGA Y GUARDADO DE DATOS JSON

**Clase clave:** `JsonDownloader`

* Se hace una request HTTP con OkHttp.
* Se parsea el JSON usando `Gson` directamente a `CiudadEntity`.
* El flujo `Flow<Pair<Int, List<CiudadEntity>>>` emite progreso y datos parcialmente descargados.

```kotlin
jsonDownloader.descargarCiudades(url).collect { (porcentaje, ciudades) ->
    repository.insertarCiudades(ciudades)
}
```

---

## 📁 PERSISTENCIA CON SQLITE

* `CiudadEntity`: entidad Room que representa la ciudad.
* `CiudadDao`: operaciones de base de datos (insert, query, update).
* `AppDatabase`: instancia central de Room.

```kotlin
@Dao
interface CiudadDao {
    @Insert(onConflict = REPLACE) suspend fun insertAll(...)
    @Query("SELECT * FROM ciudades") fun getAll(): Flow<List<CiudadEntity>>
    ...
}
```

---

## 🧠 MVVM + VIEWMODEL

* `CiudadViewModel` es el único punto de acceso de la UI a los datos.
* Expone `StateFlow`s de progreso, filtro, lista, info Wikipedia, etc.
* Se encarga de manejar la lógica de filtrado y favoritos.

```kotlin
val ciudades: StateFlow<List<CiudadEntity>> = combine(query, onlyFav) { ... }
```

---

## 🧰 CLEAN ARCHITECTURE APLICADA

No se sigue un esquema completo de capas por simplicidad, pero:

* **Data**: Room + Retrofit + JsonDownloader.
* **Domain**: `CiudadRepository` centraliza acceso a los datos (único punto de verdad).
* **Presentation**: `CiudadViewModel` y Compose UI.

> ✅ La separación de responsabilidades está clara. Podría escalarse a una capa de use-cases si hiciera falta.

---

## 📊 PRINCIPIOS SOLID EN PRÁCTICA

### S - SINGLE RESPONSIBILITY

* Cada clase tiene una única responsabilidad: `JsonDownloader` solo descarga; `WikipediaRepository` solo consume el API.

### O - OPEN/CLOSED

* `CiudadRepository` permite agregar nuevas formas de persistencia sin modificar su uso.

### L - LISKOV

* Las funciones de Room y los repositorios exponen flujos compatibles y predecibles.

### I - INTERFACE SEGREGATION

* Las funciones expuestas por `CiudadDao` y `CiudadRepository` están segmentadas por responsabilidad.

### D - DEPENDENCY INVERSION

* `CiudadViewModel` no instancia dependencias directamente: todo se inyecta vía Koin.

---

## 📆 UI Y ESTADOS

* Se muestra un `BuscadorPanel` con campo de búsqueda, favoritos y lista.
* En landscape se muestra el mapa directamente.
* Al hacer clic en "Info", se carga un popup con información desde Wikipedia.

---

## 📡 USO DE STATEFLOW Y FLOW

### ✅ 1. `Flow` para acceder a la base de datos (reactividad automática)

```kotlin
@Query("SELECT * FROM ciudades ORDER BY name ASC")
fun getAll(): Flow<List<CiudadEntity>>
```

```kotlin
fun getAll(): Flow<List<CiudadEntity>> = dao.getAll()
```

**Ventaja:**

* Room emite automáticamente los cambios.
* La UI queda siempre sincronizada.

---

### ✅ 2. `StateFlow` para estados observables en el ViewModel

```kotlin
private val _searchQuery = MutableStateFlow("")
val searchQuery: StateFlow<String> = _searchQuery
```

**Ventaja:**

* Expone datos de forma reactiva para que Compose escuche cambios sin esfuerzo adicional.

---

### ✅ 3. Combinación dinámica con `combine` y `flatMapLatest`

```kotlin
val ciudades: StateFlow<List<CiudadEntity>> = combine(query, onlyFav) { ... }
    .flatMapLatest { it }
    .stateIn(...)
```

**Ventaja:**

* Reacciona a cambios de filtros o búsqueda en tiempo real.
* `flatMapLatest` cancela búsquedas obsoletas.

---

### ✅ 4. `Flow` para mostrar progreso de descarga JSON

```kotlin
fun descargarCiudades(url: String): Flow<Pair<Int, List<CiudadEntity>>>
```

**Ventaja:**

* Se puede emitir progreso parcial.
* Mejora UX al mostrar progreso mientras se descarga.

---

### ✅ 5. Otros estados representados con `StateFlow`

```kotlin
val progreso: StateFlow<Int>
val isReady: StateFlow<Boolean>
val uiState: StateFlow<CiudadUiState>
```

**Ventaja:**

* Compose responde a estos estados automáticamente.
* Permite interfaces consistentes y sin errores de sincronización.

---

## 📊 DECISIONES IMPORTANTES

* **Sin DTOs**: `CiudadEntity` se usa como modelo de red y local, para simplificar.
* **JSON streaming**: se usa `JsonReader` para manejar archivos grandes eficientemente.
* **Jetpack Compose desde cero**: sin XML, todo programático.
* **Koin**: por su simplicidad de integración.

Se eligió descargar el JSON usando OkHttp junto con JsonReader y Gson en forma de streaming por las siguientes razones técnicas: 

### ✅ Eficiencia en memoria (streaming) 
JsonReader permite parsear el JSON elemento por elemento sin cargar todo el archivo en memoria. 
Esto es ideal cuando el JSON contiene una gran cantidad de ciudades, ya que: 
* Evita OutOfMemoryError.
* Reduce el uso de RAM.
* Permite empezar a procesar los datos antes de que la descarga finalice.

 ### ✅ Integración directa con CiudadEntity 
 No se usan DTOs intermedios: Gson parsea directamente a CiudadEntity, lo que simplifica el código. Ahorra tiempo de desarrollo y reduce errores en la conversión de datos. 
 
 ### ✅ Facilidad de testeo 
 JsonDownloader es una clase simple y desacoplada que puede ser probada unitariamente o mockeada en tests de integración.

---

## 🔧 TECNOLOGÍAS Y LIBRERÍAS

* Jetpack Compose
* Room
* Retrofit + Gson
* OkHttp
* Koin (inyección de dependencias)
* Wikipedia API
* StateFlow / Flow (para estados y reactividad)

---

