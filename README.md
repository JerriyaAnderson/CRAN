
[Deprecated, use Calcit Editor instead!](https://github.com/Cirru/calcit-editor).
----

Stack Editor

> A structured editor of Clojure(Script).

* [Client App](http://repo.cirru.org/stack-editor/)
* [Editor Shortcuts](https://github.com/Cirru/stack-editor/wiki/Keyboard-Shortcuts)
* [Code Shortcuts](https://github.com/Cirru/respo-cirru-editor/wiki/Keyboard-Shortcuts)
* [Video Introduction](https://youtu.be/PdP7DHlQBoQ)

### Usage

[Server part](https://github.com/Cirru/stack-server):

```bash
npm install -g stack-editor
```

Create `stack.cirru`:

```clojure
{:package "demo"
 :files {}}
```

Run editor:

```bash
stack-editor stack.cirru
```

![Command Line](https://pbs.twimg.com/media/DClMKBMUIAAL5X5.png:large)
![Workspace](https://pbs.twimg.com/media/DClL_EXVwAEATYj.png:large)
![Dependency tree](https://pbs.twimg.com/media/DClL4oMUMAA1iIu.png:large)
![Finder](https://pbs.twimg.com/media/DClMRUeVoAEl8Jz.png:large)

### Options

For the UI part of the editor, it's based on the address:

<http://repo.cirru.org/stack-editor/?host=localhost&port=7010>

* `port`, defaults to `7010`, server port
* `host`, defaults to `"localhost"`, server domain

For the [server part](https://github.com/Cirru/stack-server), is an npm command line:

```bash
op=watch port=7010 extension=.cljs out=src/ stack-editor stack.cirru
```

* `port`, defaults to `7010`, server port corresponding to the UI part
* `extension`, defaults to `".cljs"`, file extension of the generated files
* `out`, defaults `src/` output folder
* `op`, defaults to `watch`, or you may use `compile` to force compiling

### Develop

Project template https://github.com/mvc-works/stack-workflow

Read [developer guide](https://github.com/Cirru/stack-editor/wiki/Develop) for more.

### License

MIT
