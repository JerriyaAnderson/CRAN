
Stack Editor
----

> A syntax tree editor for Clojure(Script).

* [Client App](http://repo.cirru.org/stack-editor/)
* [Editor Shortcuts](https://github.com/Cirru/stack-editor/wiki/Keyboard-Shortcuts)
* [Code Shortcuts](https://github.com/Cirru/respo-cirru-editor/wiki/Keyboard-Shortcuts)
* [Video Introduction](https://youtu.be/PdP7DHlQBoQ)

### Different from Calcit Editor

Current status of Stack Editor is mainly for learning and research. If you want to use in you project, [use Calcit Editor instead!](https://github.com/Cirru/calcit-editor). Calcit Editor has more details and refinements.

* Stack Editor uses `stack.cirru` as snapshot file, which is mostly vectors in Cirru EDN and human-readalbe.
* Stack Editor connects server via HTTP, which is a bit harder to be consistent.
* Stack Editor has more code loaded in browser, that makes it available for more analysis.
* Stack Editor has not been actively maintained in the years, fewer features...

### Usage

```bash
npm install -g stack-editor
```

Create `stack.cirru`(witn an entry `app.main/main!`) as snapshot file:

```cirru
{} (:package |app)
  :root $ {} (:ns |main) (:def |main!)
  :files $ {}
```

Run editor:

```bash
stack-editor stack.cirru
```

![Files](https://user-images.githubusercontent.com/449224/77821627-af2ed780-7126-11ea-8f85-a6e84603a840.png)
![Workspace](https://user-images.githubusercontent.com/449224/77821629-b2c25e80-7126-11ea-8931-69aae531fb8d.png)
![Dependency tree](https://user-images.githubusercontent.com/449224/77821631-b524b880-7126-11ea-8a00-b8f1e1576938.png)

### Options

For the UI, it's served at http://repo.cirru.org/stack-editor/?host=localhost&port=7010 with options:

* `port`, defaults to `7010`, server port
* `host`, defaults to `"localhost"`, server domain

The CLI support options:

```bash
op=watch port=7010 extension=.cljs out=src/ stack-editor stack.cirru
```

* `port`, defaults to `7010`, server port corresponding to the UI part
* `extension`, defaults to `".cljs"`, file extension of the generated files
* `out`, defaults `src/` output folder
* `op`, defaults to `watch`, or you may use `op=compile` to force compiling

### License

MIT
