package com.dwolla.fs2aws

import shapeless.tag
import shapeless.tag._

package s3 {
  trait BucketTag
  trait PrefixTag
  trait KeyTag
}

package object s3 {
  type Bucket = String @@ BucketTag
  type Prefix = String @@ PrefixTag
  type Key = String @@ KeyTag

  val tagBucket: String => Bucket = tag[BucketTag][String]
  val tagPrefix: String => Prefix = tag[PrefixTag][String]
  val tagKey: String => Key = tag[KeyTag][String]

  implicit class EnhancedPrefix(val prefix: Prefix) extends AnyVal {
    def /(id: String): Key = tagKey(s"$prefix/$id")
  }
}
