
gradlew:
	gradle gradlew

lock:
	./gradlew clean generateLock saveLock

run:
	./gradlew bootRun

all:
	./gradlew build

clean:
	gradle clean

infer:
	infer -- ./gradlew build
