# Open JSON [![Build Status](https://travis-ci.org/openjson/openjson.svg?branch=master)](https://travis-ci.org/openjson/openjson) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This code is extracted from the Android project to allow
a clean-room implementation of the popular JSON API to be
available under a free license as a small and independent
dependency.

The [original library](http://www.json.org/) is [licensed under a standard BSD license with an additional line that requires the use of the software only for "non-evil" purposes](http://www.json.org/license.html).
Since this is ill-defined, many downstream consumers of this software find this license condition unacceptable.
The moral is don't put jokes into legal documents.
More background information is collected by the Debian team members at <https://wiki.debian.org/qa.debian.org/jsonevil>.
Relicensing the original library is impossible. See <https://github.com/stleary/JSON-java/issues/331> for more information.

## Maven
```xml
    <dependency>
        <groupId>com.github.openjson</groupId>
        <artifactId>openjson</artifactId>
        <version>1.0.11</version>
    </dependency>
```
  
# Acknowledgements

Thanks to the Android team for doing 99% of the work.

Thanks also to Simon Lessard for lending his critical eye and excellent suggestions.

Thanks to Tobias Soloschenko and Martin Grigorov with suggestions so open-json can help [Apache Wicket](https://wicket.apache.org/) avoid the problem.
