# GetTasksDone-server

Servidor y BD del TFG de GTD para el curso 2023-24 de la UCM. La BD se utiliza una imagen estándar de mysql importada en el docker-compose

# Depliegue del contenedor de github

```bash

./run.sh

```

Te preguntará:

 ¿Deseas desplegar el proyecto para el frontend (S/n)?, si la respuesta es que sí, la opción ya está seleccionada por defecto. Por el contrario si queremos construir desde cero la imagen ponemos "n" (esta parte la podemos hacer sin estar logeados con ghcr.) y está mas enfocado para el desarrollo en el backend.

**Para poder descarga la imagen desde el repositorio debemos estar autorizados con usuario y contraseña.**

```bash

docker login ghcr.io -u USUARIO -p ACCESS_TOKEN

```

# Parar contenedores y borrarlos

```bash

docker rm $(docker stop $(docker ps -aq))

```

# Eliminar imagenes, volumenes (usar con cuidado)

```bash

docker system prune -a --volumes

```
