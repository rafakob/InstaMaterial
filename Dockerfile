FROM quay.io/ainoya/android-project

MAINTAINER Naoki AINOYA <ainonic@gmail.com>

ENV PROJECT /project
RUN mkdir $PROJECT
WORKDIR $PROJECT
COPY . $PROJECT

RUN echo "sdk.dir=$ANDROID_HOME" > local.properties
RUN ./gradlew --stacktrace androidDependencies

CMD ./gradlew --stacktrace build
