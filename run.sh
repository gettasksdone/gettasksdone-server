#!/bin/bash

echo "¿Deseas desplegar el proyecto para el frontend (S/n)?"

DOCKER_COMPOSE_FILE_FRONTED=".docker-compose/docker-compose.fronted.yml"
DOCKER_COMPOSE_DEVELOP="docker-compose.dev.yml"

# Lee la entrada del usuario con 'S' como opción por defecto
read -r -p "[S/n]: " response
response=${response,,} 

if [[ $response =~ ^(s|si|S|SI|)$ ]]; then
    echo "Desplegando contenedor para el frontend..."
   
    # Desplegamos el proyecto de gettaskdone-server
    docker-compose -f  $DOCKER_COMPOSE_FILE_FRONTED up --build
else
    echo "Desplegando proyecto desde cero..."
    docker-compose -f $DOCKER_COMPOSE_DEVELOP up --build;
    
fi

