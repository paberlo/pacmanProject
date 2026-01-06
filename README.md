# PacmanProject

Juego básico de Pac-Man desarrollado en Java utilizando Swing para la interfaz gráfica.

[![CI/CT](https://github.com/alexgc04/pacmanGPS/actions/workflows/ci-ct.yaml/badge.svg)](https://github.com/alexgc04/pacmanGPS/actions/workflows/ci-ct.yaml)

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
├── main/java/pacman/
│   ├── Game.java         # Clase principal: inicializa la ventana y el juego
│   ├── Board.java        # Lógica y renderizado del tablero
│   ├── Pacman.java       # Lógica y renderizado de Pac-Man
│   ├── Ghost.java        # Lógica y renderizado de los fantasmas
│   ├── GhostType.java    # Enum para los tipos de fantasmas
│   └── Direction.java    # Enum para las direcciones de movimiento
└── test/java/pacman/
    ├── GameTest.java
    ├── BoardTest.java
    ├── PacmanTest.java
    ├── GhostTest.java
    ├── GhostTypeTest.java
    └── DirectionTest.java
```

## Requisitos

- Java 17 o superior
- Maven 3.8+
- (Opcional) IDE como IntelliJ IDEA, Eclipse, VSCode, etc.

## Compilación y Ejecución

### Con Maven (recomendado)

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar pruebas
mvn test

# Ejecutar pruebas con cobertura
mvn test jacoco:report

# Construir el proyecto completo
mvn clean install
```

### Desde la terminal (legacy)

```bash
javac -d bin src/main/java/pacman/*.java
java -cp bin pacman.Game
```

## Controles

- **Flechas del teclado** para mover a Pac-Man: izquierda, derecha, arriba, abajo.

## Integración Continua

Este proyecto utiliza GitHub Actions para CI/CT (Integración Continua / Prueba Continua):

- ✅ Se ejecutan pruebas automáticamente en cada push y pull request a `main` y `develop`
- 📊 Se genera un informe de cobertura con JaCoCo
- 🔄 Se usa concurrencia para evitar ejecuciones duplicadas

## Cobertura de Tests

| Métrica | Cobertura |
|---------|-----------|
| Instrucciones | 99% |
| Ramas | 96% |
| Líneas | 100% |
| Métodos | 100% |
| Clases | 100% |

## Extensiones Futuras

- Mejorar la IA de los fantasmas.
- Añadir niveles y nuevos mapas.
- Implementar efectos de sonido.
- Añadir "power-ups" y más funcionalidades clásicas del juego.

## Autor

Proyecto desarrollado por [paberlo].

---

¡Disfruta programando y jugando Pac-Man!
