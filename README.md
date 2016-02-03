=====================================================================
![Logo](https://github.com/ogreyesp/JCLAL/blob/master/icon.png)                                                                   
	                         JCLAL
                            7 November 2014
                                                                       
 		A Java Class Library for Active Learning  

           	    web: http://jclal.sourceforge.net          
=====================================================================


Contents:
---------

1. Download and install

2. Compilation

3. JCLAL documentation

4. Datasets format

5. Executing an experiment

6. Using JCLAl

7. Source code

8. Credits

9. Submission of code and bug reports

10. Copyright


-----------------------------------------------------------------------------

1. Download and install:
-----------------------------------------------------------------------------

JCLAL framework is open source software and it is distributed under the GNU general public license. It is constructed with a high-level software environment, with a strong object oriented design and use of design patterns, which allow to the developers reuse, modify and extend the framework. Up to date, JCLAL uses the Weka (http://weka.sourceforge.net) and Mulan (http://mulan.sourceforge.net) libraries to implement the most significant query strategies that have appeared on the single-label and multi-label learning paradigms. For next versions, we hope to include query strategies related with multi-instance and multi-label-multi-instance learning paradigms.

The user can download the current version of JCLAL from SourceForge at:

http://sourceforge.net/projects/jclal/files/

JCLAL can also be obtained using SVN, GIT and Mercurial via SourceForge. Further information is available in the following link:

https://sourceforge.net/projects/jclal/

Moreover, JCLAL can be imported as project in Eclipse or NetBeans. Information about how to import JCLAL as an Eclipse and Netbeans projects is available in the following link:

http://sourceforge.net/projects/jclal/files/jclal-documentation.pdf/download

----------------------------------------------------------------------------
2. Compilation:
----------------------------------------------------------------------------

The source code can be compiled in two ways:

1. Using the ant compiler and the build.xml file.

Open a terminal console located in the project path and type "ant".  A jar file named "jclal-1.0.jar" will be created. For cleaning the project just type "ant clean".

2. Using the maven compiler and the pom.xml file.

Open a terminal console located in the project path and type:
   1. "mvn initialize" to initialize the project setup and repositories.
   2. "mvn install" to build the jar file.

A jar file named "jclal-1.0.jar" will be created. For cleaning the project just type "mvn clean".

----------------------------------------------------------------------------
3. JCLAL documentation:
----------------------------------------------------------------------------

Any information related to the JCLAL framework is available in:

http://jclal.sourceforge.net

Additionally, there is available a document for the users and developers. The document can be downloaded from:

http://sourceforge.net/projects/jclal/files/jclal-documentation.pdf/download

----------------------------------------------------------------------------
4. Data sets format:
----------------------------------------------------------------------------

JCLAL uses the ARFF data set format. An ARFF (Attribute-Relation File Format) file is a text file that describes a list of instances sharing a set of attributes. The ARFF format was developed by the Machine Learning Project at the Department of Computer Science of The University of Waikato. The ARFF datasets are the format used by the Weka and Mulan libraries.

The following information was obtained from the WEKA webpage: 

A dataset has to start with a declaration of its name:

@relation name

followed by a list of all the attributes in the dataset (including 
the class attribute). These declarations have the form

@attribute attribute_name specification

If an attribute is nominal, specification contains a list of the 
possible attribute values in curly brackets:

@attribute nominal_attribute {first_value, second_value, third_value}

If an attribute is numeric, specification is replaced by the keyword 
numeric: (Integer values are treated as real numbers.)

@attribute numeric_attribute numeric

After the attribute declarations, the actual data is introduced by a 

@data

tag, which is followed by a list of all the instances. The instances 
are listed in comma-separated format, with a question mark 
representing a missing value. 

Comments are lines starting with % and are ignored.

Additionally, in the case of multi-label datasets, Mulan needs a XML file which describes the label space of the data.

----------------------------------------------------------------------------
5. Executing an experiment:
----------------------------------------------------------------------------

The JCLAL framework allows to users execute an experiment through an configuration file as well as directly from a Java code. We recommend that the users run the experiments through a configuration file, owing to it is the easier, intuitive and faster manner.

A configuration file comprises a series of parameters required to run an experiment. The most important parameters are described as follows:

-The evaluation method: this parameter establishes the evaluation method used in the learning process. Up to date, the HoldOut and k-fold cross validation methods are supported. A sample of the HoldOut evaluation method is shown:

<process evaluation-method-type="net.sf.jclal.evaluation.method.HoldOut">

- The dataset: the dataset to use in the experiment can be declared in several ways. For example, only one file dataset can be declared and the evaluation method splits the dataset into the train and test set using a sampling method. On the other hand, the train and test sets could be passed directly as parameters. A sample of the case when only one file is passed is shown:

<file-dataset>datasets/iris/iris.arff</file-dataset>
<percentage-split>66</percentage-split>

- The labeled and unlabeled sets: The labeled and unlabeled sets can be declared in several ways. The users can opt to extract the labeled and unlabeled sets from the training set, for doing it a sampling method must be declared. On the other hand, the labeled and unlabeled sets could be passed directly as parameters. A sample of the case when the train set is splitted into labeled and unlabeled sets is shown:

<rand-gen-factory seed="1299961164" type="net.sf.jclal.util.random.RanecuFactory"/>
<sampling-method type="net.sf.jclal.sampling.unsupervised.Resample">
                <percentage-to-select>10</percentage-to-select>
</sampling-method>

- The Active Learning algorithm: this parameter establishes the AL protocol used in the learning process. Up to date, the classical AL algorithm is supported, i.e. in each iteration a query strategy selects the most informative instance (a set of instances may be selected) from the unlabeled set. Afterward, an oracle labels the selected instance and the instance is added to the labeled set and removed from the unlabeled set. A sample of the AL algorithm is shown:

<algorithm type="net.sf.jclal.activelearning.algorithm.ClassicalALAlgorithm">

-Stopping criterions: this parameter determines when the experiment must be stopped. The JCLAL framework provides several ways to define stopping criterions. The users can define new stopping criterions acording their needs. The maximun number of iterations must be declared at least to guarantee that the AL process finish. Once a stopping criterion is reached, the AL algorithm finishes returning its results. A sample of 100 iteration is shown:

 <max-iteration>100</max-iteration>

- Active learning scenario:  this parameter establishes the scenario used in the AL process. To date, the Pool-based sampling and Stream-based sampling scenaries are supported. A sample of a scenario is shown:

<scenario type="net.sf.jclal.activelearning.scenario.PoolBasedSamplingScenario">

- Query strategy:  this parameter establishes the query strategy used in the AL process. To date, the most significant query strategies that have appeared in the single-label and multi-label contexts are supported. Also, the base classifier to use must be defined. A sample of a single-label query strategy is shown:

<query-strategy type="net.sf.jclal.activelearning.singlelabel.querystrategy.EntropySamplingQueryStrategy">
        <wrapper-classifier type="net.sf.jclal.classifier.WekaClassifier">
            <classifier type="weka.classifiers.bayes.NaiveBayes"/>
        </wrapper-classifier>						
</query-strategy>

- The oracle: this parameter establishes the oracle used in the AL process. To date, two type of oracle are supported. A Simulated Oracle that reveals the hidden classes of a selected unlabeled instance, and a Console Human Oracle that iteratively asks to the user for the class of a selected unlabeled instance. The users can define new types of oracles acording their needs. A sample of the Simulated Oracle is shown:

<oracle type="net.sf.jclal.activelearning.oracle.SimulatedOracle"/>

- Listener: a listener to display the results of the AL process should be determined from the package net.sf.jclal.listener. In the following example, a report is made every 1 iteration. Furthermore, the report is stored on a file named Example, and the results are printed in the console too. A window where the user can visualize the learning curve of the AL process is showed, also the user can select the evaluation measure to plot.

 <listener type="net.sf.jclal.listener.GraphicalReporterListener">
         <report-frequency>1</report-frequency>
         <report-on-file>true</report-on-file>
         <report-on-console>false</report-on-console>
         <report-title>Example</report-title>
         <show-window>true</show-window>
  </listener>

The JCLAL framework provides much more XML tags to configure an experiment file. In the JCLAL API reference can be consulted the tags that nowadays are supported by JCLAL. In the “examples” folder, which is included into the downloaded file of JCLAL framework, appears several examples of experiments of single-label and multi-label query strategies.

Additionally, in the package net.sf.jclal.gui.view.xml there is a class which allows to configure an experiment through a GUI. This user interface is simple and it can be extended in order to include new features.

----------------------------------------------------------------------------
6. Using JCLAL:
----------------------------------------------------------------------------

Once you have downloaded JCLAL, and designed an experiment in the configuration file, you can build and execute the experiment. Remember that the jclal-1.0.jar file can be built using ANT (build.xml) or MAVEN (pom.xml). 

Assuming you are in the directory containing the jar file and your configuration file (experiment) is located in examples/SingleLabel/EntropySamplingQueryStrategy.cfg,  in a command-line just type:

java -jar jclal-1.0.jar -cfg=”examples/SingleLabel/EntropySamplingQueryStrategy.cfg”

In a command-line just typing:

java -jar jclal-1.0.jar

a list of the command-line options that JCLAL accepts is printed. One of the options that JCLAL accepts via the command-line is to execute a set of experiment in parallel manner.

Further information about how to run algorithms using command-line, Eclipse or NetBeans IDE is available in the following link:

http://sourceforge.net/projects/jclal/files/jclal-documentation.pdf/download

In the “examples” folder, which is included into the downloaded file of JCLAL framework, appears several examples of experiments of single-label and multi-label query strategies.

----------------------------------------------------------------------------
7. Source code:
----------------------------------------------------------------------------

The source code of JCLAL is available for downloading at SourceForge via
direct download, SVN, GIT and Mercurial at the link:

https://sourceforge.net/projects/jclal/

The source code of JCLAL is free and available under the GNU General Public
License (GPL) v3. Thus, it can be distributed and modified without any fee.

----------------------------------------------------------------------------
8. Credits:
----------------------------------------------------------------------------

Refer to the web page for a list of contributors:

https://jclal.sourceforge.net/

----------------------------------------------------------------------------
9. Submission of code and bug reports:
----------------------------------------------------------------------------

If you have implemented an AL query strategy, scenario or extension using the JCLAL classes, and you would like to make it available to the community, please submit your contribution to ogreyesp@gmail.com.

If you find any bugs, please send a bug report to ogreyesp@gmail.com. Any comment or constribution will be well received.

----------------------------------------------------------------------------
10. Copyright:
----------------------------------------------------------------------------

The JCLAL framework is distributed under the GNU public license v3. Please read the file COPYING.
