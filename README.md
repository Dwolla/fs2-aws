# FS2 AWS Utilities

[![Travis](https://img.shields.io/travis/Dwolla/fs2-aws.svg?style=flat-square)](https://travis-ci.org/Dwolla/fs2-aws)
[![Bintray](https://img.shields.io/bintray/v/dwolla/maven/fs2-aws.svg?style=flat-square)](https://bintray.com/dwolla/maven/fs2-aws/view)
[![license](https://img.shields.io/github/license/Dwolla/fs2-aws.svg?style=flat-square)](https://github.com/Dwolla/fs2-aws/blob/master/LICENSE)

Utility classes for working with the [Java AWS SDKs](https://github.com/aws/aws-sdk-java) from Scala using [fs2](https://github.com/functional-streams-for-scala/fs2).

Projects including this library will also need to explicitly include the AWS SDK libraries they will rely on, to avoid inadvertently importing more libraries than are required.

This library is essentially Dwolla’s [scala-aws-utils](https://github.com/Dwolla/scala-aws-utils) ported to fs2.

## Artifacts

#### Library

```scala
"com.dwolla" %% "fs2-aws" % "1.0.0"
```

#### Core

Non-AWS-specific utilities are published separately for the JVM and Scala.js.

##### JVM

```scala
"com.dwolla" %% "fs2-utils" % "1.0.0"
```

##### JS

```scala
"com.dwolla" %%% "fs2-utils" % "1.0.0"
```

## Examples

All examples assume the following imports.

```scala
import cats.effect._
import com.amazonaws.services.cloudformation._
import com.amazonaws.services.cloudformation.model._
```

### Paginate over an AWS resource

Given an AWS Async client and a base request builder, obtain an fs2 `Stream` of the resource.

```scala
val client: AmazonCloudFormationAsync = ???
val requestFactory = () ⇒ new DescribeStackEventsRequest()
val x: Stream[IO, StackEvent] = requestFactory.fetchAll[IO](client.describeStackEventsAsync)(_.getStackEvents.asScala)
```

Note that settings can be changed inside the `() => Request` function. The pagination logic takes the result of calling the function and sets the next page token on the request before handing it to the AWS Async client.

### Retrieve an AWS resource

Given an AWS Async client and a request, obtain a [cats-effect `Async`](https://typelevel.org/cats-effect/typeclasses/async.html) that will contain the resource upon completion.

For paginated resources, this retrieves the first page. For non-paginated resources, this retrieves the entire resource.

```scala
val client: AmazonCloudFormationAsync = ???
val req = new DescribeStackEventsRequest()
val x: IO[DescribeStackEventsResult] = req.executeVia[IO](client.describeStackEventsAsync)
```
