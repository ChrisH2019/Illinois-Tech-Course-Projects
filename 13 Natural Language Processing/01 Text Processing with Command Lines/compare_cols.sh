#!/bin/bash
cat test.in.2.txt > test
awk -F ',' '{print $3}' test| awk -F ' ' '{print $1}' > w1.txt
echo "first words:"
cat w1.txt
awk -F ',' '{print $5}' test > c5.txt
echo "words of 5th columns:"
cat c5.txt
count=1
match=0
echo "Start searching!"
for i in $(cat w1.txt)
do 
head -n $count c5.txt | tail -n 1 > h1.txt
echo $i 
cat h1.txt
found=$(grep -c -i -w $i h1.txt)
echo "found?" $found
match=$((match + found))
echo "Total number of matches:" $match
count=$((count+1))
done
echo "Done searching!"
echo "Final total number of matches: " $match
