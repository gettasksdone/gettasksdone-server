
#!/bin/bash

# USO PERSONAL

# # Detener todos los contenedores para asegurar una actualización limpia
# docker-compose -f dc-ci-cd.yml down

# # Descargar la última versión de la imagen
# echo "Comprobando la última versión de la imagen"
# docker-compose -f dc-ci-cd.yml pull server

# # Levantar los servicios nuevamente
# echo "Iniciado los servicios..."
# docker-compose -f dc-ci-cd.yml up --build  -d

# echo "Ultima version de contendores."
# docker ps


#!/bin/bash

# Reemplaza USER, REPO y IMAGE con tus propios datos de usuario, repositorio y nombre de imagen
USER="hackingsecurity"
GIT_HUB_USER_NAME="jcandela@ucm.es"
REPO="tfg_api_rest_gtd"
IMAGE="feature"

# Reemplaza 'YOUR_TOKEN' con tu token de acceso personal
TOKEN="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"


TAG=$(curl -s -H "Authorization: token $TOKEN" \
    "https://api.github.com/users/$USER/packages/container/$IMAGE/versions" \
    | jq -r '.[0].metadata.container.tags[0]')

echo $TAG

echo "$TOKEN" | docker login ghcr.io -u $GIT_HUB_USER_NAME --password-stdin

docker pull ghcr.io/$USER/$IMAGE:$TAG
