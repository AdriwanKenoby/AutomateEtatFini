#!/bin/bash

for f in ${1}??.dot; do
  dot -Tgif -o $f.gif $f 
done
convert -loop -1 -delay 120 ${1}??.dot.gif ${1/dot/gif}.gif 
rm ${1}??.dot.gif ${1}??.dot

#xv ${1}.gif
# autre visualiseur:
# firefox g.gif
#
# sous MacOS
# open g.gif


