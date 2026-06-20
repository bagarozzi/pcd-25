all: build run


# in /jpf-core directory
build:
	docker-compose build

# in /jpf-core directory
run: 
	docker-compose run --rm -v ..\assignment-01\thread-based\jpftesting:/pcd-jpf jpf-dev

# inside container, only the first time
# if there's no other gradle running than you can run only the last line
jar:
	sed -i 's/\r$//' gradlew
	chmod +x gradlew
	mkdir -p /tmp/gradle
	GRADLE_USER_HOME=/tmp/gradle ./gradlew clean build

# compile the jpftesting directory
# positioned in /home/jpf-core
compile:
	javac --release 11 -d /pcd-jpf/jpftesting/target/classes -sourcepath src $(find /pcd-jpf/jpftesting -name '*.java')

# run the test
# positioned in /home/jpf-core
test:
	java -jar build/RunJPF.jar /pcd-jpf/jpftesting/Poool.jpf