# Out-of-Order Data Stream datagen.DataGen

This is a TU Berlin Bachelor's Thesis "Out-of-Order Data Stream Generator". 

## Setup
### Installation
```
$ git clone https://github.com/the-sea-ink/data-generator.git
$ cd data-generator
```
To run the generator: 
```
$ java -cp data-generator.jar datagen.DataGen
```
To run the analyzer: 
```
$ java -cp data-generator.jar datagen.Analyzer
```
To run the visualizer: 
```
$ java -cp data-generator.jar datagen.Visualizer 
```
Visualizer builds a histogramm for the first sensor by default. If a histogram for any other existing source is wanted, simply give its ordinal number as an argument, for example: 
```
$ java -cp data-generator.jar datagen.Visualizer 3
```


### Config File Setup
In order to be able to use the tool properly it is necessary to setup the configuration file. 
An example of a configuration file:
```
${
$  "outputFile" : "output/output.csv",
$
$  "runtimeInSeconds" : 100,
$  "startingTime": "2021-03-31 17:03:20.004",
$  "milliSecondsBetweenEvents": 347,
$  "shortestDelayInMilliseconds" : 50,
$  "longestDelayInMilliseconds" : 974,
$  "delayPattern": 1,
$  "oooPercentage": 50,
$
$  "networkAnomalyDurationInSeconds": 50,
$
$  "amountOfSources": 4,
$  "outliers" : [
$    {"sourceID":2, "pattern":1, "oooPercentage" : 20},
$    {"sourceID":3, "pattern":2, "networkAnomalyDuration" : 30},
$    {"sourceID":4, "pattern":3, "networkAnomalyDuration" : 50}
$    ],
$
$  "eventTimeColumn": 2,
$  "processingTimeColumn" : 3,
$  "warningColumn": 6
$} 
```
