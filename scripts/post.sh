#!/bin/bash

while read req; do
  echo $req
  curl -XPOST "$req"
done < load.txt
