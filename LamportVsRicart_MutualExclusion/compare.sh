#!/bin/bash
format="$1"
user="$2"
config="config.txt"
result="result.txt"
output="output.csv"
rm -f "$output"
temp="../../temp.txt"

src_dir="src"
bin_dir="bin"
mkdir $bin_dir
javac $src_dir/* -d $bin_dir
output_dir="output"
mkdir $output_dir

N=(5 15)
D=(100 200 500)
C=(100 500 1000)
read -r line < "$format"
line=($line)
r=${line[3]}

for n in "${N[@]}"; do
	for d in "${D[@]}"; do
		for c in "${C[@]}"; do
			cp $format $config
			sed -i "s/x/$n/g" "$config"
			sed -i "s/y/$d/g" "$config"
			sed -i "s/z/$c/g" "$config"

			for (( i=1; i>0; i-- )); do
				./launcher.sh "$config" "$user" 0
				./cleanup.sh "$config" "$user"
				rm -f "$temp"
				read -r line0 < "$result"

				./launcher.sh "$config" "$user" 1
				./cleanup.sh "$config" "$user"
				rm -f "$temp"
				line1=($line1)

				echo "$n,$d,$c,$r,$line0,$line1" >> "$output"
			done

			echo ",,,,,,," >> "$output"
			rm -f "$config"
		done
	done
done
rm -f "$result"
