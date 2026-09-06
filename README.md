
Stack Editor
----

> A ClojureScript implementation of [Clouditor](https://github.com/Cirru/clouditor/), which is a structured editor beyond the concepts of files and syntax.

* [Client App](http://repo.cirru.org/stack-editor/)
* [Editor Shortcuts](https://github.com/Cirru/stack-editor/wiki/Keyboard-Shortcuts)
* [Code Shortcuts](https://github.com/Cirru/respo-cirru-editor/wiki/Keyboard-Shortcuts)
* [Video Introduction](https://youtu.be/PdP7DHlQBoQ)

Server part:

```bash
npm install -g stack-editor
stack-editor stack-sepal.ir
```

Demo of `stack-sepal.ir`

```clojure
{:package "demo"
 :files {}}
```

![Command Line](https://pbs.twimg.com/media/C9yqvkoUQAApmpZ.png:large)
![Definitions](https://pbs.twimg.com/media/C9yqb_aUAAEVE-N.png:large)
![Workspace](https://pbs.twimg.com/media/C9yqd79UAAABjvD.png:large)

### Options

For the UI part of the editor, it's based on the address:

<http://repo.cirru.org/stack-editor/?host=localhost&port=7010>

* `port`, defaults to `7010`, server port
* `host`, defaults to `"localhost"`, server domain

For the server part, is an npm command line:

```bash
op=watch port=7010 extension=.cljs out=src/ stack-editor stack-sepal.ir
```

* `port`, defaults to `7010`, server port corresponding to the UI part
* `extension`, defaults to `".cljs"`, file extension of the generated files
* `out`, defaults `src/` output folder
* `op`, defaults to `watch`, or you may use `compile` to force compiling

### Develop

Project template https://github.com/mvc-works/stack-workflow

Read [developer.md](./docs/develop.md) for more.

### License

MIT
