# RestfulDSL

Kotlin DSL for APIs with Retrofit, applying Coroutines.

 

Usage:

 

```java

interface DemoService {

    companion object {

        private const val DEMO_URL = "/{appId}/profile"

    }

 

    @POST(DEMO_URL)

    fun fetchItemsAsync(

        @Path("appId") appId: String = ""

    ): Deferred<Response<List<DemoItem>>>

}
......

object DemoApi : BaseApi<DemoService>(DemoService::class.java)

  ......


  suspend fun demoAction(): AsyncResult<List<DemoItem>> {

    return callAsync { DemoApi.service.fetchItemsAsync() }

  }

 

  ......

 

  launch {

            demoAction()

                .onSuccess {

                    Toast.makeText(this@MainActivity, "Successfully Fetched ${it?.size} Items", Toast.LENGTH_LONG)

                        .show()

                }

                .onError {

                    Toast.makeText(this@MainActivity, "Failed!! -- $it", Toast.LENGTH_LONG).show()

                }

        } 

  ```
