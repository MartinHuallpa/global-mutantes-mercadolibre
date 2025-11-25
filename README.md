## Examen MercadoLibre – Detector de Mutantes

**Autor:** Martín Huallpa
**Universidad Tecnológica Nacional – Facultad Regional Mendoza**
**Año:** 2025

---

## 1. Introducción

Este proyecto implementa la API del desafío de MercadoLibre para determinar si una secuencia de ADN pertenece a un mutante.
La verificación se basa en identificar **más de una secuencia de cuatro letras iguales consecutivas** en direcciones **horizontal, vertical, diagonal ascendente y diagonal descendente**.

Se desarrolló con **Spring Boot 3**, **Java 21**, **Gradle**, **H2**, **Spring Data JPA**, **Bean Validation**, **Swagger** y **JUnit + Mockito** para testing.

---

## 2. Tecnologías utilizadas

| Tecnología             | Uso                             |
| ---------------------- | ------------------------------- |
| **Java 21**            | Lenguaje principal              |
| **Spring Boot 3.4.12** | Web, MVC, configuración general |
| **Spring Web**         | Exposición de endpoints REST    |
| **Spring Data JPA**    | Persistencia                    |
| **H2 Database**        | Base de datos en memoria        |
| **Lombok**             | Reducción de boilerplate        |
| **Spring Validation**  | Validación de entrada           |
| **Springdoc OpenAPI**  | Documentación Swagger           |
| **JUnit 5 + Mockito**  | Tests unitarios e integración   |
| **JaCoCo**             | Cobertura de código             |

---

## 3. Arquitectura del proyecto

La aplicación respeta una arquitectura limpia en **capas**:

```
src/main/java/org/global/mutantes_ds/
│
├── config/                → Configuración global (Swagger)
├── controller/            → Capa de presentación
├── dto/                   → Data Transfer Objects
├── entity/                → Entidades JPA
├── exception/             → Manejo de excepciones
├── repository/            → Acceso a datos (Spring Data)
├── service/               → Lógica de negocio
├── validation/            → Validaciones personalizadas
└── MutantesDsApplication  → Clase principal
```

---

## 4. Funcionamiento general

### 4.1 Detección de mutantes

Se analiza un array `String[] DNA` para buscar secuencias de **4 letras iguales** (A, T, C, G) en las direcciones permitidas.

### 4.2 Deduplicación

Antes de analizar un ADN, se calcula su **hash SHA-256**.
Si ya existe en la base de datos, se utiliza el resultado previo (optimización requerida por la rúbrica).

### 4.3 Persistencia

Los resultados se almacenan en la tabla **DNA_RECORDS**, incluyendo:

* hash del ADN
* si es mutante
* fecha de creación

### 4.4 Estadísticas

El endpoint `/stats` devuelve:

* cantidad de mutantes
* cantidad de humanos
* ratio mutantes/humanos

---

## 5. Endpoints REST

### POST **/mutant**

Determina si el ADN es mutante.

**Request (JSON):**

```json
{
  "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"]
}
```

**Respuestas:**

| Código              | Significado   |
| ------------------- | ------------- |
| **200 OK**          | Es mutante    |
| **403 Forbidden**   | No es mutante |
| **400 Bad Request** | ADN inválido  |

---

### GET **/stats**

Devuelve estadísticas agregadas.

**Response:**

```json
{
  "count_mutant_dna": 2,
  "count_human_dna": 3,
  "ratio": 0.66
}
```

---

## 6. Documentación Swagger

Disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

---

## 7. Validaciones

El proyecto incluye una anotación personalizada:

```
@ValidDnaSequence
```

Valida:

* ADN no vacío
* NxN
* Solo caracteres válidos `A,T,C,G`

---

## 8. Tests

### 8.1 Resumen de cantidad

| Test                     | Cantidad | Tipo                 |
| ------------------------ | -------- | -------------------- |
| **MutantDetectorTest**   | 16       | Algoritmo puro       |
| **MutantServiceTest**    | 5        | Tests con mocks      |
| **StatsServiceTest**     | 6        | Tests con mocks      |
| **MutantControllerTest** | 8        | Tests de integración |

Total: **35 tests**, cumpliendo con la rúbrica de “35+”.

---

### 8.2 Cobertura JaCoCo

<img width="1440" height="294" alt="Captura de pantalla 2025-11-25 a la(s) 11 04 48" src="https://github.com/user-attachments/assets/b2fd4004-2f64-454b-b545-edbdac3bd158" />


La cobertura global del proyecto es **≥90%**, cumpliendo la categoría “Excelente”.

---

## 9. Diagrama de Secuencia (PlantUML)

<img width="1540" height="1215" alt="DS-MartinHuallpa" src="https://github.com/user-attachments/assets/3fba8743-f880-4744-ad8c-386ef432c6ca" />

---

## 10. Ejecución del proyecto

### 10.1 Clonar el repositorio

```bash
git clone https://github.com/MartinHuallpa/global-mercadolibre-mutantes.git
cd global-mercadolibre-mutantes
```

### 10.2 Ejecutar

```bash
./gradlew bootRun
```

### 10.3 Probar endpoints

Usando curl:

**Mutante**

```bash
curl -X POST http://localhost:8080/mutant \
-H "Content-Type: application/json" \
-d '{"dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'
```

**Stats**

```bash
curl -X GET http://localhost:8080/stats
```

---

## 11. Base de datos H2

Consola accesible en:

```
http://localhost:8080/h2-console
```

Configuración:

| Campo    | Valor                   |
| -------- | ----------------------- |
| JDBC URL | `jdbc:h2:mem:mutantsdb` |
| User     | `sa`                    |
| Password | *(vacío)*               |

---

## 12. Requisitos del examen

Este proyecto cumple con todos los ítems evaluados:

* Optimización del análisis (SHA-256 + deduplicación)
* Algoritmo eficiente O(N²)
* Cobertura JaCoCo > 90%
* 35+ tests completos
* Arquitectura en 6 capas
* Swagger totalmente operativo
* Validaciones personalizadas
* Manejo de errores y respuestas correctas
* H2 + JPA + repositorio
* Controladores REST limpios
* Código estructurado y documentado

---

## 13. Autor

**Martín Huallpa**
Ingeniería en Sistemas de Información
Universidad Tecnológica Nacional – Facultad Regional Mendoza
2025
