
#!/bin/bash

# FOR TEAM

# Detener todos los contenedores para asegurar una actualización limpia
docker-compose -f dc-ci-cd.yml down

# Descargar la última versión de la imagen
echo "Comprobando la última versión de la imagen"
#docker-compose -f dc-ci-cd.yml pull server

# Levantar los servicios nuevamente
echo "Iniciado los servicios..."
docker-compose -f dc-ci-cd.yml up --build  -d

#Listamos los contenedores iniciados

docker ps 2>error.log


# Puertos que estan  a la escucha
echo -e "\nPUERTOS QUE ESTAN A LA ESCUCHA.... \n"
netstat -tulnp | grep -E ':8000|:8080' 



echo -e "\nYA PUEDES ATACAR CON POSTMAN.... \n"
echo  "HAZ UN [GET] o en desde un navegador copia el siguiente enlace:"
echo -e "\tAPUNTA: http://127.0.0.1:8080/api/test \n\n"
