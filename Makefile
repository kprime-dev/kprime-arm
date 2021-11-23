compile:
	mvn clean compile -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

test:
	mvn clean test -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

install:
	mvn clean install -Dorg.slf4j.simpleLogger.defaultLogLevel=warn
