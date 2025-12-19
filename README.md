> ⚠️ This software project is in the earliest planning and exploration stage.
>
> The goal is to create a brain dump outlining my thoughts on flexible,
> scalable, and idiomatic patterns for web application development using
> ClojureScript and Clojure. I’m still exploring initial ideas and strategies,
> drawing partly from my previous work with the Moira library [^2]. Inspired by
> David Nolan’s Clojure Conj 2025 talk [^1], this project aims for a simpler, more
> functional mental model with minimal reliance on the JavaScript ecosystem
> (e.g., React).

# 🎪 Circus

Functional UI in pure Clojure(Script).

## Rationale

The React and JavaScript ecosystem is bloated and highly opinionated, imposing
the use of specific build tools and prescriptive development styles on
programmers. This approach introduces hidden layers of complexity that create
friction when paired with Clojure’s expressive minimalism and REPL-driven
workflows. [^1]

Circus is a minimalist and flexible framework for building user interfaces of
any complexity. It provides developers with a solid foundation that aligns well
with the core principles and philosophy of Clojure and ClojureScript.

## Architecture Ideas

* 🎩 Event-driven architecture based on a unified application log [^2]
* 🤡 Unidirectional data flow and shared rendering between client and server
* 🦁 Data-driven system lifecycle management and dependency injection
* 🐘 Local-first state management & synchronisation
* 🤹🏻‍♂️ Embrace namespaces as basic unit of composition

## Development

### Prerequisites

* [Clojure](https://clojure.org)
* [Node.js](https://nodejs.org)
* [Babashka](https://babashka.org)

### Setup

```sh
bb dev:init
```

### Start

For Clojure development, simply start the nREPL server with:

```sh
bb dev
```

To spawn a complete ClojureScript development environment, the following will

* watch and compile an example project available at [localhost:9000](http://localhost:9000)
* watch and compile the test runner available at [localhost:8000](http://localhost:8000)
* watch and compile CSS

```sh
bb dev:cljs
```

See [Shadow CLJS help](https://shadow-cljs.github.io/docs/UsersGuide.html#_editor_integration)
for details on how to select the respective ClojureScript build inside your
editor of choice.

```clj
(shadow/select :dev)
```

### Test

Run all tests once on Clojure with

```sh
bb test
```

To watch and compile all tests for ClojureScript, start the test runner and
open it in a browser at[localhost:8000](http://localhost:8000).

```sh
bb test:cljs
```

This can also be used for a more minimal ClojureScript development setup, where
you connect to the build of the ClojureScript test runner:

```clj
(shadow/select :test)
```

[^1]: [A ClojureScript Survival Kit](https://github.com/swannodette/conj-2025-talk)
[^2]: [Moira](https://github.com/pitch-io/moira)
