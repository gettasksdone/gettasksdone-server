#!/bin/bash

# Problemática de la partimos:
# debe poder desplegarse el proyecto una vez en el servidor
# debe permitirse actualizar un los contendores
# debe tener la version de desarollo para el backend

#anotaciones:
# es mejor crear funciones para no tener redundancias.
# añadida las anotaciones de David
# comprobar que la base de datos no se modifica y que se mantiene el servidor
#  -> comprobamos que da aqui un fallo (revisarlo)
function confirmacion() {

    read -r -p "¿Estás seguro de tu elección [S/n]? " confirmacion
    confirmacion=${confirmacion,,}
    if [[ $confirmacion =~ ^(s|si|S|SI|)$ ]]; then
        return 0
    else
        return 1
    fi
}

function mostrar_menu() {
    echo "Selecciona una opción:"
    echo "1) Actualizar contenedor del frontend"
    echo "2) construir contenedores en el servidor desde cero"
    echo "3) Modo Desarrollador Backend"
    echo "4) Borrar todo (PELIGRO)"
    echo "0) Para salir"
}

DOCKER_COMPOSE_FILE_FRONTED=".docker-compose/docker-compose.fronted.yml"
DOCKER_COMPOSE_DEVELOP="docker-compose.dev.yml"

while true; do
    mostrar_menu

    read -r -p "Introduce tu opción [0-4]: " option

    case $option in
        1)
            echo "Has seleccionado Modo Actualización (frontend)."
            if confirmacion; then
                echo "Actualizando y desplegando el frontend..."

                #las modificaciones que existan en la base de datos quedan
                # reflejadas en los volumunes creados con docker  (tener en cuenta)
                docker-compose -f $DOCKER_COMPOSE_FILE_FRONTED stop
                docker-compose -f $DOCKER_COMPOSE_FILE_FRONTED pull
                docker-compose -f $DOCKER_COMPOSE_FILE_FRONTED up -d

            else
                echo "Operación cancelada."
            fi
            break
            ;;
        2)
            echo "Has seleccionado Lanzar en el servidor."
            if confirmacion; then
                echo "Desplegando desde cero el proyecto en el servidor..."
                docker-compose -f $DOCKER_COMPOSE_FILE_FRONTED down -v
                docker-compose -f $DOCKER_COMPOSE_FILE_FRONTED up --build
            else
                echo "Operación cancelada."
            fi
            break
            ;;
        3)
            echo "Has seleccionado Modo Desarrollador Backend."
            if confirmacion; then
                echo "Desplegando proyecto para desarrollo backend..."
                docker-compose -f $DOCKER_COMPOSE_FILE_FRONTED down -v
                docker-compose -f $DOCKER_COMPOSE_DEVELOP up --build
            else
                echo "Operación cancelada."
            fi
            break
            ;;
        4)
            echo "Has seleccionado Eliminar todo (imagenes, contenedores) de docker."
            if confirmacion; then
                echo "Eliminando todo..."
                docker rm $(docker stop $(docker ps -aq))
                docker system prune -a --volumes
            else
                echo "Operación cancelada."
            fi
            break
            ;;
        0)
            echo "Saliendo del script"
            exit 0
            ;;
        *)
            echo "Opción no válida. Intenta de nuevo."
            ;;
    esac
done
