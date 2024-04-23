#!/bin/bash

function confirmacion() {
    read -r -p "ADVERTENCIA: ¡EJECUTAR ESTA ACCIÓN ELIMINARÁ TODA LA INFORMACIÓN ALMACENADA EN LA BASE DE DATOS!. ¿ESTÁS SEGURO DE QUERER RESTABLECER EL SERVIDOR? [S/n]" confirmacion
    confirmacion=${confirmacion,,}
    if [[ $confirmacion =~ ^(s|si|S|SI|)$ ]]; then
        return 0
    else
        return 1
    fi
}

function mostrar_menu() {
    echo "Selecciona una opción:"
    echo "1) Iniciar servidor"
    echo "2) Restablecer servidor (ZONA DE PELIGRO)"
    echo "0) Para salir "
}

while true; do
    mostrar_menu
    read -r -p "Introduce tu opción [0-2]: " option

    case $option in
        1)
            echo "Iniciando servidor, por favor espere..."
            docker-compose -f docker-compose.dev.yml up --build
            break
            ;;
        2)
            if confirmacion; then
                echo "Restableciendo servidor..."
                #Detiene los contenedores en caso de que esten en ejecucion
                docker stop gettasksdone-server-server-1;
                docker stop gettasksdone-server-db-1;
                #Elimina los contenedores
                docker rm gettasksdone-server-server-1;
                docker rm gettasksdone-server-db-1;
                #Elimina la imagen del API y las que no están en uso, no hace falta borrar la SQL
                docker rmi gettasksdone-server-server;
                docker image prune;
                #Elimina los volúmenes de la base de datos (API no usa volúmenes)
                docker volume rm gettasksdone-server_db_config;
                docker volume rm gettasksdone-server_db_data;
            else
                echo "Operación abortada."
            fi
            ;;
        0)
            echo "Finalizando script."
            exit 0
            ;;
        *)
            echo "Opción no válida."
            ;;
    esac
done