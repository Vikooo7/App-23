# App23 · Pedidos sin conexión con Room y Cloud Firestore

App Android (Kotlin + Jetpack Compose) para registrar pedidos, aunque no haya Internet. Cada pedido se guarda primero en el celular con **Room** y después se envía en lote a **Cloud Firestore** al pulsar *Sincronizar*. Una franja superior roja o verde avisa en todo momento si hay conexión.

Proyecto de la Semana 10 del curso *Desarrollo de Apps Móviles* (Universidad Continental).

---

## Funcionalidades

- **Offline-first:** la pantalla siempre lee y escribe en Room; la red solo se usa al sincronizar.
- **Detección de conexión en tiempo real** con `ConnectivityManager`: franja verde *En línea* o roja *Sin conexión · Modo offline*, más un aviso cuando la señal se pierde o vuelve.
- **Estado por pedido:**

  | Estado | Cuándo | En pantalla |
  |---|---|---|
  | `PENDIENTE` | Recién guardado en Room | Reloj ámbar, "Pendiente" |
  | `SINCRONIZADO` | Firestore confirmó el lote | Nube verde, "Enviado" y la hora |
  | `ERROR` | Firestore rechazó el lote o no respondió en 15 s | Alerta roja y el motivo; se reintenta al sincronizar |

- **Envío en lote atómico** (`WriteBatch`, hasta 400 pedidos por lote): se guardan todos o ninguno.
- **Sin duplicados:** cada pedido tiene un UUID generado en el celular, que es también el id del documento en Firestore (`pedidos/{uuid}`). Reenviar un pedido sobrescribe el mismo documento.
- **Validación en el servidor** con las reglas de seguridad de Firestore (`firestore.rules`).

## Arquitectura (MVVM)

```
View (Compose) ──► ViewModel ──► Repository ──► Room (pedidos.db)       ← siempre
                                     │
                                     └────────► Firestore (WriteBatch)  ← solo al sincronizar
```

```
app/src/main/java/com/example/app23_jc_room_sync_firestore
├── model/
│   ├── Pedido.kt                     modelo de la app + EstadoSync
│   ├── PedidoMapper.kt               Entity → modelo y Entity → documento de Firestore
│   ├── PedidoRepository.kt           guarda en Room y sincroniza con Firestore
│   ├── MonitorConexion.kt            observa la red (Flow<Boolean>)
│   ├── local/                        Room: PedidoEntity, PedidoDao, AppDatabase
│   └── remote/
│       └── PedidoRemoteDataSource.kt envía lotes a Cloud Firestore
├── viewmodel/                        PedidoUiState, PedidoViewModel
├── view/                             PantallaPedidos, BannerConexion, PanelSincronizacion,
│                                     FormularioPedido, PedidoItem, FormatoFecha
├── ui/theme/
└── MainActivity.kt
```

### Documento en Firestore

```
pedidos/{uuid}
  ├── producto:      "Arroz Costeño 5 kg"   (string)
  ├── cantidad:      12                     (number)
  ├── fechaRegistro: timestamp              hora del celular al crear el pedido
  └── recibidoEn:    timestamp              hora del servidor al recibirlo
```

## Tecnologías

| | Versión |
|---|---|
| Android Gradle Plugin | 9.4.1 |
| Kotlin / KSP | 2.2.10 / 2.3.12 |
| Gradle | 9.6.0 (wrapper incluido) |
| compileSdk / targetSdk / minSdk | 37 / 37 / 24 |
| Jetpack Compose (BoM) | 2026.02.01 |
| Room | 2.8.5 |
| Firebase BoM (Firestore) | 33.11.0 |
| google-services | 4.4.4 |

---

## Instalación y configuración

### Requisitos

- Android Studio con soporte para AGP 9.4 y el SDK de Android 37 instalado.
- Un emulador o celular con Android 7.0 (API 24) o superior y acceso a Internet.
- Una cuenta de Google para crear el proyecto de Firebase.

### 1. Clonar el repositorio

```bash
git clone https://github.com/Vikooo7/App-23.git
```

Ábrelo en Android Studio con **File → Open** y elige la carpeta `App-23`. Android Studio crea `local.properties` con la ruta de tu SDK.

### 2. Crear el proyecto de Firebase y la base Firestore

> El archivo `google-services.json` **no está en el repositorio** porque contiene la configuración privada del proyecto de Firebase. Cada persona debe usar el suyo; sin él, la app no compila.

1. En [console.firebase.google.com](https://console.firebase.google.com) crea un proyecto (por ejemplo `app23-jc-room-sync-firestore`). Google Analytics no es necesario.
2. Ve a **Bases de datos y almacenamiento → Firestore Database → Crear base de datos**:
   - Edición: **Standard**
   - ID de base de datos: **(default)**
   - Ubicación: **southamerica-east1 (São Paulo)** (no se puede cambiar después)
   - Reglas: **Iniciar en modo de producción**
3. No hace falta crear la colección `pedidos`: aparece sola con el primer pedido sincronizado.

### 3. Publicar las reglas de seguridad

En la pestaña **Reglas** de Firestore, reemplaza todo por el contenido de [`firestore.rules`](firestore.rules) y pulsa **Publicar**.

Las reglas exigen exactamente cuatro campos (`producto`, `cantidad`, `fechaRegistro`, `recibidoEn`), un producto de 1 a 120 caracteres, una cantidad entera mayor a 0, una fecha de tipo timestamp y que `recibidoEn` sea la hora del servidor. Los pedidos no se pueden borrar desde la app.

> Estas reglas permiten leer y escribir sin iniciar sesión. Sirven para el laboratorio; en un proyecto real se debe exigir `request.auth != null`.

### 4. Registrar la app Android y descargar `google-services.json`

1. En **Descripción general → + Agregar app → Android**, usa este nombre de paquete:

   ```
   com.example.app23_jc_room_sync_firestore
   ```

2. Pulsa **Registrar app** y luego **Descargar google-services.json**.
3. Copia el archivo en la carpeta `app/` del proyecto, al lado de `app/build.gradle.kts`.
4. En los pasos del SDK no copies nada: las dependencias ya están configuradas. Termina con **Ir a la consola**.

### 5. Compilar y ejecutar

En Android Studio pulsa **Sync Now** y luego **Run ▶**.

También puedes generar el APK desde la terminal:

```bash
./gradlew assembleDebug
```

En Windows usa `gradlew.bat assembleDebug`. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

---

## Uso

1. **Registrar un pedido:** escribe el producto y la cantidad y pulsa **Guardar pedido**. Se guarda en el celular como *Pendiente*, con o sin Internet.
2. **Sin conexión:** la franja se pone roja, los pedidos se siguen guardando y el botón **Sincronizar** se desactiva hasta que vuelva la señal.
3. **Sincronizar:** con conexión, pulsa **Sincronizar con Firestore**. Los pendientes y los que tuvieron error se envían en lote y pasan a *Enviado*.

### Prueba guiada

| # | Acción | Resultado esperado |
|---|---|---|
| 1 | Abrir la app con Internet | Franja verde "En línea" |
| 2 | Registrar "Aceite 1 L", cantidad 6 | Estado *Pendiente*; Pendientes de envío: 1 |
| 3 | Activar modo avión | Franja roja y aviso de conexión perdida |
| 4 | Registrar 2 pedidos más | Aviso "guardado sin conexión"; Pendientes: 3; botón desactivado |
| 5 | Cerrar la app desde recientes y abrirla | Los 3 pedidos siguen ahí (Room) |
| 6 | Desactivar modo avión | Franja verde y aviso "Conexión recuperada" |
| 7 | Pulsar **Sincronizar con Firestore** | "Sincronización completa: 3 pedido(s)"; 3 documentos en `pedidos` |
| 8 | Pulsar **Sincronizar** otra vez | "No hay pedidos pendientes"; siguen 3 documentos |
| 9 | En las reglas, cambiar `cantidad > 0` por `> 100`; registrar uno de 150 y otro de 5; sincronizar | Los dos quedan en *Error* (`PERMISSION_DENIED`): el lote es atómico. Restaurar la regla y sincronizar: ambos pasan a *Enviado* |

Para ver la base local mientras la app corre: **View → Tool Windows → App Inspection → Database Inspector**, tabla `pedidos`.

---

## Problemas frecuentes

| Síntoma | Causa y solución |
|---|---|
| No compila: *File google-services.json is missing* | Falta el archivo en `app/` (paso 4). |
| No compila: *No matching client found for package name* | El `google-services.json` es de otra app. Regístrala con el paquete `com.example.app23_jc_room_sync_firestore` y descarga el archivo otra vez. |
| *The database (default) does not exist* | Firestore no se creó o se creó con otro ID. Debe ser **(default)** (paso 2). |
| Todos los pedidos en error con *PERMISSION_DENIED* | Las reglas no se publicaron o no coinciden (paso 3). |
| *Firestore no confirmó a tiempo* con señal | La primera conexión puede tardar. Vuelve a sincronizar. |
| Pedido en *Error* que sí aparece en la consola | Firestore lo envió desde su caché después del límite de 15 s. Al sincronizar otra vez pasa a *Enviado*, sin duplicarse. |
| La franja siempre está roja con Wi-Fi | El emulador no tiene Internet real y Android no marca la red como validada. Prueba abriendo una página en el navegador del emulador. |
| *ksp-x is too old/new for kotlin-x* | Usa la versión de KSP compatible con tu versión de Kotlin en `gradle/libs.versions.toml`. |

## Autor

Viko23 · Desarrollo de Apps Móviles · Universidad Continental
