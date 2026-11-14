# 📋 Sistema de Gestión PQRSD

### Plataforma web para la gestión, seguimiento y control de Peticiones, Quejas, Reclamos, Sugerencias y Denuncias.

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle" alt="Gradle">
</p>

---

## 📖 Descripción del proyecto

El **Sistema de Gestión PQRSD** es una aplicación web desarrollada para facilitar la administración, organización y seguimiento de las solicitudes presentadas por los ciudadanos ante una organización o entidad.

La plataforma busca centralizar el proceso de recepción y gestión de Peticiones, Quejas, Reclamos, Sugerencias y Denuncias (PQRSD), permitiendo llevar un control organizado de las solicitudes y facilitar el seguimiento de sus respectivos tiempos de atención.

Uno de sus componentes principales es el **sistema de semáforo de vencimientos**, diseñado para identificar visualmente el estado de las solicitudes según sus tiempos de respuesta, facilitando la identificación de aquellas que requieren atención prioritaria.

El proyecto está desarrollado utilizando Java y Spring Boot bajo una arquitectura web, integrando una base de datos relacional para la persistencia de la información.

## 🎯 Objetivo general

Desarrollar una aplicación web que permita gestionar y realizar seguimiento a las solicitudes PQRSD, facilitando su administración, control de tiempos de respuesta y consulta de estados mediante un sistema visual de semaforización.

## 🎯 Objetivos específicos

- Centralizar la información correspondiente a las solicitudes PQRSD.
- Facilitar la administración y seguimiento de las solicitudes registradas.
- Implementar un mecanismo de semaforización para controlar los tiempos de vencimiento.
- Identificar solicitudes que requieren atención prioritaria.
- Gestionar la información mediante una base de datos relacional.
- Incorporar mecanismos de seguridad para controlar el acceso a la aplicación.
- Proporcionar una interfaz web que facilite la interacción con el sistema.

---

## 🚀 Características principales

### 📝 Gestión de solicitudes PQRSD

Administración de solicitudes relacionadas con:

- **Peticiones:** solicitudes formales realizadas por los ciudadanos.
- **Quejas:** manifestaciones de inconformidad frente a un servicio o actuación.
- **Reclamos:** solicitudes relacionadas con inconvenientes o incumplimientos.
- **Sugerencias:** propuestas orientadas al mejoramiento de los servicios.
- **Denuncias:** comunicaciones sobre posibles irregularidades.

### 🚦 Sistema de semaforización

Implementación de un mecanismo visual para facilitar el seguimiento de los tiempos de atención de las solicitudes.

El propósito es permitir identificar rápidamente las solicitudes según su proximidad al vencimiento y facilitar la priorización de su gestión.

### 🔐 Seguridad

Integración de Spring Security para incorporar mecanismos de autenticación y control de acceso a la aplicación.

### 🗄️ Persistencia de información

Utilización de MySQL como sistema de gestión de bases de datos y Spring Data JPA para facilitar las operaciones de persistencia.

### 🌐 Interfaz web

Implementación de una interfaz web mediante Thymeleaf, permitiendo integrar las vistas HTML con la lógica de la aplicación desarrollada en Spring Boot.

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Descripción |
|---|---|
| Java 17 | Lenguaje de programación principal. |
| Spring Boot 3.3.5 | Framework para el desarrollo de la aplicación. |
| Spring MVC | Gestión de solicitudes HTTP y controladores web. |
| Spring Data JPA | Persistencia y gestión de entidades. |
| Spring Security | Seguridad y control de acceso. |
| Thymeleaf | Motor de plantillas para las vistas web. |
| MySQL | Sistema de gestión de bases de datos. |
| Gradle | Automatización de compilación y gestión de dependencias. |
| Lombok | Reducción de código repetitivo en las clases Java. |
| Jakarta Validation | Validación de información. |
| HTML / CSS | Estructuración y presentación de la interfaz. |

---

## ⚙️ Requisitos previos

Para ejecutar el proyecto en un entorno local es necesario contar con:

- Java Development Kit (JDK) 17.
- MySQL Server.
- Gradle Wrapper (incluido en el repositorio).
- IDE compatible con Java, como IntelliJ IDEA, Eclipse o Visual Studio Code.
- Git para clonar el repositorio.

---

## 📥 Instalación y configuración

### 1. Clonar el repositorio

Ejecutar el siguiente comando en una terminal:

```bash
git clone https://github.com/Esneider-afk/pqrs.git
```

### 2. Ingresar al directorio

```bash
cd pqrs
```

### 3. Crear la base de datos

Ingresar a MySQL y ejecutar:

```sql
CREATE DATABASE pqrs;
```

### 4. Configurar la conexión a MySQL

Ubicar el archivo de configuración de Spring Boot:

`src/main/resources/application.properties`

Configurar los parámetros de conexión según las credenciales del entorno local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pqrs
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

**Nota:** Los parámetros anteriores son una configuración de referencia. Deben ajustarse a la configuración real del proyecto y del servidor MySQL.

No se deben publicar contraseñas reales ni credenciales de acceso en el repositorio.

### 5. Ejecutar la aplicación

**Windows:**

```bash
gradlew.bat bootRun
```

**Linux / macOS:**

```bash
./gradlew bootRun
```

### 6. Acceder al sistema

Una vez iniciada correctamente la aplicación, ingresar desde el navegador:

```text
http://localhost:8080
```

La dirección corresponde al puerto predeterminado de Spring Boot, salvo que el proyecto tenga configurado otro puerto.

---

## 📂 Estructura general del proyecto

El proyecto utiliza Gradle como herramienta de construcción y organización de dependencias.

```text
pqrs/
│
├── gradle/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── uts/
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   │
│   └── test/
│
├── .gitattributes
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
└── settings.gradle
```

*La estructura interna de los paquetes y recursos debe ajustarse a la organización definitiva del código fuente.*

---

## 🔒 Seguridad y buenas prácticas

Para mantener la integridad y seguridad de la aplicación se contempla el uso de:

- Spring Security para la protección de los recursos de la aplicación.
- Validación de datos mediante Jakarta Validation.
- Persistencia mediante Spring Data JPA.
- Separación de la configuración del entorno y las credenciales.
- Organización del código bajo las convenciones de desarrollo de Spring Boot.

---

## 🧪 Pruebas

El proyecto incorpora Spring Boot Starter Test y JUnit Platform para la ejecución de pruebas.

Para ejecutar las pruebas automatizadas:

**Windows:**

```bash
gradlew.bat test
```

**Linux / macOS:**

```bash
./gradlew test
```

---

## 🔮 Posibles mejoras futuras

Entre las funcionalidades que pueden ampliar las capacidades del sistema se encuentran:

- Incorporación de un panel de estadísticas y métricas.
- Generación de reportes de solicitudes.
- Notificaciones sobre solicitudes próximas a vencer.
- Historial de seguimiento y modificaciones.
- Filtros avanzados de búsqueda.
- Exportación de información a Excel o PDF.
- Registro de auditoría de las operaciones realizadas.
- Optimización de los indicadores visuales del semáforo.

---

## 👨‍💻 Autor

**Esneider Afanador**

Estudiante de Tecnología en Desarrollo de Sistemas Informáticos.

Desarrollador del proyecto Sistema de Gestión PQRSD.

**GitHub:** [Esneider-afk](https://github.com/Esneider-afk)

---

## 📄 Licencia

Este proyecto tiene fines académicos y de desarrollo de software.

La utilización, distribución o modificación del código debe respetar las condiciones de uso que establezca el autor.

---

<p align="center">
  Desarrollado con Java y Spring Boot.
  <br>
  <strong>Sistema de Gestión PQRSD</strong>
</p>
