# Dependency Service #

A simple web service built on Scalatra to provide some analytics on data from software projects.

**Implements:** a GET API at /dependencies which takes a query parameter named 'variable' and returns a list of JSON objects with two fields:
* `variable`, the name of the dependent variable
* `mi`, the mututal information between the two variables

The returned list is sorted in descending order of `mi`. 

## Implementation Choices ##
**Framework:** [Scalatra](http://www.scalatra.org/) was chosen to implement the web service. This is a lightweight Scala framework that makes it very simple to do the HTTP routing necessary. I looked at other frameworks like Play, but they seemed more heavyweight than needed for this project.

**Data Model:** I used a simple implementation of a Map from string keys to a List of Dependency objects (which are just wrappers for the variable name and mi). This is generated from the CSV when the Servlet launches, since the data set is so small. I didn't think it was worth the overhead of adding some kind of persistence solution.

However, the contract of the data model is simple and the specific implementation hidden from the web service. If the data were bigger or the API more complex, the code could easily be changed to work with a database of some kind without changing the contract. 

I also used [Saddle](https://saddle.github.io/) for loading the CSV file and representing/calculating with the data to obtain the mutual information. The library approximates Pandas in scientific python, and simplified the process of data analysis and file parsing significantly. Libraries like Spark seemed a bit too heavyweight for what I needed to do in this assignment.

**Testing:** I used [ScalaTest](http://www.scalatest.org/) to unit test the probability code and data model I wrote, and Scalatra's Specs2 library to test the web service. 

In both cases, the testing harness is quite lightweight and doesn't necessarily cover the full range of edge cases that might exist. In a real project obviously this would be a bigger concern, but here I just wanted a "good enough" solution.

## Potential improvements ##

As mentioned above, one area for improvement is the coverage of the testing harness. Other areas include:

* The code to estimate probabilities and mutual information is just a simple maximum-likelihood approach that counts occurences of the different values in the data. Given the data is quite small, these estimates could be quite biased. It might be worth trying a MAP approach and include some prior knowledge in the calculation (if such knowledge exists. 

* Missing value handling: currently I just reduce the dataset to the smallest set that has no missing values. However, this can get rid of over half the examples in for some features. It would be good to try and impute the missing values to improve robustness of probability and MI estimates.

* Currently, the API is just one method and so I didn't implement any authentication, rate limiting on requests, timeout behaviour, or similar. In a production API, particularly where the API can be used to modify data and not just read it, these features would be needed. 

* The documentation could be improved - currently it's limited to ScalaDoc style comments (although I haven't actually generated any ScalaDoc). The Scalatra library supports [Swagger](http://swagger.io/), a tool to generate interactive documentation and add discoverability in the API. If it were to be extended or used in production, such a feature could be useful.

## Build & Run ##

```sh
$ cd DependencyService
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

To test the API locally once jetty is running, visit http://localhost:8080/dependencies?variable=<name_of_variable>, where <name_of_variable> is one of: 

```
customer_understands_products
customer_understands_technology
customer_is_hands_on
customer_seems_fair
customer_shares_our_expectations
customer_is_old
customer_is_big
people_are_interested
people_know_the_technology
people_are_senior
project_is_technically_challenging
project_is_large
project_has_clear_scope_and_focus
project_has_optimistic_schedule
project_has_unknowns
project_tries_new_ways_or_technologies
contract_has_fixed_scope
contract_has_fixed_price
result_customer_was_happy
result_people_were_happy
result_numbers_ended_good
result_things_went_as_predicted
result_product_was_good
result_new_doors_were_opened
iterative
retrospectives
acceptance_testing
customer_as_product_owner
ux_kickstart
multicompetence_team
```