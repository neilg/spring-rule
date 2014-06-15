# Meles Spring Rule [![Build Status](https://travis-ci.org/neilg/spring-rule.svg?branch=develop)](https://travis-ci.org/neilg/spring-rule)

A [JUnit rule](https://github.com/junit-team/junit/wiki/Rules) to manage [Spring](https://spring.io) contexts within a test class.

## Using

```java
public class SomeTest {

    @Rule
    public SpringContext springContext = SpringContext.builder()
            .withConfig(SomeConfig.class, MoreConfig.class)
            .build();

    @Autowired
    public FooService fooService

    @Before
    public void autowire() {
        springContext.autowire(this);
    }

    @Test
    public void someTestUsingFooService() {
        Object result = fooService.performAction();
        // various assertions
    }
    
}
```
 
## Building
   
```shell
mvn clean install
```

