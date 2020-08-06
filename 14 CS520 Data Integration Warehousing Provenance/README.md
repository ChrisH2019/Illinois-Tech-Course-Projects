# CS520 Data Curation Project Group2

## Dataset Description

- In this project, we chose a subset of Million Songs Dataset that contains 10,000 songs (1%, 1.8 GB compressed, URL: http://static.echonest.com/millionsongsubset_full.tar.gz). Each file corresponds to one song and one artist.
The fields of each file is organized into hierarchical directories that we extracted fields of interest from them:

	- artist id
	- artist name
	- artist latitude
	- artist longitude
	- artist location
	- artist familiarity
	- artist hotttnesss
	- songs id
	- songs title
	- song hotttnesss
	- song release year
	- duration
	- end of fade in
	- key
	- key confidence
	- loudness
	- mode
	- mode confidence
	- start of fade out
	- tempo
	- time signature
	- time signature confidence
	- track id
	- album name

## Dataset ETL

- First, we downloaded the dataset to the local disk from the above mentioned URL. Then, we extracted 10,000 h5 files and loaded the whole dataset into csv file for later use.

- Besides the songs dataset, we also downloaded two master datasets. The word cities dataset (URL: https://simplemaps.com/static/data/world-cities/basic/simplemaps_worldcities_basicv1.6.zip) contained the latitude and longtitude of most cities around the world. The US cities dataset (URL: https://simplemaps.com/static/data/us-cities/1.6/basic/simplemaps_uscities_basicv1.6.zip) contained US cities only. We extracted fields such as city name, State/Province name and country name to form a new field location. Then we loaded the extraxted fields into csv file for later use.

## Data Cleaning

### Data Quality Dianosis

- All fields in the songs dataset are string type. We reserved the original data types of fields as such artist_id, artist_name, artist_location,  song_id, song_title, track_id, album_name and converted all others to float type.

- There were constraint violations in the field song release year where some of values are less than 1922. 

- The datast contained 6258 missing values in latitude and longitude respectively, 4295 in location, 4 in familiarity, 1 in title, 4352 in song hottness and 5320 in song release year.

- There were mixed formats in the field location that some of them contained a combination of city, province/state and/or country.

- Incorrect information in field location.

### Imputing Missing Values

#### Imputing artist familiarity

- Since there are only 4 missing values in this field, we simply imputed them with median strategy.

#### Imputing artist latitude and longitude

- Since latitude and longitude is determined by location, we used the Jaccard similarity to measure the the values in the location field of the song dataset against those of two master data respectively. I first set the simliarity threshold to be greater than 0.7, and runned one pass through the world cities dataset and the us cities dataset. Number of missing values in the aforementioned fields dropped from 6258 to 4423. Then I lower the threshold from 0.7 to 0.5, the number dropped to 4339 which was pretty closed the number of missing values of the location (4295).

- Since there were only 10 locations where latitude and longitude are NaN, I manually imputed the missing values of those two fields by creating a dictionary.  

- Since thre were 2089 artist who did not had location value in the dataset and we had not found any reliable master data 
about theirlocations, at this moment, brute-forced imputation was impractical, so we were not able to impute the missing values remained in the latitude, longitude and location.

#### Imputing song hottness and release year

- Since we had around 5,000 labeled samples in this dataset, it was too few to use any supervised machine learning method to impute the fields hottness and year. Even though we did so, the accuracy rate is low and unreliable. Intead, we used an
unsupervied machine learning mehod NearestNeighbor to impute all missing values in those fields based on the predicted features of audio. 

## Evaluation

- We did a simple evaluation on the release year estimation by obtaining the release year info from music brainz, we compared the estimated years with actual years and find out while predicted numbers are on average 3 years lower than the actual year, considering the sample size is too small, the result is pretty good.

## How to run

- It's done in vizier with generated files all in data folder
- The vizier project is commited to a docker image and pushed to docker hub at pzhzqt/g2-p2-520
- To set up the container
~~~sh
cd docker
docker-compose build
docker-compose up
~~~
- To tear down
~~~sh
docker-compose down
~~~

## References

Thierry Bertin-Mahieux, Daniel P.W. Ellis, Brian Whitman, and Paul Lamere. 

The Million Song Dataset. In Proceedings of the 12th International Society for Music Information Retrieval Conference (ISMIR 2011), 2011.

World Cities Database. Simplemaps.com, 2019.
