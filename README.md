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
1. HeLiFit ontology V1.5.1 - containing all the classes and properties used for transforming EMR (T2D Lab Tests) and PHR (Samsung Health data) into HeLiFit
2. helifit.template.ttl - containing all the RML mapping rules that support the transformation into HeLiFit. You can find the spec of the mapping under rdfizer --> src --> resources --> mappings.helifit


## Some Bookmark

[Ant Tutorial](https://www.javaguicodexample.com/antworksheet3.html)

[Logback documentation](https://logback.qos.ch/documentation.html)  
[Logback configuration](https://logback.qos.ch/manual/configuration.html)

[RMLMapper - Examples](https://github.com/RMLio/rmlmapper-java/tree/master/src/test/java/be/ugent/rml)  
[RMLMapper - Usage example](https://github.com/RMLio/rmlmapper-java/blob/master/src/test/java/be/ugent/rml/readme/ReadmeTest.java)  
[RMLMapper - Logback conf](https://github.com/RMLio/rmlmapper-java/blob/master/src/test/resources/logback.xml)

[IBM - FHIR Model Guide](https://ibm.github.io/FHIR/guides/FHIRModelGuide)  
[IBM - FHIR Model Javadoc](https://ibm.github.io/FHIR/javadocs/latest/overview-summary.html)

[HL7 -FHIR Resource List](https://hl7.org/fhir/2021may/resourcelist.html)  
[GATEKEEPER - FHIR Artifacts Summary](https://build.fhir.org/ig/gatekeeper-project/gk-fhir-ig/artifacts.html)