dir /s /B *.java > sources.txt
javac @sources.txt -d compiled
del "sources.txt"
cd compiled
java game.Main