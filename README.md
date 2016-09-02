Multi Rename Picture

Multi Rename Picture is a tool that allows users to rename pictures adding the creation/modification date as the
first part of it's name.

To build please use Gradle:

gradle install

To run simply execute the resulting jar file that will be placed in "build/libs"


pol@Yoga:~/dev/multirenamepicture$ java -jar build/libs/multirenamepicture-1.0.jar -h
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
