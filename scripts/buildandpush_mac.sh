BuildSpringBootApplication () {
  echo "[2-$1] build $2 "
  ./gradlew grida-core:"$2":build
}

CreateDockerImage () {
  echo "[2-$1] create docker image $2 "
  cd grida-core/"$2" || exit
  docker build --platform linux/amd64 -t wwan13/grida-"$2":prod .
  cd ../..
}

PushToDockerHub () {
  echo "[2-$1] push to docker hub $2 "
  docker push wwan13/grida-"$2":prod
}

BuildAndPushToDockerHub () {
  echo "[2-1] for $2 "
  BuildSpringBootApplication "$1" "$2"
  CreateDockerImage "$1" "$2"
  PushToDockerHub "$1" "$2"
}

echo "build applications start"

echo "[1] clean project"
./gradlew clean

echo "[2] build and push to docker hub"
BuildAndPushToDockerHub 1 core-api