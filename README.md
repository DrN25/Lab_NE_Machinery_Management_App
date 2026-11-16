# Machinery Management App (M-Business)
**Universidad Nacional de San Agustín de Arequipa (UNSA)**  
**Curso:** Negocios Electrónicos — Dr. Ing. César Baluarte Araya  
**Subgrupo 12:** Módulo 12 — Maestro de Maquinarias (Soluciones Móviles S.A.C.)

---

## Descripción del Proyecto

Aplicación móvil nativa para el sistema operativo **Android** desarrollada con **Kotlin**, **Jetpack Compose (Material Design 3)** y persistencia local relacional en **SQLite** mediante la biblioteca **AndroidX Room**.

Implementa el mantenimiento del **Módulo 12: Maestro de Maquinarias** y sus tablas referenciales (*Planta de Producción*, *Línea de Producción*, *Ubicación en la línea*), bajo el diseño de interfaz ergonómico **Layout Propuesta 2 (P2)**:
* Lista de registros con badges semánticos de estado (`A` = Activo, `I` = Inactivo, `*` = Eliminado).
* Chips horizontales de filtrado superior con conteos numéricos en tiempo real (*Todos*, *Activos*, *Inact.*, *Elim.*).
* Conmutador de ordenamiento dinámico (*Código* / *Nombre*).
* Buscador predictivo en tiempo real con limpieza rápida.
* **Panel Inferior de Acciones Rápidas:** Al pulsar una tarjeta, emerge en la zona ergonómica del pulgar un panel con las operaciones inmediatas (`Modificar`, `Inactivar` / `Reactivar`, `Eliminar`).
* **Modal BottomSheet:** Formulario deslizante para *Adición* (con estado default `'A'`) y *Modificación* (código protegido con candado) sobre fondo atenuado sin perder la referencia espacial de la lista.

---

## Estructura de Arquitectura (Clean Architecture + MVVM)

```
com.solucionesmoviles.maquinarias/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   └── PlantaProduccionDao.kt
│   │   ├── entity/
│   │   │   └── PlantaProduccionEntity.kt
│   │   └── AppDatabase.kt
│   └── repository/
│       └── PlantaProduccionRepository.kt
├── ui/
│   ├── screen/
│   │   └── PlantaProduccionScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   └── Theme.kt
│   └── viewmodel/
│       └── PlantaProduccionViewModel.kt
└── MainActivity.kt
```

---

## Requisitos de Ejecución

* **Android Studio:** Ladybug (2024.2.1) o superior.
* **Android SDK:** Compile SDK 35, Min SDK 26 (Android 8.0 Oreo).
* **JDK:** OpenJDK 17 LTS.
* **Emulador / Terminal:** Dispositivo Android API 26+ (recomendado Pixel 7 / Android 14+).
