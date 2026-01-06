Este repositorio del juego de PacMan contiene solo código Java, a partir de la carpeta src/.
Cuando se te asigne una tarea, sigue estas guías:
## Estructura del proyecto
- `/src/main/java` : Código fuente principal.
- `/src/test/java` : Pruebas unitarias organizadas por paquete.
- `pom.xml` : Archivo de configuración de Maven.
## Convenciones para pruebas unitarias
1. **Ubicación**: Las clases de prueba deben estar en `/src/test/java`, replicando la estructura
de paquetes del código fuente.
2. **Nomenclatura**: Cada clase de prueba debe terminar en `Test`, por ejemplo:
`UserServiceTest`.
3. **Cobertura mínima**: Se espera una cobertura del 100% para clases nuevas. Usa `@Test`
para cada método relevante.
4. **Aislamiento**: Las pruebas deben ser independientes y no depender de estado compartido.
5. **Validación**: Ejecuta `mvn test` para validar los cambios antes de cada commit.
6. **Estilo**: Sigue las convenciones de código de Java (nombres en camelCase, clases en
PascalCase).
## Herramientas y dependencias
- Java 17
- JUnit 5 para pruebas unitarias
- Maven 3.8+
- JaCoCo para análisis de cobertura
## Flujo de desarrollo
- Formatear código: `mvn formatter:format`
- Ejecutar pruebas: `mvn test`
- Ver cobertura: `mvn jacoco:report`
- Validación completa: `mvn verify`
## Actions de Integración y Prueba Continua
- Ejecuta la acción en cada push y pull request dirigido a las ramas principales (`main`,
`develop`).
- Instala las dependencias y construye el proyecto utilizando Maven (`mvn clean install`).
- Ejecuta las pruebas unitarias con Maven (`mvn test`).
- Genera un informe de cobertura de código utilizando JaCoCo con Maven: `mvn
jacoco:report`) y publícalo como artefacto del workflow.
- Si alguna prueba falla, detén el flujo de trabajo y proporciona un informe detallado del
error en la salida.
- Si todas las pruebas pasan, permite la continuación del flujo de trabajo, como despliegue
automático si lo hubiera.
- Utiliza variables de entorno seguras para el manejo de credenciales y claves si las
hubiera.
- Limpia el entorno al finalizar, eliminando archivos temporales y artefactos innecesarios
generados durante la ejecución.
- Notifica el estado final (éxito o error) en el pull request o commit correspondiente
mediante comentarios o checks de GitHub.
- Mantén el archivo `copilot-instructions.md` actualizado para reflejar cualquier cambio
relevante en el proceso de integración y prueba continua.
- Todos los workflows deben incluir una sección `concurrency` para evitar ejecuciones duplicadas
en Pull Requests.
