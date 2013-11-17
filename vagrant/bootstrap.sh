#!/usr/bin/env bash

# Halt on errors
set -e

# Set timezone to Swedish timezone
echo "Europe/Stockholm" > /etc/timezone
dpkg-reconfigure -f noninteractive tzdata

# Specifies locale for all users
echo "LANGUAGE=en_US.UTF-8
LC_ALL=en_US.UTF-8
LANG=en_US.UTF-8
LC_TYPE=en_US.UTF-8
" > /etc/environment

# Add homeki user
useradd --home-dir /home/homeki --create-home --groups sudo,plugdev homeki

# Add homeki repository to apt
echo "
# Homeki repository
deb http://repository.homeki.com/packages stable main" >> /etc/apt/sources.list
pushd /home/homeki
wget http://repository.homeki.com/homeki-public.key
apt-key add homeki-public.key
rm homeki-public.key
popd

apt-get update

# From standard repositories
apt-get install -y openjdk-6-jre-headless postgresql-9.1 libjna-java libgnumail-java vim

# From homeki repository
apt-get install -y telldus owfs

# Create database and database user
echo "CREATE USER homeki WITH PASSWORD 'homeki';" | sudo -u postgres psql
echo "CREATE DATABASE homeki WITH owner = homeki;" | sudo -u postgres psql

