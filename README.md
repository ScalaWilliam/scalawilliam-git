# indieGit


> Software-enforced workflows with Git

## Problem space
Centralised Git hosting services come with batteries included but they don't scale to your workflows.
You end up using multiple systems just to complete one piece of work, and none of them integrate very nicely.

For example, for a typical piece of work:

1. Create JIRA/GitHub issue
2. Decide with business which is most important
3. Create a branch with a name that may or may not match the JIRA issue's name
4. Some people skip all the first three steps.
5. Create a PR, then finally merge.
6. You have noise all over the place :-)

Why so many variables?

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


## Integrated issue system

This Issue system will only allow users to push to specific branches that are created as a result of an Issue.

Branch names will be pre-allocated and can be cloned as well, with easy intructions.

No more messing about in deciding what branch name to use - or using some funky scheme. Let the software decide.