package com.dwolla.fs2aws

package object kms {
  type Transform[A] = A => Array[Byte]

  val noopTransform: Transform[Array[Byte]] = identity
  val base64DecodingTransform: Transform[String] = javax.xml.bind.DatatypeConverter.parseBase64Binary
}
