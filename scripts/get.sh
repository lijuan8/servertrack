#!/bin/bash

while read req; do
  echo $req >> test.result
  curl  -XGET "$req" >>  test.result
  echo  >> test.result
  echo  >> test.result
done < request.txt
