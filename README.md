# PouJump

Juego de plataformas estilo Doodle Jump protagonizado por **Pou**. Salta de nube en nube e intenta alcanzar la mayor altura posible sin caer.

## Controles

| Tecla | Acción |
|-------|--------|
| ← / A | Mover a la izquierda |
| → / D | Mover a la derecha |
| ENTER | Iniciar / Reiniciar partida |
| ESC | Volver al menú principal |

## Tipos de nubes

| Color | Tipo | Comportamiento |
|-------|------|----------------|
| Verde | Normal | Plataforma estable |
| Azul | Móvil | Se desplaza horizontalmente |
| Roja | Frágil | Se rompe al pisarla |

## Requisitos

- Java 8 o superior
- `jlayer-1.0.1.jar` (incluido en `lib/`)

## Cómo ejecutar

1. Abrir el proyecto en **IntelliJ IDEA**.
2. Verificar que `jlayer-1.0.1.jar` esté agregado como dependencia:
   `File → Project Structure → Modules → Dependencies → + → JARs or Directories → seleccionar lib/jlayer-1.0.1.jar → Apply`.
3. Ejecutar `Main.java`.

## Estructura del proyecto

```
src/
├── Main.java                  # Punto de entrada
└── com/pou/
    ├── entities/              # Modelo: entidades del juego
    │   ├── Pou.java
    │   └── Nube.java
    ├── logic/                 # Controlador: lógica del juego
    │   ├── GameLoop.java
    │   ├── GameState.java
    │   ├── ControlJugador.java
    │   ├── GeneradorNubes.java
    │   ├── Colision.java
    │   ├── Puntaje.java
    │   └── Cronometro.java
    ├── gui/                   # Vista: renderizado de pantallas
    │   ├── Pantalla.java      # Interfaz común de todas las pantallas
    │   ├── PantallaMenu.java
    │   ├── PantallaJuego.java
    │   └── PantallaGameOver.java
    └── util/                  # Utilidades
        ├── Recursos.java
        └── GestorSonido.java
```

## Arquitectura

El proyecto sigue el patrón **MVC**:

- **Modelo** (`entities`, `logic`): `Pou`, `Nube`, `Puntaje`, `GeneradorNubes`, `Colision`, `Cronometro`
- **Vista** (`gui`): `Pantalla`, `PantallaMenu`, `PantallaJuego`, `PantallaGameOver`
- **Controlador** (`logic`): `GameLoop`, `ControlJugador`

### Pilares de POO aplicados

- **Encapsulación**: campos `private` con getters/setters en `Pou`, `Nube` y `Puntaje`.
- **Herencia**: `GameLoop` extiende `JPanel`; `ControlJugador` extiende `KeyAdapter`; `PantallaMenu`, `PantallaJuego` y `PantallaGameOver` implementan la interfaz `Pantalla`.
- **Polimorfismo**: `GameLoop` mantiene una referencia de tipo `Pantalla` (`pantallaPrimaria`) y llama a `dibujar()` en tiempo de ejecución sin conocer el tipo concreto — la implementación correcta se resuelve automáticamente según el estado del juego.

### Hilos y control de tiempo

- El ciclo de juego corre en un hilo dedicado (daemon) a **60 FPS** mediante delta-time con `System.nanoTime()`.
- La música de fondo y los efectos de sonido se reproducen en hilos daemon independientes para no bloquear el hilo principal.
- El tiempo de partida se mide con `Cronometro`, que usa `javax.swing.Timer` disparando cada **1 000 ms**. El tiempo se muestra en el HUD durante la partida y en la tarjeta de Game Over.
