FROM eclipse-temurin:17-alpine


# Hemos pensado en implementar seguridad en los contendores
# Realizar buenas practica y no tener credenciales sensibles en nuestro imagenes

# Tenemos de seguridad -> usaremos un usuario no root 
RUN addgroup -g 1028 devopsc \
    && adduser -D -G devopsc admin

VOLUME /tmp

# Copiaremos el jar a la imagen y credenciales de vale de datos al ejecutar nuestro contendores
ARG JAR_FILE
ARG ARG DB_PASSWORD
COPY ${JAR_FILE} /tmp/app.jar

# guardaremos la constraseña pasada en una variable de entorno
ENV DB_PASSWORD=$DB_PASSWORD

# Cambios al usuario que hemos creado
RUN chown -R admin:devopsc /tmp
USER admin

# Ejecutamos el jar al iniciar nuestro contenedor
ENTRYPOINT [ "java","-jar","/tmp/app.jar"]



