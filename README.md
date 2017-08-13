
# ScalaWilliam Git

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
$ git clone http://localhost:8080/git/some-repo
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

Data is stored inside the Git repository itself using [Git notes](https://git-scm.com/docs/git-notes) 
([another link](http://alblue.bandlem.com/2011/11/git-tip-of-week-git-notes.html), &
[some tips](https://gist.github.com/topheman/ec8cde7c54e24a785e52)).

Appending a note, for example:

```
$ git notes --ref change-requests append -m '{"created_time": "2017-08-11T10:00:00Z", "title": "Figure out how to host two servlets in the same space", "description": "Setting one servlet at `/` and another at `/*` always directs to the second one, regardless of the mapping order.", "by": "William"}'
```

## Developing with iterations

Use sbt-revolver command `re-start` to start the app in background. Use it again to restart it.

Make change to source-code and it'll restart the app for you - using `~re-start`.


## Reference

This book is great: https://github.com/centic9/jgit-cookbook/


## More explanation

Because all data is in Git and independent...

We don't need no APIs

We don't need no REST

We don't need no GraphQL

We don't need no artificial rate limiting

We don't need no complication

And many other benefits!

## Provisioning

Using [Ansible](https://www.ansible.com/get-started):

```
$ sbt 'show universal:packageZipTarball'
$ ansible-playbook --user=root -i swgit, ansible/prod-playbook.yml
$ 
$ ansible all --user=root -i swgit, -a 'systemctl status gitsw'
```


## Continuous deployment

Using [websub-execute](https://github.com/ScalaWilliam/websub-execute),

```
$ git clone https://git.digitalocean.scalawilliam.com/git/some-repo ~/Projects/test-gsw-1
$ websub-execute -u https://git.digitalocean.scalawilliam.com/ -i -- 'cd ~/Projects/test-gsw-1 && git pull && sbt "show universal:packageZipTarball" && ansible-playbook -i swgit, ansible/prod-playbook.yml'
```

I can run this on my Mac if I need to :-)
And then scale up to something bigger when more people are using the project...

### Continuous sync to GitHub

Super easy:
```
$ cd ~/Projects/test-gsw-1
$ git remote add github git@github.com:ScalaWilliam/scalawilliam-git.git
$ websub-execute -u https://git.digitalocean.scalawilliam.com/ -i -- 'cd ~/Projects/test-gsw-1 && git pull && git push github HEAD'
```

... try to do this with GitHub webhooks :-)
