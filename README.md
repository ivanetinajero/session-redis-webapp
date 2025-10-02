# Guía: Uso de Redis para almacenar sesiones en Spring Boot

Este archivo explica cómo configurar tu aplicación Spring Boot para guardar sesiones en Redis, detalles sobre la serialización de objetos, y comandos útiles para inspeccionar y administrar sesiones.

---

## 1. Configuración de Spring Boot para guardar sesiones en Redis

### 1.1. Agrega las dependencias necesarias en tu archivo `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 1.2. Configura la conexión a Redis en tu `application.properties`:

```properties
# Guardar sesiones en REDIS
spring.data.redis.host=127.0.0.1
spring.data.redis.password=admin
spring.data.redis.port=6389
```

### 1.3 Crear una clase de configuración para las sesiones en REDIS
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {

}
```

### 1.4. ¡Listo!
Con esto, Spring Boot almacenará automáticamente las sesiones en Redis.

---

## 2. ¿Por qué los objetos deben ser `Serializable`?

**Serializable** es una interfaz de Java que permite convertir un objeto en una secuencia de bytes, para luego poder almacenarlo (por ejemplo, en Redis) y recuperarlo más tarde.

### ¿Por qué es necesario?
- **Spring Session** serializa todos los atributos guardados en la sesión antes de enviarlos a Redis.
- Si intentas guardar un objeto que no implementa `Serializable`, verás un error como:
  > `DefaultSerializer requires a Serializable payload but received an object of type [TuClase]`

### ¿Cómo hacerlo?

Supón que tienes una clase `Usuario` que quieres guardar en la sesión:

```java
import java.io.Serializable;

@Entity
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    // tus campos...
}
```

**¡Importante!**
- Todos los objetos que guardes en la sesión deben ser serializables.
- Si tienes atributos que son otros objetos (por ejemplo, una lista de perfiles, o una sucursal), esos objetos también deben implementar `Serializable`.

#### ¿Qué es `serialVersionUID`?
Es un identificador de versión para la clase serializable. Ayuda a evitar problemas al deserializar objetos si la estructura de la clase cambia. Puedes poner `1L` o cualquier otro número.

---

## 3. Acceso y administración de sesiones en Redis

### 3.1. Acceder al CLI de Redis en Docker

```bash
docker exec -it <nombre_del_contenedor_redis> redis-cli
```
Reemplaza `<nombre_del_contenedor_redis>` por el nombre real de tu contenedor (normalmente es `redis`).

---

### 3.2. Autenticación (si Redis tiene contraseña)

```bash
AUTH tu_contraseña
```

---

### 3.3. Listar todas las claves de sesión de Spring Boot

```bash
KEYS *
KEYS spring:session:sessions:*
```

---

### 3.4. Ver el contenido de una sesión

```bash
HGETALL spring:session:sessions:<session-id>
```

---

### 3.5. Saber cuánto espacio ocupa una sesión

```bash
MEMORY USAGE spring:session:sessions:<session-id>
```

---

### 3.6. Borrar una sola sesión

```bash
DEL spring:session:sessions:<session-id>
```

---

### 3.7. Borrar todas las sesiones (deslogueo global). Acceder al CLI de Redis en Docker.

```bash
docker exec -it <nombre_del_contenedor_redis> bash

redis-cli --scan --pattern 'spring:session:sessions:*' | xargs redis-cli DEL

redis-cli -a mipassword --scan --pattern 'spring:session:sessions:*' | xargs redis-cli -a mipassword DEL

```

---

## 4. ¿Qué se guarda en cada clave de sesión?

- **Atributos personalizados:** Lo que agregues con `session.setAttribute(...)`.
- **Atributos internos de Spring Security:** Contexto de autenticación, roles, etc.
- **Metadatos de sesión:** Tiempos de creación, expiración, último acceso, etc.

Los datos complejos se guardan serializados (no legibles directamente), pero puedes leerlos desde tu aplicación con los métodos normales de sesión.

---

## 5. Recomendaciones

- Mantén las sesiones ligeras: menos de 1KB por usuario es ideal.
- Serializa solo lo necesario.
- Si usas colecciones, asegúrate que los objetos internos también sean serializables.
- Si tienes campos que no deben serializarse, márcalos como `transient`.

---

## Recursos útiles

- [Documentación oficial de Redis](https://redis.io/commands/)
- [Spring Session Data Redis](https://docs.spring.io/spring-session/reference/configuration/redis.html)
- [Serialización en Java (Oracle)](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/serial-arch.html)

---

## Contribuciones

¿Tienes más comandos o tips útiles? ¡Agrega tu experiencia y ayúdanos a mejorar este repositorio!
