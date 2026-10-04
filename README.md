# **TaskFlow 🚀**

TaskFlow es un gestor de tareas full-stack diseñado como prueba de concepto para demostrar una arquitectura moderna, escalable y lista para producción. Incluye un frontend en React, una API REST robusta en Spring Boot, base de datos relacional y notificaciones asíncronas por correo, todo orquestado en contenedores Docker.

## **🏗 Arquitectura del Proyecto**

El sistema está compuesto por 4 contenedores dentro de una red interna de Docker Compose:

1. **Frontend (Nginx \+ React):** Sirve los archivos estáticos de la aplicación React y actúa como proxy inverso (/api/\*) hacia el backend, evitando problemas de CORS en producción.  
2. **Backend (Spring Boot):** Expone la API REST. Está estructurado por funcionalidad (Task, Notification) e implementa un diseño por capas (Controller \-\> Service \-\> Repository).  
3. **Base de Datos (PostgreSQL):** Almacena la información de las tareas. Su esquema es gestionado y versionado a través de **Flyway**.  
4. **Servidor SMTP Falso (Mailpit):** Atrapa los correos de notificación enviados por el backend para visualizarlos en una interfaz web local sin usar cuentas reales.

*(Puedes agregar aquí la imagen del diagrama de arquitectura arquitectura.png)*

## **🛠 Stack Tecnológico**

| Capa | Tecnología | Detalles |
| :---- | :---- | :---- |
| **Frontend** | React \+ Vite | Renderizado rápido y empaquetado optimizado. |
| **Backend** | Java 17 \+ Spring Boot 3 | API REST con Spring Web, Validation y Actuator. |
| **Persistencia** | PostgreSQL 16 \+ Spring Data JPA | ORM seguro con queries derivadas. Migraciones con Flyway. |
| **Mensajería** | Spring Mail \+ Mailpit | Notificaciones por correo vía SMTP. |
| **Tests** | JUnit 5, Mockito, Testcontainers | Tests unitarios, de capa web (MockMvc) y de integración real. |
| **Infraestructura** | Docker \+ Docker Compose | Builds multi-stage y orquestación local. |

## **🚀 Cómo levantar el proyecto localmente**

El proyecto está completamente dockerizado. No necesitas tener Java, Node.js ni PostgreSQL instalados en tu máquina, únicamente **Docker** y **Docker Compose**.

1. **Clona el repositorio:**  
   git clone https\://github.com/tu-usuario/taskflow.git  
   cd taskflow

2. **Levanta la infraestructura:**  
   docker compose up \--build \-d

3. **Accede a los servicios:**

| Servicio | URL |
| :---- | :---- |
| **Aplicación Web (React)** | [http\://localhost:3000](http://localhost:3000) |
| **Bandeja de Correo (Mailpit)** | [http\://localhost:8025](http://localhost:8025) |
| **Health Check del Backend** | [http\://localhost:8080/actuator/health](http://localhost:8080/actuator/health) |

Para detener y limpiar los contenedores (conservando los datos en el volumen):

docker compose down

## **🧠 Decisiones Técnicas Destacadas**

* **Eventos de Dominio Asíncronos:** El envío de correos no bloquea la petición HTTP. Se utiliza @TransactionalEventListener(phase \= TransactionPhase.AFTER\_COMMIT) junto con @Async. Si la transacción de base de datos falla, el correo *no* se envía, garantizando la consistencia.  
* **Bloqueo Optimista (Optimistic Locking):** Implementado con la anotación @Version en JPA para prevenir que dos usuarios sobrescriban el estado de una tarea simultáneamente.  
* **DTOs inmutables con Records:** Exponen solo la información necesaria en la API, protegiendo las entidades de base de datos (evitando el *LazyInitializationException* y fugas de datos).  
* **Manejo de Errores Global:** Implementación del estándar RFC 9457 con ProblemDetail usando un @RestControllerAdvice, garantizando que el frontend siempre reciba una estructura de error predecible.  
* **Docker Multi-stage Build:** Los Dockerfiles compilan el código fuente descartando el JDK y dependencias de build en la imagen final, reduciendo drásticamente el tamaño y la superficie de ataque.

## **🧪 Pruebas (Testing)**

La suite de pruebas cubre la pirámide de testing:

* **Unitarios:** Lógica de negocio aislada usando Mockito.  
* **Capa Web:** Verificación de contratos HTTP y validaciones con @WebMvcTest.  
* **Integración:** Pruebas de flujo completo interactuando con una base de datos PostgreSQL real efímera levantada automáticamente con Testcontainers.