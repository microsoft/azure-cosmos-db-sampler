---
ArtifactType: nupkg, executable, azure-web-app, azure-cloud-service, etc. More requirements for artifact type standardization may come later.
Documentation: URL
Language: typescript, csharp, java, js, python, golang, powershell, markdown, etc. More requirements for language names standardization may come later.
Platform: windows, node, linux, ubuntu16, azure-function, etc. More requirements for platform standardization may come later.
Stackoverflow: URL
Tags: comma,separated,list,of,tags
---

# Azure Cosmos Sampler

The Azure Cosmos Sampler Plugin allows you to make queries to your Azure Cosmos DB instance from
[Apache JMeter](https://jmeter.apache.org/). After executing your queries, you will get back the
CosmosDB metrics in your response body that you can view from the `View Results Tree` lisenter.
Furthermore, if you want to view the metrics in application insights, you can send these to your
Azure Monitor instance with the [AppInsights Backend Listener](https://github.com/adrianmo/jmeter-backend-azure)
plugin. More details below about how to get that piece up and running.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

1. JDK version >= 11.

2. Some knowledge of [Apache JMeter](https://jmeter.apache.org).

3. Install Apache JMeter [here](https://jmeter.apache.org/download_jmeter.cgi). If you are on a
   mac, you can use install it via brew: `brew install jmeter`.

4. Install the [plugin manager](https://www.blazemeter.com/blog/how-install-jmeter-plugins-manager).
   If you used brew to install, the path will be located in `$(brew --prefix)/Cellar/jmeter/5.4.3/libexec/lib/ext`

5. Add our CosmosDB Sampler to your external plugins folder.

   - For `OSX`, you can run
     ```bash
     cd /Your/path/AzureCosmosSampler
     mvn package
     ```
     to build and package the JAR files. You'll want to copy the jar
     `cosmossampler-1.0-SNAPSHOT-jar-with-dependencies.jar` to the same place you copied the
     plugin manager on step 4.

6. **READ FURTHER ONLY IF YOU WANT TO USE THE TEMP FORK OF THE AppInsights Plugin TO VIEW METRICS IN APPINSIGHTS**:
   - Pull down this [repo](https://github.com/GreenCee/jmeter-backend-azure).
   - Run the maven package command.
   - Put the Jar file in your external Apache JMeter Plugins folder.
   - Add a `Backend Listener` to your thread group.
   - Select the AzureBackendClient backend listener in the drop down.
   - Enter your connection string and all other applicable values to the configuration.

## Running your first test

1. Create a thread group.
2. Add the Azure Cosmos Sampler to the thread group.
3. Enter your query and configuration values.
4. Add a `View Result Tree` listener so you can see your response and status.
5. Press the green play button on the tool bar and view results in the `View Result Tree`.

## Quickstart

1. Import the `.jmx` file in the example folder into Apache JMeter.
2. Enter your query and configuration values insight the sampler and backend listener. You can
   disable or remove the backend listener if you don't want to send metrics to Application Insights.
3. Hit the play button and view results in the `View Result Tree`.

## Contributing

Please read our [CONTRIBUTING.md](CONTRIBUTING.md) which outlines all of our policies, procedures,
and requirements for contributing to this project.

## Versioning and changelog

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the
[tags on this repository](link-to-tags-or-other-release-location).

It is a good practice to keep `CHANGELOG.md` file in repository that can be updated as part of a
pull request.

## License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE.txt)
file for details.

## Trademark Notice

Trademarks This project may contain trademarks or logos for projects, products, or services.
Authorized use of Microsoft trademarks or logos is subject to and must follow
[Microsoft’s Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion
or imply Microsoft sponsorship. Any use of third-party trademarks or logos are subject to those
third-party’s policies.
