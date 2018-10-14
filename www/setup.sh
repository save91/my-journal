#!/bin/bash

println () {
	printf "\e[1;34m$2$1\e[0m\n"
}

section () {
	printf "\e[1;33m--- $2$1 ---\e[0m\n"
}

section "Setup project"
cd /var/www/html

#clean existent installation
println "Cleaning existent database and files..."
wp db drop --yes
wp db create
rm -rf /var/www/html/wp-content/uploads/*

#install wordpress
println "Installing wordpress..."
wp core install

#customize wordpress
println "Customizing wordpress..."
wp language core install it_IT
wp language core activate it_IT

section "Completed with success"