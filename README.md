
Boot Workflow
----

Personal project template based on Respo, Boot, ClojureScript, Cirru Sepal...

### Develop

Genetate HTML(`target/index.html`), watch and build ClojureScript:

```bash
boot dev
```

Generate HTML, compile ClojureSript:

```bash
boot build-simple
```

Compile and optimize ClojureScript, generate HTML with revision:

```bash
boot build-advanced
```

Package jar file and install locally:

```bash
boot build
```

Package jar file and send to Clojars:

```bash
boot deploy
```

Get ClojureScript code:

```bash
boot compile-cirru
```

### License

MIT
