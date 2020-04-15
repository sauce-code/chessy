# Chessy

Chessy is a simple Chess A.I. using a look-ahead strategy. Its provided as a Maven project, encoded in UTF8, written in Java and published under [GPLv3](http://www.gnu.de/documents/gpl.de.html). The chess images used were published by Igor Krizanovskij under [Public Domain](https://creativecommons.org/publicdomain/zero/1.0/). JetBrains Mono is released under [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).

## Prerequisites

- Maven
- Java 11

## Install

Just run the following commands:

```bash
git clone https://github.com/sauce-code/chessy.git
cd chessy
mvn install
```

## Run

### Maven Runner

```bash
cd chessy-gui
mvn javafx:run
```

### Command Line

```bash
cd chessy-gui\target
java --module-path %PATH_TO_FX% --add-modules javafx.base,javafx.graphics,javafx.controls -jar chessy-gui-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

## Changelog

Can be found [here](CHANGELOG.md).
