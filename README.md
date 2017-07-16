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

You'll find the documentation and information of the API inside the directory `doc/` along with the original `*.javadoc.jar` file. Also, do check for simple examples the [jclal-1.1-documentation.pdf](https://github.com/mlliarm/JCLAL/blob/master/jclal-1.1-documentation.pdf).

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

# Features of this version

Taken from the file [CHANGELIST](https://github.com/mlliarm/JCLAL/blob/master/CHANGELIST).

## Added

- Some evaluation measures were included for further analysis, e.g. the time for selecting the unlabeled instances, the time for training and testing  the base classifier.

- It is possible to show by console the labels of the queried instances in a SimulatedOracle.

- It is possible to retrieve the last instances that were labeled by an oracle.

- The ConsoleHumanOracle class was improved. The human annotator can skip a query instance.

- Supports for incremental classifiers. If the base classifier is updatable then the base classifier is not retrained with all labeled instances from scratch in every iteration.

- An abstract method was added to the IqueryStrategy interface for executing any necessary action after the active learning process is finished, e.g. free memory, delete temporal files, etc.

- Supports for CLUS and LibSVM multilabel datasets.

- The 5X2 cross validation was added.

- The Leave One Out cross validation was added.

- We have included a real-usage scenario, where the user provides a small labeled set from which the initial classifier is trained, and an unlabeled set for determining the unlabeled instances that should be query in each iteration. In each iteration, the unlabeled instances selected are showed to the user, the user labels the instances and they are added to labeled set.  This real-usage scenario allows to obtain the set of labeled instances at the end of the session for further analysis.

-Several new features were added to the ExternalBasicChart class, e.g. curve options, view dash types, view shapes, export the graphics in EPS and SVG formats.

- The KnearestDistanceContainer class was added. It extends DistanceContainer class and allows to store the distance between an instance and its k-nearest neighbours.

- Several features were added to the LearningCurveUtility, e.g. creation of CSV files with AULC's values from directory of reports for further analysis.

- We have incorporated a new package net.sf.jclal.gui.view.components.statistical for performing statistical tests. The StatisticalWindow class is a user-friendly GUI that allows to perform the most popular non parametric statistical tests. The code was adopted from KEEL software (http://www.keel.es).

- The BinaryRelevanceUpdateable was added. It allows to execute the Binary Relevance approach with incremental classifiers as binary classifiers.

- The LCI and MMU multi-label strategies were added. (Li, X., & Guo, Y. 2013. Active Learning with Multi-label SVM Classification.)

-Supports for incremental classifiers of MOA library.

## Deleted

- The package net.sf.jclal.gui.view.xml was removed from this version. The GUI for configuring the experiments (GUIXML) only works with JCLAL-1.0 version. We are working in a more user-friendly GUI for configuring the experiments. 
- For the same reason, we've removed the plugin for working into Weka's explorer.

## Bugs fixed

- Bug at the time to show several curves which have different metrics. (ExternalBasicChart class)

- Bug in kfoldCrossValidation in the aggregation step of the fold's results. The error was only in the summary data, e.g. the size of the initial unlabeled set and the size of the initial labeled set. (kFoldCrossValidation class)

## Older version

* Version 1.0. Download from [sourceforge](https://sourceforge.net/projects/jclal/files/jclal-1.0/).

# Development

If you find a bug, send a [pull request](https://yangsu.github.io/pull-request-tutorial/) and we'll discuss things.
