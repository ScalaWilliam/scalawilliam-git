# indieGit


> Software-enforced workflows with Git

## Problem space

## Technical choices

- Scala & SBT - what I'm specialised in & it runs on JVM nicely.
- Jetty & Servlet - because JGit's Git Server is built on top of Servlet and there's no need to reinvent the wheel

## Usage

First, install [sbt](https://www.scalawilliam.com/essential-sbt/).

```
$ export HTTP_PORT=8080
$ export GIT_DIR=/some/repo/.git/
$ sbt run
```
