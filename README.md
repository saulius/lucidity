# lucidity

[![Build Status](https://travis-ci.org/zcaudate/lucidity.png?branch=master)](https://travis-ci.org/im.chit/lucidity)
[![Clojars](https://img.shields.io/clojars/v/im.chit/lucid.svg)](https://clojars.org/im.chit/lucid)

tools for code clarity - see the world not as it is but as it should be.

# Welcome

<img src="http://docs.caudate.me/lucidity/img/logo.png" width="150"></img>

### What is it?

[lucidity](https://github.com/zcaudate/lucidity) is a set of libraries that assist in making clojure's already awesome **in-repl** development experience even more awesome. It provides:

- a rich set of functions to enable better reasoning about the code.
- tools for managing both the source and tests.
- tools for publishing documentation for the library.

### Why was it made?

[lucidity](https://github.com/zcaudate/lucidity) has been steadily built around the workflow of it's [author](https://github.com/zcaudate) to automate repetitive tasks when coding. It is the merging of five libraries: [jai](https://github.com/zcaudate/jai), [lein-midje-doc](https://github.com/zcaudate/lein-midje-doc), [lein-repack](https://github.com/zcaudate/lein-repack), [vinyasa](https://github.com/zcaudate/lucidity/tree/vinyasa) and [wu.kong](https://github.com/zcaudate/wu.kong) and is used extensively in the management of quality assurance, deployment and publication of the [hara](https://github.com/zcaudate/hara) suite.

### Okay, lets go!

List of current libraries:

- [lucid.aether](http://docs.caudate.me/lucidity/lucid-aether.html) - wrapper for org.eclipse.aether
- [lucid.core](http://docs.caudate.me/lucidity/lucid-core.html) - functions for the code environment
- [lucid.distribute](http://docs.caudate.me/lucidity/lucid-distribute.html) - code repackaging and distribution
- [lucid.mind](http://docs.caudate.me/lucidity/lucid-mind.html) - contemplative reflection for the jvm
- [lucid.package](http://docs.caudate.me/lucidity/lucid-package.html) - project packaging and dependencies
- [lucid.publish](http://docs.caudate.me/lucidity/lucid-publish.html) - generate documentation from code
- [lucid.query](http://docs.caudate.me/lucidity/lucid-query.html) - intuitive search for code
- [lucid.unit](http://docs.caudate.me/lucidity/lucid-unit.html) - metadata through unit tests

## License

Copyright © 2016 Chris Zheng

Distributed under the MIT License
