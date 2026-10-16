podman run -it --rm \
  --name mysql \
  --publish 3306:3306 \
  -e MYSQL_ROOT_PASSWORD='CHANGEME' \
  -e MYSQL_DATABASE='appdb' \
  mysql:latest