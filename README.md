
Stack Editor
----

ClojureScript editor inspired by Clouditor.

Editor http://repo.cirru.org/stack-editor/

### Usage

This is the web interface for Stack Editor, we also need a server.
I made a workflow https://github.com/mvc-works/stack-workflow for starting.

Steps to use it:

* create a project with a `stack-sepal.ir`

Default content should be `{}` or a snapshot from another project(need to update namespace).

* run boot task

Read more here https://github.com/Cirru/boot-stack-server

Use `boot repl` and `(boot (start-stack-editor!))` to start the server.

* edit with web

Open http://repo.cirru.org/stack-editor/ to connect http://localhost:7001

### Develop

https://github.com/mvc-works/boot-workflow

### License

MIT
