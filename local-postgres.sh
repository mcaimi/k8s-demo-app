sudo podman run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 \
           --name postgres-quarkus-hibernate -e POSTGRES_USER=hibernate \
           -e POSTGRES_PASSWORD=hibernate -e POSTGRES_DB=hibernate_db \
           -p 5432:5432 postgres:10.5
