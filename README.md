
# Log Analyzing Service


This application offers two primary services: log data ingestion and reporting on the ingested data. The data is stored in ElasticSearch using Spring Boot JPA, which facilitates the storage and retrieval of relational data. Multiple services are provided to enable users to query the stored data, including features such as cardinality, groupBy, projection, filter, tabular, and nested aggregation. Hence allowing users to retrieve data in a highly customizable and dynamic manner.

Application can generate reports with the queried data in a tabular format, which is highly intuitive and accessible to users. 

Overall, this application is a  data processing and reporting tool to provide users with an efficient and intuitive way to interact with their log data. Its flexible query capabilities and user-friendly reporting features makes it easier for organizations looking to gain insights from large and complex log data sets.



## API Reference

#### Get all data 
#### api has feature for search using paging as well as scroll to fetch data efficiently from ElasticSearch database

```
  GET /api/log/search
```


#### Get cardinality

```
  GET /api/log/cardinality/?fieldValue=field
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `fieldValue`      | `string` | **Required**.  the name of the field for which cardinality is to be returned|


#### Get data by applying time filters

```
  GET /api/log/filterByTime?start=starttime&end=endtime
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `start`      | `string` | **Required**.  earliest expected time|
| `end`      | `string` | **Required**.  oldest expected time|


#### Get data by applying terms filter

```
  POST /api/log/filerByTerms
```

| RequestBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `fieldValue`      | `string` | **Required**.  the name of the field for which the data is to be returned|
| `terms`      | `array of strings` | **Required**.  expected field values|


#### Get doc count of a field

```
  GET /api/log/groupBy?fieldValue=filed
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `fieldValue`      | `string` | **Required**.  the name of the field for which the query is to be performed|

#### Get only specific fields of the data 

```
  POST /api/log/projectBy
```

| RequestBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `terms`      | `array of strings` | **Required**.  requires field names|

## Other Features


- The service layer of this application offers services for complex nested and tabular aggregation. Although these features were not demonstrated in the sample data, the application has the capability to perform these advanced aggregations once the appropriate data is available.

- To facilitate debugging for developers, the application provides a helper class that enables them to view the queries generated by the system to fetch data. 

- The application also offers server-side rendering of data fetched from the database in the form of tables. 

- the application incorporates rigorous validation checks during the data ingestion process. These checks include verifying that data is not null, validating data types, and ensuring that file formats are correct. 

## Comparison with other databases

Please go through the documentation I made which compares ElasticSearch and MongoDB.

https://sprinklr-my.sharepoint.com/:w:/p/shyam_prajapati/ETjdBtTRrrlEgkcS0mVIWLMBt2m3Lsw9KIW7Gu8BCmXXfA?e=Mn8LWT

## Setup the Environment
- Install Elasticsearch and Kibana on your machine. Version 7.15 of both were used while developing this application
- Go to bin directory in the downloaded ElasticSearch module and run the ElasticSearch application. You can verify by hitting the endpoint at port 9200.
- Similarly go to bin directory in the downloaded Kibana module and run the Kibana application. You can verify by hitting the endpoint at port 5601.
- Install any java ide Intellij or Eclipse to build and run the application easily.

## Run Locally

Clone the project

```bash
  git clone https://github.com/shyam2672/summer2023
```

Go to the project directory

```bash
  cd my-project
```

Install dependencies by building the project


```bash
  mvn compile
  mvn package
```

Start the server by running the main class

It's advisable to use intellij or eclipse for better experience



## Unit Testing

The code has been tested using JUnit testing framework. The total line coverage is 91% and 100% methods of all classes are covered. Total 28 tests are written till now. There is no interaction with database for testing as the service layer and helper classes are tested using mockito and the controller is tested with mockMVC webtesting.
## Tech Stack

Spring Boot, Java, ElasticSearch, Thymeleaf, Junit, MockMvc 

## Learnings

- Got to learn Spring Boot. Dependency Injection, Inversion of control, how to handle beans. It makes easier to test, deploy and manage applications.

- Learned ElasticSearch and Kibana. How the data is stored as documents and how the documents are indexed. Tried various queries on the sample data provided by ElasticSearch. Completed the ElasticSearch course available on official ElasticSearch youtube channel.

- Learned MongoDB. Completed the course available on their official site

- Learned unit testing and used various tools such as Mockito and Mockmvc to test the code efficiently.

- Learned more about GitHub. How to work on shared repo, how to apply branch protection rules, how to delete past commits.

- Learned about how to use thymeleaf templates so that we can visualize the data fetched by the server without any 3rd party client, i.e Server side rendering.

- Few other things: Intellij debugger, gradle, java web services,SOLID design principles, java memory management, read java head first book.

## Screen Shots

#### Search results
<img width="612" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/5a811bf7-58dd-422b-83e8-fe060fde33e2">


#### Search results using paging
<img width="612" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/043ba10e-45f9-4900-a720-80e8e5adc831">



#### Search results using scroll
<img width="612" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/880f00a8-2e8f-40e8-ba27-2fee33a209f5">



#### GroupBy source
<img width="509" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/ce90d54f-6f81-4ae2-981b-d3371ab7093c">




#### Search results with time filter
<img width="626" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/7ec0c946-e3a4-43b4-a928-753fa7aef666">




#### Get Cardinality

<img width="392" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/cefe5209-3057-4ad6-b438-1f2a777bbb01">

#### Filter data by terms

<img width="449" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/ff16694a-f94e-42f2-82a8-c12f11c4d66f">

#### Project only certain feilds

<img width="336" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/899a9e56-de9e-4201-84fa-303331f1376f">


#### Class Diagram

![uml loganalyzer](https://github.com/shyam2672/summer2023/assets/91652089/87516ea5-40d8-4231-bd62-4ebb1594f1d4)

#### Class dependencies

![class dependecies](https://github.com/shyam2672/summer2023/assets/91652089/68a4f3dd-46f5-48c9-bc4f-2662cdd91f27)


#### source code directory structure
<img width="431" alt="Screenshot 2023-07-07 at 2 35 57 PM" src="https://github.com/shyam2672/summer2023/assets/91652089/c08ae918-928d-4b8d-ab0d-beab68b85f84">

#### Document Structure
  <img width="613" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/e03884df-0580-479e-8913-9fd2f988b17f">

  
#### Index Mapping
<img width="512" alt="image" src="https://github.com/shyam2672/summer2023/assets/91652089/bfba700a-f827-471f-945c-e62f9f5e4d75">
  

