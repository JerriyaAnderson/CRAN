
Stack Editor
----

ClojureScript editor inspired by [Clouditor](https://github.com/Cirru/clouditor/).

Editor http://repo.cirru.org/stack-editor/

![Workspace](https://pbs.twimg.com/media/Cw0tNoCUAAAgJLe.png:large)
![Definitions](https://pbs.twimg.com/media/Cw0tPbHVQAIGIt0.png:large)
![Command Palette](https://pbs.twimg.com/media/Cp50MD6WcAEXq0B.png:large)

### Usage

I have to say it's quite hard to pick up. You may need to watch the video first:
https://www.youtube.com/watch?v=uCNJUxGVcqs

The template project in the video is: https://github.com/mvc-works/stack-workflow

I've listed the keyboard shortcuts here, you will need it:
https://github.com/Cirru/respo-cirru-editor/wiki/Keyboard-Shortcuts

Stack Editor is built on top of [Boot](http://boot-clj.com).
You can find more about the task at: https://github.com/Cirru/boot-stack-server

### Steps to use it

If you choose to setup by yourself, here's a short guide:

**Step 1:**

Create a file `stack-sepal.ir` with content `{}` in project root directory.

**Step 2:**

Add a task in `build.boot`:

```clojure
; from package [cirru/boot-stack-server "0.1.22" :scope "test"]
(require '[stack-server.core  :refer [start-stack-editor! transform-stack]])

(deftask editor! []
  (comp
    (start-stack-editor!)
    (target :dir #{"src/"})
    (repl)))
```

Run `boot editor!` to listen on http://localhost:7010 .

**Step 3:**

Open editor at http://repo.cirru.org/stack-editor/

Probably there will be bugs, refer to my demo for details:
https://github.com/mvc-works/stack-workflow

### Options

For remote working, there are two parameters:

http://repo.cirru.org/stack-editor/?host=tiye.me&port=7011

Default values are `{"host" "localhost", "port" "7010"}`.

### Develop

https://github.com/mvc-works/stack-workflow

Build steps:

```bash
boot build-advanced
export boot_deps=`boot show -c`
planck -c $boot_deps:src/ -i render.cljs
webpack
# boot rsync
```

### License

MIT
