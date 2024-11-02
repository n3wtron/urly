# URLY
Spring Boot Url shortener

# Endpoints
## POST /urls
### Request
```json
{"url":"http://www.urlToShort.com"}
```
### Response (shortUrl)
```text
sjkahasXXs
```

## GET /urls?url=[shortUrl]
### Response 
```text
http://www.urlToShort.com
```

# Shortener Algorithm
Current implementation of `UrlShortenerService` is using `(MD5 digest + salt).take([size])` to shorting the original url.

Using only the first `urly.shortener.md5.size` digits imply possible high collision rate, to mitigate that, a random `salt` is used combined with a retry mechanism in `UrlService`.


# How to Contribute
## Directory structures & patterns ( DDD + Hexagonal )
- `application/ports`: contains inbound/outbound ports (e.g. persistence)
- `application/services`: business logic services
- `domain/entities`: domain related object/entity (NOT DB ENTITY)
- `domain/aggregation`: aggregation object
- `domain/models`: value objects (see DDD doc)
- `infrastructure/adapters`: related `application/ports` implementations 
- `infrastructure/adapters/inbound/rest`: REST controllers
- `infrastructure/adapters/outbound/persistence`: Persistence layer (e.g. JPA)

## Code guideline
* Use ktlint linter
* Commits & PRs has to mention the related issue number
* Commit messages should complete the sentence `Applying this commit, it will [commit message]`
* Add new endpoints calls on `manualRequests.http` (IJ http client file)

## TODO
- [X] Create Domain classes
- [X] Create Service
- [X] Create a proper URLShortener algorithm
- [X] Connect to DB
- [X] Expose REST endpoints
- [X] Manage possible duplicates
- [X] Add Metrics
- [ ] Add Redirect endpoint
- [ ] Add Security
- [ ] Improve logs
- [ ] Update Endpoints
- [ ] Delete Endpoints
- [ ] Audit Logs
- [ ] Add OpenAPI
- [ ] Add ktlint check on `gradle build`
- [ ] Add CD/CI actions

