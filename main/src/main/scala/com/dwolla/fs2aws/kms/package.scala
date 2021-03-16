package com.dwolla.fs2aws

import java.util.Base64

package object kms {
  type Transform[A] = A => Array[Byte]

  val noopTransform: Transform[Array[Byte]] = identity
  private val decoder: Base64.Decoder = Base64.getDecoder
  val base64DecodingTransform: Transform[String] = decoder.decode
}
