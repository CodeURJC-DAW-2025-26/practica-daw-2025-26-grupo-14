FROM mysql:8.4

COPY ./initdb.d /docker-entrypoint-initdb.d