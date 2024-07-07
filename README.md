
Stack Editor
----

ClojureScript editor inspired by Clouditor.

Editor http://repo.cirru.org/stack-editor/

![Workspace](https://pbs.twimg.com/media/Cp50FsiWcAEe-bH.png:large)
![Definitions](https://pbs.twimg.com/media/Cp50FuUWcAACfOi.png:large)
![Command Palette](https://pbs.twimg.com/media/Cp50MD6WcAEXq0B.png:large)

### Usage

I have to say it's quite hard to pick up. You may need to watch the video first:
https://www.youtube.com/watch?v=uCNJUxGVcqs

QuickStart template project https://github.com/mvc-works/stack-workflow

I've listed the keyboard shortcuts here, you will need it:
https://github.com/Cirru/respo-cirru-editor/wiki/Keyboard-Shortcuts

Stack Editor is built on top of [Boot](http://boot-clj.com).
You can find more about the task at: https://github.com/Cirru/boot-stack-server

### Steps to use it

If you choose to setup by yourself, here's a short guide:

**Step 1:**

Create a file named `stack-sepal.ir` beside `build.boot` file.
Add `{}` in the file as the default data.

**Step 2:**

Add a task in `build.boot`, which listens to

```clojure
(deftask run-stack-server! []
  (comp
    (watch)
    (start-stack-editor!)
    (target :dir #{"src/"})))
```

The function maybe be obtained from:

```clojure
# from package [cirru/stack-server "0.1.6" :scope "test"]
(require '[stack-server.core  :refer [start-stack-editor! transform-stack]])
```

Now you can run `boot run-stack-server!` and it will listen to http://localhost:7010 .

**Step 3:**

Open http://repo.cirru.org/stack-editor/ to find the editor.

### Develop

https://github.com/mvc-works/boot-workflow

### License

MIT
