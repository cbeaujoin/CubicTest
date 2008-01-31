cd CubicTestPlugin
cp cubictest-1.8.1.jar ~/.m2/repository/org/cubictest/cubictest/1.8.1
sha1 cubictest-1.8.1.jar | awk '{print $1}' > ~/.m2/repository/org/cubictest/cubictest/1.8.1/cubictest-1.8.1.jar.sha1
md5 cubictest-1.8.1.jar | awk '{print $1}' > ~/.m2/repository/org/cubictest/cubictest/1.8.1/cubictest-1.8.1.jar.md5

cp cubictest-1.8.1.pom ~/.m2/repository/org/cubictest/cubictest/1.8.1
sha1 cubictest-1.8.1.pom | awk '{print $1}' > ~/.m2/repository/org/cubictest/cubictest/1.8.1/cubictest-1.8.1.pom.sha1
md5 cubictest-1.8.1.pom | awk '{print $1}' > ~/.m2/repository/org/cubictest/cubictest/1.8.1/cubictest-1.8.1.pom.md5

cd ../CubicTestSeleniumExporter
cp selenium-exporter-1.8.1.jar ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1
sha1 selenium-exporter-1.8.1.jar | awk '{print $1}' > ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1/selenium-exporter-1.8.1.jar.sha1
md5 selenium-exporter-1.8.1.jar | awk '{print $1}' > ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1/selenium-exporter-1.8.1.jar.md5

cp selenium-exporter-1.8.1.pom ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1
sha1 selenium-exporter-1.8.1.pom | awk '{print $1}' > ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1/selenium-exporter-1.8.1.pom.sha1
md5 selenium-exporter-1.8.1.pom | awk '{print $1}' > ~/.m2/repository/org/cubictest/selenium-exporter/1.8.1/selenium-exporter-1.8.1.pom.md5

cd ../CubicTestMojo
mvn clean install -DcreateChecksum=true
cd ..

