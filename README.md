# librAIry NLP toolkit for English texts

`nlpEN-service` provides a fast and easy way to analyze texts from large document corpora by using both HTTP-Restful and AVRO API.

## Features
- Part-of-Speech Tagger (and filter)
- Lemmatizer
- N-Grams Identifier
- Wikipedia resource Finder
- Annotate your text with the elements discovered
- Can be run locally using multiple threads, or in parallel on multiple machines


## Quick Start

## Run locally
To run NLP-EN service using the default dataset:
1. Install [Docker](https://docs.docker.com/install/) and [Docker-Compose](https://docs.docker.com/compose/install/) 
1. Clone this repo and move into its top-level directory.

	```
	git clone git@github.com:librairy/nlpEN-service.git
	```
1. Run the service by: `docker-compose up -d`
1. You should be able to monitor the progress by: `docker-compose logs -f`

- The above command runs two services: DBpedia Spotlight and librAIry NLP-EN, and uses the settings specified within `docker-compose.yml`.
- The HTTP Restful-API should be available at: `http://localhost:7777/en` 

## Run in distributed mode
Create a [Swarm](https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/) and configure as services as you need.



## Configuration
To change configuration, just edit the [docker-compose.yml](docker-compose.yml) file.

| Config  |  Description |
|---|---|
| `REST_PATH`  |Endpoint where service is listening.    |
| `HTTP port`   |Internal HTTP port is 7777|
| `AVRO port`  | Internal AVRO port is 65111  |


## Reference

You can use the following to cite the service:

```
@inproceedings{Badenes-Olmedo:2017:DTM:3103010.3121040,
 author = {Badenes-Olmedo, Carlos and Redondo-Garcia, Jos{\'e} Luis and Corcho, Oscar},
 title = {Distributing Text Mining Tasks with librAIry},
 booktitle = {Proceedings of the 2017 ACM Symposium on Document Engineering},
 series = {DocEng '17},
 year = {2017},
 isbn = {978-1-4503-4689-4},
 pages = {63--66},
 numpages = {4},
 url = {http://doi.acm.org/10.1145/3103010.3121040},
 doi = {10.1145/3103010.3121040},
 acmid = {3121040},
 publisher = {ACM},
 keywords = {data integration, large-scale text analysis, nlp, scholarly data, text mining},
} 

```

## Contact
This repository is maintained by [Carlos Badenes-Olmedo](mailto:cbadenes@gmail.com). Please send me an e-mail or open a GitHub issue if you have questions. 


