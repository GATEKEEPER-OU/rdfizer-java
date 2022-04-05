# RDFizer-java <!-- omit in toc -->

This component aims at to transform EMR (T2D Lab Tests) and PHR (Samsung Health data) to HeLiFit ontology through the use of the RML Mapper.

## Build and Create RDFizer.jar with IntelliJ IDEA as development environment 

1. git clone https://github.ecodesamsung.com/Health-Innovation/rdfizer.git
2. open the project IntelliJ IDEA
3. make sure you are using Java 17.0.2 with IntelliJ IDEA: File --> Project Structure --> Project --> SDK 
4. build Project. Build --> Build Project
5. create JAR. Build --> Build Artefact --> Rebuild
6. rdfizer.jar is created and will appear under the project folder: out --> artifacts --> rdfizer_jar


## What is it based on?
1. HeLiFit ontology V1.4.1 - containing all the classes and properties used for transforming EMR (T2D Lab Tests) and PHR (Samsung Health data) into HeLiFit
2. helifit.template.ttl - containing all the RML mapping rules that support the transformation into HeLiFit. You can find the spec of the mapping under rdfizer --> src --> resources --> mappings.helifit
