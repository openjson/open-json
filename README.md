# Open JSON

[![Build Status](https://travis-ci.org/openjson/openjson.svg?branch=master)]	(https://travis-ci.org/openjson/openjson)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


This code is extracted from the Android project to allow
a clean-room implementation of the popular JSON API to be
available under a free license as a small and independent
dependency.

The original library [1] is licensed under a standard BSD
license [2] with an additional line that requires the use of
the software only for "non-evil" purposes. Since this is
ill-defined, many downstream consumers of this software
find this license condition unacceptable. The moral is 
don't put jokes into legal documents.

## Maven

    <dependency>
        <groupId>com.github.openjson</groupId>
        <artifactId>openjson</artifactId>
        <version>1.0.0</version>
    </dependency>
    
# Acknowledgements

Thanks to the Android team for doing 99% of the work.

Thanks also to Simon Lessard for lending his critical eye 
and excellent suggestions.

Thanks to Tobias Soloschenko and Martin Grigorov with 
suggestions so open-json can help Wicket avoid the problem.

[1] http://www.json.org/
[2] http://www.json.org/license.html 


