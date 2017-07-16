***
                                 JCLAL
                A Java Class Library for Active Learning

                            9 February 2016

                             version 1.1

                    web: http://jclal.sourceforge.net
                    https://github.com/ogreyesp/JCLAL
***

# Introduction

`JCLAL` framework is open source software and it is distributed under the GNU general public license. It is constructed with a high-level software environment, with a strong object oriented design and use of design patterns, which allow to the developers reuse, modify and extend the framework. Up to date, JCLAL uses the WEKA (http://weka.sourceforge.net) and MULAN (http://mulan.sourceforge.net) libraries to implement the most significant query strategies that have appeared on the single-label and multi-label learning paradigms. For next versions, we hope to include query strategies related with multi-instance and multi-label-multi-instance learning paradigms.

# Sources

The user can download the current version of JCLAL from:

 * SourceForge: <http://sourceforge.net/projects/jclal/files/>

 * GitHub: <https://github.com/ogreyesp/JCLAL/archive/master.zip> or fork/`git clone` the source.

 * Maven repository: <http://search.maven.org/remotecontent?filepath=net/sf/jclal/jclal/1.1/>
		  <http://mvnrepository.com/artifact/net.sf.jclal/jclal/1.1>

 * Sonatype OSSRH repository: <https://oss.sonatype.org/#nexus-search;quick~jclal>

# Documentation

You'll find the documentation and API inside the directory `doc/` along with the original `*.javadoc.jar` file.

# Using JCLAL

The fastest way to use JCLAL is from the terminal. In the following examples it's assumed that you have a GNU/Linux flavor, and Java installed (v1.7+).

The examples are taken from the [jclal-1.1-documentation.pdf](https://github.com/mlliarm/JCLAL/blob/master/jclal-1.1-documentation.pdf)
page 16-18.

## Configuring an experiment

The good thing with JCLAL is that the user has to deal only with one XML configuration file per experiment, where all the parameters are tuned according to his needs.

The most important parameters that the user can control are:

 1. **The evaluation method**
 2. **The dataset**
 3. **Labelled and unlabelled sets**
 4. **The Active Learning algorithm**
 5. **The stopping criteria**
 6. **The AL scenario**
 7. **The query strategy**
 8. **The oracle**: there are provided two types of oracle. A simulated one and one that interacts with the user (Console Human Oracle).
 9. **Listeners**: we can define how the results of the experiment will be presented. The default method is storing the results in reports, in the `reports` directory.
 
You can see examples of XML configuration files (`cfg`) in the `examples` directory.

## Executing an experiment

Once the user has created a XML config. file, the fastest way to execute an experiment is from the terminal: 

```sh
$ java -jar jclal-1.1.jar -cfg example/SingleLabel/HoldOut/EntropySamplingQueryStrategy.cfg
```

One can run multiple experiments, by feeding JCLAL all the `cfg` files that exist in a directory, in our example `HoldOut`:

```sh
$ java -jar jclal-1.1.jar -d examples/SingleLabel/HoldOut
```

## Visualizations

It is possible to see the learning curve in each experiment by invoking `net.sf.jclal.gui.view.components.chart.ExternalBasicChart`.

# Development

If you find a bug, send a [pull request](https://yangsu.github.io/pull-request-tutorial/) and we'll discuss things.
