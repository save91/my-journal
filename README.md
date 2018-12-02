# my-journal
A boilerplate for the creation of a complete journal system with backend, frontend, PWA and apps.

## Prerequisites

- docker [https://docs.docker.com/install/](https://docs.docker.com/install/)
- docker-compose [https://docs.docker.com/compose/install/#install-compose](https://docs.docker.com/compose/install/#install-compose)

## Setup

- `docker build -t myjournal:wp-dev -f Dockerfile_wp-dev .`

## Start Docker

- `docker-compose up -d`


## Access SSH to container:

- *WP*: `docker exec -u www-data -it wp /bin/bash`
- *DB*: `docker exec -u www-data -it db /bin/bash`

If you want tu use shell without limit use:

- *WP*: `make ssh container=wp`
- *DB*: `make ssh container=db`

## Build(only the first time)

Inside `wp` run the following commands:

- `cd /var/www/html`
- `make build`

This command will create an empty wp project

## Access WordPress

- URL: http://localhost
- Admin: http://localhost/wp-admin
    - Username: `admin`
    - Password: `admin`

## Delete all containers

- `docker-compose stop && docker-compose rm`
