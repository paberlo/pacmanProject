# PacmanProject

Juego básico de Pac-Man desarrollado en Java utilizando Swing para la interfaz gráfica.

## Características

- Laberinto donde Pac-Man y los fantasmas se mueven.
- Movimiento de Pac-Man controlado por el usuario (teclas de flecha).
- Fantasmas con movimiento automático.
- Puntos coleccionables que aumentan el puntaje.
- Condiciones de victoria (comer todos los puntos) y derrota (ser atrapado por un fantasma).
- Interfaz gráfica sencilla y modular.

## Estructura de Archivos

```
src/
├── Game.java         # Clase principal: inicializa la ventana y el juego
├── Board.java        # Lógica y renderizado del tablero
├── Pacman.java       # Lógica y renderizado de Pac-Man
├── Ghost.java        # Lógica y renderizado de los fantasmas
└── Direction.java    # Enum para las direcciones de movimiento
```

## Requisitos

- Java 8 o superior
- (Opcional) IDE como IntelliJ IDEA, Eclipse, VSCode, etc.

## Compilación y Ejecución

Desde la terminal, navega al directorio raíz y ejecuta:

```bash
javac -d bin src/*.java
java -cp bin Game
```

O si usas un IDE, simplemente importa el proyecto y ejecuta la clase `Game`.

## Controles

- **Flechas del teclado** para mover a Pac-Man: izquierda, derecha, arriba, abajo.

## Extensiones Futuras

- Mejorar la IA de los fantasmas.
- Añadir niveles y nuevos mapas.
- Implementar efectos de sonido.
- Añadir “power-ups” y más funcionalidades clásicas del juego.

## Autor

Proyecto desarrollado por [paberlo].

---

¡Disfruta programando y jugando Pac-Man!