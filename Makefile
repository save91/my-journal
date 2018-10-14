setup:
	cd /var/www/html/ && ./setup.sh

build:
	make setup

ssh:
	docker exec -e COLUMNS="`tput cols`" -e LINES="`tput lines`" -it -u www-data $(container) /bin/bash
