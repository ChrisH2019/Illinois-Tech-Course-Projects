#!/bin/bash

# Replace this line with one or more shell commands
# You may write to intermediate text files on disk if necessary
for f in test_*.txt
do 
	grep -w -c "the" $f > the
	grep -w -c "a" $f > a
	grep -w -c "an" $f > an
	echo $f > f
	paste -d "," f the a an
done
