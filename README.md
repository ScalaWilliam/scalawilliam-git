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
$ git clone http://localhost:8080/any-repo-name 
```

To enable HTTP pushes, edit `GIT_DIR` and add the following:
```
[http]
	receivepack = true
```

Then you can do:

```
$ git push origin HEAD
```


## Debugging just in case

Cause we're dealing with lots of mutable Java code behind servlets, some things may not work as expected.
In order to debug, from SBT, you can right click MainServer and press Debug. You will need to edit the 'configuration' of this run
to add the environment variables above as well.

