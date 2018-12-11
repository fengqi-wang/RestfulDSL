# RestfulDSL
Kotlin DSL for APIs with Retrofit, applying Coroutines.

Usage:

```java
  interface RestService {
        @GET("/items")
        fun fetchItems(@Header(ACCESS_TOKEN) accessToken: String): Deferred<Response<Item>>
  }
  
  ......
  
  restyCall<Item>(this) {
            request = { fetchItems("myToken") }
            onSuccess = {
                data_view.text = it?.name
            }
            onError = {
                error_view.text = it.message
            }
            onException = {
                exception_view.text = it.message
            }
  }
  ```
