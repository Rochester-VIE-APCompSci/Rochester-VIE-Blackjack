#!/bin/bash

jar_dir=${1:?"Please enter directory with student jars"};


MAIN="student.player.MyPlayer"
STUDENT_CLASS="student.player.MyPlayer"
SOURCE_DIR="student_java_source"

MY_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

FRAMEWORK_JAR="$MY_HOME/build/build4student/blackjack.jar"

LIB=$(find $MY_HOME/lib -name "*.jar" -printf "%p:")
echo $LIB

let jar_count=0

rm -rf $SOURCE_DIR

# Recompile function in case student didn't submit jar with class file
function recompile()
{
  echo "  Recompiling $1"

  rm -rf $jar_dir/temp*
  
  mkdir $jar_dir/temp
  unzip "$1" -d $jar_dir/temp
  local source=$(find $jar_dir/temp/student -name "*.java")
  javac -cp $FRAMEWORK_JAR:$LIB -sourcepath $jar_dir/temp $source

  cd $jar_dir/temp
  zip -q -r "../temp.jar" .
  cd -
}


## Loop through all jars in provided directory
find $jar_dir -type f -name "*.jar" -print0 | while IFS= read -r -d '' jar; 
do
  echo $jar >> jar_list.txt

  # Get name without spaces if students were evil enough to submit a jar with spaces
  new_name=`echo $jar | sed -e "s/\s/_/g ; s/.*\///g ; s/\./_/g"`
  echo
  echo "Running $jar_count: $new_name"


  # Extract source code out for viewing later
  mkdir -p $SOURCE_DIR/$new_name
  unzip "$jar" "*.java" -d $SOURCE_DIR/$new_name
  

  # Ensure jar contains class file, if not, recompile 
  num_sources=`jar tf "$jar" | grep '\.java$' | wc -l | tr -d '\n'`
  num_classes=`jar tf "$jar" | grep class | wc -l | tr -d '\n'`
  echo "sources: $num_sources classes: $num_classes"
  if [[ "$num_classes" -lt "$num_sources" ]]; then
    recompile "$jar"
    jar=$jar_dir/temp.jar
  fi

  cp "$jar" current-player.jar

  # Run player against boards
  java -DGRADE_BLACKJACK=true -cp $FRAMEWORK_JAR:$LIB:current-player.jar $MAIN > player.out

  jar_count=$((jar_count + 1))
done

echo
echo "Please see result.csv for player results. Additionally, see $SOURCE_DIR for corresponding source code"
echo

rm -rf $jar_dir/temp* current-player.jar player.out
