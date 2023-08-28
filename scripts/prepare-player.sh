#!/bin/bash

basedir="$( cd "$(dirname ${BASH_SOURCE[0]})" && pwd)"

sed -e "s/\"FirstName LastName\"//g" -i $(find $basedir -name MyPlayer.java)
