cd parent
mvn clean && mvn install

cd ../core
mvn clean && mvn install

cd ../security
mvn clean && mvn install

cd ../console
mvn clean && mvn install