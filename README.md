## Multi Rename Images

Multi Rename Images is a tool that allows users to rename pictures adding the creation/modification date as the
first part of it's name.


#### Build

To build please use Gradle:

~~~
gradle install
~~~

#### Run

To run simply execute the resulting jar file that will be placed in "build/libs"

There is a GUI and a Console mode. For GUI simply double click the JAR file or run:

~~~
java -jar build/libs/multirenamepicture-1.0.jar
~~~

For Console mode run:

~~~
pol@Yoga:~/$ java -jar build/libs/multirenamepicture-1.0.jar -h
usage: multirename [options]
Rename pictures using their creation date
-d,--dir <d>      Directory where images are located
-f,--format <f>   Set the date format for renaming, for example:
                  yyyyMMdd_HHmmss
                  yyyy-MM-dd_HHmmss
                  yyyyMMddHHmmss
                  yyyyMMdd
-h,--help         Print help
-m,--modify <m>   Modify the original date by given umber of seconds (can be a positive or negative number)
-u,--undo         Undo the renaming
-y,--run          Without this option the tool will only show how files will ne renamed
~~~