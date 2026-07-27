
Stack Editor
----

ClojureScript editor inspired by [Clouditor](https://github.com/Cirru/clouditor/).

Editor http://repo.cirru.org/stack-editor/

[Keyboard-Shortcuts](https://github.com/Cirru/stack-editor/wiki/Keyboard-Shortcuts).

![Workspace](https://pbs.twimg.com/media/Cw0tNoCUAAAgJLe.png:large)
![Definitions](https://pbs.twimg.com/media/Cw0tPbHVQAIGIt0.png:large)
![Command Palette](https://pbs.twimg.com/media/Cp50MD6WcAEXq0B.png:large)

### Usage

I have to say it's quite hard to pick up. You may need to watch the video first:
https://www.youtube.com/watch?v=uCNJUxGVcqs

The template project in the video is: https://github.com/mvc-works/stack-workflow

I've listed the keyboard shortcuts here, you will need it:
https://github.com/Cirru/respo-cirru-editor/wiki/Keyboard-Shortcuts

Browse `npm-package/` folder for the command-line tool to run the server.

Stack Editor is built on top of [Boot](http://boot-clj.com).
You can find more about the task at: https://github.com/Cirru/boot-stack-server

### Run the demo

This demo itself is a based on Stack Editor, so you can clone this repo and try.

```bash
git clone git@github.com:Cirru/stack-editor.git
cd stack-editor
export deps=`boot show -c` # prepare classpath for Lump
env=dev lumo -Kc $deps:src/ -i tasks/render.cljs # render dev.html

npm i -g stack-editor
op=compile stack-editor # option, for compiling code only
stack-editor # starting file server to edit
boot dev
```

Open `target/dev.html` with an HTTP server, then you will see the editor.

### Options

For remote working, there are two parameters:

http://repo.cirru.org/stack-editor/?host=tiye.me&port=7011

Default values are `{"host" "localhost", "port" "7010"}`.

### Develop

https://github.com/mvc-works/stack-workflow

Build steps:

```bash
boot build-advanced
export deps=`boot show -c`
rm .lumo_cache/stack_editor_SLASH_* # optional, remove Lumo caches
lumo -Kc $deps:src/ -i tasks/render.cljs
webpack
bash tasks/rsync.sh
```

### License

MIT
