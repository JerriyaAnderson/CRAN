
{} (:package |app)
  :root $ {} (:ns |main) (:def |main!)
  :files $ {}
    |updater.router $ {}
      :ns $ [] |ns |app.updater.router
        [] |:require ([] |[] |app.util.stack |:refer $ [] |[] |get-path) ([] |[] |clojure.string |:as |string)
      :defs $ {}
        |toggle-palette $ [] |defn |toggle-palette ([] |store |op-data |op-id)
          [] |update-in |store ([] |[] |:router |:show-palette?) (, |not)
        |route $ [] |defn |route ([] |store |op-data)
          [] |let ([] $ [] |router |op-data) ([] |assoc |store |:router |router)
        |open-file-tree $ [] |defn |open-file-tree ([] |store |op-data |op-id)
          [] |let
            [] $ [] |code-path ([] |get-path |store)
            [] |-> |store
              [] |assoc-in ([] |[] |:router |:name) (, |:file-tree)
              [] |assoc-in ([] |[] |:graph |:ns-path)
                [] |vec $ [] |string/split ([] |:ns |code-path) (, ||.)
      :procs $ []
    |comp.brief-file $ {}
      :ns $ [] |ns |app.comp.brief-file
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |list-> |<> |span |input) ([] |[] |clojure.string |:as |string) ([] |[] |respo-ui.core |:as |ui) ([] |[] |respo.comp.space |:refer $ [] |[] |=<) ([] |[] |app.style.widget |:as |widget) ([] |[] |app.util.keycode |:as |keycode)
      :defs $ {}
        |comp-brief-file $ [] |defcomp |comp-brief-file ([] |states |ns-text |file)
          [] |let
            [] ([] |cursor $ [] |:cursor |states)
              [] |state $ [] |or ([] |:data |states) (, ||)
            [] |div ([] |{} $ [] |:style |style-file)
              [] |div ([] |{} $ [] |:style |ui/row) ([] |<> |ns-text |nil) ([] |=< |16 |nil)
                [] |span $ [] |{} ([] |:inner-text ||ns) ([] |:style |style-link) ([] |:on-click $ [] |on-edit-ns |ns-text)
                [] |=< |16 |nil
                [] |span $ [] |{} ([] |:inner-text ||procs) ([] |:style |style-link) ([] |:on-click $ [] |on-edit-procs |ns-text)
                [] |=< |16 |nil
                [] |span $ [] |{} ([] |:inner-text ||Delete) ([] |:style |widget/clickable-text) ([] |:on-click $ [] |on-remove |ns-text)
              [] |div ([] |{})
                [] |input $ [] |{} ([] |:value |state) ([] |:placeholder "||new def") ([] |:style |widget/input)
                  [] |:on-input $ [] |fn ([] |e |d!) ([] |d! |cursor $ [] |:value |e)
                  [] |:on-keydown $ [] |fn ([] |e |d!)
                    [] |if ([] |= |keycode/key-enter $ [] |:key-code |e)
                      [] |if ([] |not $ [] |string/blank? |state)
                        [] |do ([] |d! |:collection/add-definition $ [] |[] |ns-text |state) ([] |d! |cursor ||)
              [] |=< |nil |8
              [] |list-> ([] |{})
                [] |->> ([] |:defs |file) ([] |sort |compare)
                  [] |map $ [] |fn ([] |entry)
                    [] |let
                      [] $ [] |def-text ([] |key |entry)
                      [] |[] |def-text $ [] |div
                        [] |{} ([] |:inner-text |def-text) ([] |:style |style-link) ([] |:on-click $ [] |on-edit-def |ns-text |def-text)
        |on-edit-procs $ [] |defn |on-edit-procs ([] |ns-text)
          [] |fn ([] |e |dispatch!)
            [] |dispatch! |:collection/edit $ [] |{} ([] |:kind |:procs) ([] |:ns |ns-text) ([] |:extra |nil) ([] |:focus $ [] |[])
        |style-link $ [] |def |style-link ([] |{} $ [] |:cursor |:pointer)
        |on-remove $ [] |defn |on-remove ([] |ns-text)
          [] |fn ([] |e |d! |m!) ([] |d! |:collection/remove-file |ns-text)
        |on-keydown $ [] |defn |on-keydown ([] |ns-text |def-text)
          [] |fn ([] |e |d! |m!) ([] |println ||event)
            [] |if ([] |= |keycode/key-enter $ [] |:key-code |e)
              [] |if ([] |not $ [] |string/blank? |def-text)
                [] |do ([] |d! |:collection/add-definition $ [] |[] |ns-text |def-text) ([] |m! ||)
        |on-edit-ns $ [] |defn |on-edit-ns ([] |ns-text)
          [] |fn ([] |e |dispatch!)
            [] |dispatch! |:collection/edit $ [] |{} ([] |:kind |:ns) ([] |:ns |ns-text) ([] |:extra |nil) ([] |:focus $ [] |[])
        |on-edit-def $ [] |defn |on-edit-def ([] |ns-text |def-text)
          [] |fn ([] |e |dispatch!)
            [] |dispatch! |:collection/edit $ [] |{} ([] |:kind |:defs) ([] |:ns |ns-text) ([] |:extra |def-text) ([] |:focus $ [] |[] |2)
        |style-file $ [] |def |style-file
          [] |{} ([] |:padding ||16px) ([] |:font-size |16) ([] |:line-height |1.6)
      :procs $ []
    |comp.loading $ {}
      :ns $ [] |ns |app.comp.loading
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |<> |span |input) ([] |[] |respo-ui.core |:as |ui)
      :defs $ {}
        |comp-loading $ [] |defcomp |comp-loading ([])
          [] |div
            [] |{} $ [] |:style ([] |merge |ui/fullscreen |ui/row-center |style-loading)
            [] |<> |span ||Loading... |nil
        |style-loading $ [] |def |style-loading
          [] |{} ([] |:background-color $ [] |hsl |200 |40 |10) ([] |:justify-content ||center) ([] |:color $ [] |hsl |0 |0 |80) ([] |:font-size ||32px) ([] |:font-weight ||100) ([] |:font-family "||Josefin Sans")
      :procs $ []
    |comp.command $ {}
      :ns $ [] |ns |app.comp.command
        [] |:require ([] |[] |clojure.string |:as |string) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |<> |span |input) ([] |[] |hsl.core |:refer $ [] |[] |hsl)
      :defs $ {}
        |style-command $ [] |def |style-command
          [] |{} ([] |:backgroud-color $ [] |hsl |0 |0 |0) ([] |:padding "||0 8px") ([] |:line-height |2.4) ([] |:font-family "||Source Code Pro, Menlo,monospace") ([] |:cursor ||pointer)
        |on-click $ [] |defn |on-click ([] |on-select)
          [] |fn ([] |e |dispatch!) ([] |on-select |dispatch!)
        |comp-command $ [] |defcomp |comp-command ([] |command |selected? |on-select)
          [] |div
            [] |{}
              [] |:style $ [] |merge |style-command
                [] |if |selected? $ [] |{} ([] |:background-color $ [] |hsl |0 |0 |30)
              [] |:on-click $ [] |on-click |on-select
            [] |<> |span ([] |string/join "|| " |command) (, |nil)
      :procs $ []
    |updater $ {}
      :ns $ [] |ns |app.updater
        [] |:require ([] |[] |respo.cursor |:refer $ [] |[] |update-states) ([] |[] |app.updater.router |:as |router) ([] |[] |app.updater.collection |:as |collection) ([] |[] |app.updater.notification |:as |notification) ([] |[] |app.updater.stack |:as |stack) ([] |[] |app.updater.modal |:as |modal) ([] |[] |app.updater.graph |:as |graph)
      :defs $ {}
        |updater $ [] |defn |updater ([] |store |op |op-data |op-id)
          [] |let
            [] $ [] |handler
              [] |case |op
                [] |:states $ [] |fn ([] |x) ([] |update-states |x |op-data)
                [] |:router/route |router/route
                [] |:router/toggle-palette |router/toggle-palette
                [] |:router/open-file-tree |router/open-file-tree
                [] |:collection/add-definition |collection/add-definition
                [] |:collection/add-namespace |collection/add-namespace
                [] |:collection/edit |collection/edit
                [] |:collection/edit-ns |collection/edit-ns
                [] |:collection/write |collection/write-code
                [] |:collection/load |collection/load-remote
                [] |:collection/remove-this |collection/remove-this
                [] |:collection/remove-file |collection/remove-file
                [] |:collection/rename |collection/rename
                [] |:collection/hydrate |collection/hydrate
                [] |:notification/add-one |notification/add-one
                [] |:notification/remove-one |notification/remove-one
                [] |:notification/remove-since |notification/remove-since
                [] |:stack/goto-definition |stack/goto-definition
                [] |:stack/dependents |stack/dependents
                [] |:stack/go-back |stack/go-back
                [] |:stack/go-next |stack/go-next
                [] |:stack/point-to |stack/point-to
                [] |:stack/collapse |stack/collapse
                [] |:stack/shift |stack/shift-one
                [] |:modal/mould |modal/mould
                [] |:modal/recycle |modal/recycle
                [] |:graph/load-graph |graph/load-graph
                [] |:graph/view-path |graph/view-path
                [] |:graph/view-ns |graph/view-ns
                [] |:graph/edit-current |graph/edit-current
                [] |:graph/show-orphans |graph/show-orphans
                , |default-handler
            [] |handler |store |op-data |op-id
        |default-handler $ [] |defn |default-handler ([] |store |op-data) (, |store)
      :procs $ []
    |util.keycode $ {} (:ns $ [] |ns |app.util.keycode)
      :defs $ {} (|key-esc $ [] |def |key-esc |27) (|key-u $ [] |def |key-u |85) (|key-down $ [] |def |key-down |40) (|key-s $ [] |def |key-s |83) (|key-d $ [] |def |key-d |68) (|key-j $ [] |def |key-j |74) (|key-a $ [] |def |key-a |65) (|key-k $ [] |def |key-k |75) (|key-p $ [] |def |key-p |80) (|key-enter $ [] |def |key-enter |13) (|key-i $ [] |def |key-i |73) (|key-b $ [] |def |key-b |66) (|key-up $ [] |def |key-up |38) (|key-e $ [] |def |key-e |69)
      :procs $ []
    |updater.stack $ {}
      :ns $ [] |ns |app.updater.stack
        [] |:require ([] |[] |clojure.string |:as |string) ([] |[] |app.util.analyze |:refer $ [] |[] |list-dependent-ns |parse-ns-deps |extract-deps) ([] |[] |app.util.detect |:refer $ [] |[] |strip-atom |contains-def? |=path?) ([] |[] |app.util |:refer $ [] |[] |remove-idx |helper-notify |helper-create-def |make-path |has-ns?) ([] |app.util.stack |:refer $ [] |push-path |push-paths) ([] |clojure.set |:refer $ [] |union)
      :defs $ {}
        |collapse $ [] |defn |collapse ([] |store |op-data |op-id)
          [] |let ([] $ [] |cursor |op-data)
            [] |update |store |:writer $ [] |fn ([] |writer)
              [] |-> |writer ([] |assoc |:pointer |0)
                [] |update |:stack $ [] |fn ([] |stack) ([] |subvec |stack |cursor)
        |go-next $ [] |defn |go-next ([] |store |op-data)
          [] |-> |store $ [] |update |:writer
            [] |fn ([] |writer)
              [] |if
                [] |< ([] |:pointer |writer)
                  [] |dec $ [] |count ([] |:stack |writer)
                [] |-> |writer ([] |update |:pointer |inc) ([] |assoc |:focus $ [] |[])
                , |writer
        |dependents $ [] |defn |dependents ([] |store |op-data |op-id)
          [] |let
            [] ([] |writer $ [] |:writer |store)
              []
                [] |{} ([] |stack |:stack) ([] |pointer |:pointer)
                , |writer
              [] |code-path $ [] |get |stack |pointer
              []
                [] |{} ([] |ns-part |:ns) ([] |kind |:kind) ([] |extra-name |:extra)
                , |code-path
              [] |pkg $ [] |get-in |store ([] |[] |:collection |:package)
              [] |def-as-dep $ [] |{} ([] |:ns $ [] |str |pkg ||. |ns-part) ([] |:def |extra-name) ([] |:external? |false)
              [] |files $ [] |get-in |store ([] |[] |:collection |:files)
              [] |ns-list $ [] |list-dependent-ns |ns-part |files |pkg
            [] |case |kind
              [] |:defs $ [] |let
                [] $ [] |new-paths
                  [] |->> ([] |conj |ns-list |ns-part)
                    [] |map $ [] |fn ([] |ns-text)
                      [] |let
                        [] $ [] |file ([] |get |files |ns-text)
                        [] |into ([] |#{})
                          [] |concat
                            [] |->> ([] |:defs |file)
                              [] |filter $ [] |fn ([] |entry)
                                [] |let
                                  [] $ [] |def-deps
                                    [] |extract-deps
                                      [] |subvec ([] |val |entry) (, |2)
                                      , |ns-part |file |pkg
                                  [] |contains? |def-deps |def-as-dep
                              [] |map $ [] |fn ([] |entry)
                                [] |{} ([] |:kind |:defs) ([] |:ns |ns-text) ([] |:extra $ [] |first |entry) ([] |:focus $ [] |[] |2)
                            [] |let
                              [] $ [] |proc-deps
                                [] |extract-deps ([] |:procs |file) (, |ns-part |file |pkg)
                              [] |if ([] |contains? |proc-deps |def-as-dep)
                                [] |list $ [] |{} ([] |:kind |:procs) ([] |:ns |ns-text) ([] |:extra |nil) ([] |:focus $ [] |[] |0)
                                [] |list
                    [] |apply |concat
                    [] |filter $ [] |fn ([] |x) ([] |not $ [] |=path? |x |code-path)
                [] |if ([] |empty? |new-paths) ([] |update |store |:notifications $ [] |helper-notify |op-id "||Nothing found.") ([] |update |store |:writer $ [] |push-paths |new-paths)
              [] |:ns $ [] |let
                [] $ [] |new-paths
                  [] |map
                    [] |fn ([] |x) ([] |[] |x |:ns)
                    , |ns-list
                [] |; |println |pointer |new-paths
                [] |update |store |:writer $ [] |push-paths |new-paths
              , |store
        |point-to $ [] |defn |point-to ([] |store |op-data |op-id)
          [] |let ([] $ [] |pointer |op-data)
            [] |assoc-in |store ([] |[] |:writer |:pointer) (, |pointer)
        |go-back $ [] |defn |go-back ([] |store |op-data)
          [] |-> |store $ [] |update |:writer
            [] |fn ([] |writer)
              [] |if ([] |pos? $ [] |:pointer |writer)
                [] |-> |writer ([] |update |:pointer |dec) ([] |assoc |:focus $ [] |[])
                , |writer
        |shift-one $ [] |defn |shift-one ([] |store |op-data |op-id)
          [] |let ([] $ [] |pointer |op-data)
            [] |update |store |:writer $ [] |fn ([] |writer)
              [] |-> |writer
                [] |update |:stack $ [] |fn ([] |stack) ([] |remove-idx |stack |pointer)
                [] |update |:pointer $ [] |fn ([] |p)
                  [] |if ([] |= |p |pointer)
                    [] |if ([] |pos? |p) ([] |dec |p) (, |p)
                    [] |if ([] |< |p |pointer) (, |p) ([] |dec |p)
        |goto-definition $ [] |defn |goto-definition ([] |store |op-data |op-id)
          [] |let
            [] ([] |forced? |op-data)
              []
                [] |{} ([] |pkg |:package) ([] |files |:files)
                [] |get-in |store $ [] |[] |:collection
              [] |pkg_ $ [] |str |pkg ||.
              []
                [] |{} ([] |stack |:stack) ([] |pointer |:pointer)
                [] |:writer |store
              [] |code-path $ [] |get |stack |pointer
              [] |focus $ [] |:focus |code-path
              [] |target $ [] |strip-atom
                [] |get-in |store $ [] |concat ([] |make-path |code-path) (, |focus)
              [] |ns-deps $ [] |parse-ns-deps
                [] |get-in |files $ [] |[] ([] |:ns |code-path) (, |:ns)
              [] |current-ns-defs $ [] |get-in |files
                [] |[] ([] |:ns |code-path) (, |:defs)
              [] |dep-info $ [] |if ([] |has-ns? |target)
                [] |let
                  []
                    [] ([] |[] |ns-text |def-text) ([] |string/split |target ||/)
                    [] |maybe-info $ [] |get |ns-deps |ns-text
                  [] |if
                    [] |and ([] |some? |maybe-info) ([] |= |:as $ [] |:kind |maybe-info)
                    [] |{} ([] |:ns $ [] |:ns |maybe-info) ([] |:def |def-text)
                    , |nil
                [] |let
                  [] $ [] |maybe-info ([] |get |ns-deps |target)
                  [] |if
                    [] |and ([] |some? |maybe-info) ([] |= |:refer $ [] |:kind |maybe-info)
                    [] |{} ([] |:ns $ [] |:ns |maybe-info) ([] |:def |target)
                    [] |if
                      [] |or ([] |contains? |current-ns-defs |target) (, |forced?)
                      [] |{}
                        [] |:ns $ [] |str |pkg_ ([] |:ns |code-path)
                        [] |:def |target
                      , |nil
            [] |; |println |target |dep-info
            [] |if ([] |some? |dep-info)
              [] |if
                [] |string/starts-with? ([] |:ns |dep-info) (, |pkg_)
                [] |let
                  []
                    [] |existed? $ [] |some?
                      [] |get-in |files $ [] |[] ([] |:ns |dep-info) (, |:defs) ([] |:def |dep-info)
                    [] |shorten-ns $ [] |string/replace-first ([] |:ns |dep-info) (, |pkg_ ||)
                    [] |touch-def $ [] |fn ([] |store) ([] |println ||touching |existed?)
                      [] |if |existed? |store $ [] |-> |store
                        [] |update-in ([] |[] |:collection |:files)
                          [] |helper-create-def |shorten-ns ([] |:def |dep-info) (, |code-path) ([] |:focus |code-path)
                  [] |-> |store ([] |touch-def)
                    [] |update |:writer $ [] |push-path
                      [] |{} ([] |:kind |:defs) ([] |:ns |shorten-ns) ([] |:extra $ [] |:def |dep-info) ([] |:focus $ [] |[] |2)
                [] |-> |store $ [] |update |:notifications
                  [] |helper-notify |op-id $ [] |str "||External package: " ([] |:ns |dep-info)
              [] |-> |store $ [] |update |:notifications ([] |helper-notify |op-id $ [] |str "||Can't find: " |target)
      :procs $ []
    |util.analyze $ {}
      :ns $ [] |ns |app.util.analyze
        [] |:require ([] |[] |clojure.string |:as |string) ([] |[] |app.util.detect |:refer $ [] |[] |contains-def? |use-vector?)
      :defs $ {}
        |expand-deps-tree $ [] |defn |expand-deps-tree ([] |internal-ns |def-text |files |pkg |parents)
          [] |let
            [] ([] |this-file $ [] |get |files |internal-ns)
              [] |def-expr $ [] |get-in |this-file ([] |[] |:defs |def-text)
              [] |stamp $ [] |{} ([] |:ns |internal-ns) ([] |:def |def-text)
              [] |base-dep $ [] |{} ([] |:ns |internal-ns) ([] |:def |def-text) ([] |:external? |false) ([] |:circular? |false)
            [] |if ([] |nil? |def-expr) ([] |assoc |base-dep |:external? |true)
              [] |if ([] |contains? |parents |stamp) ([] |assoc |base-dep |:circular? |true)
                [] |assoc |base-dep |:deps $ [] |let
                  [] $ [] |def-deps
                    [] |if ([] |some? |def-expr)
                      [] |extract-deps ([] |subvec |def-expr |2) (, |internal-ns |this-file |pkg)
                      , |nil
                  [] |->> |def-deps
                    [] |map $ [] |fn ([] |dep-info)
                      [] |if ([] |:external? |dep-info) (, |dep-info)
                        [] |let
                          []
                            [] |child-internal-ns $ [] |string/replace-first ([] |:ns |dep-info) ([] |str |pkg ||.) (, ||)
                            [] |child-def $ [] |:def |dep-info
                            [] |next-parents $ [] |conj |parents |stamp
                          [] |expand-deps-tree |child-internal-ns |child-def |files |pkg |next-parents
                    [] |into $ [] |#{}
        |parse-rule $ [] |defn |parse-rule ([] |dict |rule)
          [] |let
            []
              [] |clean-rule $ [] |if ([] |= ||[] $ [] |first |rule) ([] |subvec |rule |1) (, |rule)
              [] |ns-text $ [] |first |clean-rule
              [] |binding-rule $ [] |subvec |clean-rule |1
            [] |loop
              [] ([] |left-binding |binding-rule) ([] |result |dict)
              [] |; |println "||doing loop:" |left-binding |result
              [] |if
                [] |< ([] |count |left-binding) (, |2)
                , |result
                [] |let
                  [] ([] |kind $ [] |first |left-binding) ([] |data $ [] |get |left-binding |1)
                  [] |recur ([] |subvec |left-binding |2)
                    [] |cond
                      [] ([] |= ||:as |kind)
                        [] |assoc |result |data $ [] |{} ([] |:kind |:as) ([] |:ns |ns-text) ([] |:text |data)
                      [] ([] |= ||:refer |kind)
                        [] |->> |data
                          [] |filter $ [] |fn ([] |x) ([] |not= |x ||[])
                          [] |map $ [] |fn ([] |x)
                            [] |[] |x $ [] |{} ([] |:kind |:refer) ([] |:ns |ns-text) ([] |:text |x)
                          [] |into $ [] |{}
                          [] |merge |result
                      [] |:else |result
        |list-dependent-ns $ [] |defn |list-dependent-ns ([] |ns-name |files |pkg)
          [] |let
            [] ([] |full-ns $ [] |str |pkg ||. |ns-name)
              [] |pick-ns $ [] |fn ([] |xs)
                [] |if ([] |use-vector? |xs) ([] |get |xs |1) ([] |first |xs)
            [] |->> |files
              [] |filter $ [] |fn ([] |entry)
                [] |let
                  []
                    [] ([] |[] |ns-part |file) (, |entry)
                    [] |ns-expr $ [] |:ns |file
                    [] |ns-rules $ [] |->> ([] |subvec |ns-expr |2) ([] |map |rest) ([] |apply |concat) ([] |map |pick-ns) ([] |into $ [] |#{})
                  [] |; |println ||Search: |ns-name |ns-rules
                  [] |contains? |ns-rules |full-ns
              [] |map |first
              [] |into $ [] |#{}
        |pick-dep $ [] |defn |pick-dep ([] |token)
          [] |cond
            [] ([] |string/blank? |token) (, |nil)
            [] ([] |string/starts-with? |token ||:) (, |nil)
            [] ([] |string/starts-with? |token ||.) (, |nil)
            [] ([] |string/starts-with? |token |||) (, |nil)
            [] ([] |string/starts-with? |token ||#) (, |nil)
            [] ([] |string/starts-with? |token ||[) (, |nil)
            [] ([] |string/starts-with? |token ||') (, |nil)
            [] ([] |string/starts-with? |token ||{) (, |nil)
            [] ([] |string/starts-with? |token ||%) (, |nil)
            [] ([] |string/starts-with? |token ||\) (, |nil)
            [] ([] |= |token ||--) (, |nil)
            [] ([] |string/includes? |token ||/)
              [] |let
                [] $ [] ([] |[] |ns-piece |def-piece) ([] |string/split |token ||/)
                [] |{} ([] |:kind |:ns) ([] |:data |ns-piece) ([] |:extra |def-piece)
            [] ([] |string/includes? |token ||.)
              [] |let
                [] $ [] ([] |[] |def-piece |prop-piece) ([] |string/split |token ||.)
                [] |{} ([] |:kind |:def) ([] |:data |def-piece)
            [] ([] |string/starts-with? |token ||@)
              [] |{} ([] |:kind |:def) ([] |:data $ [] |subs |token |1)
            [] |:else $ [] |{} ([] |:kind |:def) ([] |:data |token)
        |parse-ns-deps $ [] |defn |parse-ns-deps ([] |expression)
          [] |let
            [] $ [] |branches
              [] |->> ([] |subvec |expression |2)
                [] |filter $ [] |fn ([] |expr) ([] |= ||:require $ [] |first |expr)
            [] |if ([] |empty? |branches) ([] |{})
              [] |doall $ [] |reduce |parse-rule ([] |{}) ([] |rest $ [] |first |branches)
        |extract-deps $ [] |defn |extract-deps ([] |expression |internal-ns |file |pkg)
          [] |let
            []
              [] |external? $ [] |fn ([] |ns-text)
                [] |not $ [] |string/starts-with? |ns-text ([] |str |pkg ||.)
              [] |ns-deps $ [] |parse-ns-deps ([] |:ns |file)
            [] |->> |expression ([] |flatten) ([] |map |pick-dep) ([] |filter |some?)
              [] |map $ [] |fn ([] |info)
                [] |case ([] |:kind |info)
                  [] |:def $ [] |let
                    [] ([] |def-text $ [] |:data |info) ([] |defs $ [] |:defs |file)
                    [] |cond
                      [] ([] |contains? |ns-deps |def-text)
                        [] |let
                          [] $ [] |using-mapping ([] |get |ns-deps |def-text)
                          [] |if ([] |= |:refer $ [] |:kind |using-mapping)
                            [] |let
                              [] $ [] |ns-text ([] |:ns |using-mapping)
                              [] |{} ([] |:ns |ns-text) ([] |:def |def-text) ([] |:external? $ [] |external? |ns-text)
                            , |nil
                      [] ([] |contains? |defs |def-text)
                        [] |{} ([] |:ns $ [] |str |pkg ||. |internal-ns) ([] |:def |def-text) ([] |:external? |false)
                      [] |:else |nil
                  [] |:ns $ [] |let
                    [] $ []
                      [] |{} ([] |ns-text |:data) ([] |def-text |:extra)
                      , |info
                    [] |if ([] |contains? |ns-deps |ns-text)
                      [] |let
                        [] $ [] |using-mapping ([] |get |ns-deps |ns-text)
                        [] |if ([] |= |:as $ [] |:kind |using-mapping)
                          [] |let
                            [] $ [] |ns-text ([] |:ns |using-mapping)
                            [] |{} ([] |:ns |ns-text) ([] |:def |def-text) ([] |:external? $ [] |external? |ns-text)
                          , |nil
                      , |nil
                  , |nil
              [] |filter |some?
              [] |into $ [] |#{}
      :procs $ []
    |util $ {}
      :ns $ [] |ns |app.util
        [] |:require ([] |[] |app.util.detect |:refer $ [] |[] |contains-def? |=path?) ([] |clojure.set |:refer $ [] |union) ([] |clojure.string |:as |string)
      :defs $ {}
        |remove-idx $ [] |defn |remove-idx ([] |xs |idx)
          [] |let
            [] $ [] |xs-size ([] |count |xs)
            [] |cond
              []
                [] |or ([] |>= |idx |xs-size) ([] |neg? |idx)
                , |xs
              [] ([] |= |xs-size |1) ([] |[])
              [] ([] |zero? |idx) ([] |subvec |xs |1)
              [] ([] |= |idx $ [] |dec |xs-size) ([] |subvec |xs |0 |idx)
              [] |:else $ [] |into ([] |[])
                [] |concat ([] |subvec |xs |0 |idx) ([] |subvec |xs $ [] |inc |idx)
        |make-focus-path $ [] |defn |make-focus-path ([] |store)
          [] |let
            [] ([] |writer $ [] |:writer |store) ([] |pointer $ [] |:pointer |writer) ([] |stack $ [] |:stack |writer) ([] |code-path $ [] |get |stack |pointer)
            [] |concat ([] |make-path |code-path) ([] |:focus |code-path)
        |has-ns? $ [] |defn |has-ns? ([] |x) ([] |string/includes? |x ||/)
        |helper-notify $ [] |defn |helper-notify ([] |op-id |data)
          [] |fn ([] |notifications)
            [] |into ([] |[])
              [] |cons ([] |[] |op-id |data) (, |notifications)
        |make-path $ [] |defn |make-path ([] |info)
          [] |let
            [] $ [] |kind ([] |:kind |info)
            [] |if ([] |= |kind |:defs)
              [] |[] |:collection |:files ([] |:ns |info) (, |:defs) ([] |:extra |info)
              [] |[] |:collection |:files ([] |:ns |info) (, |kind)
        |view-focused $ [] |defn |view-focused ([] |store) ([] |get-in |store $ [] |make-focus-path |store)
        |make-short-path $ [] |defn |make-short-path ([] |info)
          [] |let
            [] $ [] |kind ([] |:kind |info)
            [] |if ([] |= |kind |:defs)
              [] |[] ([] |:ns |info) (, |:defs) ([] |:extra |info)
              [] |[] ([] |:ns |info) (, |kind)
        |helper-create-def $ [] |defn |helper-create-def ([] |ns-part |name-part |code-path |focus)
          [] |fn ([] |files)
            [] |if ([] |contains-def? |files |ns-part |name-part) (, |files)
              [] |assoc-in |files ([] |[] |ns-part |:defs |name-part)
                [] |let
                  [] $ [] |as-fn?
                    [] |and ([] |not $ [] |empty? |focus) ([] |zero? $ [] |last |focus)
                  [] |if |as-fn?
                    [] |let
                      [] $ [] |expression
                        [] |get-in |files $ [] |concat ([] |make-short-path |code-path) ([] |butlast |focus)
                      [] |if
                        [] |> ([] |count |expression) (, |1)
                        [] |[] ||defn |name-part $ [] |subvec |expression |1
                        [] |[] ||defn |name-part $ [] |[]
                    [] |[] ||def |name-part $ [] |[]
        |now! $ [] |defn |now! ([]) ([] |.now |js/performance)
        |collect-defs $ [] |defn |collect-defs ([] |node)
          [] |let
            [] $ [] |base-result
              [] |#{} $ [] |select-keys |node ([] |[] |:ns |:def)
            [] |if ([] |contains? |node |:deps)
              [] |union
                [] |apply |union $ [] |map |collect-defs ([] |:deps |node)
                , |base-result
              , |base-result
      :procs $ []
    |comp.workspace $ {}
      :ns $ [] |ns |app.comp.workspace
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |<> |>> |span |input) ([] |[] |respo.comp.space |:refer $ [] |[] |=<) ([] |[] |respo.comp.inspect |:refer $ [] |[] |comp-inspect) ([] |[] |respo-ui.core |:as |ui) ([] |[] |app.comp.hot-corner |:refer $ [] |[] |comp-hot-corner) ([] |[] |app.comp.stack |:refer $ [] |[] |comp-stack) ([] |[] |cirru-editor.comp.editor |:refer $ [] |[] |comp-editor) ([] |[] |app.util.keycode |:as |keycode) ([] |[] |app.util.dom |:as |dom) ([] |[] |app.util |:refer $ [] |[] |make-path) ([] |[] |app.style.widget |:as |widget)
      :defs $ {}
        |on-command $ [] |defn |on-command ([] |store)
          [] |fn ([] |snapshot |dispatch! |e)
            [] |let
              [] ([] |code $ [] |:key-code |e) ([] |event $ [] |:original-event |e)
                [] |command? $ [] |or ([] |.-metaKey |event) ([] |.-ctrlKey |event)
                [] |shift? $ [] |.-shiftKey |event
              [] |cond
                [] ([] |= |code |keycode/key-d)
                  [] |do ([] |.preventDefault |event) ([] |dispatch! |:stack/goto-definition |shift?)
                [] ([] |= |code |keycode/key-u)
                  [] |do ([] |.preventDefault |event) ([] |dispatch! |:stack/dependents |nil)
                [] ([] |= |code |keycode/key-i)
                  [] |do ([] |.preventDefault |event) ([] |dispatch! |:stack/go-back |nil)
                [] ([] |= |code |keycode/key-k)
                  [] |do ([] |.preventDefault |event) ([] |dispatch! |:stack/shift $ [] |-> |store |:writer |:pointer)
                [] ([] |= |code |keycode/key-j)
                  [] |do ([] |.preventDefault |event) ([] |dispatch! |:stack/go-next |nil)
                [] ([] |= |code |keycode/key-s)
                  [] |do ([] |.preventDefault |event) ([] |dispatch! |:effect/submit |shift?)
                [] ([] |and |command? $ [] |= |code |keycode/key-p)
                  [] |do ([] |.preventDefault |event) ([] |.stopPropagation |event) ([] |dispatch! |:router/toggle-palette |nil) ([] |dom/focus-palette!)
                []
                  [] |and |command? ([] |not |shift?) ([] |= |code |keycode/key-e)
                  [] |do ([] |.preventDefault |event) ([] |dispatch! |:collection/edit-ns |nil)
                [] |:else |nil
        |comp-workspace $ [] |defcomp |comp-workspace ([] |store)
          [] |let
            [] ([] |router $ [] |:router |store) ([] |states $ [] |:states |store) ([] |writer $ [] |:writer |store)
              [] |stack $ [] |get-in |store ([] |[] |:writer |:stack)
              [] |pointer $ [] |get-in |store ([] |[] |:writer |:pointer)
              [] |code-path $ [] |get |stack |pointer
              [] |tree $ [] |if ([] |some? |code-path) ([] |get-in |store $ [] |make-path |code-path) (, |nil)
            [] |div
              [] |{} $ [] |:style ([] |merge |ui/fullscreen |ui/row |style-container)
              [] |div
                [] |{} $ [] |:style ([] |merge |ui/column |style-sidebar)
                [] |comp-hot-corner |router $ [] |:writer |store
                [] |comp-stack |stack |pointer
              [] |; |comp-inspect |writer |style-debugger
              [] |if ([] |some? |tree)
                [] |div
                  [] |{} $ [] |:style ([] |merge |ui/column |ui/flex)
                  [] |comp-editor ([] |>> |states |:editor)
                    [] |{} ([] |:tree |tree) ([] |:focus $ [] |:focus |code-path) ([] |:clipboard $ [] |:clipboard |writer)
                    , |on-update
                    [] |on-command |store
                  [] |div
                    [] |{} $ [] |:style ([] |merge |ui/row |style-toolbar)
                    [] |div $ [] |{} ([] |:inner-text ||Rename) ([] |:class-name ||is-unremarkable) ([] |:style |widget/clickable-text) ([] |:on-click $ [] |on-rename |code-path)
                    [] |=< |8 |nil
                    [] |div $ [] |{} ([] |:inner-text ||Delete) ([] |:class-name ||is-unremarkable) ([] |:style |widget/clickable-text) ([] |:on-click |on-remove)
                [] |div
                  [] |{} $ [] |:style ([] |merge |ui/column |ui/flex)
                  [] |div ([] |{} $ [] |:style |style-removed) ([] |<> |span "||No expression" |nil)
        |on-update $ [] |defn |on-update ([] |snapshot |dispatch!) ([] |dispatch! |:collection/write |snapshot)
        |style-toolbar $ [] |def |style-toolbar
          [] |{} ([] |:background-color $ [] |hsl |0 |0 |0) ([] |:justify-content ||flex-start)
        |style-container $ [] |def |style-container
          [] |{} $ [] |:background-color ([] |hsl |0 |0 |0)
        |style-debugger $ [] |def |style-debugger
          [] |{} ([] |:z-index |999) ([] |:background-color $ [] |hsl |0 |0 |0) ([] |:opacity |1)
        |style-sidebar $ [] |def |style-sidebar
          [] |{} ([] |:width ||180px) ([] |:background-color $ [] |hsl |0 |0 |0) ([] |:color $ [] |hsl |0 |0 |80)
        |on-rename $ [] |defn |on-rename ([] |code-path)
          [] |fn ([] |e |dispatch!) ([] |println "||the code path:" |code-path)
            [] |dispatch! |:modal/mould $ [] |{} ([] |:title |:rename-path) ([] |:data |code-path)
            [] |dom/focus-rename!
        |on-remove $ [] |defn |on-remove ([] |e |dispatch!) ([] |dispatch! |:collection/remove-this |nil)
        |style-removed $ [] |def |style-removed
          [] |{} ([] |:margin "||32px 16px") ([] |:font-size ||20px) ([] |:font-weight ||lighter) ([] |:color $ [] |hsl |0 |80 |50) ([] |:font-family "||Josefin Sans") ([] |:padding "||0 16px") ([] |:display ||inline-block) ([] |:max-width ||400px)
      :procs $ []
    |comp.graph $ {}
      :ns $ [] |ns |app.comp.graph
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |list-> |<> |span |input |button) ([] |[] |respo-ui.core |:as |ui) ([] |[] |app.comp.def |:refer $ [] |[] |comp-def) ([] |[] |app.util.detect |:refer $ [] |[] |def-order |=def?) ([] |[] |respo.comp.space |:refer $ [] |[] |=<) ([] |[] |app.style.widget |:as |widget) ([] |[] |clojure.set |:as |set)
      :defs $ {}
        |style-body $ [] |def |style-body
          [] |{} ([] |:flex |1) ([] |:overflow |:auto)
        |style-graph $ [] |def |style-graph
          [] |{} ([] |:background-color $ [] |hsl |0 |0 |0) ([] |:overflow |:auto)
        |on-load $ [] |defn |on-load ([] |e |dispatch!) ([] |dispatch! |:graph/load-graph |nil)
        |style-toolbar $ [] |def |style-toolbar ([] |{} $ [] |:padding |16)
        |on-files $ [] |defn |on-files ([] |e |dispatch!)
          [] |dispatch! |:router/route $ [] |{} ([] |:name |:file-tree) ([] |:data |nil)
        |style-column $ [] |def |style-column
          [] |{} ([] |:min-width |80) ([] |:overflow |:auto) ([] |:padding "||16px 16px") ([] |:flex-shrink |0)
        |comp-graph $ [] |defcomp |comp-graph ([] |store)
          [] |div
            [] |{} $ [] |:style ([] |merge |ui/fullscreen |ui/column |style-graph)
            [] |render-toolbar
            [] |let
              []
                [] |tree $ [] |get-in |store ([] |[] |:graph |:tree)
                [] |root-tree $ [] |assoc ([] |get-in |store $ [] |[] |:collection |:root) (, |:deps) ([] |#{} |tree)
                [] |view-path $ [] |get-in |store ([] |[] |:graph |:path)
              [] |println ||tree |tree
              [] |if ([] |some? |tree)
                [] |list->
                  [] |{} $ [] |:style ([] |merge |ui/row |style-body)
                  [] |loop
                    [] ([] |branch |root-tree) ([] |children $ [] |[]) ([] |path $ [] |[])
                    [] |let
                      []
                        [] |next-path $ [] |conj |path ([] |get |view-path $ [] |count |path)
                        [] |next-pos $ [] |get |view-path ([] |count |path)
                        [] |next-children $ [] |conj |children
                          [] |[] ([] |count |children)
                            [] |list-> ([] |{} $ [] |:style |style-column)
                              [] |->> ([] |:deps |branch) ([] |sort |def-order)
                                [] |map-indexed $ [] |fn ([] |idx |child-node)
                                  [] |[] |idx $ [] |comp-def |child-node |path ([] |=def? |next-pos |child-node)
                      [] |if ([] |= |path |view-path) (, |next-children)
                        [] |let
                          [] $ [] |next-branch
                            [] |->> ([] |:deps |branch)
                              [] |set/select $ [] |fn ([] |x)
                                [] |=def? ([] |get |view-path $ [] |count |path) (, |x)
                              [] |first
                          [] |recur |next-branch |next-children |next-path
                [] |<> |div "||Not generated yet." $ [] |{} ([] |:padding "||0 16px")
        |render-toolbar $ [] |defn |render-toolbar ([])
          [] |div ([] |{} $ [] |:style |style-toolbar)
            [] |div ([] |{})
              [] |button $ [] |{} ([] |:inner-text ||Files) ([] |:style |widget/button) ([] |:on-click |on-files)
              [] |=< |8 |nil
              [] |button $ [] |{} ([] |:inner-text ||Edit) ([] |:style |widget/button) ([] |:on-click |on-edit)
              [] |=< |64 |nil
              [] |button $ [] |{} ([] |:inner-text "||Build tree") ([] |:style |widget/button) ([] |:on-click |on-load)
              [] |=< |8 |nil
              [] |button $ [] |{} ([] |:inner-text "||Find orphans") ([] |:style |widget/button) ([] |:on-click |on-orphans)
        |on-orphans $ [] |defn |on-orphans ([] |e |dispatch!) ([] |dispatch! |:graph/show-orphans |nil)
        |on-edit $ [] |defn |on-edit ([] |e |dispatch!) ([] |dispatch! |:graph/edit-current |nil)
      :procs $ []
    |updater.graph $ {}
      :ns $ [] |ns |app.updater.graph
        [] |:require ([] |app.util.analyze |:refer $ [] |parse-ns-deps |pick-dep |expand-deps-tree) ([] |app.util.stack |:refer $ [] |push-path) ([] |clojure.set |:refer $ [] |union |difference) ([] |app.util |:refer $ [] |collect-defs) ([] |clojure.string |:as |string)
      :defs $ {}
        |load-graph $ [] |defn |load-graph ([] |store |op-data)
          [] |let
            []
              [] |root-info $ [] |get-in |store ([] |[] |:collection |:root)
              [] |files $ [] |get-in |store ([] |[] |:collection |:files)
              [] |internal-ns $ [] |:ns |root-info
              [] |ns-deps $ [] |parse-ns-deps ([] |get-in |files $ [] |[] |internal-ns |:ns)
              [] |def-expr $ [] |get-in |files
                [] |[] ([] |:ns |root-info) (, |:defs) ([] |:def |root-info)
              [] |pkg $ [] |get-in |store ([] |[] |:collection |:package)
              [] |this-file $ [] |get |files |internal-ns
              [] |deps-tree $ [] |expand-deps-tree |internal-ns ([] |:def |root-info) (, |files |pkg) ([] |#{})
            [] |; |println |ns-deps
            [] |println
            [] |-> |store $ [] |assoc-in ([] |[] |:graph |:tree) (, |deps-tree)
        |view-path $ [] |defn |view-path ([] |store |op-data)
          [] |assoc-in |store ([] |[] |:graph |:path) (, |op-data)
        |view-ns $ [] |defn |view-ns ([] |store |op-data)
          [] |assoc-in |store ([] |[] |:graph |:ns-path) (, |op-data)
        |edit-current $ [] |defn |edit-current ([] |store |op)
          [] |let
            [] $ [] |maybe-path
              [] |last $ [] |get-in |store ([] |[] |:graph |:path)
            [] |if ([] |some? |maybe-path)
              [] |-> |store
                [] |update |:writer $ [] |push-path
                  [] |{} ([] |:ns $ [] |:ns |maybe-path) ([] |:kind |:defs) ([] |:extra $ [] |:def |maybe-path) ([] |:focus $ [] |[])
                [] |assoc |:router $ [] |{} ([] |:name |:workspace) ([] |:data |nil)
              , |store
        |show-orphans $ [] |defn |show-orphans ([] |store |op-data)
          [] |let
            []
              [] |all-defs $ [] |->> ([] |get-in |store $ [] |[] |:collection |:files)
                [] |map $ [] |fn ([] |file-entry)
                  [] |let
                    [] ([] |ns-text $ [] |first |file-entry)
                      [] |defs $ [] |keys ([] |:defs $ [] |val |file-entry)
                    [] |->> |defs
                      [] |map $ [] |fn ([] |def-text)
                        [] |{} ([] |:ns |ns-text) ([] |:def |def-text)
                      [] |into $ [] |#{}
                [] |apply |union
              [] |deps-tree $ [] |get-in |store ([] |[] |:graph |:tree)
              [] |defs-in-tree $ [] |collect-defs |deps-tree
            [] |update |store |:modal-stack $ [] |fn ([] |xs)
              [] |conj |xs $ [] |{} ([] |:title |:orphans) ([] |:data $ [] |difference |all-defs |defs-in-tree)
      :procs $ []
    |schema $ {} (:ns $ [] |ns |app.schema)
      :defs $ {}
        |store $ [] |def |store
          [] |{}
            [] |:router $ [] |{} ([] |:name |:loading) ([] |:data |:definitions) ([] |:show-palette? |false)
            [] |:collection $ [] |{} ([] |:package |nil) ([] |:root |nil) ([] |:files $ [] |{})
            [] |:graph $ [] |{} ([] |:tree |nil) ([] |:orphans |nil) ([] |:path $ [] |[]) ([] |:ns-path $ [] |[])
            [] |:writer $ [] |{} ([] |:stack $ [] |[]) ([] |:pointer |0) ([] |:clipboard $ [] |[])
            [] |:notifications $ [] |[]
            [] |:modal-stack $ [] |[]
            [] |:states $ [] |{}
      :procs $ []
    |comp.container $ {}
      :ns $ [] |ns |app.comp.container
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.comp.inspect |:refer $ [] |[] |comp-inspect) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |>> |<> |span |input) ([] |[] |respo-ui.core |:as |ui) ([] |[] |app.comp.loading |:refer $ [] |[] |comp-loading) ([] |[] |app.comp.workspace |:refer $ [] |[] |comp-workspace) ([] |[] |app.comp.notifications |:refer $ [] |[] |comp-notifications) ([] |[] |app.comp.palette |:refer $ [] |[] |comp-palette) ([] |[] |app.comp.modal-stack |:refer $ [] |[] |comp-modal-stack) ([] |[] |app.comp.graph |:refer $ [] |[] |comp-graph) ([] |[] |app.util.keycode |:as |keycode) ([] |[] |app.util.dom |:as |dom) ([] |[] |app.style.widget |:as |widget) ([] |[] |app.comp.file-tree |:refer $ [] |[] |comp-file-tree)
      :defs $ {}
        |comp-container $ [] |defcomp |comp-container ([] |store)
          [] |let
            [] ([] |router $ [] |:router |store) ([] |states $ [] |:states |store)
            [] |div
              [] |{} ([] |:tab-index |0)
                [] |:style $ [] |merge |ui/global
                  [] |{} ([] |:background-color $ [] |hsl |0 |0 |0) ([] |:color $ [] |hsl |0 |0 |70)
              [] |case ([] |:name |router) ([] |:loading $ [] |comp-loading) ([] |:workspace $ [] |comp-workspace |store) ([] |:graph $ [] |comp-graph |store)
                [] |:file-tree $ [] |comp-file-tree ([] |>> |states |:file-tree) (, |store)
                [] |<> ([] |str |router) (, |nil)
              [] |comp-notifications $ [] |:notifications |store
              [] |; |comp-inspect ||Store |store $ [] |{} ([] |:bottom |0) ([] |:background-color $ [] |hsl |0 |0 |0) ([] |:opacity |1) ([] |:color |:white)
              [] |if ([] |:show-palette? |router)
                [] |comp-palette ([] |>> |states |:palette) ([] |:files $ [] |:collection |store)
              [] |comp-modal-stack |states $ [] |:modal-stack |store
      :procs $ []
    |comp.def $ {}
      :ns $ [] |ns |app.comp.def
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |<> |span |input) ([] |[] |respo-ui.core |:as |ui) ([] |[] |respo.comp.space |:refer $ [] |[] |=<)
      :defs $ {}
        |comp-def $ [] |defcomp |comp-def ([] |child-node |path |selected?)
          [] |div
            [] |{}
              [] |:style $ [] |merge |style-def
                [] |if ([] |:external? |child-node) (, |style-external)
                [] |if |selected? |style-highlight
                [] |if ([] |:circular? |child-node) (, |style-circular)
              [] |:on-click $ [] |on-view |path |child-node
            [] |<> |span
              [] |str ([] |:ns |child-node) (, "|| / ") ([] |:def |child-node)
              , |nil
            [] |=< |4 |nil
            [] |let
              [] $ [] |many-deps ([] |count $ [] |:deps |child-node)
              [] |if ([] |pos? |many-deps) ([] |<> |span |many-deps |style-count)
        |on-view $ [] |defn |on-view ([] |path |child-node)
          [] |fn ([] |e |dispatch!)
            [] |if ([] |.-metaKey $ [] |:original-event |e)
              [] |dispatch! |:collection/edit $ [] |{} ([] |:kind |:defs) ([] |:ns $ [] |:ns |child-node) ([] |:extra $ [] |:def |child-node) ([] |:focus $ [] |[])
              [] |dispatch! |:graph/view-path $ [] |conj |path
                [] |{} ([] |:ns $ [] |:ns |child-node) ([] |:def $ [] |:def |child-node)
        |style-circular $ [] |def |style-circular ([] |{} $ [] |:text-decoration |:underline)
        |style-def $ [] |def |style-def
          [] |{} ([] |:color $ [] |hsl |0 |0 |70 |0.7) ([] |:font-size |14) ([] |:cursor |:pointer) ([] |:white-space |:nowrap)
        |style-external $ [] |def |style-external
          [] |{} ([] |:color $ [] |hsl |260 |16 |44) ([] |:font-size |12) ([] |:cursor |:default)
        |style-highlight $ [] |def |style-highlight
          [] |{} $ [] |:color ([] |hsl |0 |0 |100 |0.86)
        |style-count $ [] |def |style-count
          [] |{} ([] |:font-size |12) ([] |:color $ [] |hsl |0 |0 |100 |0.4)
      :procs $ []
    |comp.modal-stack $ {}
      :ns $ [] |ns |app.comp.modal-stack
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |list-> |div |<> |>> |span |input) ([] |[] |respo-ui.core |:as |ui) ([] |[] |app.style.widget |:as |widget) ([] |[] |app.comp.rename-path |:refer $ [] |[] |comp-rename-path) ([] |[] |app.comp.hydrate |:refer $ [] |[] |comp-hydrate) ([] |[] |app.comp.orphans |:refer $ [] |[] |comp-orphans)
      :defs $ {}
        |on-tip $ [] |defn |on-tip ([] |e |dispatch!)
        |style-modal $ [] |def |style-modal
          [] |merge |ui/center $ [] |{} ([] |:background-color $ [] |hsl |0 |0 |0 |0.6) ([] |:z-index |900) ([] |:position |:fixed) ([] |:top |0) ([] |:right |0) ([] |:width ||100%) ([] |:height ||100%)
        |on-recycle $ [] |defn |on-recycle ([] |e |dispatch!) ([] |dispatch! |:modal/recycle |nil)
        |renderer $ [] |defn |renderer ([] |states |kind |title |data)
          [] |div ([] |{})
            [] |case |title
              [] |:rename-path $ [] |comp-rename-path ([] |>> |states |:rename-path) (, |data)
              [] |:hydrate $ [] |comp-hydrate ([] |>> |states |:hydrate)
              [] |:orphans $ [] |comp-orphans |data
              [] |<> |span |title |nil
        |comp-modal-stack $ [] |defcomp |comp-modal-stack ([] |states |modal-stack)
          [] |list-> ([] |{})
            [] |->> |modal-stack $ [] |map-indexed
              [] |fn ([] |idx |modal)
                [] |let
                  [] ([] |kind $ [] |:kind |modal) ([] |title $ [] |:title |modal) ([] |data $ [] |:data |modal)
                  [] |[] |idx $ [] |div
                    [] |{} ([] |:style |style-modal) ([] |:on-click |on-recycle)
                    [] |div ([] |{} $ [] |:on-click |on-tip) ([] |renderer |states |kind |title |data)
      :procs $ []
    |comp.rename-path $ {}
      :ns $ [] |ns |app.comp.rename-path
        [] |:require ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |<> |span |input) ([] |[] |respo.comp.space |:refer $ [] |[] |=<) ([] |[] |respo-ui.core |:as |ui) ([] |[] |app.style.widget |:as |widget) ([] |[] |app.util.keycode |:as |keycode)
      :defs $ {}
        |init-state $ [] |defn |init-state ([] |code-path)
          [] |let
            [] $ []
              [] |{} ([] |ns-part |:ns) ([] |kind |:kind) ([] |extra-name |:extra)
              , |code-path
            [] |if ([] |= |kind |:defs) ([] |str |ns-part ||/ |extra-name) (, |ns-part)
        |comp-rename-path $ [] |defcomp |comp-rename-path ([] |states |code-path)
          [] |let
            [] $ [] |state
              [] |or ([] |:data |states) ([] |init-state |code-path)
            [] |div ([] |{})
              [] |div ([] |{})
                [] |<> |span
                  [] |str "||Rename: " ([] |:ns |code-path) (, ||/) ([] |:extra |code-path)
                  , |nil
              [] |div ([] |{})
                [] |input $ [] |{} ([] |:value |state) ([] |:id ||rename-box)
                  [] |:style $ [] |merge |ui/input ([] |{} $ [] |:width |400)
                  [] |:on-input |on-input
                  [] |:on-keydown $ [] |on-keydown |code-path |state
                [] |=< |16 |nil
                [] |div $ [] |{} ([] |:inner-text ||Rename) ([] |:style |widget/button) ([] |:on-click $ [] |on-rename |code-path |state)
        |on-input $ [] |defn |on-input ([] |e |dispatch! |m!) ([] |m! $ [] |:value |e)
        |on-rename $ [] |defn |on-rename ([] |code-path |text)
          [] |fn ([] |e |d! |m!) ([] |d! |:collection/rename $ [] |[] |code-path |text) ([] |d! |:modal/recycle |nil) ([] |m! |nil)
        |on-keydown $ [] |defn |on-keydown ([] |code-path |text)
          [] |fn ([] |e |d! |m!) ([] |println |keycode/key-esc)
            [] |cond
              []
                [] |= ([] |:key-code |e) (, |keycode/key-enter)
                [] |do ([] |d! |:collection/rename $ [] |[] |code-path |text) ([] |d! |:modal/recycle |nil) ([] |m! |nil)
              []
                [] |= ([] |:key-code |e) (, |keycode/key-esc)
                [] |d! |:modal/recycle |nil
              [] |:else |nil
      :procs $ []
    |util.dom $ {} (:ns $ [] |ns |app.util.dom)
      :defs $ {}
        |focus-palette! $ [] |defn |focus-palette! ([])
          [] |js/requestAnimationFrame $ [] |fn ([])
            [] |let
              [] $ [] |target ([] |.querySelector |js/document ||#command-palette)
              [] |if ([] |some? |target) ([] |.focus |target)
        |focus-rename! $ [] |defn |focus-rename! ([])
          [] |-> ([] |.querySelector |js/document ||#rename-box) ([] |.focus)
      :procs $ []
    |main $ {}
      :ns $ [] |ns |app.main
        [] |:require ([] |[] |respo.core |:refer $ [] |[] |render! |clear-cache! |render-element |realize-ssr!) ([] |[] |app.schema |:as |schema) ([] |[] |app.comp.container |:refer $ [] |[] |comp-container) ([] |[] |cljs.reader |:refer $ [] |[] |read-string) ([] |[] |app.updater |:refer $ [] |[] |updater) ([] |[] |app.util.keycode |:as |keycode) ([] |[] |app.util.dom |:as |dom) ([] |[] |app.util |:refer $ [] |[] |now!) ([] |[] |app.actions |:refer $ [] |[] |load-collection! |submit-collection! |submit-changes! |display-code!) ([] |[] |cirru-editor.util.dom |:refer $ [] |[] |focus!)
      :defs $ {}
        |ssr? $ [] |def |ssr? ([] |some? $ [] |.querySelector |js/document ||meta.server-rendered)
        |*focus-moved? $ [] |def |*focus-moved? ([] |atom |false)
        |dispatch! $ [] |defn |dispatch! ([] |op |op-data) ([] |println ||Dispatch! |op)
          [] |case |op
            [] |:effect/submit $ [] |let
              [] ([] |shift? |op-data) ([] |sepal-data $ [] |:collection |@*store)
              [] |if |shift? ([] |submit-collection! |sepal-data |dispatch!) ([] |submit-changes! |sepal-data |dispatch!)
            [] |:effect/dehydrate $ [] |display-code! |@*store
            [] |:effect/load $ [] |load-collection! |dispatch! |false
            [] |let
              [] $ [] |new-store ([] |updater |@*store |op |op-data $ [] |now!)
              [] |reset! |*focus-moved? $ [] |not
                [] |and
                  [] |identical? ([] |:collection |@*store) ([] |:collection |new-store)
                  [] |identical? ([] |:writer |@*store) ([] |:writer |new-store)
              [] |reset! |*store |new-store
        |*store $ [] |defonce |*store ([] |atom |schema/store)
        |main! $ [] |defn |main! ([]) ([] |if |ssr? $ [] |render-app! |realize-ssr!) ([] |render-app! |render!)
          [] |add-watch |*store |:changes $ [] |fn ([]) ([] |render-app! |render!)
          [] |.addEventListener |js/window ||keydown $ [] |fn ([] |event)
            [] |let
              [] ([] |code $ [] |.-keyCode |event)
                [] |command? $ [] |or ([] |.-metaKey |event) ([] |.-ctrlKey |event)
                [] |shift? $ [] |.-shiftKey |event
              [] |cond
                [] ([] |and |command? $ [] |= |code |keycode/key-p)
                  [] |do ([] |.preventDefault |event) ([] |.stopPropagation |event) ([] |dispatch! |:router/toggle-palette |nil) ([] |dom/focus-palette!)
                [] ([] |and |shift? |command? $ [] |= |code |keycode/key-a)
                  [] |do $ [] |let
                    [] ([] |router $ [] |:router |@*store) ([] |writer $ [] |:writer |@*store)
                    [] |if
                      [] |= ([] |:name |router) (, |:workspace)
                      [] |dispatch! |:router/route $ [] |{} ([] |:name |:graph) ([] |:data |nil)
                      [] |if
                        [] |not $ [] |empty? ([] |:stack |writer)
                        [] |dispatch! |:router/route $ [] |{} ([] |:name |:workspace) ([] |:data |nil)
                [] |:else |nil
          [] |println "||app started!"
          [] |load-collection! |dispatch! |true
        |render-app! $ [] |defn |render-app! ([] |renderer)
          [] |renderer |mount-target ([] |comp-container |@*store) (, |dispatch!)
          [] |if |@*focus-moved? $ [] |do ([] |reset! |*focus-moved? |false) ([] |focus!)
        |reload! $ [] |defn |reload! ([]) ([] |clear-cache!) ([] |render-app! |render!) ([] |println "||Code updated.")
        |mount-target $ [] |def |mount-target ([] |.querySelector |js/document ||.app)
      :procs $ []
    |updater.notification $ {} (:ns $ [] |ns |app.updater.notification)
      :defs $ {}
        |add-one $ [] |defn |add-one ([] |store |op-data |op-id)
          [] |let ([] $ [] |notification |op-data)
            [] |-> |store $ [] |update |:notifications
              [] |fn ([] |notifications)
                [] |into ([] |[])
                  [] |cons ([] |[] |op-id |notification) ([] |take |3 |notifications)
        |remove-since $ [] |defn |remove-since ([] |store |op-data)
          [] |let ([] $ [] |pos |op-data)
            [] |-> |store $ [] |update |:notifications
              [] |fn ([] |notifications) ([] |take |pos |notifications)
        |remove-one $ [] |defn |remove-one ([] |store |op-data)
          [] |let ([] $ [] |notification-id |op-data)
            [] |-> |store $ [] |update |:notifications
              [] |fn ([] |notifications)
                [] |filterv
                  [] |fn ([] |notification) ([] |not= |notification-id $ [] |first |notification)
                  , |notifications
      :procs $ []
    |comp.orphans $ {}
      :ns $ [] |ns |app.comp.orphans
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |list-> |<> |span |input) ([] |[] |respo-ui.core |:as |ui)
      :defs $ {}
        |comp-orphans $ [] |defcomp |comp-orphans ([] |orphans)
          [] |div ([] |{} $ [] |:style |style-container)
            [] |div ([] |{}) ([] |<> |span ||Orphans: |style-title)
            [] |list-> ([] |{})
              [] |->> |orphans $ [] |map
                [] |fn ([] |def-info)
                  [] |let
                    [] $ [] |def-id
                      [] |str ([] |:ns |def-info) (, ||/) ([] |:def |def-info)
                    [] |[] |def-id $ [] |div
                      [] |{} ([] |:inner-text |def-id) ([] |:style |style-def) ([] |:on-click $ [] |on-edit |def-info)
        |style-container $ [] |def |style-container
          [] |{} ([] |:width |800) ([] |:height |400) ([] |:overflow |:auto) ([] |:padding |16) ([] |:background $ [] |hsl |0 |0 |0 |0.9)
        |style-def $ [] |def |style-def
          [] |{} ([] |:min-width |200) ([] |:display |:inline-block) ([] |:cursor |:pointer)
        |style-title $ [] |def |style-title
          [] |{} ([] |:font-size |24) ([] |:font-weight |100) ([] |:font-family "||Josefin Sans")
        |on-edit $ [] |defn |on-edit ([] |def-info)
          [] |fn ([] |e |dispatch!)
            [] |dispatch! |:collection/edit $ [] |{} ([] |:kind |:defs) ([] |:ns $ [] |:ns |def-info) ([] |:extra $ [] |:def |def-info) ([] |:focus $ [] |[] |1)
            [] |dispatch! |:modal/recycle |nil
      :procs $ []
    |comp.palette $ {}
      :ns $ [] |ns |app.comp.palette
        [] |:require ([] |[] |clojure.string |:as |string) ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |list-> |div |<> |span |input) ([] |[] |respo-ui.core |:as |ui) ([] |[] |cirru-editor.util.dom |:refer $ [] |[] |focus!) ([] |[] |app.comp.command |:refer $ [] |[] |comp-command) ([] |[] |app.util.keycode |:as |keycode) ([] |[] |app.style.widget |:as |widget) ([] |[] |app.util.detect |:refer $ [] |[] |fuzzy-search)
      :defs $ {}
        |on-input $ [] |defn |on-input ([] |cursor |state)
          [] |fn ([] |e |dispatch!)
            [] |dispatch! |:states $ [] |[] |cursor
              [] |merge |state $ [] |{} ([] |:text $ [] |:value |e) ([] |:cursor |0)
        |handle-command $ [] |defn |handle-command ([] |cursor |commands |files |dispatch!)
          [] |let
            [] $ [] |command
              [] |get
                [] |into ([] |[]) (, |commands)
                , |cursor
            [] |println ||Command $ [] |pr-str |command
            [] |dispatch! |:router/toggle-palette |nil
            [] |case ([] |first |command) ([] |:load $ [] |dispatch! |:effect/load |nil) ([] |:patch $ [] |dispatch! |:effect/submit |true) ([] |:dehydrate $ [] |dispatch! |:effect/dehydrate |nil)
              [] |:hydrate $ [] |dispatch! |:modal/mould
                [] |{} ([] |:title |:hydrate) ([] |:data |nil)
              [] |:graph $ [] |dispatch! |:router/route
                [] |{} ([] |:name |:graph) ([] |:data |nil)
              [] |:defs $ [] |do
                [] |dispatch! |:collection/edit $ [] |{} ([] |:ns $ [] |get |command |1) ([] |:kind |:defs) ([] |:extra $ [] |last |command) ([] |:focus $ [] |[] |0)
              [] |:ns $ [] |do
                [] |dispatch! |:collection/edit $ [] |{} ([] |:ns $ [] |get |command |1) ([] |:kind |:ns) ([] |:extra |nil) ([] |:focus $ [] |[] |0)
              [] |:procs $ [] |do
                [] |dispatch! |:collection/edit $ [] |{} ([] |:ns $ [] |get |command |1) ([] |:kind |:procs) ([] |:extra |nil) ([] |:focus $ [] |[] |0)
              , |nil
        |comp-palette $ [] |defcomp |comp-palette ([] |states |files)
          [] |let
            []
              [] |ns-names $ [] |->> ([] |keys |files)
                [] |map $ [] |fn ([] |path) ([] |[] |:ns |path)
              [] |cursor $ [] |:cursor |states
              [] |state $ [] |or ([] |:data |states) (, |initial-state)
              [] |def-paths $ [] |->> |files
                [] |map $ [] |fn ([] |entry)
                  [] |let
                    [] $ [] ([] |[] |ns-part |tree) (, |entry)
                    [] |->> ([] |:defs |tree) ([] |keys)
                      [] |map $ [] |fn ([] |def-name) ([] |[] |:defs |ns-part |def-name)
                [] |apply |concat
              [] |procedure-names $ [] |->> ([] |keys |files)
                [] |map $ [] |fn ([] |proc-name) ([] |[] |:procs |proc-name)
              [] |queries $ [] |string/split ([] |:text |state) (, "|| ")
              [] |commands $ [] |->> ([] |concat |def-paths |ns-names |procedure-names |basic-commands)
                [] |filter $ [] |fn ([] |command) ([] |fuzzy-search |command |queries)
            [] |div
              [] |{} $ [] |:style ([] |merge |ui/fullscreen |ui/row |style-container)
              [] |div
                [] |{} $ [] |:style
                  [] |merge |ui/column $ [] |{} ([] |:background-color $ [] |hsl |0 |0 |0 |0.8) ([] |:width ||800px)
                [] |input $ [] |{} ([] |:placeholder "||write command...") ([] |:id ||command-palette) ([] |:value $ [] |:text |state)
                  [] |:style $ [] |merge |widget/input
                    [] |{} ([] |:width ||100%) ([] |:line-height ||40px)
                  [] |:autocomplete "|\"off"
                  [] |:on-input $ [] |on-input |cursor |state
                  [] |:on-keydown $ [] |on-keydown |cursor |state |commands ([] |:cursor |state) (, |files)
                [] |list->
                  [] |{} $ [] |:style
                    [] |merge |ui/flex $ [] |{} ([] |:overflow ||auto)
                  [] |->> |commands $ [] |map-indexed
                    [] |fn ([] |idx |command)
                      [] |[] |idx $ [] |comp-command |command ([] |= |idx $ [] |:cursor |state) ([] |on-select |idx |commands |files)
        |style-container $ [] |def |style-container
          [] |{} ([] |:position ||fixed) ([] |:background-color $ [] |hsl |200 |40 |10 |0.8) ([] |:justify-content ||center)
        |on-select $ [] |defn |on-select ([] |cursor |commands |files)
          [] |fn ([] |dispatch!) ([] |handle-command |cursor |commands |files |dispatch!)
        |initial-state $ [] |def |initial-state
          [] |{} ([] |:text ||) ([] |:cursor |0)
        |basic-commands $ [] |def |basic-commands
          [] |[] ([] |[] |:save) ([] |[] |:load) ([] |[] |:hydrate) ([] |[] |:dehydrate) ([] |[] |:graph)
        |on-keydown $ [] |defn |on-keydown ([] |respo-cursor |state |commands |cursor |collection)
          [] |fn ([] |e |dispatch!)
            [] |let
              [] ([] |code $ [] |:key-code |e) ([] |total $ [] |count |commands)
              [] |cond
                [] ([] |= |code |keycode/key-esc)
                  [] |do
                    [] |dispatch! |:states $ [] |[] |respo-cursor
                      [] |merge |state $ [] |{} ([] |:text ||)
                    [] |dispatch! |:router/toggle-palette |nil
                    [] |focus!
                [] ([] |= |code |keycode/key-down)
                  [] |if ([] |< |cursor $ [] |dec |total)
                    [] |dispatch! |:states $ [] |[] |respo-cursor
                      [] |merge |state $ [] |{} ([] |:cursor $ [] |inc |cursor)
                [] ([] |= |code |keycode/key-up)
                  [] |if ([] |> |cursor |0)
                    [] |dispatch! |:states $ [] |[] |respo-cursor
                      [] |merge |state $ [] |{} ([] |:cursor $ [] |dec |cursor)
                [] ([] |= |code |keycode/key-enter)
                  [] |do
                    [] |dispatch! |:states $ [] |[] |respo-cursor
                      [] |merge |state $ [] |{} ([] |:text ||)
                    [] |handle-command |cursor |commands |collection |dispatch!
                [] |:else |nil
      :procs $ []
    |comp.stack $ {}
      :ns $ [] |ns |app.comp.stack
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |list-> |<> |span |input) ([] |[] |clojure.string |:as |string) ([] |[] |respo-ui.core |:as |ui)
      :defs $ {}
        |style-ns $ [] |def |style-ns
          [] |{} ([] |:font-size ||11px) ([] |:line-height |1.4) ([] |:color $ [] |hsl |0 |0 |50) ([] |:font-family ||Hind)
        |style-bright $ [] |def |style-bright
          [] |{} $ [] |:color ([] |hsl |0 |0 |90)
        |style-ns-main $ [] |def |style-ns-main
          [] |{} ([] |:padding "||0 8px") ([] |:line-height ||36px) ([] |:cursor ||pointer) ([] |:color $ [] |hsl |0 |0 |60) ([] |:font-family ||Hind) ([] |:font-size ||13px) ([] |:white-space ||nowrap)
        |on-click $ [] |defn |on-click ([] |pointer)
          [] |fn ([] |e |dispatch!)
            [] |let
              [] ([] |event $ [] |:original-event |e)
                [] |command? $ [] |or ([] |.-ctrlKey |event) ([] |.-metaKey |event)
                [] |shift? $ [] |.-shiftKey |event
              [] |cond ([] |command? $ [] |dispatch! |:stack/collapse |pointer)
                [] |shift? $ [] |do ([] |.preventDefault |event) ([] |dispatch! |:stack/shift |pointer)
                [] |:else $ [] |do ([] |dispatch! |:stack/point-to |pointer)
        |style-container $ [] |def |style-container
          [] |{} ([] |:overflow ||auto) ([] |:padding "||16px 0 160px 0") ([] |:user-select |:nonworkspacee)
        |style-bar $ [] |def |style-bar
          [] |{} ([] |:padding "||4px 8px") ([] |:cursor ||pointer) ([] |:color $ [] |hsl |0 |0 |60) ([] |:font-family "||Source Code Pro,Menlo,monospace") ([] |:font-size |13) ([] |:line-height |1.4) ([] |:white-space ||nowrap)
        |comp-stack $ [] |defcomp |comp-stack ([] |stack |pointer)
          [] |list->
            [] |{} $ [] |:style ([] |merge |ui/flex |style-container)
            [] |->> |stack $ [] |map-indexed
              [] |fn ([] |idx |item)
                [] |[] |idx $ [] |let
                  [] $ []
                    [] |{} ([] |ns-part |:ns) ([] |kind |:kind) ([] |extra-name |:extra)
                    , |item
                  [] |if ([] |= |kind |:defs)
                    [] |div
                      [] |{} ([] |:style |style-bar) ([] |:on-click $ [] |on-click |idx)
                      [] |div
                        [] |{} $ [] |:style
                          [] |if ([] |= |idx |pointer) (, |style-bright)
                        [] |<> |span |extra-name |nil
                      [] |div ([] |{} $ [] |:style |style-ns) ([] |<> |span |ns-part |nil)
                    [] |div
                      [] |{}
                        [] |:style $ [] |merge |style-ns-main
                          [] |if ([] |= |idx |pointer) (, |style-bright)
                        [] |:on-click $ [] |on-click |idx
                      [] |<> |span |ns-part |nil
      :procs $ []
    |comp.notifications $ {}
      :ns $ [] |ns |app.comp.notifications
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo-ui.core |:as |ui) ([] |[] |respo.core |:refer $ [] |[] |defcomp |list-> |div |<> |span |input)
      :defs $ {}
        |comp-notifications $ [] |defcomp |comp-notifications ([] |notifications)
          [] |list-> ([] |{})
            [] |->> |notifications $ [] |map-indexed
              [] |fn ([] |idx |entry)
                [] |[] ([] |first |entry)
                  [] |div
                    [] |{}
                      [] |:style $ [] |merge |style-notification
                        [] |{} $ [] |:top
                          [] |str ([] |+ |8 $ [] |* |40 |idx) (, ||px)
                      [] |:on-click $ [] |on-click |idx
                    [] |<> |span ([] |last |entry) (, |nil)
        |on-click $ [] |defn |on-click ([] |idx)
          [] |fn ([] |e |dispatch!) ([] |dispatch! |:notification/remove-since |idx)
        |style-notification $ [] |def |style-notification
          [] |{} ([] |:position ||fixed) ([] |:top ||8px) ([] |:right ||8px) ([] |:transition ||320ms) ([] |:line-height ||32px) ([] |:white-space ||nowrap) ([] |:color $ [] |hsl |0 |0 |100 |0.5) ([] |:background-color $ [] |hsl |300 |30 |70 |0.2) ([] |:z-index |999) ([] |:min-width ||160px) ([] |:padding "||0 16px") ([] |:cursor ||pointer) ([] |:border-radius ||2px)
      :procs $ []
    |util.querystring $ {}
      :ns $ [] |ns |app.util.querystring ([] |:require $ [] |[] |clojure.string |:as |string)
      :defs $ {}
        |parse-query $ [] |defn |parse-query ([] |search)
          [] |if ([] |= |search ||) ([] |{})
            [] |let
              [] ([] |content $ [] |subs |search |1)
                [] |pairs $ [] |map
                  [] |fn ([] |piece) ([] |string/split |piece ||=)
                  [] |string/split |content "||&"
              [] |into ([] |{}) (, |pairs)
      :procs $ []
    |util.detect $ {}
      :ns $ [] |ns |app.util.detect ([] |:require $ [] |[] |clojure.string |:as |string)
      :defs $ {}
        |fuzzy-search $ [] |defn |fuzzy-search ([] |pieces |queries)
          [] |every?
            [] |fn ([] |query)
              [] |some
                [] |fn ([] |piece)
                  [] |string/includes? ([] |str |piece) (, |query)
                , |pieces
            , |queries
        |contains-def? $ [] |defn |contains-def? ([] |files |ns-part |name-part) ([] |println "||Contains def:" |ns-part |name-part)
          [] |if ([] |contains? |files |ns-part)
            [] |let
              [] $ [] |dict ([] |get-in |files $ [] |[] |ns-part |:defs)
              [] |contains? |dict |name-part
            , |false
        |cirru-vec? $ [] |defn |cirru-vec? ([] |x)
          [] |if ([] |vector? |x)
            [] |every?
              [] |fn ([] |y)
                [] |or ([] |string? |y) ([] |cirru-vec? |y)
              , |x
            , |false
        |def-order $ [] |defn |def-order ([] |x |y)
          [] |cond
            []
              [] |and ([] |:circular? |x) ([] |not $ [] |:circular? |y)
              , |-1
            []
              [] |and ([] |:circular? |y) ([] |not $ [] |:circular? |x)
              , |1
            []
              [] |and ([] |:external? |x) ([] |not $ [] |:external? |y)
              , |1
            []
              [] |and ([] |:external? |y) ([] |not $ [] |:external? |x)
              , |-1
            [] |:else $ [] |compare
              [] |str ([] |:ns |x) (, ||/) ([] |:def |x)
              [] |str ([] |:ns |y) (, ||/) ([] |:def |y)
        |use-vector? $ [] |defn |use-vector? ([] |xs) ([] |= ||[] $ [] |first |xs)
        |=def? $ [] |defn |=def? ([] |x |y)
          [] |and
            [] |= ([] |:ns |x) ([] |:ns |y)
            [] |= ([] |:def |x) ([] |:def |y)
        |strip-atom $ [] |defn |strip-atom ([] |token)
          [] |-> |token
            [] |string/replace ([] |re-pattern ||^@) (, ||)
            [] |string/replace ([] |re-pattern ||\.$) (, ||)
            [] |string/replace ([] |re-pattern ||/@) (, ||/)
        |=path? $ [] |defn |=path? ([] |x |y)
          [] |and
            [] |= ([] |:ns |x) ([] |:ns |y)
            [] |= ([] |:kind |x) ([] |:kind |y)
            [] |= ([] |:extra |x) ([] |:extra |y)
      :procs $ []
    |updater.modal $ {} (:ns $ [] |ns |app.updater.modal)
      :defs $ {}
        |mould $ [] |defn |mould ([] |store |op-data |op-id)
          [] |let ([] $ [] |modal |op-data)
            [] |update |store |:modal-stack $ [] |fn ([] |stack) ([] |conj |stack |modal)
        |recycle $ [] |defn |recycle ([] |store |op-data |op-id)
          [] |update |store |:modal-stack $ [] |fn ([] |stack)
            [] |into ([] |[]) ([] |butlast |stack)
      :procs $ []
    |comp.hydrate $ {}
      :ns $ [] |ns |app.comp.hydrate
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |<> |>> |span |input |button |textarea) ([] |[] |respo.comp.space |:refer $ [] |[] |=<) ([] |[] |respo-ui.core |:as |ui) ([] |[] |app.style.widget |:as |widget) ([] |[] |cljs.reader |:refer $ [] |[] |read-string) ([] |[] |app.util.detect |:refer $ [] |[] |cirru-vec?)
      :defs $ {}
        |style-textarea $ [] |def |style-textarea
          [] |{} ([] |:background-color $ [] |hsl |0 |0 |100 |0.2) ([] |:font-family "||Source Code Pro, Menlo") ([] |:color |:white) ([] |:resize |:none) ([] |:width |640) ([] |:height |200) ([] |:line-height ||24px)
        |style-hint $ [] |def |style-hint ([] |{} $ [] |:font-family ||Hind)
        |style-toolbar $ [] |def |style-toolbar ([] |{} $ [] |:justify-content |:flex-end)
        |on-change $ [] |defn |on-change ([] |cursor)
          [] |fn ([] |e |dispatch!)
            [] |dispatch! |:states $ [] |[] |cursor ([] |:value |e)
        |comp-hydrate $ [] |defcomp |comp-hydrate ([] |states)
          [] |let
            [] ([] |cursor $ [] |:cursor |states) ([] |state $ [] |:data |states)
            [] |div ([] |{})
              [] |div ([] |{} $ [] |:style |style-hint) ([] |<> |span "||EDN Cirru code to hydrate:" |nil)
              [] |div ([] |{})
                [] |textarea $ [] |{} ([] |:value |state) ([] |:style $ [] |merge |ui/textarea |style-textarea) ([] |:on-input $ [] |on-change |cursor)
              [] |=< |nil |8
              [] |div
                [] |{} $ [] |:style ([] |merge |ui/row |style-toolbar)
                [] |button
                  [] |{} ([] |:style |widget/button) ([] |:on-click $ [] |on-hydrate |state)
                  [] |<> |span ||Hydrate |nil
        |on-hydrate $ [] |defn |on-hydrate ([] |text)
          [] |fn ([] |e |dispatch!)
            [] |let
              [] $ [] |piece ([] |read-string |text)
              [] |if ([] |cirru-vec? |piece)
                [] |do ([] |dispatch! |:collection/hydrate |piece) ([] |dispatch! |:modal/recycle |nil)
                [] |dispatch! |:notification/add-one $ [] |str "||Checking failed: " ([] |pr-str |text)
      :procs $ []
    |comp.file-tree $ {}
      :ns $ [] |ns |app.comp.file-tree
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |div |list-> |>> |<> |span |input |button) ([] |[] |respo-ui.core |:as |ui) ([] |[] |clojure.string |:as |string) ([] |[] |respo.comp.space |:refer $ [] |[] |=<) ([] |[] |app.comp.brief-file |:refer $ [] |[] |comp-brief-file) ([] |[] |app.style.widget |:as |widget) ([] |[] |app.util.keycode |:as |keycode)
      :defs $ {}
        |comp-file-tree $ [] |defcomp |comp-file-tree ([] |states |store)
          [] |let
            [] ([] |cursor $ [] |:cursor |states)
              [] |state $ [] |or ([] |:data |states)
                [] |{} ([] |:draft "|\"") ([] |:selected-ns |nil)
              [] |files $ [] |get-in |store ([] |[] |:collection |:files)
              [] |selected-ns $ [] |:selected-ns |state
            [] |div
              [] |{} $ [] |:style ([] |merge |ui/fullscreen |ui/column |style-file-tree)
              [] |render-toolbar ([] |:draft |state) (, |cursor)
              [] |div
                [] |{} $ [] |:style ([] |merge |ui/row |style-body)
                [] |list->
                  [] |{} $ [] |:style
                    [] |{} ([] |:overflow |:auto) ([] |:padding "|\"16px 16px 200px 16px")
                  [] |->> ([] |keys |files) ([] |sort)
                    [] |map $ [] |fn ([] |ns-name)
                      [] |[] |ns-name $ [] |div
                        [] |{}
                          [] |:on-click $ [] |fn ([] |e |d!) ([] |d! |cursor $ [] |assoc |state |:selected-ns |ns-name)
                          [] |:style $ [] |merge
                            [] |{} ([] |:color $ [] |hsl |0 |0 |50) ([] |:cursor |:pointer)
                            [] |if ([] |= |ns-name |selected-ns)
                              [] |{} $ [] |:color ([] |hsl |0 |0 |100)
                        [] |<> |ns-name
                [] |if ([] |contains? |files |selected-ns)
                  [] |comp-brief-file ([] |>> |states |selected-ns) (, |selected-ns) ([] |get |files |selected-ns)
        |style-body $ [] |def |style-body
          [] |{} ([] |:flex |1) ([] |:overflow |:auto)
        |style-toolbar $ [] |def |style-toolbar ([] |{} $ [] |:padding "||16px 16px")
        |on-change $ [] |defn |on-change ([] |cursor)
          [] |fn ([] |e |dispatch!)
            [] |dispatch! |:states $ [] |[] |cursor ([] |:value |e)
        |style-file-tree $ [] |def |style-file-tree
          [] |{} ([] |:background-color $ [] |hsl |0 |0 |0) ([] |:padding "|\"0 16px")
        |render-toolbar $ [] |defn |render-toolbar ([] |draft |cursor)
          [] |div ([] |{} $ [] |:style |style-toolbar)
            [] |button $ [] |{} ([] |:inner-text ||Graph) ([] |:style |widget/button) ([] |:on-click |on-graph)
            [] |=< |8 |nil
            [] |button $ [] |{} ([] |:inner-text ||Stack) ([] |:style |widget/button) ([] |:on-click |on-stack)
            [] |=< |8 |nil
            [] |input $ [] |{} ([] |:value |draft) ([] |:placeholder "||ns/def or ns") ([] |:style |widget/input) ([] |:on-input $ [] |on-change |cursor) ([] |:on-keydown $ [] |on-keydown |draft |cursor)
        |on-keydown $ [] |defn |on-keydown ([] |draft |cursor)
          [] |fn ([] |e |dispatch!)
            [] |if
              [] |= |keycode/key-enter $ [] |.-keyCode ([] |:original-event |e)
              [] |do
                [] |if ([] |string/includes? |draft ||/) ([] |dispatch! |:collection/add-definition $ [] |string/split |draft ||/) ([] |dispatch! |:collection/add-namespace |draft)
                [] |dispatch! |cursor $ [] |{} ([] |:draft "|\"")
        |on-graph $ [] |defn |on-graph ([] |e |dispatch!)
          [] |dispatch! |:router/route $ [] |{} ([] |:name |:graph) ([] |:data |nil)
        |on-stack $ [] |defn |on-stack ([] |e |d! |m!)
          [] |d! |:router/route $ [] |{} ([] |:name |:workspace) ([] |:data |nil)
      :procs $ []
    |page $ {}
      :ns $ [] |ns |app.page
        [] |:require ([] |[] |respo.core |:refer $ [] |[] |create-element) ([] |[] |respo.core |:refer $ [] |[] |html |head |title |script |style |meta' |div |link |body) ([] |[] |respo.render.html |:refer $ [] |[] |make-string) ([] |[] |app.comp.container |:refer $ [] |[] |comp-container) ([] |[] |shell-page.core |:refer $ [] |[] |make-page |slurp |spit) ([] |[] |app.schema |:as |schema) ([] |[] |cljs.reader |:refer $ [] |[] |read-string)
      :defs $ {}
        |main! $ [] |defn |main! ([])
          [] |if ([] |= |js/process.env.env ||dev) ([] |spit ||target/index.html $ [] |dev-page) ([] |spit ||dist/index.html $ [] |prod-page)
        |base-info $ [] |def |base-info
          [] |{} ([] |:title "||Stack Editor") ([] |:icon ||http://logo.cirru.org/cirru-400x400.png) ([] |:ssr |nil) ([] |:inilne-html ||) ([] |:styles $ [] |[] ||http://cdn.tiye.me/favored-fonts/main-fonts.css)
        |prod-page $ [] |defn |prod-page ([])
          [] |let
            []
              [] |html-content $ [] |make-string ([] |comp-container |schema/store)
              [] |assets $ [] |read-string ([] |slurp ||dist/assets.edn)
            [] |make-page |html-content $ [] |merge |base-info
              [] |{} $ [] |:scripts
                [] |map
                  [] |fn ([] |x) ([] |:output-name |x)
                  , |assets
        |dev-page $ [] |defn |dev-page ([])
          [] |make-page || $ [] |merge |base-info
            [] |{} $ [] |:scripts ([] |[] ||/client.js)
      :procs $ []
    |updater.collection $ {}
      :ns $ [] |ns |app.updater.collection
        [] |:require ([] |[] |clojure.string |:as |string) ([] |[] |app.util |:refer $ [] |[] |helper-notify |make-path |view-focused |make-focus-path) ([] |[] |app.util.detect |:refer $ [] |[] |=path?) ([] |app.util.stack |:refer $ [] |push-path)
      :defs $ {}
        |rename $ [] |defn |rename ([] |store |op-data |op-id)
          [] |let
            []
              [] ([] |[] |code-path |new-form) (, |op-data)
              []
                [] |{} ([] |ns-part |:ns) ([] |kind |:kind) ([] |extra-name |:extra) ([] |focus |:focus)
                , |code-path
              [] |pointer $ [] |get-in |store ([] |[] |:writer |:pointer)
            [] |case |kind
              [] |:ns $ [] |-> |store
                [] |update-in ([] |[] |:collection |:files)
                  [] |fn ([] |files)
                    [] |-> |files ([] |dissoc |ns-part) ([] |assoc |new-form $ [] |get |files |ns-part)
                [] |assoc-in ([] |[] |:writer |:stack |pointer)
                  [] |{} ([] |:ns |new-form) ([] |:kind |:ns) ([] |:extra |nil) ([] |:focus |focus)
              [] |:defs $ [] |let
                [] $ [] ([] |[] |new-ns |new-name) ([] |string/split |new-form ||/)
                [] |-> |store
                  [] |update-in ([] |[] |:collection |:files)
                    [] |fn ([] |files)
                      [] |if ([] |= |new-ns |ns-part)
                        [] |update-in |files ([] |[] |ns-part |:defs)
                          [] |fn ([] |dict)
                            [] |let
                              [] $ [] |def-code ([] |get |dict |extra-name)
                              [] |-> |dict ([] |dissoc |extra-name) ([] |assoc |new-name $ [] |assoc |def-code |1 |new-name)
                        [] |let
                          [] $ [] |def-code ([] |get-in |files $ [] |[] |ns-part |:defs |extra-name)
                          [] |-> |files
                            [] |update-in ([] |[] |ns-part |:defs)
                              [] |fn ([] |dict) ([] |dissoc |dict |extra-name)
                            [] |assoc-in ([] |[] |new-ns |:defs |new-name) ([] |assoc |def-code |1 |new-name)
                  [] |assoc-in ([] |[] |:writer |:stack |pointer)
                    [] |{} ([] |:ns |new-ns) ([] |:kind |:defs) ([] |:extra |new-name) ([] |:focus |focus)
              [] |do ([] |println "||Cannot rename:" |code-path |new-form) (, |store)
        |remove-this $ [] |defn |remove-this ([] |store |op-data |op-id)
          [] |let
            [] ([] |writer $ [] |:writer |store) ([] |stack $ [] |:stack |writer) ([] |pointer $ [] |:pointer |writer)
              []
                [] |{} ([] |ns-part |:ns) ([] |kind |:kind) ([] |extra-name |:extra)
                [] |get |stack |pointer
            [] |-> |store
              [] |update-in ([] |[] |:collection |:files)
                [] |fn ([] |files)
                  [] |case |kind
                    [] |:defs $ [] |update-in |files ([] |[] |ns-part |:defs)
                      [] |fn ([] |defs) ([] |dissoc |defs |extra-name)
                    [] |:procs $ [] |assoc-in |files ([] |[] |ns-part |:procs) ([] |[])
                    [] |:ns $ [] |dissoc |files |ns-part
                    , |files
              [] |update-in ([] |[] |:writer |:stack)
                [] |fn ([] |stack)
                  [] |cond
                    [] ([] |empty? |stack) (, |stack)
                    [] ([] |zero? |pointer) ([] |subvec |stack |1)
                    []
                      [] |= ([] |inc |pointer) ([] |count |stack)
                      [] |subvec |stack |0 $ [] |dec ([] |count |stack)
                    [] |:else $ [] |into ([] |[])
                      [] |concat ([] |subvec |stack |0 |pointer) ([] |subvec |stack $ [] |inc |pointer)
              [] |update-in ([] |[] |:writer |:pointer)
                [] |fn ([] |pointer)
                  [] |if ([] |pos? |pointer) ([] |dec |pointer) (, |pointer)
        |write-code $ [] |defn |write-code ([] |store |op-data)
          [] |let
            [] ([] |tree $ [] |:tree |op-data) ([] |focus $ [] |:focus |op-data) ([] |writer $ [] |:writer |store) ([] |stack $ [] |:stack |writer) ([] |pointer $ [] |:pointer |writer) ([] |clipboard $ [] |:clipboard |op-data) ([] |path-info $ [] |get |stack |pointer)
            [] |-> |store
              [] |assoc-in ([] |[] |:writer |:stack |pointer |:focus) (, |focus)
              [] |assoc-in ([] |[] |:writer |:clipboard) (, |clipboard)
              [] |assoc-in ([] |make-path |path-info) (, |tree)
        |hydrate $ [] |defn |hydrate ([] |store |op-data |op-id)
          [] |let
            [] ([] |writer $ [] |:writer |store) ([] |collection $ [] |:collection |store)
              [] |code-path $ [] |get ([] |:stack |writer) ([] |:pointer |writer)
            [] |println |code-path
            [] |assoc-in |store
              [] |concat ([] |make-path |code-path) ([] |:focus |code-path)
              , |op-data
        |edit $ [] |defn |edit ([] |store |op-data)
          [] |let ([] $ [] |path |op-data)
            [] |-> |store ([] |update |:writer $ [] |push-path |op-data)
              [] |assoc |:router $ [] |{} ([] |:name |:workspace) ([] |:data |nil)
        |add-definition $ [] |defn |add-definition ([] |store |op-data)
          [] |let
            []
              [] ([] |[] |that-ns |that-name) (, |op-data)
              [] |path $ [] |[] |:collection |:files |that-ns |:defs |that-name
              [] |maybe-definition $ [] |get-in |store |path
            [] |if ([] |some? |maybe-definition) (, |store)
              [] |assoc-in |store |path $ [] |[] ||defn |that-name ([] |[])
        |edit-ns $ [] |defn |edit-ns ([] |store |op-data |op-id)
          [] |let
            [] ([] |writer $ [] |:writer |store) ([] |stack $ [] |:stack |writer) ([] |pointer $ [] |:pointer |writer) ([] |code-path $ [] |get |stack |pointer)
              [] |pkg $ [] |get-in |store ([] |[] |:collection |:package)
            [] |; |println "||Edit ns:" |code-path
            [] |if
              [] |= ([] |:kind |code-path) (, |:ns)
              [] |let
                [] ([] |guess-ns $ [] |view-focused |store)
                  [] |ns-name $ [] |if ([] |some? |guess-ns)
                    [] |string/replace |guess-ns ([] |str |pkg ||.) (, ||)
                    , |nil
                [] |if
                  [] |and ([] |some? |ns-name)
                    [] |some? $ [] |get-in |store ([] |[] |:collection |:files |ns-name)
                  [] |update |store |:writer $ [] |push-path
                    [] |{} ([] |:ns |ns-name) ([] |:kind |:ns) ([] |:extra |nil) ([] |:focus $ [] |[])
                  [] |update |store |:notifications $ [] |fn ([] |notifications) ([] |helper-notify |op-id $ [] |str "||\"" |ns-name "||\" not found")
              [] |update |store |:writer $ [] |push-path
                [] |{} ([] |:ns $ [] |:ns |code-path) ([] |:kind |:ns) ([] |:extra |nil) ([] |:focus $ [] |[])
        |load-remote $ [] |defn |load-remote ([] |store |op-data)
          [] |let ([] $ [] |collection |op-data) ([] |; |println ||loading: |collection)
            [] |-> |store $ [] |update |:collection
              [] |fn ([] |cursor) ([] |merge |cursor |collection)
        |add-namespace $ [] |defn |add-namespace ([] |store |op-data)
          [] |let
            [] ([] |namespace' |op-data)
              [] |basic-code $ [] |[] ||ns
                [] |str ([] |get-in |store $ [] |[] |:collection |:package) (, ||. |namespace')
            [] |-> |store $ [] |assoc-in ([] |[] |:collection |:files |namespace')
              [] |{} ([] |:ns |basic-code) ([] |:defs $ [] |{}) ([] |:procs $ [] |[])
        |remove-file $ [] |defn |remove-file ([] |store |op-data |op-id)
          [] |update-in |store ([] |[] |:collection |:files)
            [] |fn ([] |files) ([] |dissoc |files |op-data)
      :procs $ []
    |util.stack $ {}
      :ns $ [] |ns |app.util.stack
        [] |:require $ [] |[] |app.util.detect |:refer ([] |[] |=path?)
      :defs $ {}
        |push-paths $ [] |defn |push-paths ([] |new-paths)
          [] |fn ([] |writer)
            [] |if ([] |empty? |new-paths) (, |writer)
              [] |let
                [] ([] |stack $ [] |:stack |writer) ([] |pointer $ [] |:pointer |writer)
                [] |if
                  [] |and ([] |= |1 $ [] |count |new-paths)
                    [] |>= ([] |path-index-of |stack $ [] |first |new-paths) (, |0)
                  [] |do
                    [] |println ||hit $ [] |path-index-of |stack ([] |first |new-paths)
                    [] |assoc |writer |:pointer $ [] |path-index-of |stack ([] |first |new-paths)
                  [] |cond
                    [] ([] |empty? |stack)
                      [] |{}
                        [] |:stack $ [] |into ([] |[]) (, |new-paths)
                        [] |:pointer |0
                        [] |:focus $ [] |:focus ([] |first |new-paths)
                    []
                      [] |= ([] |inc |pointer) ([] |count |stack)
                      [] |-> |writer
                        [] |assoc |:stack $ [] |into ([] |[]) ([] |concat |stack |new-paths)
                        [] |update |:pointer |inc
                        [] |assoc |:focus $ [] |:focus ([] |first |new-paths)
                    [] |:else $ [] |-> |writer
                      [] |assoc |:stack $ [] |into ([] |[])
                        [] |concat ([] |subvec |stack |0 $ [] |inc |pointer) (, |new-paths) ([] |subvec |stack $ [] |inc |pointer)
                      [] |update |:pointer |inc
                      [] |assoc |:focus $ [] |:focus ([] |first |new-paths)
        |push-path $ [] |defn |push-path ([] |x) ([] |push-paths $ [] |[] |x)
        |get-path $ [] |defn |get-path ([] |store)
          [] |let
            [] ([] |writer $ [] |:writer |store)
              [] ([] |{} |stack |:stack |pointer |:pointer) (, |writer)
            [] |get |stack |pointer
        |path-index-of $ [] |defn$ |path-index-of
          [] ([] |xs |y) ([] |path-index-of |xs |y |0)
          [] ([] |xs |y |idx)
            [] |if ([] |empty? |xs) (, |-1)
              [] |if ([] |=path? |y $ [] |first |xs) (, |idx)
                [] |recur ([] |rest |xs) (, |y) ([] |inc |idx)
      :procs $ []
    |comp.hot-corner $ {}
      :ns $ [] |ns |app.comp.hot-corner
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo.core |:refer $ [] |[] |defcomp |<> |div |span) ([] |[] |respo.comp.space |:refer $ [] |[] |=<) ([] |[] |respo-ui.core |:as |ui)
      :defs $ {}
        |comp-hot-corner $ [] |defcomp |comp-hot-corner ([] |router |writer)
          [] |div
            [] |{}
              [] |:style $ [] |{} ([] |:font-size ||24px) ([] |:font-weight ||300) ([] |:text-align ||center) ([] |:cursor ||pointer)
              [] |:on-click $ [] |on-switch |router |writer
            [] |<> |span "||Stack Editor" $ [] |{} ([] |:font-family "||Josefin Sans")
        |on-switch $ [] |defn |on-switch ([] |router |writer)
          [] |fn ([] |e |dispatch!)
            [] |if
              [] |= ([] |:name |router) (, |:workspace)
              [] |dispatch! |:router/open-file-tree |nil
              [] |if
                [] |not $ [] |empty? ([] |:stack |writer)
                [] |dispatch! |:router/route $ [] |{} ([] |:name |:workspace) ([] |:data |nil)
      :procs $ []
    |actions $ {}
      :ns $ [] |ns |app.actions
        [] |:require ([] |[] |cljs.reader |:refer $ [] |[] |read-string) ([] |[] |ajax.core |:refer $ [] |[] |GET |POST |PATCH |json-request-format) ([] |[] |app.util |:refer $ [] |[] |make-path) ([] |[] |app.util.querystring |:refer $ [] |[] |parse-query) ([] |[] |shallow-diff.diff |:refer $ [] |[] |diff)
      :defs $ {}
        |load-collection! $ [] |defn |load-collection! ([] |dispatch! |file-tree?) ([] |println $ [] |pr-str |options)
          [] |GET
            [] |str ||http:// ([] |get |options ||host) (, ||:) ([] |get |options ||port)
            [] |{}
              [] |:handler $ [] |fn ([] |response)
                [] |let
                  [] $ [] |sepal-data ([] |read-string |response)
                  [] |if ([] |not $ [] |contains? |sepal-data |:package) ([] |js/alert "||Cannot find a :package field")
                  [] |dispatch! |:collection/load |sepal-data
                  [] |if |file-tree? $ [] |do
                    [] |dispatch! |:router/route $ [] |{} ([] |:name |:file-tree) ([] |:data |nil)
                    [] |; |dispatch! |:graph/load-graph |nil
                  [] |reset! |*remote-sepal |sepal-data
              [] |:error-handler $ [] |fn ([] |error) ([] |println |error) ([] |dispatch! |:notification/add-one "||Failed to fetch collection")
        |submit-changes! $ [] |defn |submit-changes! ([] |collection |dispatch!)
          [] |PATCH
            [] |str ||http:// ([] |get |options ||host) (, ||:) ([] |get |options ||port)
            [] |{} ([] |:format $ [] |json-request-format)
              [] |:body $ [] |pr-str ([] |diff |@*remote-sepal |collection)
              [] |:handler $ [] |fn ([] |response) ([] |println |response) ([] |dispatch! |:notification/add-one ||Patched) ([] |reset! |*remote-sepal |collection)
              [] |:error-handler $ [] |fn ([] |error) ([] |println |error)
                [] |if ([] |zero? $ [] |:status |error) ([] |dispatch! |:notification/add-one "||Connection failed!")
                  [] |let
                    [] $ [] |response ([] |read-string $ [] |:response |error)
                    [] |dispatch! |:notification/add-one $ [] |:status |response
        |*remote-sepal $ [] |defonce |*remote-sepal ([] |atom |nil)
        |options $ [] |def |options
          [] |merge
            [] |{} ([] ||port ||7010) ([] ||host ||localhost)
            [] |parse-query $ [] |.-search |js/location
        |submit-collection! $ [] |defn |submit-collection! ([] |collection |dispatch!)
          [] |POST
            [] |str ||http:// ([] |get |options ||host) (, ||:) ([] |get |options ||port)
            [] |{} ([] |:format $ [] |json-request-format) ([] |:body $ [] |pr-str |collection)
              [] |:handler $ [] |fn ([] |response) ([] |println |response) ([] |dispatch! |:notification/add-one ||Saved) ([] |reset! |*remote-sepal |collection)
              [] |:error-handler $ [] |fn ([] |error) ([] |println |error)
                [] |if ([] |zero? $ [] |:status |error) ([] |dispatch! |:notification/add-one "||Connection failed!")
                  [] |let
                    [] $ [] |response ([] |read-string $ [] |:response |error)
                    [] |dispatch! |:notification/add-one $ [] |:status |response
        |display-code! $ [] |defn |display-code! ([] |store)
          [] |let
            [] ([] |writer $ [] |:writer |store) ([] |collection $ [] |:collection |store)
              [] |path-info $ [] |get ([] |:stack |writer) ([] |:pointer |writer)
              [] |tree $ [] |get-in |store ([] |make-path |path-info)
            [] |if ([] |some? |tree)
              [] |-> |js/window ([] |.open) ([] |.-document)
                [] |.write ||<pre><code> ([] |pr-str |tree) (, ||</code></pre>)
      :procs $ []
    |style.widget $ {}
      :ns $ [] |ns |app.style.widget
        [] |:require ([] |[] |hsl.core |:refer $ [] |[] |hsl) ([] |[] |respo-ui.core |:as |ui)
      :defs $ {}
        |entry-line $ [] |def |entry-line
          [] |merge |var-entry $ [] |{} ([] |:display ||block)
        |clickable-text $ [] |def |clickable-text
          [] |{} ([] |:text-decoration |:underline) ([] |:cursor |:pointer) ([] |:color $ [] |hsl |0 |0 |80) ([] |:font-family "||Josefin Sans")
        |input $ [] |def |input
          [] |merge |ui/input $ [] |{} ([] |:background-color $ [] |hsl |0 |0 |100 |0.14) ([] |:color $ [] |hsl |0 |0 |100) ([] |:font-family "||Source Code Pro,Menlo,monospace") ([] |:width ||320px) ([] |:border |:none)
        |var-entry $ [] |def |var-entry
          [] |{} ([] |:color $ [] |hsl |0 |0 |80) ([] |:cursor ||pointer) ([] |:font-family "||Source Code Pro,Menlo,monospace") ([] |:font-size ||14px) ([] |:line-height ||24px) ([] |:min-width ||160px)
        |entry $ [] |def |entry
          [] |{} ([] |:display ||inline-block) ([] |:background-color $ [] |hsl |200 |10 |40 |0) ([] |:color $ [] |hsl |0 |0 |100) ([] |:padding "||0 8px") ([] |:cursor ||pointer) ([] |:margin-bottom ||8px)
        |button $ [] |def |button
          [] |merge |ui/button $ [] |{} ([] |:background-color $ [] |hsl |0 |0 |100 |0.2) ([] |:color $ [] |hsl |0 |0 |100 |0.6) ([] |:height |28) ([] |:line-height ||28px) ([] |:border |:none)
      :procs $ []
