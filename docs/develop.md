
## Develop

### Steps to run the project

This demo itself is a based on Stack Editor, so you can clone this repo and try.

1. Get demo repository

```bash
git clone git@github.com:Cirru/stack-editor.git
cd stack-editor
```

2. Render `target/dev.html`

```bash
export deps=`boot show -c` # prepare classpath for Lump
env=dev lumo -Kc $deps:src/ -i tasks/render.cljs # render dev.html
```

3. Start editor server

```bash
npm i -g stack-editor
stack-editor
```

and a new window for another watching task:

```bash
boot dev
```

4. Open the page

Open `target/dev.html` with an HTTP server, then you will see the editor.

```bash
npm i -g http-server
http-server -c-1 target/
```

### To build:

```bash
boot build-advanced
export deps=`boot show -c`
rm .lumo_cache/stack_editor_SLASH_* # optional, remove Lumo caches
lumo -Kc $deps:src/ -i tasks/render.cljs
webpack
# bash tasks/rsync.sh
```
