# api-platform-admin-api

This API provides an interface to API Platform data to be consumed by other MDTP services.

It provides stable endpoints for consumers and allows for free growth and maintenance of the API Platform services.

The details of the endpoints can be found in the [OpenAPI Specification](./conf/api-platform-admin-api.yaml).

## Running locally
To run the service locally, start all the dependant services by running:

```bash
./run_local_with_dependencies.sh
```

This service uses internal auth to secure its endpoints.

Add an authorisation token of '15505' by running:

```bash
curl -X POST "http://localhost:8470/test-only/token" \
-H "content-type: application/json" \
-d "{
  \"token\": \"15505\",
  \"principal\": \"api-platform-admin-api-local-test\",
  \"permissions\": [{
     \"resourceType\": \"api-platform-admin-api\",
     \"resourceLocation\": \"*\",
     \"actions\": [ \"READ\" ]
  }]
}"
```

This token will remain in your local database for a year, so does not have to be generated every time.

Then call an endpoint such as:

```bash
curl "http://localhost:15505/applications/:applicationId" -H "Authorization: 15505"
```

Use one of your local applicationIds for the path parameter `:applicationId`.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").